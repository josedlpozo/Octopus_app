package com.josedlpozo.bluetootharduino;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by josedlpozo on 11/4/15.
 */
public class Bqzum {


    // Debugging
    private static final String TAG = "BQZUM";
    private static final boolean D = true;

    // Member object for the bt services
    private BluetoothService mBtServiceMotor;
    private BluetoothService mBtServiceSensor;

    private String startCmd = "CMD=1";
    private String endCmd = "CMD=2";

    private static final int timeBlock = 150;
    private int intensity = 100;

    // private String cmd1 = "CMD=0,005,050,01," + intensity;
    private String cmd2 = "CMD=0,002,050,01," + intensity;

    private String cmd3 = "CMD=0,004,050,04," + intensity;


    private String shortCmd = "CMD=0,001,015,01,95";
    private String longCmd = "CMD=0,001,060,01,100";
    private String silence = "CMD=0,001,025,01,0";

    public Bqzum() throws Exception {
        mBtServiceMotor = new BluetoothService(Variables.INSTANCE.getAppContext());
        mBtServiceSensor = new BluetoothService(Variables.INSTANCE.getAppContext());
    }

    public void connect() throws Exception {
        sameMacAddr();
        // Get the devices MAC addresses
        String motorAddress = Variables.INSTANCE.getMotorAddress();
        String sensorAddress = Variables.INSTANCE.getSensorAddress();
        // Attempt to connect to the device
        mBtServiceMotor.connect(motorAddress);
        mBtServiceSensor.connect(sensorAddress);
        // Wait till connected
        start(1);
        start(0);
    }

    private void sameMacAddr() throws Exception {
        String motorAddress = Variables.INSTANCE.getMotorAddress();
        String sensorAddress = Variables.INSTANCE.getSensorAddress();
        if (motorAddress.equals(sensorAddress)
                && !(motorAddress.equals("00:00:00:00:00:00") && sensorAddress
                .equals("00:00:00:00:00:00")))
            throw new Exception(
                    "Left and right devices has the same mac address.");
    }

    public void connectSensor() throws Exception {
        sameMacAddr();
        // Get the devices MAC addresses
        String rightAddress = Variables.INSTANCE.getSensorAddress();
        // Attempt to connect to the device
        mBtServiceSensor.connect(rightAddress);
        start(1);
    }

    public void connectMotor() throws Exception {
        sameMacAddr();
        // Get the devices MAC addresses
        String motorAddress = Variables.INSTANCE.getMotorAddress();
        // Attempt to connect to the device
        mBtServiceMotor.connect(motorAddress);
        start(0);
    }

    public void start(final int lr) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    switch (lr) {
                        case 0:
                            while (!getMotorStatus())
                                ;
                            break;
                        case 1:
                            while (!getSensorStatus())
                                ;
                            break;
                    }
                    SystemClock.sleep(timeBlock);
                    sendData(startCmd, lr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public void disconnect() {
        if (getStatus()) {
            sendData(endCmd, 0);
            sendData(endCmd, 1);
        }
        SystemClock.sleep(timeBlock);
        stop();
    }

    public void stop() {
        if (mBtServiceSensor != null)
            mBtServiceSensor.stop();
        if (mBtServiceMotor != null)
            mBtServiceMotor.stop();
    }

    public void disconnectMotor() {
        sendData(endCmd, 0);
        SystemClock.sleep(timeBlock);
        if (mBtServiceMotor != null)
            mBtServiceMotor.stop();
    }

    public void disconnectSensor() {
        sendData(endCmd, 1);
        SystemClock.sleep(timeBlock);
        if (mBtServiceSensor != null)
            mBtServiceSensor.stop();
    }

    public boolean btEnabled() {
        return mBtServiceMotor.isBluetoothAdapterEnabled()
                && mBtServiceSensor.isBluetoothAdapterEnabled();
    }


    public void arrived() {
        //sendData(cmd2, 0);
        //sendData(cmd2, 1);
        sendData("CMD=0,001,020,01,100", 1);
        sendData("CMD=0,001,020,01,100", 0);
        sendData("CMD=0,001,020,01,100", 1);
        sendData("CMD=0,001,020,01,100", 0);
    }


    public void errorCase() {
        sendData(cmd3, 0);
        sendData(cmd3, 1);
    }


    public boolean getStatus() {
        return (mBtServiceSensor.getState() == BluetoothService.STATE_CONNECTED && mBtServiceMotor
                .getState() == BluetoothService.STATE_CONNECTED);
    }

    public boolean getSensorStatus() {
        return (mBtServiceSensor.getState() == BluetoothService.STATE_CONNECTED);
    }

    public boolean getMotorStatus() {
        return (mBtServiceMotor.getState() == BluetoothService.STATE_CONNECTED);
    }


    public void recalculating() throws Exception {
        sendData(cmd3, 0);
        sendData(cmd3, 1);
    }


    public void reset() throws Exception {
        // sendData(endCmd, 0);
        // disconnect();
        // connect();
        // sendData(startCmd, 0);
    }


    public void startInstruction() {
        //sendData(cmd2, 0);
        //sendData(cmd2, 1);
        sendData("CMD=0,001,020,01,100", 1);
        sendData("CMD=0,001,020,01,100", 0);
        sendData("CMD=0,001,020,01,100", 1);
        sendData("CMD=0,001,020,01,100", 0);


    }


    public void trafficCercle(int numberExits, int exits) {
        sendData(cmd3, 0);
    }


    public void turnLeft() {
        //sendData(cmd2, 0);
        sendData(shortCmd, 0);
        sendData(silence, 0);
        sendData(longCmd, 0);
    }


    public void turnRight() {
        //sendData(cmd2, 1);
        sendData(shortCmd, 1);
        sendData(silence, 1);
        sendData(longCmd, 1);
        //SystemClock.sleep(1000);
    }


    public void turnU() {
        sendData(cmd2, 0);
    }

    /*
     * Sends an instruction.
     *
     * @param message A string of text to send.
     *
     * @param leftRight Left(0) or right(1) sensor.
     */
    public void sendData(String message, int leftRight) {
        if (D) Log.d(TAG, "Send message: " + message + " to: " + leftRight);
        SendThread t = new SendThread(message, leftRight);
        t.start();
        //SystemClock.sleep(timeBlock); //TODO
    }

    class SendThread extends Thread {
        String message;
        int leftRight;

        public SendThread(String message, int leftRight) {
            this.message = message;
            this.leftRight = leftRight;
        }

        @Override
        public void run() {
            if (leftRight == 0) {
                // Check that there's actually something to send
                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothService to
                    // write
                    byte[] send = message.getBytes();
                    mBtServiceMotor.write(send);
                }
            } else {
                // Check that there's actually something to send
                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothService to
                    // write
                    byte[] send = message.getBytes();
                    mBtServiceSensor.write(send);
                }
            }
        }
    }
}

