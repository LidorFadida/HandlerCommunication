package com.lidor.CommunicationUsingHandlers.views;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lidor.CommunicationUsingHandlers.R;
import com.lidor.CommunicationUsingHandlers.application.AppDelegate;
import com.lidor.CommunicationUsingHandlers.game.message.LFGameMessageCallbackReceiver;
import com.lidor.CommunicationUsingHandlers.game.message.LFGameViewMessageRunnable;
import com.lidor.CommunicationUsingHandlers.game.LFGameViewThread;
import com.lidor.CommunicationUsingHandlers.viewmodels.BaseGameViewModel;

import java.lang.ref.WeakReference;

import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.ADD_GAME_VIEW_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.FINISH_REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.GAME_VIEW_WAS_ADDED_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.REFRESH_GAME_VIEW_GRID_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.RESET_GAME_STATE;

public class MainActivity
        extends
        AppCompatActivity
        implements
        LFGameMessageCallbackReceiver.OnGameMessageCallbackReceiverDelegate {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Handler mUIThreadHandler;
    private WeakReference<LFGameViewThread> mGameViewThread;

    private LFGameViewMessageRunnable mSharedMessageRunnable;
    private BaseGameViewModel mBaseGameViewModel;


    private Runnable simulate = () -> {
        try {
            Thread.sleep(16_000);
            final LFGameViewThread gameViewThread = mGameViewThread.get();
            if
            (gameViewThread != null) {
                mSharedMessageRunnable.sendMessage(
                        RESET_GAME_STATE
                );
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };


    private LinearLayout mLinearLayoutMineContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        registerViewModelObservers();
    }

    private void registerViewModelObservers() {
        if
        (mBaseGameViewModel != null){
//            mBaseGameViewModel
        }
    }

    private void initialize() {
        mBaseGameViewModel = new ViewModelProvider(this).get(BaseGameViewModel.class);
        //Currently on the Main Thread as the Handle doc says mLopper = Current Thread Looper
        final AppDelegate application = (AppDelegate) getApplication();
        LFGameViewThread gameViewThread = application.mLFGameViewThread;
        mGameViewThread = new WeakReference<>(gameViewThread);
        application.mGameMessageCallbackReceiver.registerEventDelegate(
                this
        );

        mUIThreadHandler = new Handler(Looper.getMainLooper());
        mLinearLayoutMineContainer = findViewById(R.id.main_layout);

        mSharedMessageRunnable = LFGameViewMessageRunnable.getInstance();

        simulateGameLoop();
    }

    private void simulateGameLoop() {
        if (mGameViewThread != null) {
            final LFGameViewThread weakLFGameViewThread = mGameViewThread.get();
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
    public void onEventFired(LFGameMessageCallbackReceiver.MessageEventReporter messageEvent) {
        Log.e(TAG, "onEventFired: " + messageEvent);
        switch (messageEvent) {
            case START_ADDING_VIEWS:
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
                break;
            case VIEW_WAS_ADDED:
                if (mSharedMessageRunnable != null) {
                    mSharedMessageRunnable.sendMessage(
                            REFRESH_GAME_VIEW_GRID_VALUE
                    );
                }
                break;
            case REFRESH_GAME_VIEW_GRID:
                if (mUIThreadHandler != null) {
                    mUIThreadHandler.post(this::refreshGridView);
                    if (mSharedMessageRunnable != null) {
                        mSharedMessageRunnable.sendMessage(
                                FINISH_REFRESH_GAME_VIEW_GRID_VALUE
                        );
                    }
                }
                break;
            case FINISH_REFRESH_GAME_VIEW_GRID:
                if (mSharedMessageRunnable != null) {
                    mSharedMessageRunnable.sendMessage(
                            ADD_GAME_VIEW_VALUE
                    );
                }
                break;
        }
    }
}