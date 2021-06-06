package com.lidor.CommunicationUsingHandlers.game.contracts;

public interface LFGameViewHandlerContract {
    String MSG_KEY = "MessageHandlerKey";
    String ADD_GAME_VIEW_VALUE = "MessageHandlerAddView";
    String GAME_VIEW_WAS_ADDED_VALUE = "MessageHandlerViewWasAdded";
    String REFRESH_GAME_VIEW_GRID_VALUE = "MessageHandlerRefreshGrid";
    String FINISH_REFRESH_GAME_VIEW_GRID_VALUE = "MessageHandlerFinishRefreshGrid";
    String RESET_GAME_STATE = "MessageHandlerBackToNormal";

    interface OnMessageAddingViewReceivedDelegate {
        void onMessageStartAddingViewReceived();
    }

    interface OnMessageViewWasAddedDelegate {
        void onMessageViewWasAddedReceived();
    }

    interface OnMessageRefreshGameViewGridDelegate {
        void onMessageRefreshGameViewGridReceived();
    }

    interface OnMessageFinishRefreshGameViewGridDelegate {
        void onMessageFinishRefreshGameViewGridReceived();
    }
}
