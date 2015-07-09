package de.kit.esmac.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;

/**
 * Created by Robert on 25.03.2015.
 */
public class CounterAlarmBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = CounterAlarmBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        resetCounter(context);
    }

    private void resetCounter(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("de.kit.esmdummy", Context.MODE_PRIVATE);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notificationCounter", 0);
        editor.commit();
        wl.release();
    }


}