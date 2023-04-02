package com.nilscreation.healthfacts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "My Channel";
    private static final int NOTIFICATION_ID = 100;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null)
            pushNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getNotification().getImageUrl()
            );
    }

    private void pushNotification(String title, String msg, Uri imageUrl) {

//        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.emoji, null);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//        Bitmap largeIcon = bitmapDrawable.getBitmap();

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setLargeIcon(Icon.createWithContentUri(imageUrl))
                    .setSmallIcon(R.drawable.app_logo)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText("Text Here")
                    .setSubText(msg)
                    .setChannelId(CHANNEL_ID)
                    .build();

            if (nm != null) {
                nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "new channel", NotificationManager.IMPORTANCE_HIGH));
            }

        } else {
            notification = new Notification.Builder(this)
                    .setLargeIcon(Icon.createWithContentUri(imageUrl))
                    .setSmallIcon(R.drawable.app_logo)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText("Text Here")
                    .setSubText(msg)
                    .build();
        }

        if (nm != null) {
            nm.notify(NOTIFICATION_ID, notification);

        }
    }

}

