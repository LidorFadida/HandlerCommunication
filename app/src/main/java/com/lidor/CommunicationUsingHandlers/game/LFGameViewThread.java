package com.lidor.CommunicationUsingHandlers.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lidor.CommunicationUsingHandlers.game.message.LFGameMessageCallbackReceiver;

import java.lang.ref.WeakReference;

import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.ADD_GAME_VIEW_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.FINISH_REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.GAME_VIEW_WAS_ADDED_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.MSG_KEY;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.RESET_GAME_STATE;

public final class LFGameViewThread extends
        LFGameViewThreadProtocol {
    private static final Object mLock = new Object();
    private static LFGameViewThread mInstance;

    private LFGameViewThread() {
    }

    public static LFGameViewThread getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new LFGameViewThread();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    public void run() {
        Looper.prepare();
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                synchronized (this) {
                    Bundle bundle = msg.getData();
                    String messageTypeALike = bundle.getString(MSG_KEY);
                    if (messageTypeALike == null)
                        return;
                    if (flagCanAddGameView) {
                        if (mOnGameMessageCallbackReceiver != null) {
                            final LFGameMessageCallbackReceiver receiver = mOnGameMessageCallbackReceiver.get();
                            if (receiver != null) {
                                switch (messageTypeALike) {
                                    case ADD_GAME_VIEW_VALUE:
                                        receiver.onMessageStartAddingViewReceived();
                                        break;
                                    case GAME_VIEW_WAS_ADDED_VALUE:
                                        receiver.onMessageViewWasAddedReceived();
                                        break;
                                    case REFRESH_GAME_VIEW_GRID_VALUE:
                                        receiver.onMessageRefreshGameViewGridReceived();
                                        break;
                                    case FINISH_REFRESH_GAME_VIEW_GRID_VALUE:
                                        receiver.onMessageFinishRefreshGameViewGridReceived();
                                        break;
                                    case RESET_GAME_STATE:
                                        mHandler.removeCallbacks(null);
                                        flagCanAddGameView = false;
                                        break;
                                }
                            }
                        }
                    } else {
                        Log.e("TAG", "handleMessage: " + bundle.getString(MSG_KEY));
                    }
                }
            }
        };
        Looper.loop();
    }


    public void setOnGameMessageCallbackReceiver(
            @NonNull LFGameMessageCallbackReceiver receiver
    ) {
        this.mOnGameMessageCallbackReceiver = new WeakReference<>(receiver);
    }
}