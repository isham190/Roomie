package com.ish.roomie.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ish.roomie.R;
import com.ish.timebar.TimeBar;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display  {@link com.ish.roomie.model.Room}
 */
public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.RoomViewHolder> {

    private final HomePresenter homePresenter;

    public RoomRecyclerViewAdapter(HomePresenter homePresenter) {
        this.homePresenter = homePresenter;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_room_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RoomViewHolder holder, int position) {
        homePresenter.bindViewFromHolder(position, holder);
    }

    @Override
    public int getItemCount() {
        return homePresenter.getRoomListCount();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder implements HomeContract.RoomsRowView {
        @BindView(R.id.room_item_name)
        protected TextView roomNameTextView;

        @BindView(R.id.room_item_capacity)
        protected TextView roomCapacityTextView;

        @BindView(R.id.room_item_location)
        protected TextView roomLocationTextView;

        @BindView(R.id.room_item_iv)
        protected ImageView itemImageView;

        @BindView(R.id.room_item_equipments)
        protected TextView roomEquipmentsTextView;

        @BindView(R.id.room_item_timebar)
        protected TimeBar roomTimeBar;

        public RoomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setRoomName(String name) {
            roomNameTextView.setText(name);
        }

        @Override
        public void setLocation(String location) {
            roomLocationTextView.setText(location);
        }

        @Override
        public void setCapacity(String capacity) {
            roomCapacityTextView.setText(capacity);
        }

        @Override
        public void setEquipments(String equipments) {
            roomEquipmentsTextView.setText(equipments);
        }

        @Override
        public void setItemImage(String url) {
            Picasso.with(itemImageView.getContext()).load(url).placeholder(R.drawable.holder).into(itemImageView);
        }

        @Override
        public void setTimeAvailable(List<String> timeAvailable) {
            roomTimeBar.setAvailableTime(timeAvailable);
        }
    }
}
