package com.example.zacharycolburn.programmingnews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Zachary Colburn on 8/17/2017.
 */

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ReadReminderReceiver.setupAlarm(context);
    }
}