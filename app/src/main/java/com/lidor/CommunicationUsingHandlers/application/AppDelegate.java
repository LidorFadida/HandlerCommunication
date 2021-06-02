package com.lidor.CommunicationUsingHandlers.application;

import android.app.Application;

import com.lidor.CommunicationUsingHandlers.game.LFGameViewThread;

public class AppDelegate extends Application {
    public LFGameViewThread mLFGameViewThread;
    @Override
    public void onCreate() {
        super.onCreate();
        mLFGameViewThread = new LFGameViewThread();
        mLFGameViewThread.start();
    }
}
