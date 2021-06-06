package com.lidor.CommunicationUsingHandlers.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lidor.CommunicationUsingHandlers.game.message.LFGameMessageCallbackReceiver;
import com.lidor.CommunicationUsingHandlers.game.message.LFGameViewMessageRunnable;
import com.lidor.CommunicationUsingHandlers.game.model.LFGameBrain;
import com.lidor.CommunicationUsingHandlers.game.model.LFGameBrainProtocol;

import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.ADD_GAME_VIEW_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.GAME_VIEW_WAS_ADDED_VALUE;
import static com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract.REFRESH_GAME_VIEW_GRID_VALUE;

public class BaseGameViewModel
        extends
        AndroidViewModel
        implements
        BaseViewModelProtocol,
        LFGameMessageCallbackReceiver.OnGameMessageCallbackReceiverDelegate {

    private static final String TAG = BaseGameViewModel.class.getSimpleName();
    private LFGameMessageCallbackReceiver mGameMessageCallbackReceiver;
    private LFGameViewMessageRunnable mSharedMessageRunnable;
    private LFGameBrainProtocol mGameBrain;
    private MutableLiveData<LFGameMessageCallbackReceiver.MessageEventReporter> mMessageEventReporter;

    public BaseGameViewModel(@NonNull Application application) {
        super(application);
        mGameBrain = new LFGameBrain();
        mMessageEventReporter = new MutableLiveData<>(
                LFGameMessageCallbackReceiver.MessageEventReporter.START_GAME
        );
    }


    @Override
    public void startPenalty() {
        mGameBrain.startPenalty();
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
//                if (mUIThreadHandler != null) {
//                    mUIThreadHandler.post(this::refreshGridView);
//                    if (mSharedMessageRunnable != null) {
//                        mSharedMessageRunnable.sendMessage(
//                                FINISH_REFRESH_GAME_VIEW_GRID_VALUE
//                        );
//                    }
//                }
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


    //View


}
