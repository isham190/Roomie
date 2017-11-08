package com.ish.roomie.home;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ish.roomie.R;
import com.ish.roomie.base.BasePresenterImpl;
import com.ish.roomie.model.Filter;
import com.ish.roomie.model.Room;
import com.ish.roomie.service.ApiServiceBody;
import com.ish.roomie.service.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Presenter class for {@link HomeFragment}
 *
 * Has the view login in here. Thanks to MVP.
 *
 */
public class HomePresenter extends BasePresenterImpl<HomeContract.HomeView> implements HomeContract.HomePresenter {

    private static final String TAG = HomePresenter.class.getSimpleName();
    private Callback callback;

    private List<Room> roomList;
    private List<Room> filteredRoomList;
    RoomRecyclerViewAdapter roomRecyclerViewAdapter;

    public HomePresenter(Callback callback) {
        this.callback = callback;
    }

    /**
     * Method called to trigger data fetch and update the UI
     * @param filter filters applied by the user. Null if no filter has to be applied.
     */
    public void initialise(Filter filter) {
        String dateFilter = "now";
        if (filter != null) {
            if( filter.getDateFilter() != null) {
                String filterValue = filter.getDateFilter();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM.dd.yyyy");
                if (filterValue.equalsIgnoreCase("tomorrow")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE, 1);
                    dateFilter = String.valueOf(calendar.getTime().getTime() / 1000);
                } else {
                    try {
                        Date dateToFilter = filterValue.equalsIgnoreCase("today") ? new Date() : simpleDateFormat.parse(filterValue);
                        dateFilter = String.valueOf(dateToFilter.getTime() / 1000);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(filter.getTimeFilter() != null){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR, 1);
                dateFilter = String.valueOf(calendar.getTime().getTime() / 1000);
            }

        }
        RetrofitClient.getAPIServiceInstance().getRooms(new ApiServiceBody(dateFilter)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                setUpRecycler(roomList);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                ((HomeActivity)getView().getActivity()).onError();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        });
    }

    /**
     * Recyclerview is being handled in presenter since easy data binding and maintenance.
     * @param roomList
     */
    private void setUpRecycler(List<Room> roomList) {
        this.roomList = new ArrayList<>(roomList);
        this.filteredRoomList = new ArrayList<>(roomList);

        RecyclerView homeRecyclerView = (RecyclerView) getView().getContentView().findViewById(R.id.room_recycler_view);
        roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(this);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(homeRecyclerView.getContext()));
        homeRecyclerView.setAdapter(roomRecyclerViewAdapter);
    }

    /**
     * Bind View based on rows passed by the adapter
     * @param position view position
     * @param rowView list row
     */
    public void bindViewFromHolder(int position, RoomRecyclerViewAdapter.RoomViewHolder rowView) {
        final Room room = filteredRoomList.get(position);
        rowView.setRoomName("Room " + room.getName());
        rowView.setLocation("Location: " + room.getLocation());
        rowView.setCapacity("Capacity of " + room.getCapacity());
        rowView.setTimeAvailable(room.getAvail());

        String equipments = "";
        for (String equipment : room.getEquipment()) {
            equipments += equipment + "\n";
        }
        rowView.setEquipments(equipments);
        rowView.setItemImage("https://challenges.1aim.com/roombooking_app/" + room.getImages().get(0));

        rowView.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onListItemClicked(room);
            }
        });
    }

    public int getRoomListCount() {
        return filteredRoomList == null ? 0 : filteredRoomList.size();
    }

    /**
     * Method handles search changes in the UI
     * @param searchText
     */
    public void onSearchTextChanged(String searchText) {

        if (searchText == null || searchText.isEmpty()) {
            filteredRoomList.clear();
            filteredRoomList.addAll(roomList);
        } else {

            filteredRoomList.clear();
            //TODO: Use RXy method later
            for (Room room : roomList) {
                if (room.getName().toLowerCase().contains(searchText)) {
                    filteredRoomList.add(room);
                }
            }
        }
        roomRecyclerViewAdapter.notifyDataSetChanged();

    }

    /**
     * Communication channel for presenter to communicate to the fragment
     */
    public interface Callback {
        void onListItemClicked(Room room);
    }
}
