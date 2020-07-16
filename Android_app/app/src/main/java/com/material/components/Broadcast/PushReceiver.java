package com.material.components.Broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.material.components.R;
import com.material.components.activity.chat.ChatWhatsapp;
import com.material.components.activity.login.LoginSimpleGreen;

import me.pushy.sdk.Pushy;

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationTitle = "Fusion";
        String notificationText = "Test notification";
        String number = "number", sender = "sender", msgID = "0";





        if (intent.getStringExtra("number") != null) {
            number = intent.getStringExtra("number");
        }

        if (intent.getStringExtra("sender") != null) {
            sender = intent.getStringExtra("sender");
        }

        if (intent.getStringExtra("id") != null) {
            msgID = intent.getStringExtra("id");
        }
        if (intent.getStringExtra("message") != null) {
            //notificationText = msgID + "_" + number + "_" + sender + "_" + intent.getStringExtra("message");
            notificationText = intent.getStringExtra("message");
        }


        //Bitmap icon = BitmapFactory.decodeResource(context.getResources(), android.R.mipmap.);

        // Prepare a notification with vibration, sound and lights
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentTitle("Fusion")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.custom_ic_notification))
                .setContentText(notificationText)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ChatWhatsapp.class), PendingIntent.FLAG_UPDATE_CURRENT));

        // Automatically configure a Notification Channel for devices running Android O+
        Pushy.setNotificationChannel(builder, context);

        // Get an instance of the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Build the notification and display it
        notificationManager.notify(1, builder.build());
    }
}