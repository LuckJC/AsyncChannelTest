package com.huangchao.asyncchanneltest;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Messenger;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SyncConnectTest extends AppCompatActivity {
    static private String TAG = "SyncConnectTest";
    static private final int CMD1 = 0x00086000;
    static private final int CMD2 = 0x00086001;
    AsyncChannel myAsyncChannel1 = new AsyncChannel();
    AsyncChannel myAsyncChannel2 = new AsyncChannel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        HandlerThread handlerThread1 = new HandlerThread("Thread1");
        handlerThread1.start();
        Handler handler1 = new Handler(handlerThread1.getLooper()) {
            public void handleMessage(android.os.Message msg) {
                if (msg != null) {
                    Log.d(TAG, "handler1 " + AsyncChannel.cmdToString(msg.what) + "(" + msg.what + ")");
                    switch (msg.what) {
                        case AsyncChannel.CMD_CHANNEL_DISCONNECTED:
                            myAsyncChannel1.disconnected();
                            break;
                    }
                }
            }
        };

        HandlerThread handlerThread2 = new HandlerThread("Thread2");
        handlerThread2.start();
        Handler handler2 = new Handler(handlerThread2.getLooper()) {
            public void handleMessage(android.os.Message msg) {
                if (msg != null) {
                    Log.d(TAG, "handler2 " + AsyncChannel.cmdToString(msg.what) + "(" + msg.what + ")");
                    switch (msg.what) {
                        case AsyncChannel.CMD_CHANNEL_FULL_CONNECTION:
                            myAsyncChannel2.replyToMessage(msg,
                                    AsyncChannel.CMD_CHANNEL_FULLY_CONNECTED,
                                    AsyncChannel.STATUS_SUCCESSFUL);
                            myAsyncChannel2.connected(SyncConnectTest.this, this, new Messenger(handler1));
                            break;
                        case AsyncChannel.CMD_CHANNEL_DISCONNECTED:
                            myAsyncChannel2.disconnected();
                            break;
                    }
                }

            }
        };

        int status = myAsyncChannel1.fullyConnectSync(SyncConnectTest.this, handler1,
                handler2);
        Log.d(TAG, "status " + status);

        Thread a = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(100);
                    Log.d(TAG, "myAsyncChannel1: CMD1(" + CMD1 + ")");
                    myAsyncChannel1.sendMessage(CMD1);
                    Log.d(TAG, "myAsyncChannel2: CMD2(" + CMD2 + ")");
                    myAsyncChannel2.sendMessage(CMD2);
                    myAsyncChannel1.disconnect();
                    //myAsyncChannel2.disconnect();
                } catch (Exception e) {

                }
            }
        };
        a.start();
    }
}
