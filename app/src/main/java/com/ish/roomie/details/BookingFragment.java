package com.ish.roomie.details;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ish.roomie.R;
import com.ish.roomie.model.Attendee;
import com.ish.roomie.model.Room;
import com.ish.roomie.service.ApiServiceBody;
import com.ish.roomie.service.RetrofitClient;
import com.ish.timebar.TimeBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Fragment responsible for booking the room
 *
 * Not adding presenter since no much view logic
 */
public class BookingFragment extends Fragment {

    private static final String TAG = BookingFragment.class.getSimpleName();
    private Room roomObject;
    @BindView(R.id.toolbar_booking)
    protected Toolbar bookingToolbar;
    @BindView(R.id.booking_timebar)
    protected TimeBar bookingTimeBar;
    @BindView(R.id.add_attendee_layout)
    protected ViewGroup detailsContainer;
    @BindView(R.id.booking_title)
    protected EditText bookingTitleEditText;
    @BindView(R.id.booking_description)
    protected EditText bookingDescEditText;


    public BookingFragment() {
    }

    public static BookingFragment newInstance() {
        BookingFragment fragment = new BookingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        ButterKnife.bind(this, view);
        initialiseViews();
        return view;
    }

    private void initialiseViews() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(bookingToolbar);
        bookingToolbar.setTitle("Room: " + roomObject.getName());
        bookingToolbar.setSubtitle(roomObject.getLocation());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bookingToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        bookingTimeBar.setAvailableTime(roomObject.getAvail());
    }

    public void setRoomObject(Room roomObject) {
        this.roomObject = roomObject;
    }

    @OnClick(value = R.id.add_attendee_button)
    protected void onAddButtonClicked() {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.attendee_details_layout, null);
        Button remove = v.findViewById(R.id.attendee_remove); //No help from butterknife since dynamically added
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) v.getParent().getParent();
                detailsContainer.removeView(parent);
            }
        });

        // insert into main view
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);
        detailsContainer.addView(v, detailsContainer.getChildCount(), layoutParams);
    }

    @OnClick(R.id.attendee_remove)
    protected void onAttendeeRemove() {
        Button remove = getView().findViewById(R.id.attendee_remove);
        ViewGroup parent = (ViewGroup) remove.getParent().getParent();
        detailsContainer.removeView(parent);
    }


    @OnClick(R.id.booking_confirm_button)
    protected void onConfirmClicked() {
        List<Attendee> attendeeList = new ArrayList<>();
        for (int i = 0; i < detailsContainer.getChildCount(); i++) {
            View child = detailsContainer.getChildAt(i);
            String name = ((EditText) child.findViewById(R.id.attendeee_name)).getText().toString().trim();
            String email = ((EditText) child.findViewById(R.id.attendeee_email)).getText().toString().trim();
            String phone = ((EditText) child.findViewById(R.id.attendeee_phone)).getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Please enter all the details", Toast.LENGTH_SHORT).show();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getActivity(), "Please enter valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            Attendee attendee = new Attendee(name, email, phone);
            attendeeList.add(attendee);
        }
        processBooking(attendeeList);
    }

    private void processBooking(List<Attendee> attendeeList) {
        String title = bookingTitleEditText.getText().toString();
        String desc = bookingDescEditText.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc)) {
            Toast.makeText(getActivity(), "Please enter booking title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Bundle>>() {
        }.getType();
        String attendeeJson = gson.toJson(attendeeList, type);
        JSONObject parent = new JSONObject();
        try {
            JSONObject bookingObject = new JSONObject();
            bookingObject.put("date", "now");
            bookingObject.put("time_start", 1510143180);
            bookingObject.put("time_end", 1510243180);
            bookingObject.put("title", title);
            bookingObject.put("description", desc);
            bookingObject.put("room", roomObject.getName());

            parent.put("booking", bookingObject);
            parent.put("passes", attendeeJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RetrofitClient.getAPIServiceInstance().sendpass(parent.toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JSONObject>>() {
                    @Override
                    public ObservableSource<? extends JSONObject> apply(Throwable throwable) throws Exception {
                        return null;
                    }
                }).subscribe(new Observer<JSONObject>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(JSONObject result) {
                Log.i(TAG, "onNext: " + result);
                showConfirmation();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                Toast.makeText(getActivity(), "Could not complete your booking. Please check your connectivity.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        });
    }

    private void showConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_booking_confirmed)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
}
