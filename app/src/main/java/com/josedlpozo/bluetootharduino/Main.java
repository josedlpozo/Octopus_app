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



    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Bqzum bqzum;

    private Button joinCar;
    private Button joinSensor;
    private Button ON_Sensor;
    private Button OFF_Sensor;
    private Button BLINK_Sensor;
    private Button ON_Motor;
    private Button OFF_Motor;
    private Button BLINK_Motor;

    private int motorSensor = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Variables.INSTANCE.init(getApplication());
        } catch (Exception e) {
            e.printStackTrace();
        }
        bqzum = (Bqzum) Variables.INSTANCE.getBqzum();

        joinCar = (Button) findViewById(R.id.btnJoinCar);
        joinSensor = (Button) findViewById(R.id.btnJoinSensor);

        ON_Sensor = (Button) findViewById(R.id.ON_SENSOR);
        OFF_Sensor = (Button) findViewById(R.id.OFF_SENSOR);
        BLINK_Sensor = (Button) findViewById(R.id.BLINK_SENSOR);
        ON_Motor = (Button) findViewById(R.id.ON_MOTOR);
        OFF_Motor = (Button) findViewById(R.id.OFF_MOTOR);
        BLINK_Motor = (Button) findViewById(R.id.BLINK_MOTOR);

        setupBotones();
    }

    @Override
    public void onStart() {
        super.onStart();
            Log.e(TAG, "++ ON START ++");
        if (!bqzum.btEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            setupCar();
            setupSensor();
        }

    }

    private void setupBotones(){
        /*Variables.INSTANCE.ON_Motor.setEnabled(false);
        Variables.INSTANCE.OFF_Motor.setEnabled(false);
        Variables.INSTANCE.BLINK_Motor.setEnabled(false);

        Variables.INSTANCE.ON_Sensor.setEnabled(false);
        Variables.INSTANCE.OFF_Sensor.setEnabled(false);
        Variables.INSTANCE.BLINK_Sensor.setEnabled(false);*/

        ON_Sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"ON - SENSOR");
                bqzum.sendData("1",1);
            }
        });

        OFF_Sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"OFF - SENSOR");
                bqzum.sendData("0",1);
            }
        });

        BLINK_Sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"BLINK - SENSOR");
                bqzum.sendData("2",1);
            }
        });

        ON_Motor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"ON - MOTOR");
                bqzum.sendData("1",0);
            }
        });

        OFF_Motor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"OFF - SENSOR");
                bqzum.sendData("0",0);
            }
        });

        BLINK_Motor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"BLINK - SENSOR");
                bqzum.sendData("2",0);
            }
        });
    }

    private void setupCar() {
        joinCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motorSensor = 0;
                if (bqzum.getMotorStatus()) {
                    bqzum.disconnectMotor();
                } else {
                    Intent serverIntent = new Intent(Main.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    private void setupSensor() {
        joinSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motorSensor = 1;
                if (bqzum.getSensorStatus()) {
                    bqzum.disconnectSensor();
                } else {
                    Intent serverIntent = new Intent(Main.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    if (motorSensor == 0) {
                        Variables.INSTANCE.setMotorAddress(address);
                        try {
                            bqzum.connectMotor();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast
                                    .makeText(this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Variables.INSTANCE.setSensorAddress(address);
                        try {
                            bqzum.connectSensor();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast
                                    .makeText(this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupCar();
                    setupSensor();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "BT not enabled",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }



}
