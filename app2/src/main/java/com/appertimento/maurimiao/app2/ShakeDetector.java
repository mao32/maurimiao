package com.appertimento.maurimiao.app2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by mcolombo on 04/05/14.
 */
public class ShakeDetector implements SensorEventListener {
    private final static String TAG="ShakeDetector";
    // Shake thresholds - derived by trial
    private static final double weakShakeThreshold = 5.0;
    private static final double moderateShakeThreshold = 13.0;
    private static final double strongShakeThreshold = 20.0;

    // Cache for shake detection
    private static final int SENSOR_CACHE_SIZE = 10;
    private final Queue<Float> X_CACHE = new LinkedList<Float>();
    private final Queue<Float> Y_CACHE = new LinkedList<Float>();
    private final Queue<Float> Z_CACHE = new LinkedList<Float>();

    //Specifies the minimum time interval between calls to Shaking()
    private int minimumInterval=400;

    //Specifies the time when Shaking() was last called
    private long timeLastShook;

    private OnShakeListener mOnShakeListener;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    public ShakeDetector(OnShakeListener onShakeListener){
        mOnShakeListener=onShakeListener;
        sensorManager= (SensorManager)  MaurimiaoApp.getContext().getSystemService(MaurimiaoApp.getContext().SENSOR_SERVICE);
        accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // Assumes that sensorManager has been initialized, which happens in constructor
    public void startListening() {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    // Assumes that sensorManager has been initialized, which happens in constructor
    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //verifico che sia l'accelerometro
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float xAccel=sensorEvent.values[0];
            float yAccel=sensorEvent.values[1];
            float zAccel=sensorEvent.values[2];

            addToSensorCache(X_CACHE, xAccel);
            addToSensorCache(Y_CACHE, yAccel);
            addToSensorCache(Z_CACHE, zAccel);

            long currentTime = System.currentTimeMillis();

            //Checks whether the phone is shaking and the minimum interval
            //has elapsed since the last registered a shaking event.
            if ((isShaking(X_CACHE, xAccel) || isShaking(Y_CACHE, yAccel) || isShaking(Z_CACHE, zAccel))
                    && (timeLastShook == 0 || currentTime >= timeLastShook + minimumInterval)) {
                Log.i(TAG, "onSensorChanged - shake detected");
                timeLastShook = currentTime;
                Shaking();
            }
        }
    }

    private void Shaking(){
        if(mOnShakeListener!=null) {
            mOnShakeListener.onShake();
        }
    }

    /*
   * Updating sensor cache, replacing oldest values.
   */
    private void addToSensorCache(Queue<Float> cache, float value) {
        if (cache.size() >= SENSOR_CACHE_SIZE) {
            cache.remove();
        }
        cache.add(value);
    }

    /*
     * Indicates whether there was a sudden, unusual movement.
     */
    // TODO(user): Maybe this can be improved.
    // See http://www.utdallas.edu/~rxb023100/pubs/Accelerometer_WBSN.pdf.
    private boolean isShaking(Queue<Float> cache, float currentValue) {
        float average = 0;
        for (float value : cache) {
            average += value;
        }

        average /= cache.size();

        /***
        if (Sensitivity() == 1) { //sensitivity is weak
            return Math.abs(average - currentValue) > strongShakeThreshold;
        } else if (Sensitivity() == 2) { //sensitivity is moderate
            return ((Math.abs(average - currentValue) > moderateShakeThreshold)
                    && (Math.abs(average - currentValue) < strongShakeThreshold));
        } else { //sensitivity is strong
            return ((Math.abs(average - currentValue) > weakShakeThreshold)
                    && (Math.abs(average - currentValue) < moderateShakeThreshold));
        }
         *******/

        //considerata moderata
        return ((Math.abs(average - currentValue) > moderateShakeThreshold)  && (Math.abs(average - currentValue) < strongShakeThreshold));
    }

    public interface OnShakeListener {
        public abstract void onShake();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
