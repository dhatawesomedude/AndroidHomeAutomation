package com.example.capstoneapp.app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.MalformedURLException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by Orlando on 24/7/2014.
 */
public class PlayNotificationActivity {

    private static final int MY_NOTIFICATION_ID = 129;

    ///Notification Text Elements
    private  CharSequence tickerText = "Movie now playing!";
    private final CharSequence contentTitle = "NFC-Automation";
    private final CharSequence contentText = "Movie Plays";



    //Notification Vibration on Arrival
    private long[] playVibratePattern = {0, 200, 200, 300};

    RemoteViews playContentView = new RemoteViews("com.example.capstoneapp.app", R.layout.play_notification);

    public PlayNotificationActivity (Activity context, String movieTitle, Integer thumbnailID) {
        //Notification Action Elements
        Intent playNotificationIntent;
        PendingIntent playContentIntent;

        playNotificationIntent = new Intent(context.getApplicationContext(), MovieList.class);
        playContentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, playNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Notification expanded View;
        playContentView.setTextViewText(R.id.play_notification_text, contentText);

        //Build Notification
        Notification.Builder playNotificationBuilder = new Notification.Builder(
                context.getApplicationContext())
                .setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_speakerphone)
                .setVibrate(playVibratePattern)
                .setContent(playContentView);

        //Begin Thread
        //new RecSockets().execute(movieTitle);

        //Invoke Notification Manager

            NotificationManager playNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            playNotificationManager.notify(MY_NOTIFICATION_ID, playNotificationBuilder.build());

    }




}
