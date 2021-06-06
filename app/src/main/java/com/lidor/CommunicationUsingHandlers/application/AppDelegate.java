package com.lidor.CommunicationUsingHandlers.application;

import android.app.Application;

import com.lidor.CommunicationUsingHandlers.game.message.LFGameMessageCallbackReceiver;
import com.lidor.CommunicationUsingHandlers.game.LFGameViewThread;
import com.lidor.CommunicationUsingHandlers.modules.GameModule;

public class AppDelegate extends Application {
    public LFGameViewThread mLFGameViewThread;
    public LFGameMessageCallbackReceiver mGameMessageCallbackReceiver;
    public GameModule mGameModule;
    @Override
    public void onCreate() {
        super.onCreate();
        this.mGameModule = GameModule.startModule();
    }
}
