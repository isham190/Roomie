package com.ish.timebar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class represents availability based on time value provided.
 */
class AvailabilityBar {

    private final Paint mPaint;

    private final float mConnectingLineWeight;
    private final float mY;
    private final List<String> availableTime;
    private float mTickDistance;

    AvailabilityBar(Context ctx, float y, float connectingLineWeight, int connectingLineColor, List<String> availableTime, float mTickDistance) {

        final Resources res = ctx.getResources();

        mConnectingLineWeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                connectingLineWeight,
                res.getDisplayMetrics());

        // Initialize the paint, set values
        mPaint = new Paint();
        mPaint.setAlpha(255);
        mPaint.setColor(connectingLineColor);
        mPaint.setStrokeWidth(mConnectingLineWeight);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaint.setAntiAlias(true);

        mY = y;
        this.availableTime = availableTime;
        this.mTickDistance = mTickDistance;
    }

    /**
     * Draw the connecting line between the two thumbs.
     *
     * @param canvas     the Canvas to draw to
     * @param x  the left starting point
     */
    void draw(Canvas canvas, float x) {

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < availableTime.size(); i++) {
            String plainTime = availableTime.get(i);
            String startTime = plainTime.split("-")[0].trim();
            String endTime = plainTime.split("-")[1].trim();
            try {
                long timeDifferenceInMillis = (dateFormat.parse(endTime).getTime() - dateFormat.parse(startTime).getTime());
                float timeDifferenceInHours = TimeUnit.MINUTES.convert(timeDifferenceInMillis, TimeUnit.MILLISECONDS) / 60f; //calculate time difference and convert into hours
                float busyLinewidth = mTickDistance * timeDifferenceInHours; //line width (busy time)
                float busyStarting = (TimeUnit.MINUTES.convert(dateFormat.parse(startTime).getTime() - dateFormat.parse("07:00").getTime(), TimeUnit.MILLISECONDS)) / 60f * mTickDistance; //Starting point for busy red line calculated from default start time 7am
                canvas.drawLine(x + busyStarting, mY, x + busyStarting + busyLinewidth, mY, mPaint); // draw red line

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
