package com.ish.roomie.filter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ish.roomie.R;
import com.ish.roomie.model.Filter;
import com.ish.roomie.viewutils.tagview.OnTagClickListener;
import com.ish.roomie.viewutils.tagview.Tag;
import com.ish.roomie.viewutils.tagview.TagView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment responsible for filters.
 *
 * TODO: Implement capacity filter and multiple filters at a time
 */
public class FilterFragment extends Fragment {

    @BindView(R.id.tag_group_date)
    protected TagView dateTagView;
    @BindView(R.id.tag_group_hour)
    protected TagView hourTagView;
    @BindView(R.id.tag_group_capacity)
    protected TagView capacityTagView;
    @BindView(R.id.toolbar_filter)
    protected Toolbar filterToolbar;
    @BindView(R.id.filter_date_selector)
    protected EditText filterDateEditText;

    private Filter filterObject = new Filter();

    public FilterFragment() {
    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);
        initialiseViews();
        return view;
    }

    /**
     * Initialise the views for the fragment.
     */
    private void initialiseViews() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(filterToolbar);
        filterToolbar.setTitle(getString(R.string.fragment_title_filter));
        filterToolbar.setSubtitle(getString(R.string.fragment_subhead_filter));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        filterToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); //Toolbar navigation. Act as back button
            }
        });

        for (String date : getResources().getStringArray(R.array.array_filter_days)) {
            Tag tag = new Tag(date);
            tag.setItemObject(date);
            dateTagView.addTag(tag);
        }

        dateTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                filterObject.setDateFilter(tag.getItemObj().toString());
            }
        });

        for (String hour : getResources().getStringArray(R.array.array_filter_time)) {
            Tag tag = new Tag(hour);
            tag.setItemObject(hour);
            hourTagView.addTag(tag);
        }
        for (String capacity : getResources().getStringArray(R.array.array_filter_capacity)) { //Not functional. Added to beautify the screen
            Tag tag = new Tag(capacity);
            tag.setItemObject(capacity);
            capacityTagView.addTag(tag);
        }


        hourTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                filterObject.setTimeFilter(tag.getItemObj().toString());
            }
        });

    }

    @OnClick(value = R.id.clear_all_filter_button)
    protected void onClearAllButtonClicked() {
        filterObject.clearFilters();

    }

    @OnClick(value = R.id.filter_done_button)
    protected void onDoneButtonClicked() {
        Intent intent = new Intent(getActivity(), FilterFragment.class);
        intent.putExtra("filterdata", filterObject);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        getFragmentManager().popBackStack();
    }

    @OnClick(value = R.id.filter_date_selector)
    protected void onDateSelected() {
        DialogFragment newFragment = DatePickerFragment.newInstance(dateSetListener);
        newFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener onDateSetListener;

        //added static factory method since views cannot be accessed from this fragment since static
        static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener dateSetListener) {
            DatePickerFragment pickerFragment = new DatePickerFragment();
            pickerFragment.setOnDateSetListener(dateSetListener);

            return pickerFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
        }

        private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
            this.onDateSetListener = listener;
        }
    }

    /**
     * Dialog fragment date change listener. added outside the class to handle callback from static inner class.
     */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM.dd.yyyy");
            filterObject.setDateFilter(simpleDateFormat.format(calendar.getTime()));
            filterDateEditText.setText(simpleDateFormat.format(calendar.getTime()));
        }
    };
}
