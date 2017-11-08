package com.ish.roomie.home;

import android.app.Activity;

import com.ish.roomie.base.BasePresenter;
import com.ish.roomie.base.BaseView;

import java.util.List;

public class HomeContract {

    interface HomeView extends BaseView {

        Activity getActivity();
    }

    interface HomePresenter extends BasePresenter<HomeView> {

    }

    interface RoomsRowView {

        void setRoomName(String name);

        void setLocation(String location);

        void setCapacity(String capacity);

        void setEquipments(String equipments);

        void setItemImage(String url);

        void setTimeAvailable(List<String> timeAvailable);

    }
}
