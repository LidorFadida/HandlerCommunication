package com.lidor.CommunicationUsingHandlers.modules;

import com.lidor.CommunicationUsingHandlers.game.message.LFGameMessageCallbackReceiver;
import com.lidor.CommunicationUsingHandlers.game.LFGameViewThread;

import java.util.Objects;

public abstract class GameModule {
    public LFGameViewThread mLFGameViewThread;
    public LFGameMessageCallbackReceiver mGameMessageCallbackReceiver;
    private static final Object mLock = new Object();
    private static GameModule mInstance;

    private GameModule() {
    }

    public static GameModule startModule() {
        if
        (mInstance == null) {
            synchronized (mLock) {
                if
                (mInstance == null) {
                    mInstance = new GameModule() {
                        @Override
                        void inject() {
                            mLFGameViewThread = LFGameViewThread.getInstance();
                            mGameMessageCallbackReceiver = LFGameMessageCallbackReceiver.getInstance();
                            mLFGameViewThread.setOnGameMessageCallbackReceiver(
                                    mGameMessageCallbackReceiver
                            );
                            mLFGameViewThread.start();
                        }
                    };
                }
            }
        }
        return mInstance;
    }

    abstract void inject();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameModule that = (GameModule) o;
        return Objects.equals(mLFGameViewThread, that.mLFGameViewThread) &&
                Objects.equals(mGameMessageCallbackReceiver, that.mGameMessageCallbackReceiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mLFGameViewThread, mGameMessageCallbackReceiver);
    }
}
