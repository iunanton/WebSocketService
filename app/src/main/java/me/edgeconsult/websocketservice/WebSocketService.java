package me.edgeconsult.websocketservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {

    private static final String MAIN_ACTIVITY_TAG = MainActivity.class.getSimpleName();

    private final IBinder binder = new WebSocketBinder();
    public class WebSocketBinder extends Binder {
        WebSocketService getService() {
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "onStartCommand", Toast.LENGTH_SHORT).show();
        client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://owncloudhk.net").build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // super.onOpen(webSocket, response);
                final String jString = "{\"type\":\"login\",\"data\":{\"username\":\"WebSocketService\",\"password\":\"socket\"}}";
                Log.i(MAIN_ACTIVITY_TAG, jString);
                webSocket.send(jString);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.i(MAIN_ACTIVITY_TAG, text);
            }
        };
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
        return super.onStartCommand(intent, flags, startId);
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
