package com.lidor.CommunicationUsingHandlers.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.ADD_GAME_VIEW_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.FINISH_REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.GAME_VIEW_WAS_ADDED_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.MSG_KEY;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.RESET_GAME_STATE;

public class LFGameViewThread extends Thread {
    //can be singleton too
    public Handler mHandler;

    private WeakReference<LFGameViewHandlerContract.OnMessageAddingViewReceivedDelegate> mOnAddViewMessageDelegate;
    private WeakReference<LFGameViewHandlerContract.OnMessageViewWasAddedDelegate> mOnViewAddedDelegate;
    private WeakReference<LFGameViewHandlerContract.OnMessageRefreshGameViewGridDelegate> mOnRefreshGridDelegate;
    private WeakReference<LFGameViewHandlerContract.OnMessageFinishRefreshGameViewGridDelegate> mOnFinishRefreshGridDelegate;
    private boolean canAddGameView = true;

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
                    if (canAddGameView) {
                        switch (messageTypeALike) {
                            case ADD_GAME_VIEW_VALUE:
                                if (mOnAddViewMessageDelegate != null) {
                                    final LFGameViewHandlerContract.OnMessageAddingViewReceivedDelegate delegateAddMine = mOnAddViewMessageDelegate.get();
                                    if
                                    (delegateAddMine != null) {
                                        delegateAddMine.onMessageStartAddingViewReceived();
                                    }
                                }
                                break;
                            case GAME_VIEW_WAS_ADDED_VALUE:
                                if (mOnViewAddedDelegate != null) {
                                    final LFGameViewHandlerContract.OnMessageViewWasAddedDelegate delegateOnMineWasAdded = mOnViewAddedDelegate.get();
                                    if
                                    (delegateOnMineWasAdded != null) {
                                        delegateOnMineWasAdded.onMessageViewWasAddedReceived();
                                    }
                                }
                                break;
                            case REFRESH_GAME_VIEW_GRID_VALUE:
                                if (mOnRefreshGridDelegate != null) {
                                    final LFGameViewHandlerContract.OnMessageRefreshGameViewGridDelegate delegateRefreshGrid = mOnRefreshGridDelegate.get();
                                    if
                                    (delegateRefreshGrid != null) {
                                        delegateRefreshGrid.onMessageRefreshGameViewGridReceived();
                                    }
                                }
                                break;
                            case FINISH_REFRESH_GAME_VIEW_GRID_VALUE:
                                if (mOnFinishRefreshGridDelegate != null) {
                                    final LFGameViewHandlerContract.OnMessageFinishRefreshGameViewGridDelegate delegateFinishRefreshGrid = mOnFinishRefreshGridDelegate.get();
                                    if
                                    (delegateFinishRefreshGrid != null) {
                                        delegateFinishRefreshGrid.onMessageFinishRefreshGameViewGridReceived();
                                    }
                                }
                                break;
                            case RESET_GAME_STATE:
                                mHandler.removeCallbacks(null);
                                canAddGameView = false;
                                break;
                        }
                    } else {
                        Log.e("TAG", "handleMessage: " + bundle.getString(MSG_KEY));
                    }
                }
            }
        };

        Looper.loop();
    }

    public synchronized Handler getMHandler() {
        return mHandler;
    }

    public void setOnAddMineMessageDelegate(LFGameViewHandlerContract.OnMessageAddingViewReceivedDelegate delegate) {
        this.mOnAddViewMessageDelegate = new WeakReference<>(delegate);
    }

    public void setOnMineAddedDelegate(LFGameViewHandlerContract.OnMessageViewWasAddedDelegate delegate) {
        this.mOnViewAddedDelegate = new WeakReference<>(delegate);
    }

    public void setOnRefreshGridDelegate(LFGameViewHandlerContract.OnMessageRefreshGameViewGridDelegate delegate) {
        this.mOnRefreshGridDelegate = new WeakReference<>(delegate);
    }

    public void setOnFinishRefreshGridDelegate(LFGameViewHandlerContract.OnMessageFinishRefreshGameViewGridDelegate delegate) {
        this.mOnFinishRefreshGridDelegate = new WeakReference<>(delegate);
    }
}