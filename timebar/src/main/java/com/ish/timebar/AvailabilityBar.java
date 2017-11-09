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

    private final float mY;
    private final List<String> availableTime;
    private float mTickDistance;

    AvailabilityBar(Context ctx, float y, float connectingLineWeight, int connectingLineColor, List<String> availableTime, float mTickDistance) {

        // Initialize the paint, set values
        mPaint = new Paint();
        mPaint.setAlpha(255);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(connectingLineWeight);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaint.setAntiAlias(true);

        mY = y;
        this.availableTime = availableTime;
        this.mTickDistance = mTickDistance;
    }

    /**
     * Draw the busy line based on available time provided
     *
     * @param canvas the Canvas to draw to
     * @param x      the left starting point
     */
    void draw(Canvas canvas, float x) {

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < availableTime.size(); i++) {
            String plainTime = availableTime.get(i);
            String startTime = plainTime.split("-")[0].trim();//split and get the starting time
            String endTime = plainTime.split("-")[1].trim(); // get end time
            try {
                long timeDifferenceInMillis = (dateFormat.parse(endTime).getTime() - dateFormat.parse(startTime).getTime());
                float timeDifferenceInHours = TimeUnit.MINUTES.convert(timeDifferenceInMillis, TimeUnit.MILLISECONDS) / 60f; //calculate time difference and convert into hours
                float busyLinewidth = mTickDistance * timeDifferenceInHours; //line width (busy time)
                float busyStarting = (TimeUnit.MINUTES.convert(dateFormat.parse(startTime).getTime() - dateFormat.parse("07:00").getTime(), TimeUnit.MILLISECONDS)) / 60f * mTickDistance; //Starting point for busy red line calculated from default start time 7am
                float busyEndingPoint = x + busyStarting + busyLinewidth;
                canvas.drawLine(x + busyStarting, mY, busyEndingPoint, mY, mPaint); // draw red line
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
