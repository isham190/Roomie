package com.ish.timebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeBar extends View {

    private static final String TAG = TimeBar.class.getSimpleName();

    // Default values for variables
    private static final int DEFAULT_TIME_TOP_MARGIN_DP = 25;
    private static final int DEFAULT_TICK_COUNT = 24;
    private static final float DEFAULT_TICK_HEIGHT_DP = 12;
    private static final float DEFAULT_BAR_WEIGHT_PX = 2;
    private static final int DEFAULT_BAR_COLOR = Color.GREEN;
    private static final float DEFAULT_CONNECTING_LINE_WEIGHT_PX = 4;
    private static final int DEFAULT_THUMB_IMAGE_NORMAL = R.drawable.seek_thumb_normal;
    private static final int DEFAULT_THUMB_IMAGE_PRESSED = R.drawable.seek_thumb_pressed;

    // Corresponds to android.R.color.holo_blue_light.
    private static final int DEFAULT_CONNECTING_LINE_COLOR = Color.RED;

    // Indicator value tells Thumb.java whether it should draw the circle or not
    private static final float DEFAULT_THUMB_RADIUS_DP = -1;
    private static final int DEFAULT_THUMB_COLOR_NORMAL = -1;
    private static final int DEFAULT_THUMB_COLOR_PRESSED = -1;

    // Instance variables for all of the customizable attributes
    private int mTickCount = DEFAULT_TICK_COUNT;
    private float mTickHeightDP = DEFAULT_TICK_HEIGHT_DP;
    private float mTickTimeTopMarginDP = DEFAULT_TIME_TOP_MARGIN_DP;
    private float mBarWeight = DEFAULT_BAR_WEIGHT_PX;
    private int mBarColor = DEFAULT_BAR_COLOR;
    private float mConnectingLineWeight = DEFAULT_CONNECTING_LINE_WEIGHT_PX;
    private int mConnectingLineColor = DEFAULT_CONNECTING_LINE_COLOR;
    private int mThumbImageNormal = DEFAULT_THUMB_IMAGE_NORMAL;
    private int mThumbImagePressed = DEFAULT_THUMB_IMAGE_PRESSED;

    private float mThumbRadiusDP = DEFAULT_THUMB_RADIUS_DP;
    private int mThumbColorNormal = DEFAULT_THUMB_COLOR_NORMAL;
    private int mThumbColorPressed = DEFAULT_THUMB_COLOR_PRESSED;

    // setTickCount only resets indices before a thumb has been pressed or a
    // setThumbIndices() is called, to correspond with intended usage
    private boolean mFirstSetTickCount = true;

    private int mDefaultWidth = 500;
    private int mDefaultHeight = 100;

    private Thumb mLeftThumb;
    private Thumb mRightThumb;
    private Bar mBar;
    private AvailabilityBar availabilityLine;

    private OnTimeBarChangeListener mListener;
    private int mLeftIndex = 0;
    private int mRightIndex = mTickCount - 1;

    List<Time> intervals = new ArrayList<>();
    List<String> availableTime = new ArrayList<>();

    public TimeBar(Context context) {
        super(context);
    }

    public TimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TimeBarInit(context, attrs);
        getTimeIntervals();
    }

    public TimeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TimeBarInit(context, attrs);
        getTimeIntervals();
    }

    /**
     * get the intervals of time between start and end time
     */
    private void getTimeIntervals() {
        Time startTime = new Time(7, 0, 0); //Since we know start and end time
        Time endTime = new Time(19, 0, 0);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        intervals.add(startTime);
        while (cal.getTime().before(endTime)) {
            cal.add(Calendar.HOUR, 1);
            intervals.add(new Time(cal.getTimeInMillis()));
        }
    }

    /**
     * Set available time to populate in the time bar
     *
     * @param availableTime
     */
    public void setAvailableTime(List<String> availableTime) {
        this.availableTime = availableTime;
    }

    // View Methods ////////////////////////////////////////////////////////////

    @Override
    public Parcelable onSaveInstanceState() {

        final Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());

        bundle.putInt("TICK_COUNT", mTickCount);
        bundle.putFloat("TICK_HEIGHT_DP", mTickHeightDP);
        bundle.putFloat("BAR_WEIGHT", mBarWeight);
        bundle.putInt("BAR_COLOR", mBarColor);
        bundle.putFloat("CONNECTING_LINE_WEIGHT", mConnectingLineWeight);
        bundle.putInt("CONNECTING_LINE_COLOR", mConnectingLineColor);

        bundle.putInt("THUMB_IMAGE_NORMAL", mThumbImageNormal);
        bundle.putInt("THUMB_IMAGE_PRESSED", mThumbImagePressed);

        bundle.putFloat("THUMB_RADIUS_DP", mThumbRadiusDP);
        bundle.putInt("THUMB_COLOR_NORMAL", mThumbColorNormal);
        bundle.putInt("THUMB_COLOR_PRESSED", mThumbColorPressed);

        bundle.putInt("LEFT_INDEX", mLeftIndex);
        bundle.putInt("RIGHT_INDEX", mRightIndex);

        bundle.putBoolean("FIRST_SET_TICK_COUNT", mFirstSetTickCount);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            final Bundle bundle = (Bundle) state;

            mTickCount = bundle.getInt("TICK_COUNT");
            mTickHeightDP = bundle.getFloat("TICK_HEIGHT_DP");
            mBarWeight = bundle.getFloat("BAR_WEIGHT");
            mBarColor = bundle.getInt("BAR_COLOR");
            mConnectingLineWeight = bundle.getFloat("CONNECTING_LINE_WEIGHT");
            mConnectingLineColor = bundle.getInt("CONNECTING_LINE_COLOR");

            mThumbImageNormal = bundle.getInt("THUMB_IMAGE_NORMAL");
            mThumbImagePressed = bundle.getInt("THUMB_IMAGE_PRESSED");

            mThumbRadiusDP = bundle.getFloat("THUMB_RADIUS_DP");
            mThumbColorNormal = bundle.getInt("THUMB_COLOR_NORMAL");
            mThumbColorPressed = bundle.getInt("THUMB_COLOR_PRESSED");

            mLeftIndex = bundle.getInt("LEFT_INDEX");
            mRightIndex = bundle.getInt("RIGHT_INDEX");
            mFirstSetTickCount = bundle.getBoolean("FIRST_SET_TICK_COUNT");

            setThumbIndices(mLeftIndex, mRightIndex);

            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));

        } else {

            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width;
        int height;

        // Get measureSpec mode and size values.
        final int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        // The TimeBar width should be as large as possible.
        if (measureWidthMode == MeasureSpec.AT_MOST) {
            width = measureWidth;
        } else if (measureWidthMode == MeasureSpec.EXACTLY) {
            width = measureWidth;
        } else {
            width = mDefaultWidth;
        }

        // The TimeBar height should be as small as possible.
        if (measureHeightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDefaultHeight, measureHeight);
        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = mDefaultHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        final Context ctx = getContext();

        // This is the initial point at which we know the size of the View.

        // Create the two thumb objects.
        final float yPos = h / 2f;
        mLeftThumb = new Thumb(ctx,
                yPos,
                mThumbColorNormal,
                mThumbColorPressed,
                mThumbRadiusDP,
                mThumbImageNormal,
                mThumbImagePressed);
        mRightThumb = new Thumb(ctx,
                yPos,
                mThumbColorNormal,
                mThumbColorPressed,
                mThumbRadiusDP,
                mThumbImageNormal,
                mThumbImagePressed);

        // Create the underlying bar.
        final float marginLeft = mLeftThumb.getHalfWidth();
        final float barLength = w - 2 * marginLeft;
        mBar = new Bar(ctx, marginLeft, yPos, barLength, mTickCount, mTickHeightDP, mBarWeight, mBarColor, mTickTimeTopMarginDP, intervals);

        // Initialize thumbs to the desired indices
        mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);

        // Set the thumb indices.
        final int newLeftIndex = mBar.getNearestTickIndex(mLeftThumb);
        final int newRightIndex = mBar.getNearestTickIndex(mRightThumb);

        // Call the listener.
        if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {

            mLeftIndex = newLeftIndex;
            mRightIndex = newRightIndex;

            if (mListener != null) {
                mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }
        }

        // Create the line connecting the two thumbs.
        float tickWidth = getBarLength() / (intervals.size() - 1);
        availabilityLine = new AvailabilityBar(ctx, yPos, mConnectingLineWeight, mConnectingLineColor, availableTime, tickWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        mBar.draw(canvas);

        availabilityLine.draw(canvas, getX() + getMarginLeft());
        mBar.drawTicks(canvas);
    }


    /**
     * Sets a listener to receive notifications of changes to the TimeBar. This
     * will overwrite any existing set listeners.
     *
     * @param listener the TimeBar notification listener; null to remove any
     *                 existing listener
     */
    public void setOnTimeBarChangeListener(OnTimeBarChangeListener listener) {
        mListener = listener;
    }

    /**
     * Sets the number of ticks in the TimeBar.
     *
     * @param tickCount Integer specifying the number of ticks.
     */
    public void setTickCount(int tickCount) {

        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;

            // Prevents resetting the indices when creating new activity, but
            // allows it on the first setting.
            if (mFirstSetTickCount) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
                }
            }
            if (indexOutOfRange(mLeftIndex, mRightIndex)) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null)
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }

            createBar();
            createThumbs();
        } else {
            Log.e(TAG, "tickCount less than 2; invalid tickCount.");
            throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
        }
    }

    /**
     * Sets the height of the ticks in the time bar.
     *
     * @param tickHeight Float specifying the height of each tick mark in dp.
     */
    public void setTickHeight(float tickHeight) {

        mTickHeightDP = tickHeight;
        createBar();
    }

    /**
     * Set the weight of the bar line and the tick lines in the time bar.
     *
     * @param barWeight Float specifying the weight of the bar and tick lines in
     *                  px.
     */
    public void setBarWeight(float barWeight) {

        mBarWeight = barWeight;
        createBar();
    }

    /**
     * Set the color of the bar line and the tick lines in the time bar.
     *
     * @param barColor Integer specifying the color of the bar line.
     */
    public void setBarColor(int barColor) {

        mBarColor = barColor;
        createBar();
    }

    /**
     * Set the weight of the connecting line between the thumbs.
     *
     * @param connectingLineWeight Float specifying the weight of the connecting
     *                             line.
     */
    public void setConnectingLineWeight(float connectingLineWeight) {

        mConnectingLineWeight = connectingLineWeight;
        createConnectingLine();
    }

    /**
     * Set the color of the connecting line between the thumbs.
     *
     * @param connectingLineColor Integer specifying the color of the connecting
     *                            line.
     */
    public void setConnectingLineColor(int connectingLineColor) {

        mConnectingLineColor = connectingLineColor;
        createConnectingLine();
    }

    /**
     * If this is set, the thumb images will be replaced with a circle of the
     * specified radius. Default width = 20dp.
     *
     * @param thumbRadius Float specifying the radius of the thumbs to be drawn.
     */
    public void setThumbRadius(float thumbRadius) {

        mThumbRadiusDP = thumbRadius;
        createThumbs();
    }

    /**
     * Sets the normal thumb picture by taking in a reference ID to an image.
     *
     * @param thumbImageNormalID Integer specifying the resource ID of the image to
     *                           be drawn as the normal thumb.
     */
    public void setThumbImageNormal(int thumbImageNormalID) {
        mThumbImageNormal = thumbImageNormalID;
        createThumbs();
    }

    /**
     * Sets the pressed thumb picture by taking in a reference ID to an image.
     *
     * @param thumbImagePressedID Integer specifying the resource ID of the image to
     *                            be drawn as the pressed thumb.
     */
    public void setThumbImagePressed(int thumbImagePressedID) {
        mThumbImagePressed = thumbImagePressedID;
        createThumbs();
    }

    /**
     * If this is set, the thumb images will be replaced with a circle. The
     * normal image will be of the specified color.
     *
     * @param thumbColorNormal Integer specifying the normal color of the circle
     *                         to be drawn.
     */
    public void setThumbColorNormal(int thumbColorNormal) {
        mThumbColorNormal = thumbColorNormal;
        createThumbs();
    }

    /**
     * If this is set, the thumb images will be replaced with a circle. The
     * pressed image will be of the specified color.
     *
     * @param thumbColorPressed Integer specifying the pressed color of the
     *                          circle to be drawn.
     */
    public void setThumbColorPressed(int thumbColorPressed) {
        mThumbColorPressed = thumbColorPressed;
        createThumbs();
    }

    /**
     * Sets the location of each thumb according to the developer's choice.
     * Numbered from 0 to mTickCount - 1 from the left.
     *
     * @param leftThumbIndex  Integer specifying the index of the left thumb
     * @param rightThumbIndex Integer specifying the index of the right thumb
     */
    public void setThumbIndices(int leftThumbIndex, int rightThumbIndex) {
        if (indexOutOfRange(leftThumbIndex, rightThumbIndex)) {

            Log.e(TAG, "A thumb index is out of bounds. Check that it is between 0 and mTickCount - 1");
            throw new IllegalArgumentException("A thumb index is out of bounds. Check that it is between 0 and mTickCount - 1");

        } else {

            if (mFirstSetTickCount == true)
                mFirstSetTickCount = false;

            mLeftIndex = leftThumbIndex;
            mRightIndex = rightThumbIndex;
            createThumbs();

            if (mListener != null) {
                mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
            }
        }

        invalidate();
        requestLayout();
    }

    /**
     * Gets the index of the left-most thumb.
     *
     * @return the 0-based index of the left thumb
     */
    public int getLeftIndex() {
        return mLeftIndex;
    }

    /**
     * Gets the index of the right-most thumb.
     *
     * @return the 0-based index of the right thumb
     */
    public int getRightIndex() {
        return mRightIndex;
    }

    // Private Methods /////////////////////////////////////////////////////////

    /**
     * Does all the functions of the constructor for TimeBar. Called by both
     * TimeBar constructors in lieu of copying the code for each constructor.
     *
     * @param context Context from the constructor.
     * @param attrs   AttributeSet from the constructor.
     * @return none
     */
    private void TimeBarInit(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimeBar, 0, 0);

        try {

            // Sets the values of the user-defined attributes based on the XML
            // attributes.
            final Integer tickCount = ta.getInteger(R.styleable.TimeBar_tickCount, DEFAULT_TICK_COUNT);

            if (isValidTickCount(tickCount)) {

                // Similar functions performed above in setTickCount; make sure
                // you know how they interact
                mTickCount = tickCount;
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onIndexChangeListener(this, mLeftIndex, mRightIndex);
                }

            } else {

                Log.e(TAG, "tickCount less than 2; invalid tickCount. XML input ignored.");
            }

            mTickHeightDP = ta.getDimension(R.styleable.TimeBar_tickHeight, DEFAULT_TICK_HEIGHT_DP);
            mTickTimeTopMarginDP = ta.getDimension(R.styleable.TimeBar_timeTopMargin, DEFAULT_TIME_TOP_MARGIN_DP);
            mBarWeight = ta.getDimension(R.styleable.TimeBar_barWeight, DEFAULT_BAR_WEIGHT_PX);
            mBarColor = ta.getColor(R.styleable.TimeBar_barColor, DEFAULT_BAR_COLOR);
            mConnectingLineWeight = ta.getDimension(R.styleable.TimeBar_connectingLineWeight,
                    DEFAULT_CONNECTING_LINE_WEIGHT_PX);
            mConnectingLineColor = ta.getColor(R.styleable.TimeBar_connectingLineColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mThumbRadiusDP = ta.getDimension(R.styleable.TimeBar_thumbRadius, DEFAULT_THUMB_RADIUS_DP);
            mThumbImageNormal = ta.getResourceId(R.styleable.TimeBar_thumbImageNormal,
                    DEFAULT_THUMB_IMAGE_NORMAL);
            mThumbImagePressed = ta.getResourceId(R.styleable.TimeBar_thumbImagePressed,
                    DEFAULT_THUMB_IMAGE_PRESSED);
            mThumbColorNormal = ta.getColor(R.styleable.TimeBar_thumbColorNormal, DEFAULT_THUMB_COLOR_NORMAL);
            mThumbColorPressed = ta.getColor(R.styleable.TimeBar_thumbColorPressed,
                    DEFAULT_THUMB_COLOR_PRESSED);

        } finally {

            ta.recycle();
        }

    }

    /**
     * Creates a new mBar
     */
    private void createBar() {

        mBar = new Bar(getContext(),
                getMarginLeft(),
                getYPos(),
                getBarLength(),
                mTickCount,
                mTickHeightDP,
                mBarWeight,
                mBarColor,
                mTickTimeTopMarginDP, intervals);
        invalidate();
    }

    /**
     * Creates a new AvailabilityBar.
     */
    private void createConnectingLine() {
        float tickWidth = getBarLength() / (intervals.size() - 1);

        availabilityLine = new AvailabilityBar(getContext(),
                getYPos(),
                mConnectingLineWeight,
                mConnectingLineColor, availableTime, tickWidth);
        invalidate();
    }

    /**
     * Creates two new Thumbs.
     */
    private void createThumbs() {

        Context ctx = getContext();
        float yPos = getYPos();

        mLeftThumb = new Thumb(ctx,
                yPos,
                mThumbColorNormal,
                mThumbColorPressed,
                mThumbRadiusDP,
                mThumbImageNormal,
                mThumbImagePressed);
        mRightThumb = new Thumb(ctx,
                yPos,
                mThumbColorNormal,
                mThumbColorPressed,
                mThumbRadiusDP,
                mThumbImageNormal,
                mThumbImagePressed);

        float marginLeft = getMarginLeft();
        float barLength = getBarLength();

        // Initialize thumbs to the desired indices
        mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);

        invalidate();
    }

    /**
     * Get marginLeft in each of the public attribute methods.
     *
     * @return float marginLeft
     */
    private float getMarginLeft() {
        return ((mLeftThumb != null) ? mLeftThumb.getHalfWidth() : 0);
    }

    /**
     * Get yPos in each of the public attribute methods.
     *
     * @return float yPos
     */
    private float getYPos() {
        return (getHeight() / 2f);
    }

    /**
     * Get barLength in each of the public attribute methods.
     *
     * @return float barLength
     */
    private float getBarLength() {
        return (getWidth() - 2 * getMarginLeft());
    }

    /**
     * Returns if either index is outside the range of the tickCount.
     *
     * @param leftThumbIndex  Integer specifying the left thumb index.
     * @param rightThumbIndex Integer specifying the right thumb index.
     * @return boolean If the index is out of range.
     */
    private boolean indexOutOfRange(int leftThumbIndex, int rightThumbIndex) {
        return (leftThumbIndex < 0 || leftThumbIndex >= mTickCount
                || rightThumbIndex < 0
                || rightThumbIndex >= mTickCount);
    }

    /**
     * If is invalid tickCount, rejects. TickCount must be greater than 1
     *
     * @param tickCount Integer
     * @return boolean: whether tickCount > 1
     */
    private boolean isValidTickCount(int tickCount) {
        return (tickCount > 1);
    }

    // Inner Classes ///////////////////////////////////////////////////////////

    /**
     * A callback that notifies clients when the TimeBar has changed. The
     * listener will only be called when either thumb's index has changed - not
     * for every movement of the thumb.
     */
    public static interface OnTimeBarChangeListener {

        public void onIndexChangeListener(TimeBar TimeBar, int leftThumbIndex, int rightThumbIndex);
    }
}
