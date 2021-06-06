package com.lidor.CommunicationUsingHandlers.game;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.lidor.CommunicationUsingHandlers.game.message.LFGameMessageCallbackReceiver;

import java.lang.ref.WeakReference;

public abstract class LFGameViewThreadProtocol extends
        Thread {

    protected Handler mHandler;
    protected WeakReference<LFGameMessageCallbackReceiver> mOnGameMessageCallbackReceiver;
    protected boolean flagCanAddGameView = true;

    public abstract void setOnGameMessageCallbackReceiver(
            @NonNull LFGameMessageCallbackReceiver receiver
    );

    public Handler getHandler() {
        synchronized (this) {
            return mHandler;
        }
    }
}
