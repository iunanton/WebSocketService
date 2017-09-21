package me.edgeconsult.websocketservice;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {

    private int count = 0;

    private NotificationManager mNotificationManager;
    private static int notificationID = 1;

    private Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    private static final String MAIN_ACTIVITY_TAG = MainActivity.class.getSimpleName();

    private final IBinder binder = new WebSocketBinder();
    public class WebSocketBinder extends Binder {
        WebSocketService getService() {
            ++count;
            return WebSocketService.this;
        }
    }

    private OkHttpClient client;
    private WebSocket ws;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://owncloudhk.net").build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                final String jString = "{\"type\":\"login\",\"data\":{\"username\":\"WebSocketService\",\"password\":\"socket\"}}";
                Log.i(MAIN_ACTIVITY_TAG, jString);
                webSocket.send(jString);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setSmallIcon(R.drawable.ic_stat_name);
                mBuilder.setColor(0xFF00CCCC);
                mBuilder.setLights(0xFF00CCCC, 500, 1500);
                mBuilder.setSound(uri);
                mBuilder.setContentTitle("New user joined");
                mBuilder.setContentText("Say \"Hi\" to him!");
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(notificationID, mBuilder.build());
            }
        };
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ws.close(1000, "Service destroyed");
        Toast.makeText(getApplicationContext(), "onDestroyCommand", Toast.LENGTH_SHORT).show();
    }

    public void send(String text) {
        ws.send(text);
    }
}
