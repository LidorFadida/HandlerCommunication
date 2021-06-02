package com.lidor.CommunicationUsingHandlers.views;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lidor.CommunicationUsingHandlers.application.AppDelegate;
import com.lidor.CommunicationUsingHandlers.R;
import com.lidor.CommunicationUsingHandlers.game.LFGameViewMessageRunnable;
import com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract;
import com.lidor.CommunicationUsingHandlers.game.LFGameViewThread;

import java.lang.ref.WeakReference;

import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.ADD_GAME_VIEW_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.FINISH_REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.GAME_VIEW_WAS_ADDED_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.MSG_KEY;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.OnMessageAddingViewReceivedDelegate;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.OnMessageRefreshGameViewGridDelegate;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.LFGameViewHandlerContract.RESET_GAME_STATE;

public class MainActivity
        extends
        AppCompatActivity
        implements
        OnMessageAddingViewReceivedDelegate,
        OnMessageRefreshGameViewGridDelegate,
        LFGameViewHandlerContract.OnMessageViewWasAddedDelegate,
        LFGameViewHandlerContract.OnMessageFinishRefreshGameViewGridDelegate {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Handler mUIThreadHandler;
    private WeakReference<LFGameViewThread> mMineThread;

    private Runnable mRunnableMineGameResetState = () -> {
        if (mMineThread != null) {
            final LFGameViewThread LFGameViewThread = mMineThread.get();
            if (LFGameViewThread != null) {
                final Handler mMineHandler = LFGameViewThread.mHandler;
                Message message = mMineHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(MSG_KEY, RESET_GAME_STATE);
                message.setData(bundle);
                mMineHandler.sendMessage(message);
            }
        }
    };

    private Runnable simulate = () -> {
        try {
            Thread.sleep(15_000);
            new Thread(mRunnableMineGameResetState).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };


    private LinearLayout mLinearLayoutMineContainer;
    private LFGameViewMessageRunnable mSharedMessageRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {

        //Currently on the Main Thread as the Handle doc says mLopper = Current Thread Looper
        final AppDelegate application = (AppDelegate) getApplication();
        LFGameViewThread LFGameViewThread = application.mLFGameViewThread;
        LFGameViewThread.setOnAddMineMessageDelegate(this);
        LFGameViewThread.setOnMineAddedDelegate(this);
        LFGameViewThread.setOnRefreshGridDelegate(this);
        LFGameViewThread.setOnFinishRefreshGridDelegate(this);
        this.mMineThread = new WeakReference<>(
                LFGameViewThread
        );
        mUIThreadHandler = new Handler(Looper.getMainLooper());
        mLinearLayoutMineContainer = findViewById(R.id.main_layout);
        mSharedMessageRunnable = LFGameViewMessageRunnable.getInstance();

        simulateGameLoop();
    }

    private void simulateGameLoop(){
        if (mMineThread != null) {
            final LFGameViewThread weakLFGameViewThread = mMineThread.get();
            if (weakLFGameViewThread != null) {
                mSharedMessageRunnable.setBackgroundThread(
                        weakLFGameViewThread
                );
                mSharedMessageRunnable.sendMessage(ADD_GAME_VIEW_VALUE);
            }
        }

        new Thread(simulate).start();
    }


    private void refreshGridView() {
        synchronized (this) { //Concurrency safety
            if
            (mLinearLayoutMineContainer != null) {

                final TextView mineToBeAdded = createGameView();
                mLinearLayoutMineContainer.addView(
                        mineToBeAdded
                );
            }

            // always hold reference to clear if needed
        }
    }

    private TextView createGameView() {
        TextView returningTextView = new TextView(
                this
        );
        returningTextView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        returningTextView.setTextColor(Color.BLACK);
        returningTextView.setText("* View *");
        return returningTextView;
    }





    @Override
    public void onMessageStartAddingViewReceived() {
        //Model heavy calculation
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //handle Looper Message Exception
        }
        mSharedMessageRunnable.sendMessage(
                GAME_VIEW_WAS_ADDED_VALUE
        );
    }

    @Override
    public void onMessageRefreshGameViewGridReceived() {
        if (mUIThreadHandler != null) {
            mUIThreadHandler.post(this::refreshGridView);
            if (mSharedMessageRunnable != null) {
                mSharedMessageRunnable.sendMessage(
                        FINISH_REFRESH_GAME_VIEW_GRID_VALUE
                );
            }
        }

    }

    @Override
    public void onMessageViewWasAddedReceived() {
        if (mSharedMessageRunnable != null) {
            mSharedMessageRunnable.sendMessage(REFRESH_GAME_VIEW_GRID_VALUE);
        }
    }

    @Override
    public void onMessageFinishRefreshGameViewGridReceived() {
        if (mSharedMessageRunnable != null) {
            mSharedMessageRunnable.sendMessage(ADD_GAME_VIEW_VALUE);
        }
    }

}