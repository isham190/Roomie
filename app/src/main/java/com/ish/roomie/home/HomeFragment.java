package com.ish.roomie.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ish.roomie.R;
import com.ish.roomie.base.BaseFragment;
import com.ish.roomie.details.RoomDetailsFragment;
import com.ish.roomie.filter.FilterFragment;
import com.ish.roomie.model.Filter;
import com.ish.roomie.model.Room;
import com.ish.roomie.viewutils.tagview.OnTagDeleteListener;
import com.ish.roomie.viewutils.tagview.Tag;
import com.ish.roomie.viewutils.tagview.TagView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of {@link Room}.
 */
public class HomeFragment extends BaseFragment<HomeContract.HomePresenter> implements HomeContract.HomeView, HomePresenter.Callback {

    private static final int FILTER_FRAGMENT_REQUEST_CODE = 0;
    private static final String INTENT_EXTRA_FILTER = "filterdata";
    private HomePresenter presenter = new HomePresenter(this);

    @BindView(R.id.toolbar_home)
    protected Toolbar homeToolbar;
    @BindView(R.id.toolbar_date_subtitle)
    protected TextView toolbarSubtitleTextView;
    @BindView(R.id.search_layout)
    protected ConstraintLayout searchLayout;
    @BindView(R.id.search_default_layout)
    protected ConstraintLayout searchDefaultLayout;
    @BindView(R.id.add_filter_layout)
    protected ConstraintLayout addFilterLayout;
    @BindView(R.id.search_ImageView)
    protected ImageView searchImageView;
    @BindView(R.id.search_edit_text)
    protected EditText searchEditText;
    @BindView(R.id.tag_home_filters)
    protected TagView filterTagView;

//    @BindView(R.id.error_textview)
//    protected TextView errorTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    @SuppressWarnings("unused")
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    protected HomePresenter getPresenterInstance() {
        return presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        presenter.initialise(null); //No filter initially. Show for "now"
        ((AppCompatActivity) getActivity()).setSupportActionBar(homeToolbar);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public View getContentView() {
        return this.getView();
    }

    @Override
    public void onListItemClicked(Room room) {
        RoomDetailsFragment roomDetailsFragment = RoomDetailsFragment.newInstance(null, null);
        roomDetailsFragment.setRoomObject(room);
        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_right_to_left,
                android.R.anim.slide_in_left).add(R.id.fragment_container, roomDetailsFragment)
                .addToBackStack(null).commit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Room item);
    }

    @OnClick(value = R.id.search_ImageView)
    protected void onSearchViewClicked() {
        searchDefaultLayout.setVisibility(View.GONE);
        // Prepare the View for the animation
        searchLayout.setVisibility(View.VISIBLE);
        searchLayout.setAlpha(0.0f);
        searchLayout.setX(searchImageView.getX());

        // Start the magic (animation)
        searchLayout.animate().setDuration(400).translationX(0).alpha(1.0f).setListener(null);
    }

    @OnClick(value = R.id.search_cancel_button)
    protected void onSearchCancelButtonClicked() {
        //Animate view away and switch visibility
        searchLayout.animate().setDuration(400).translationX(searchLayout.getWidth()).alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                searchLayout.setVisibility(View.GONE);
                searchDefaultLayout.setVisibility(View.VISIBLE);
            }
        });

        presenter.onSearchTextChanged(null);
        searchEditText.getText().clear();
        hideKeyboard();
    }

    @OnClick(value = R.id.filter_add)
    protected void onAddFilterButtonClicked() {
        FilterFragment filterFragment = FilterFragment.newInstance();
        filterFragment.setTargetFragment(HomeFragment.this, FILTER_FRAGMENT_REQUEST_CODE);
        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_right_to_left,
                android.R.anim.slide_in_left).add(R.id.fragment_container, filterFragment)
                .addToBackStack(null).commit();
    }

    @OnTextChanged(value = R.id.search_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onSearchTextChanged() {
        presenter.onSearchTextChanged(searchEditText.getText().toString().trim().toLowerCase());
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == FILTER_FRAGMENT_REQUEST_CODE) {
            Filter filter = data.getParcelableExtra(INTENT_EXTRA_FILTER);
            processFilter(filter);
        }
    }

    private void processFilter(final Filter filter) {
        if (filter.getCapacityFilter() != null) {
            filterTagView.setVisibility(View.VISIBLE);
            Tag capacityTag = new Tag(filter.getCapacityFilter());
            capacityTag.isDeletable = true;
            filterTagView.addTag(capacityTag);
        }
        if (filter.getDateFilter() != null) {
            toolbarSubtitleTextView.setText(filter.getDateFilter());
            presenter.initialise(filter);
        }
        if (filter.getTimeFilter() != null) {
            filterTagView.setVisibility(View.VISIBLE);
            Tag capacityTag = new Tag(filter.getTimeFilter());
            capacityTag.isDeletable = true;
            filterTagView.addTag(capacityTag);
            presenter.initialise(filter);
        }

        filterTagView.setOnTagDeleteListener(new OnTagDeleteListener() {
            @Override
            public void onTagDeleted(TagView view, Tag tag, int position) {
                filterTagView.remove(position);
                filter.clearFilters();
                presenter.initialise(filter);
                if (filterTagView.getTags().size() == 0) {
                    addFilterLayout.setVisibility(View.VISIBLE);
                    filterTagView.setVisibility(View.GONE);
                }
            }
        });

        addFilterLayout.setVisibility(filterTagView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}
