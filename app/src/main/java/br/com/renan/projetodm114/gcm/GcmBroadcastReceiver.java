package br.com.renan.projetodm114.gcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import br.com.renan.projetodm114.MainActivity;
import br.com.renan.projetodm114.R;

import br.com.renan.projetodm114.models.OrderInfo;
import br.com.renan.projetodm114.models.ProductInfo;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    private Context context;
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {


        GoogleCloudMessaging gcm =
                GoogleCloudMessaging.getInstance(context);
        this.context = context;
        String messageType = gcm.getMessageType(intent);


        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            Bundle extras = intent.getExtras();
            Gson gson = new Gson();


            if (extras.containsKey("orderInfo")) {
                String strOrderInfo = extras.getString("orderInfo");
                if (strOrderInfo != null) {

                    OrderInfo orderInfo = gson.fromJson(strOrderInfo, OrderInfo.class);
                    sendNotification(orderInfo);
                }
            }

            if (extras.containsKey("productOfInterest")) {
                String strProductOfInterest = extras.getString("productOfInterest");
                if (strProductOfInterest != null) {

                    ProductInfo productInfo = gson.fromJson(strProductOfInterest, ProductInfo.class);
                    sendNotification(productInfo);
                }
            }

        }

        setResultCode(Activity.RESULT_OK);
    }

    private void sendNotification(ProductInfo info) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        Notification.Builder mBuilder = null;
        PendingIntent contentIntent = null;

        intent.putExtra("productOfInterest", info);
        contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new Notification.Builder(
                context)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setAutoCancel(true)
                .setContentTitle("Virtual Store")
                .setStyle(new Notification.BigTextStyle().bigText("Produto:"
                        + info.getCode() + " - "
                        + info.getPrice()))
                .setContentText("Produto:"
                        + info.getCode() + " - "
                        + info.getPrice());


        mBuilder.setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("1");
        }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotification(OrderInfo info) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        Notification.Builder mBuilder = null;
        PendingIntent contentIntent = null;

        intent.putExtra("orderInfo", info);
        contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new Notification.Builder(
                context)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setAutoCancel(true)
                .setContentTitle("Virtual Store")
                .setStyle(new Notification.BigTextStyle().bigText("Pedido:"
                        + info.getId() + " - "
                        + info.getStatus()))
                .setContentText("Pedido:"
                        + info.getId() + " - "
                        + info.getStatus());


        mBuilder.setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("1");
        }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
