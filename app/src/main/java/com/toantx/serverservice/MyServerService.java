package com.toantx.serverservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServerService extends Service {
    private static final String TAG = "TXTX-MyServerService";

    private String previousString;
    private final ExecutorService mExecutorService;

    public MyServerService() {
        Log.i(TAG, "MyServerService: constructor()");
        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: " + intent);
        Log.i(TAG, "return mBinder: " + mBinder);
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();
        Log.i(TAG, "onCreate() done!");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mExecutorService.shutdownNow();
        super.onDestroy();
        Log.i(TAG, "onDestroy() done!");
    }

    private final IServerServiceManager.Stub mBinder = new IServerServiceManager.Stub() {
        @Override
        public String generateId(String s, int i) {
            Log.i(TAG, "generateId: previousString: " + previousString);
            previousString = s + i;
            return s + i;
        }

        @Override
        public MyPoint fastDoublePoint(MyPoint myPoint) {
            return new MyPoint(myPoint.x * 2, myPoint.y * 2);
        }

        @Override
        public void lowTriplePoint(MyPoint myPoint, MyResultListener myResultListener) {
            doTriplePoint(myPoint, myResultListener);
        }
    };

    private void doTriplePoint(MyPoint myPoint, MyResultListener myResultListener) {
        mExecutorService.execute(() -> {
            Log.i(TAG, "doTriplePoint: working!....");
            try {
                Thread.sleep(15_000); // Assume that this work takes 15 seconds to complete.
                Log.i(TAG, "doTriplePoint: done!");
                try {
                    MyPoint resultPoint = new MyPoint(myPoint.x * 3, myPoint.y * 3);
                    myResultListener.onResult(resultPoint);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
