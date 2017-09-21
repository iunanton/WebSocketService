package me.edgeconsult.websocketservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {

    private boolean mBound = false;
    private WebSocketService mWebSocketService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mWebSocketService = ((WebSocketService.WebSocketBinder)iBinder).getService();
            mBound = true;
            Toast.makeText(getApplicationContext(), "onServiceConnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            Toast.makeText(getApplicationContext(), "onServiceDisconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, WebSocketService.class));
        setContentView(R.layout.activity_main);
        Button btn_bind = (Button) findViewById(R.id.btn_bind);
        Button btn_unbind = (Button) findViewById(R.id.btn_unbind);
        btn_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mBound) {
                    Intent intent = new Intent(MainActivity.this, WebSocketService.class);
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                }
            }
        });
        btn_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    unbindService(mConnection);
                    mBound = false;
                }
            }
        });
    }
}
