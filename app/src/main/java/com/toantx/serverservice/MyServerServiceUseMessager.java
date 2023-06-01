package com.toantx.serverservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MyServerServiceUseMessager extends Service {
    private static final String TAG = "TXTX-MyServerServiceUseMessager";
    private static final int SAY_HELLO = 0;
    private static final int REPLY_HELLO = 1;

    private final Messenger mMessenger;
    private Looper mLooper;
    private Handler mHandler;

    public MyServerServiceUseMessager() {
        Log.i(TAG, "MyServerServiceUseMessager: constructor()");
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mLooper = thread.getLooper();
        mHandler = new MyHandler(mLooper);
        mMessenger = new Messenger(mHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: intent: " + intent + " - binder: " + mMessenger.getBinder());
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mLooper.quitSafely();
        mLooper = null;
        mHandler = null;
        super.onDestroy();
        Log.i(TAG, "onDestroy: done!");
    }

    private static class MyHandler extends Handler {
        private MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SAY_HELLO: {
                    Bundle bundle = (Bundle) msg.obj;
                    String context = bundle.getString("NAME");
                    Log.i(TAG, "Server service received: " + context);

                    Bundle outBundle = new Bundle();
                    MyPoint myPoint = new MyPoint(3, 4);
                    outBundle.putParcelable("MYPOINT", myPoint);

                    Log.i(TAG, "Server service sending: " + myPoint);
                    Message outMsg = Message.obtain();
                    outMsg.what = REPLY_HELLO;
                    outMsg.obj = outBundle;
                    try {
                        msg.replyTo.send(outMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}
