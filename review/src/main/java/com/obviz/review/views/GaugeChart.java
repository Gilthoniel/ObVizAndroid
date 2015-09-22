package com.obviz.review.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import com.obviz.reviews.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaylor on 08/20/2015.
 * A Gauge Chart widget for Android
 */
public class GaugeChart extends View {

    private DisplayMetrics mMetrics;

    private float mDiameter;
    private Point mOrigin;
    private float mAngle; // in degrees
    private int mMaxValue;
    private String mText;
    private Paint mTextPaint;

    private Map<Segment, Path> mSegmentsPaths;
    private Map<Arrow, Path> mArrowsPaths;
    private RectF mBounds;

    public GaugeChart(Context context, float angle) {
        super(context);

        mMetrics = context.getResources().getDisplayMetrics();
        mAngle = angle;

        // Default values

        if (mText == null) {
            mText = "";
        }

        /* Initialization */
        mOrigin = new Point(0, 0);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public GaugeChart(Context context, AttributeSet attributes) {
        super(context, attributes);

        mMetrics = context.getResources().getDisplayMetrics();

        // Get the attributes
        TypedArray array = context.getTheme().obtainStyledAttributes(attributes, R.styleable.GaugeChart, 0, 0);
        try {

            // Take an angle between 0 and 360 degrees
            mAngle = Math.min(array.getInt(R.styleable.GaugeChart_angle, -1), 360);
            mText = array.getString(R.styleable.GaugeChart_text);
        } finally {

            array.recycle();
        }

        // Default values
        if (mAngle < 0) {
            mAngle = 180;
        }

        if (mText == null) {
            mText = "";
        }

        /* Initialization */
        mOrigin = new Point(0, 0);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(GaugeChartData data) {

        mMaxValue = data.mMaxValue;

        if (data.mText != null) {
            mText = data.mText;
        }

        // Convert pixels size to dp
        mTextPaint.setTextSize(dpToPx(data.mTextSize));

        // Default black segment if none is present
        mSegmentsPaths = data.mSegments;
        if (data.mSegments.isEmpty()) {
            mSegmentsPaths.put(new Segment(0, mMaxValue, 0xff000000, 0.8f), new Path());
        }

        mArrowsPaths = data.mArrows;

        // Compute the size and the paths
        computeGeometry(getWidth(), getHeight());
        computePaths();

        // Call onDraw
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw mSegments
        if (mSegmentsPaths != null) {
            for (Map.Entry<Segment, Path> entry : mSegmentsPaths.entrySet()) {
                canvas.drawPath(entry.getValue(), entry.getKey().paint);
            }
        }

        // Draw mArrows
        if (mArrowsPaths != null) {
            for (Map.Entry<Arrow, Path> entry : mArrowsPaths.entrySet()) {
                canvas.drawPath(entry.getValue(), entry.getKey().paint);
            }
        }

        // Draw the text
        canvas.drawText(mText, mOrigin.x, mOrigin.y + mDiameter / 2, mTextPaint);
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {

        computeGeometry(width, height);

        // Compute the new paths related to the new dimension
        computePaths();
    }

    private void computeGeometry(int width, int height) {
        float xPadding = getPaddingLeft() + getPaddingRight();
        float yPadding = getPaddingTop() + getPaddingBottom();

        float mWidth = width - xPadding;
        float mHeight = height - yPadding;

        mDiameter = Math.min(mWidth, mHeight);
        mOrigin.set(Math.round(getPaddingLeft() + mWidth / 2), Math.round(getPaddingTop() + mHeight / 2));

        mBounds = new RectF(0, 0, mDiameter, mDiameter);
        mBounds.offsetTo(mOrigin.x - mDiameter / 2, mOrigin.y - mDiameter / 2);
    }

    private void computePaths() {
        if (mSegmentsPaths == null || mArrowsPaths == null) {
            return;
        }

        float offset = 90 + (360 - mAngle) / 2;
        // Compute the paths of the mSegments
        for (Map.Entry<Segment, Path> entry : mSegmentsPaths.entrySet()) {
            entry.getKey().compute(this, entry.getValue(), offset);
        }

        // Compute the paths of the mArrows
        for (Map.Entry<Arrow, Path> entry : mArrowsPaths.entrySet()) {
            entry.getKey().compute(this, entry.getValue());
        }
    }

    private float dpToPx(float dp) {

        return mMetrics.scaledDensity * dp;
    }

    public static class Arrow {

        private int value;
        private float baseLength;
        private float height;
        private float innerRadius;
        private Paint paint;

        public Arrow() {
            baseLength = 10;
            height = 1.0f;
            innerRadius = 0.2f;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0xff000000);
        }

        public void compute(GaugeChart chart, Path path) {

            float offsetBottom = innerRadius * chart.mDiameter / 2;
            float length = (chart.mDiameter / 2) * height - offsetBottom;

            path.setFillType(Path.FillType.EVEN_ODD);
            path.reset();
            path.moveTo(chart.mOrigin.x - chart.dpToPx(baseLength), chart.mOrigin.y + offsetBottom);
            path.rLineTo(chart.dpToPx(baseLength) * 2, 0);
            path.rLineTo(-chart.dpToPx(baseLength), length);
            path.close();

            // Rotate the arrow related to the value
            float angle = (360 - chart.mAngle) / 2 + (Math.min(value, chart.mMaxValue) / (float) chart.mMaxValue) * chart.mAngle;

            Matrix matrix = new Matrix();
            matrix.setRotate(angle, chart.mOrigin.x, chart.mOrigin.y);
            path.transform(matrix);
        }

        public void setValue(int v) {

            value = v;
        }

        public void setBaseLength(float value) {
            if (value > 0) {
                baseLength = value;
            }
        }

        public void setHeight(float value) {
            if (value >= 0 && value <= 1.0) {
                height = value;
            }
        }

        public void setInnerRadius(float value) {
            if (value >= 0 && value <= 1.0) {
                innerRadius = value;
            }
        }

        public void setColor(int value) {
            paint.setColor(value);
        }
    }

    public static class Segment {

        private int start;
        private int end;
        private float radius;
        private Paint paint;

        public Segment(int startValue, int endValue, int color, float innerRadius) {
            start = startValue;
            end = endValue;
            radius = 1 - innerRadius;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
        }

        private void compute(GaugeChart chart, Path path, float offset) {

            float startAngle = (chart.mAngle / chart.mMaxValue) * start + offset;
            float endAngle = (chart.mAngle / chart.mMaxValue) * end + offset;
            float length = endAngle - startAngle;

            float innerOffset = chart.mDiameter * radius;
            RectF innerBounds = new RectF(chart.mBounds.left, chart.mBounds.top, chart.mBounds.right - innerOffset,
                    chart.mBounds.bottom - innerOffset);
            innerBounds.offset(innerOffset / 2, innerOffset / 2);

            path.setFillType(Path.FillType.EVEN_ODD);
            path.reset();
            path.arcTo(chart.mBounds, startAngle, length, true);
            path.arcTo(innerBounds, endAngle, -length, false);
            path.close();
        }
    }

    public static class GaugeChartData {

        private Map<Segment, Path> mSegments;
        private Map<Arrow, Path> mArrows;
        private int mMaxValue;
        private String mText;
        private float mTextSize;

        public GaugeChartData(int maxValue) {

            mMaxValue = maxValue;
            mTextSize = 14;
            mSegments = new HashMap<>();
            mArrows = new HashMap<>();
        }

        public void setText(String text) {
            mText = text;
        }

        /**
         * Set the text size
         * @param size In pixels
         */
        public void setTextSize(float size) {
            mTextSize = size;
        }

        public void addSegments(Collection<Segment> segments) {
            for (Segment segment : segments) {
                addSegment(segment);
            }
        }

        public void addSegment(Segment segment) {
            mSegments.put(segment, new Path());
        }

        public void addArrows(Collection<Arrow> arrows) {
            for (Arrow arrow : arrows) {
                addArrow(arrow);
            }
        }

        public void addArrow(Arrow arrow) {
            mArrows.put(arrow, new Path());
        }
    }
}
