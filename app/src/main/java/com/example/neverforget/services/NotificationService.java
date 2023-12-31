package com.example.neverforget.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.neverforget.AddProductToListActivity;
import com.example.neverforget.MainActivity;
import com.example.neverforget.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public class NotificationService extends Service {

    private final String CHANNEL_ID = "raspberrypi";
    public static PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String message = "You have now " + MainActivity.numbernotificationCounter + " items to add";
        MainActivity.flag_numbernotificationCounter = false;

        Intent notificationIntent = new Intent(this, AddProductToListActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this,0,notificationIntent, PendingIntent.FLAG_ONE_SHOT); //FLAG_UPDATE_CURRENT

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.never_forget);
        builder.setContentTitle("New product Added");

        builder.setContentText(message);

        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        builder.build();
        startForeground(1, builder.build());
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);


        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        return null;
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        super.dump(fd, writer, args);
    }


}
