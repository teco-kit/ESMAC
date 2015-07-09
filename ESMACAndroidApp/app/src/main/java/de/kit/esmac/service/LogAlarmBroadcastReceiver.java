package de.kit.esmac.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Robert on 02.04.2015.
 */
public class LogAlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("de.kit.esmdummy.log");
        context.sendBroadcast(i);
    }
}
