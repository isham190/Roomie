package com.ish.roomie.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Base Fragment which forces child classes to pass Presenter instances for respective fragments.
 *
 * @param <P> BasePresenter
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {

    private Unbinder viewBinder;

    protected abstract P getPresenterInstance();

    protected P presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewBinder = ButterKnife.bind(this, this.getView());
        presenter = getPresenterInstance();
        presenter.attachView(this);
    }
}
