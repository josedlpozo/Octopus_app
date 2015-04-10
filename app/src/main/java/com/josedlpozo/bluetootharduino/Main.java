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



    private static final String TAG = "MAIN";
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private ArrayAdapter<String> mArrayAdapter;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    //private final Handler mHandler;

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button buton =(Button) findViewById(R.id.btnSearch);
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth no soportado", Toast.LENGTH_LONG).show();
            buton.setEnabled(false);
        } else {
            if(!btAdapter.isEnabled()) {
                buton.setEnabled(true);
                enableBluetooth();
            }
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

                Intent i = new Intent(Main.this, Octopus.class);
                i.putExtra(EXTRA_DEVICE_ADDRESS, address);
                startActivity(i);
            }
        });



    }


    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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

}
