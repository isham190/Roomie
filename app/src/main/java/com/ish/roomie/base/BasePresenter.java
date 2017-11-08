package com.ish.roomie.base;

public interface BasePresenter<V extends BaseView> {

    void attachView(V view);

    void detachView();

}
