package com.unisocsu.bluetoothcontroller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import androidx.core.app.NotificationCompat;

public class AudioRoutingService extends Service {

    private static final String CHANNEL_ID = "AudioRoutingServiceChannel";
    private BluetoothReceiver bluetoothReceiver;
    private TelephonyManager telephonyManager;
    private CustomPhoneStateListener phoneStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bluetooth Audio Router Active")
                .setContentText("Monitoring Bluetooth and Call states for routing split.")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .build();
        startForeground(1, notification);

        // Register Bluetooth broadcast receiver
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        registerReceiver(bluetoothReceiver, filter);

        // Register Phone State listener
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new CustomPhoneStateListener();
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
        }
        if (telephonyManager != null && phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        // NotificationChannels are only required for API 26+
    }

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                // Auto-apply split when Bluetooth connects
                AudioRouter.routeViaTinyMix(true);
            } else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                // Restore defaults when Bluetooth disconnects
                AudioRouter.routeViaTinyMix(false);
            }
        }
    }

    private class CustomPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING) {
                // During calls, ensure call routing is split cleanly
                AudioRouter.routeViaTinyMix(true);
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                // When idle, keep default state or keep splitting
            }
        }
    }
}
