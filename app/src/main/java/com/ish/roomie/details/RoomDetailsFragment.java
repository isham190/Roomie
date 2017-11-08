package com.ish.roomie.details;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ish.roomie.R;
import com.ish.roomie.model.Room;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomDetailsFragment extends Fragment {

    @BindView(R.id.details_images_recyclerview)
    protected RecyclerView imagesRecyclerView;
    @BindView(R.id.details_equipments_body)
    protected TextView whatYouGetTextView;
    @BindView(R.id.details_room_size)
    protected TextView roomSizeTextView;
    @BindView(R.id.toolbar_details)
    protected Toolbar detailsToolbar;
    @BindView(R.id.details_capacity)
    protected TextView capacityTextView;

    private Room roomObject;


    public RoomDetailsFragment() {
        // Required empty public constructor
    }

    public static RoomDetailsFragment newInstance(String param1, String param2) {
        RoomDetailsFragment fragment = new RoomDetailsFragment();
        return fragment;
    }

    public void setRoomObject(Room roomObject){
        this.roomObject = roomObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_details, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        imagesRecyclerView.setLayoutManager(mLayoutManager);
        DetailsRecyclerAdapter mRecyclerAdapter = new DetailsRecyclerAdapter(roomObject.getImages());
        imagesRecyclerView.setAdapter(mRecyclerAdapter);
        initialiseView();
        return view;
    }

    private void initialiseView() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(detailsToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Room: "+roomObject.getName());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(roomObject.getLocation());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        String equipments = "";
        for (String equipment : roomObject.getEquipment()) {
            equipments += equipment + "\n";
        }
        capacityTextView.setText(String.valueOf(roomObject.getCapacity()));
        whatYouGetTextView.setText(equipments);
        roomSizeTextView.setText(roomObject.getSize());
    }

    @OnClick(value = R.id.details_book_button)
    protected void onBookButtonClicked(){
        BookingFragment bookingFragment = BookingFragment.newInstance();
        bookingFragment.setRoomObject(roomObject);
        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_right_to_left,
                android.R.anim.slide_in_left).add(R.id.fragment_container, bookingFragment)
                .addToBackStack(null).commit();
    }

}
