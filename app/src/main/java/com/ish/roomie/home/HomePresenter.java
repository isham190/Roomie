package com.ish.roomie.home;

import android.util.Log;

import com.ish.roomie.model.Room;
import com.ish.roomie.service.ApiServiceBody;
import com.ish.roomie.service.RetrofitClient;
import com.ish.roomie.model.Rooms;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Presenter class for Home activity
 */
public class HomePresenter {

    private static final String TAG = HomePresenter.class.getSimpleName();

    public void initialise() {
        RetrofitClient.getAPIServiceInstance().getRooms(new ApiServiceBody("now")).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends List<Room>>>() {
                    @Override
                    public ObservableSource<? extends List<Room>> apply(Throwable throwable) throws Exception {
                        return null;
                    }
                }).subscribe(new Observer<List<Room>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(List<Room> roomList) {
                Log.i(TAG, "onNext: " + roomList.size());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        });
    }
}
