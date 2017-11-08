package com.ish.roomie.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ish.roomie.R;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Activity acts as base for the application.
 *
 * User interface is handled in Fragments
 */
public class HomeActivity extends AppCompatActivity {

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();

    }

    /**
     * Method shows error message. Called from the child fragments.
     */
    public void onError(){
        TextView errorTextView = (TextView) findViewById(R.id.error_textview);
        errorTextView.setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }
}
