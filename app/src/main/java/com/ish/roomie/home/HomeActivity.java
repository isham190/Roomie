package com.ish.roomie.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ish.roomie.R;

import io.reactivex.disposables.CompositeDisposable;

public class HomeActivity extends AppCompatActivity {

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new HomePresenter().initialise();

    }
}
