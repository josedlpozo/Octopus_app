package com.josedlpozo.bluetootharduino;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;


public class Main extends Activity {

    // Message types sent from the BTkitService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BTkitService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final String TAG = "MAIN";
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private ArrayAdapter<String> mArrayAdapter;
    //private final Handler mHandler;

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    private BluetoothClient mBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button buton =(Button) findViewById(R.id.btnSearch);
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth no soportado", Toast.LENGTH_LONG).show();
            buton.setEnabled(false);
        } else {
            buton.setEnabled(true);
            enableBluetooth();
        }
        mArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);

        ListView lista = (ListView) findViewById(R.id.list);
        lista.setAdapter(mArrayAdapter);
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SEARCH","Hello");
                mArrayAdapter.clear();
                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
                String info = ((TextView) view).getText().toString();
                Toast.makeText(getApplicationContext(),
                        info, Toast.LENGTH_LONG)
                        .show();
                Log.d(TAG, "TextView_info " + info);
                String address = info.substring(info.length() - 17);
                Toast.makeText(getApplicationContext(),
                        address, Toast.LENGTH_LONG)
                        .show();
                Log.d(TAG, "Address " + address);
                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                mBluetooth.connect(device);
            }
        });

    }


    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        /*public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        /*public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }*/
}
