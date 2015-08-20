package com.obviz.review.views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaylor on 08/20/2015.
 * A Gauge Chart widget for Android
 */
public class GaugeChart extends View {

    private float mDiameter;
    private Point mOrigin;
    private float mAngle; // in degrees
    private int mMaxValue;
    private String mText;
    private Paint mTextPaint;

    private Map<Segment, Path> mSegmentsPaths;
    private Map<Arrow, Path> mArrowsPaths;
    private RectF mBounds;

    public GaugeChart(Context context, AttributeSet attributes) {
        super(context, attributes);

        // TODO : attributes
        mAngle = 240;

        /* Initialization */
        mSegmentsPaths = new HashMap<>();
        mArrowsPaths = new HashMap<>();
        mOrigin = new Point(0, 0);
        mText = "";

        mTextPaint = new Paint();
        mTextPaint.setTextSize(40);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(GaugeChartData data) {

        mMaxValue = data.mMaxValue;

        if (data.mText != null) {
            mText = data.mText;
        }

        // Default black segment if none is present
        mSegmentsPaths.clear();
        if (data.segments == null || data.segments.isEmpty()) {
            mSegmentsPaths.put(new Segment(0, mMaxValue, 0xff000000, 0.8f), new Path());
        } else {

            for (Segment segment : data.segments) {
                mSegmentsPaths.put(segment, new Path());
            }
        }

        mArrowsPaths.clear();
        if (data.arrows != null) {

            for (Arrow arrow : data.arrows) {
                mArrowsPaths.put(arrow, new Path());
            }
        }

        // Compute the size and the paths
        computeGeometry(getWidth(), getHeight());
        computePaths();

        // Call onDraw
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw segments
        for (Map.Entry<Segment, Path> entry : mSegmentsPaths.entrySet()) {
            canvas.drawPath(entry.getValue(), entry.getKey().paint);
        }

        // Draw arrows
        for (Map.Entry<Arrow, Path> entry : mArrowsPaths.entrySet()) {
            canvas.drawPath(entry.getValue(), entry.getKey().paint);
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

        float offset = 90 + (360 - mAngle) / 2;
        // Compute the paths of the segments
        for (Map.Entry<Segment, Path> entry : mSegmentsPaths.entrySet()) {
            entry.getKey().compute(this, entry.getValue(), offset);
        }

        // Compute the paths of the arrows
        for (Map.Entry<Arrow, Path> entry : mArrowsPaths.entrySet()) {
            entry.getKey().compute(this, entry.getValue());
        }
    }

    public static class Arrow {

        private int value;
        private float baseLength;
        private float height;
        private float innerRadius;
        private Paint paint;

        public Arrow(float h, float i, float b, int color) {
            baseLength = b / 2;
            height = h;
            innerRadius = i;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);

        }

        public void compute(GaugeChart chart, Path path) {

            float offsetBottom = innerRadius * chart.mDiameter / 2;
            float length = (chart.mDiameter / 2) * height - offsetBottom;

            path.setFillType(Path.FillType.EVEN_ODD);
            path.reset();
            path.moveTo(chart.mOrigin.x - baseLength, chart.mOrigin.y + offsetBottom);
            path.rLineTo(baseLength * 2, 0);
            path.rLineTo(-baseLength, length);
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

        private List<Segment> segments;
        private List<Arrow> arrows;
        private int mMaxValue;
        private String mText;

        public GaugeChartData(int maxValue) {

            mMaxValue = maxValue;
        }

        public void setText(String text) {
            mText = text;
        }

        public void setSegments(List<Segment> list) {
            segments = list;
        }

        public void addArrows(List<Arrow> list) {
            arrows = list;
        }
    }
}
