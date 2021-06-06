package com.lidor.CommunicationUsingHandlers.game.model;

public class LFGameBrain
        implements
    LFGameBrainProtocol{
    private int mTotalScore;

    @Override
    public int getScore() {
        return mTotalScore;
    }

    @Override
    public void damageScore() {
        mTotalScore -= 3;
    }

    @Override
    public void startPenalty() {
        damageScore();
    }
}
