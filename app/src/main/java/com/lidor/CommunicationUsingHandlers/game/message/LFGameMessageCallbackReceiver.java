package com.lidor.CommunicationUsingHandlers.game.message;

import android.os.Build;

import androidx.annotation.NonNull;

import com.lidor.CommunicationUsingHandlers.game.contracts.LFGameViewHandlerContract;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class LFGameMessageCallbackReceiver
        implements
        LFGameViewHandlerContract.OnMessageAddingViewReceivedDelegate,
        LFGameViewHandlerContract.OnMessageRefreshGameViewGridDelegate,
        LFGameViewHandlerContract.OnMessageViewWasAddedDelegate,
        LFGameViewHandlerContract.OnMessageFinishRefreshGameViewGridDelegate {
    private static final Object mLock = new Object();
    private static LFGameMessageCallbackReceiver mInstance;

    private final Set<
            WeakReference<OnGameMessageCallbackReceiverDelegate>
            > mMessageEventReporter;

    private LFGameMessageCallbackReceiver() {
        mMessageEventReporter = new HashSet<>();
    }

    public static LFGameMessageCallbackReceiver getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new LFGameMessageCallbackReceiver();
                }
                return mInstance;
            }
        }
        return mInstance;
    }

    public final void registerEventDelegate(
            @NonNull OnGameMessageCallbackReceiverDelegate delegate
    ) {
        mMessageEventReporter.add(
                new WeakReference<>(delegate)
        );
    }

    @Override
    public final void onMessageStartAddingViewReceived() {
        notifyObservers(
                MessageEventReporter.START_ADDING_VIEWS
        );
    }

    @Override
    public final void onMessageViewWasAddedReceived() {
        notifyObservers(
                MessageEventReporter.VIEW_WAS_ADDED
        );
    }

    @Override
    public final void onMessageRefreshGameViewGridReceived() {
        notifyObservers(
                MessageEventReporter.REFRESH_GAME_VIEW_GRID
        );
    }

    @Override
    public final void onMessageFinishRefreshGameViewGridReceived() {
        notifyObservers(
                MessageEventReporter.FINISH_REFRESH_GAME_VIEW_GRID
        );
    }

    private void notifyObservers(
            MessageEventReporter forEventType
    ) {
        final Iterator<WeakReference<OnGameMessageCallbackReceiverDelegate>> iterator = mMessageEventReporter.iterator();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            iterator.forEachRemaining((ref) -> {
                if (ref != null) {
                    final OnGameMessageCallbackReceiverDelegate delegate = ref.get();
                    if (delegate != null) {
                        delegate.onEventFired(forEventType);
                    }
                }
            });
        } else {
            while (iterator.hasNext()) {
                final WeakReference<OnGameMessageCallbackReceiverDelegate> ref = iterator.next();
                if (ref != null) {
                    final OnGameMessageCallbackReceiverDelegate delegate = ref.get();
                    if (delegate != null) {
                        delegate.onEventFired(forEventType);
                    }
                }
            }
        }
    }

    public enum MessageEventReporter {
        START_GAME,
        START_ADDING_VIEWS,
        VIEW_WAS_ADDED,
        REFRESH_GAME_VIEW_GRID,
        FINISH_REFRESH_GAME_VIEW_GRID;

        public MessageEventReporter[] allCases() {
            return new MessageEventReporter[]{
                    START_ADDING_VIEWS,
                    VIEW_WAS_ADDED,
                    REFRESH_GAME_VIEW_GRID,
                    FINISH_REFRESH_GAME_VIEW_GRID
            };
        }
    }

    public interface OnGameMessageCallbackReceiverDelegate {
        void onEventFired(MessageEventReporter messageEvent);
    }
}
