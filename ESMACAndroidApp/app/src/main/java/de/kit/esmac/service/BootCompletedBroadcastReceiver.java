package de.kit.esmac.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Robert on 16.04.2015.
 * <p/>
 * Start Service if Device reboots.
 */
public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SensorEvaluationService.class);
        context.startService(i);

    }
}
