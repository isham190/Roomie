package com.ish.roomie.service;

public interface GetRoomDetailsInteractor {
    interface Callback {
        void onFetchRoomDetailsSuccess();

        void onFetchRoomDetailsFailed();
    }

    void execute(Callback callback);
}
