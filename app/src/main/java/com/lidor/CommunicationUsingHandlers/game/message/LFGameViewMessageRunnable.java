package com.lidor.CommunicationUsingHandlers.game.message;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lidor.CommunicationUsingHandlers.game.LFGameViewThread;

import java.lang.ref.WeakReference;

import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.MSG_KEY;


public class LFGameViewMessageRunnable
        implements Runnable {
    private static final String TAG = LFGameViewMessageRunnable.class.getSimpleName();


    private static final Object mLock = new Object();
    private String mMessage;
    private WeakReference<LFGameViewThread> mHostThread;
    private Thread mThread; //better to hold ref instead of local. can be customized one
    private static LFGameViewMessageRunnable mInstance;

    private LFGameViewMessageRunnable() { }

    public static LFGameViewMessageRunnable getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new LFGameViewMessageRunnable();
                }
                return mInstance;
            }
        } else {
            return mInstance;
        }
    }

    public void setBackgroundThread(
            @NonNull LFGameViewThread backgroundThread
    ) {
        mHostThread = new WeakReference<>(
                backgroundThread
        );
    }

    public void sendMessage(
            @NonNull String message
    ) {
        mMessage = message;
        this.mThread = new Thread(
                this
        );
        mThread.start();
    }

    @Override
    public void run() {
        if
        (mMessage != null) {
            if
            (mHostThread != null) {
                final LFGameViewThread LFGameViewThread = mHostThread.get();
                if (LFGameViewThread != null) {
                    final Handler mMineHandler = LFGameViewThread.getHandler();
                    Message message = mMineHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString(MSG_KEY, mMessage);
                    message.setData(bundle);
                    mMineHandler.sendMessage(message);
                } else {
                    Log.e(TAG, "run: " + "Hosting Thread cannot be null");
                }
            } else {
                Log.e("TAG", "run: " + "Hosting Thread cannot be null");
            }
        }
    }
}
