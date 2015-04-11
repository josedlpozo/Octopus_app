package com.josedlpozo.bluetootharduino;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by josedlpozo on 11/4/15.
 */
public enum Variables {


    INSTANCE;
    private Context appContext = null;
    private Application application = null;
    private Bqzum bqzum;
    private String motorAddress = "00:00:00:00:00:00";
    private String sensorAddress = "00:00:00:00:00:00";

    public Button joinCar;
    public Button joinSensor;
    public Button ON_Sensor;
    public Button OFF_Sensor;
    public Button BLINK_Sensor;
    public Button ON_Motor;
    public Button OFF_Motor;
    public Button BLINK_Motor;


    private Variables(){

    }

    public void init(Application application) throws Exception {
        this.application = application;
        this.appContext = this.application.getApplicationContext();
        this.bqzum = new Bqzum();

    }

    public Bqzum getBqzum(){
        return bqzum;
    }

    public String getMotorAddress(){
        return motorAddress;
    }

    public String getSensorAddress(){
        return sensorAddress;
    }

    public void setMotorAddress(String motorAddress){
        this.motorAddress = motorAddress;
    }

    public void setSensorAddress(String sensorAddress){
        this.sensorAddress = sensorAddress;
    }

    public Context getAppContext() {
        return appContext;
    }

    public Application getApplication() {
        return application;
    }

    public Button getJoinCar() {
        return joinCar;
    }

    public Button getJoinSensor() {
        return joinSensor;
    }

    public Button getON_Sensor() {
        return ON_Sensor;
    }

    public Button getOFF_Sensor() {
        return OFF_Sensor;
    }

    public Button getBLINK_Sensor() {
        return BLINK_Sensor;
    }

    public Button getON_Motor() {
        return ON_Motor;
    }

    public Button getOFF_Motor() {
        return OFF_Motor;
    }

    public Button getBLINK_Motor() {
        return BLINK_Motor;
    }

    public void enableBotones(boolean enable, int connect) {
        Log.i("VARIABLES", "ENABLE BOTONES" + enable + " bt :" + connect);
        switch (connect){
            case 0:
                if(enable){
                    Variables.INSTANCE.ON_Motor.setEnabled(true);
                    Variables.INSTANCE.OFF_Motor.setEnabled(true);
                    Variables.INSTANCE.BLINK_Motor.setEnabled(true);
                }else{
                    Variables.INSTANCE.ON_Motor.setEnabled(false);
                    Variables.INSTANCE.OFF_Motor.setEnabled(false);
                    Variables.INSTANCE.BLINK_Motor.setEnabled(false);
                }
                break;
            case 1:
                if(enable){
                    Variables.INSTANCE.ON_Sensor.setEnabled(true);
                    Variables.INSTANCE.OFF_Sensor.setEnabled(true);
                    Variables.INSTANCE.BLINK_Sensor.setEnabled(true);
                }else{
                    Variables.INSTANCE.ON_Sensor.setEnabled(false);
                    Variables.INSTANCE.OFF_Sensor.setEnabled(false);
                    Variables.INSTANCE.BLINK_Sensor.setEnabled(false);
                }
                break;
            default: Toast.makeText(getAppContext(), "Dispositivo no correcto.", Toast.LENGTH_SHORT);
        }
    }
}
