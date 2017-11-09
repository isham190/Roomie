/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.ish.timebar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Class for green line and ticks representing time.
 */
class Bar {

    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;
    private final Paint timeTextPaint;
    private final Paint tickPaint;

    // Left-coordinate of the horizontal bar.
    private final float mLeftX;
    private final float mRightX;
    private final float mY;

    private int mNumSegments;
    private float mTickDistance;
    private final float mTickHeight;
    private final float mTickStartY;
    private final float mTickEndY;
    private final float timeTopMargin;
    private final float tickWeight;

    private List<Time> intervals;

    // Constructor /////////////////////////////////////////////////////////////

    Bar(Context ctx,
        float x,
        float y,
        float length,
        int tickCount,
        float tickHeightDP,
        float BarWeight,
        int BarColor, float timeTopMargin,
        List<Time> intervals, float strokeWidth) {

        mLeftX = x;
        mRightX = x + length;
        mY = y;

        mNumSegments = intervals.size()-1;
        mTickDistance = length / mNumSegments;
        mTickHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                tickHeightDP,
                ctx.getResources().getDisplayMetrics());
        this.mTickStartY = mY - mTickHeight / 3f;
        this.mTickEndY = mY + mTickHeight / 3f;

        this.intervals = intervals;

        this.timeTopMargin = timeTopMargin;
        // Initialize the paint.
        mPaint = new Paint();
        mPaint.setColor(BarColor);
        final Resources res = ctx.getResources();
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);

        // Initialize the paint.
        timeTextPaint = new Paint();
        timeTextPaint.setColor(Color.BLACK);
        timeTextPaint.setStrokeWidth(BarWeight);
        timeTextPaint.setStyle(Paint.Style.FILL);
        timeTextPaint.setAntiAlias(true);
        timeTextPaint.setTextSize(20);

        tickWeight = BarWeight;

        tickPaint = new Paint();
        tickPaint.setColor(Color.BLACK);
        tickPaint.setAntiAlias(true);
        tickPaint.setStrokeWidth(tickWeight);
    }

    // Package-Private Methods /////////////////////////////////////////////////

    /**
     * Draws the bar on the given Canvas.
     *
     * @param canvas Canvas to draw on; should be the Canvas passed into {#link
     *               View#onDraw()}
     */
    void draw(Canvas canvas) {

        canvas.drawLine(mLeftX, mY, mRightX, mY, mPaint);

    }

    /**
     * Get the x-coordinate of the left edge of the bar.
     *
     * @return x-coordinate of the left edge of the bar
     */
    float getLeftX() {
        return mLeftX;
    }

    /**
     * Get the x-coordinate of the right edge of the bar.
     *
     * @return x-coordinate of the right edge of the bar
     */
    float getRightX() {
        return mRightX;
    }

    /**
     * Gets the x-coordinate of the nearest tick to the given x-coordinate.
     *
     * @param x the x-coordinate to find the nearest tick for
     * @return the x-coordinate of the nearest tick
     */
    float getNearestTickCoordinate(Thumb thumb) {

        final float nearestTickIndex = getNearestTickIndex(thumb);

        final float nearestTickCoordinate = mLeftX + (nearestTickIndex * mTickDistance);

        return nearestTickCoordinate;
    }

    /**
     * Gets the zero-based index of the nearest tick to the given thumb.
     *
     * @param thumb the Thumb to find the nearest tick for
     * @return the zero-based index of the nearest tick
     */
    float getNearestTickIndex(Thumb thumb) {

        final float nearestTickIndex = ((thumb.getX() - mLeftX + (mTickDistance/4) / 2f) / mTickDistance);

        return Math.round(nearestTickIndex*4)/4f; //round to the nearest quarter.
    }

    /**
     * Set the number of ticks that will appear in the TimeBar.
     *
     * @param tickCount the number of ticks
     */
    void setTickCount(int tickCount) {

        final float barLength = mRightX - mLeftX;

        mNumSegments = tickCount - 1;
        mTickDistance = barLength / mNumSegments;
    }

    // Private Methods /////////////////////////////////////////////////////////

    /**
     * Draws the tick marks on the bar.
     *
     * @param canvas Canvas to draw on; should be the Canvas passed into {#link
     *               View#onDraw()}
     */
    public void drawTicks(Canvas canvas) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        // Loop through and draw each tick (except final tick).
        for (int i = 0; i < mNumSegments; i++) {
            final float x = i * mTickDistance + mLeftX;

            canvas.drawLine(x, mTickStartY, x, mTickEndY, tickPaint);

            /* Manage text dimensions */
            Date formattedDate = new Date(intervals.get(i).getTime());
            String time = dateFormat.format(formattedDate);
            float value = timeTextPaint.measureText(time);
            canvas.drawText(time, x - (value / 2), mTickEndY + timeTopMargin, timeTextPaint);
        }

        // Draw final tick. draw the final tick outside the loop to avoid any
        // rounding discrepancies.
        canvas.drawLine(mRightX, mTickStartY, mRightX, mTickEndY, tickPaint);
        Date formattedDate = new Date(intervals.get(intervals.size() - 1).getTime());
        String time = dateFormat.format(formattedDate);
        float value = timeTextPaint.measureText(time);
        canvas.drawText(time, mRightX - (value / 2), mTickEndY + timeTopMargin, timeTextPaint);
    }
}
