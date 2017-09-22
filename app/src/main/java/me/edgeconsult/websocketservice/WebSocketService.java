package me.edgeconsult.websocketservice;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {

    private static final String MAIN_ACTIVITY_TAG = MainActivity.class.getSimpleName();

    private OkHttpClient client;
    private WebSocket ws;

    private final IBinder mBinder = new WebSocketBinder();
    public class WebSocketBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://owncloudhk.net").build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.i(MAIN_ACTIVITY_TAG, "onOpen");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.i(MAIN_ACTIVITY_TAG, "onMessage");
            }
        };
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    public void sendMessage(String text) {
        Log.i(MAIN_ACTIVITY_TAG, text);
        ws.send(text);
    }
}
