package com.ata.gogreenowner.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ata.gogreenowner.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StepView extends View {
    private static final int START_STEP = 1;

    private final List<String> mSteps = new ArrayList<>();
    private int mCurrentStep = START_STEP;
    private int mCircleColor = getResources().getColor(R.color.svCircleColor);
    private int mTextColor = getResources().getColor(R.color.svTextColor);
    private int mSelectedColor = getResources().getColor(R.color.svSelectedColor);
    private int mCancelColor = getResources().getColor(R.color.svCancelColor);
    private int selectedTextColor = getResources().getColor(R.color.colorAccent);
    private int mFillRadius = getResources().getDimensionPixelSize(R.dimen.svFillRadius);
    private int mStrokeWidth = getResources().getDimensionPixelSize(R.dimen.svStrokeWidth);
    private int mLineWidth = getResources().getDimensionPixelSize(R.dimen.svLineWidth);
    private int mDrawablePadding = getResources().getDimensionPixelSize(R.dimen.svDrawablePadding);
    private float stepNumberTextSize = getResources().getDimensionPixelSize(R.dimen.sv_stepNumberTextSize);
    private boolean cancelled = false;
    final int textSize = getResources().getDimensionPixelSize(R.dimen.svTextSize);

    private Paint mPaint =new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    public StepView(Context context) {
        super(context,null);

    }

    public StepView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.setBackgroundColor(Color.TRANSPARENT);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        cancel(false);
    }

    public void setSteps(List<String> steps) {
        mSteps.clear();
        if (steps != null) {
            mSteps.addAll(steps);
        }
        selectedStep(START_STEP);
    }

    public void selectedStep(int step) {
        final int selected = step < START_STEP ?
                START_STEP : (step > mSteps.size() ? mSteps.size() : step);
        mCurrentStep = selected;
        invalidate();
    }

    public void cancel(boolean state){
        this.cancelled = state;
        String[] steps;
        if(!state){
            steps = new String[]{"Requested", "Go Green Agent\nAllocated", "Garbage\nCollected", "Money\nGiven"};
        }else {
            steps = new String[]{"Requested", "Go Green Agent\nAllocated", "Garbage\nCollected", "Money\nGiven", "Cancelled"};
        }
        setSteps(Arrays.asList(steps));
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public int getStepCount() {
        return mSteps.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            final int fontHeight = (int) Math.ceil(mPaint.descent() - mPaint.ascent());
            height = getPaddingTop() + getPaddingBottom() + (mFillRadius + mStrokeWidth) * 2
                    + mDrawablePadding + fontHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int stepSize = mSteps.size();
        if (stepSize == 0) {
            return;
        }
        final int width = getWidth();

        final float ascent = mPaint.ascent();
        final float descent = mPaint.descent();
        final int fontHeight = (int) Math.ceil(descent - ascent);
        final int halfFontHeightOffset = -(int)(ascent + descent) / 2;
        final int bigRadius = mFillRadius + mStrokeWidth;
        final int startCircleY = getPaddingTop() + bigRadius;
        final int childWidth = width / stepSize;
        for (int i = 1; i <= stepSize; i++) {
            drawableStep(canvas, i, halfFontHeightOffset, fontHeight, bigRadius,
                    childWidth * i - childWidth / 2, startCircleY);
        }
        final int halfLineLength = childWidth / 2 - bigRadius;
        for (int i = 1; i < stepSize; i++) {
            final int lineCenterX = childWidth * i;
            drawableLine(canvas,i, lineCenterX - halfLineLength,
                    lineCenterX + halfLineLength, startCircleY);
        }
    }

    private void drawableStep(Canvas canvas, int step, int halfFontHeightOffset, int fontHeight,
                              int bigRadius, int circleCenterX, int circleCenterY) {
        final String text = mSteps.get(step - 1);
        if(!cancelled) {
            final boolean isSelected = step <= mCurrentStep;
            if (isSelected) {
                mPaint.setStrokeWidth(mStrokeWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius + mStrokeWidth / 2, mPaint);

                mPaint.setColor(mSelectedColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius, mPaint);

                mPaint.setFakeBoldText(true);
                mPaint.setColor(Color.WHITE);
                drawCheckMark(canvas, circleCenterX, circleCenterY);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(circleCenterX, circleCenterY, bigRadius, mPaint);
                mPaint.setFakeBoldText(true);
                mPaint.setColor(Color.WHITE);
                String number = String.valueOf(step);
                canvas.drawText(number, circleCenterX, circleCenterY + halfFontHeightOffset, mPaint);
            }

            mPaint.setFakeBoldText(false);
            mPaint.setColor(isSelected ? selectedTextColor : mTextColor);
            for (String line : text.split("\n")) {
                canvas.drawText(line, circleCenterX,
                        circleCenterY + bigRadius + mDrawablePadding + fontHeight / 2, mPaint);
                circleCenterY += -mPaint.ascent() + mPaint.descent();
            }
        }else{
            if(step == 1 || step ==5) {
                mPaint.setStrokeWidth(mStrokeWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius + mStrokeWidth / 2, mPaint);

                mPaint.setColor(mCancelColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius, mPaint);

                mPaint.setFakeBoldText(true);
                mPaint.setColor(Color.RED);
                drawCrossMark(canvas, circleCenterX, circleCenterY);

                mPaint.setFakeBoldText(false);
                mPaint.setColor(Color.RED);
                for (String line : text.split("\n")) {
                    canvas.drawText(line, circleCenterX,
                            circleCenterY + bigRadius + mDrawablePadding + fontHeight / 2, mPaint);
                    circleCenterY += -mPaint.ascent() + mPaint.descent();
                }
            }else{
                mPaint.setStrokeWidth(mStrokeWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius + mStrokeWidth / 2, mPaint);

                mPaint.setColor(mCancelColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(circleCenterX, circleCenterY, mFillRadius, mPaint);
                mPaint.setFakeBoldText(true);
                mPaint.setColor(Color.WHITE);
                String number = String.valueOf(step);
                canvas.drawText(number, circleCenterX, circleCenterY + halfFontHeightOffset, mPaint);
                mPaint.setFakeBoldText(false);
                mPaint.setColor(mTextColor);
                for (String line : text.split("\n")) {
                    mPaint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    canvas.drawText(line, circleCenterX,
                            circleCenterY + bigRadius + mDrawablePadding + fontHeight / 2, mPaint);
                    circleCenterY += -mPaint.ascent() + mPaint.descent();
                }
                mPaint.setFlags(0);
            }
        }
    }

    private void drawableLine(Canvas canvas, int step,int startX, int endX, int centerY) {
        if(!cancelled) {
            final boolean isSelected = step < mCurrentStep;
            if (isSelected) {
                mPaint.setColor(mSelectedColor);
            } else {
                mPaint.setColor(mCircleColor);
            }
            mPaint.setStrokeWidth(mLineWidth);
            if (step != mCurrentStep) {
                canvas.drawLine(startX, centerY, endX, centerY, mPaint);
            } else {
                mPaint.setColor(mSelectedColor);
                int midX = (endX + startX) / 2;
                canvas.drawLine(startX, centerY, midX, centerY, mPaint);

                mPaint.setColor(mCircleColor);
                canvas.drawLine(midX, centerY, endX, centerY, mPaint);
            }
        }else{
            mPaint.setColor(mCancelColor);
            mPaint.setStrokeWidth(mLineWidth);
            canvas.drawLine(startX, centerY, endX, centerY, mPaint);
        }
    }

    private void drawCheckMark(Canvas canvas, int circleCenterX, int circleCenterY) {
        mPaint.setColor(selectedTextColor);
        float width = stepNumberTextSize * 0.1f;
        mPaint.setStrokeWidth(width);
        Rect bounds = new Rect(
                (int) (circleCenterX - width * 4.5),
                (int) (circleCenterY - width * 3.5),
                (int) (circleCenterX + width * 4.5),
                (int) (circleCenterY + width * 3.5));
        canvas.drawLine(
                bounds.left + 0.5f * width,
                bounds.bottom - 3.25f * width,
                bounds.left + 3.25f * width,
                bounds.bottom - 0.75f * width, mPaint);
        canvas.drawLine(
                bounds.left + 2.75f * width,
                bounds.bottom - 0.75f * width,
                bounds.right - 0.375f * width,
                bounds.top + 0.75f * width, mPaint);
    }

    private void drawCrossMark(Canvas canvas, int circleCenterX, int circleCenterY) {
        mPaint.setColor(Color.WHITE);
        float width = stepNumberTextSize * 0.1f;
        mPaint.setStrokeWidth(width);
        Rect bounds = new Rect(
                (int) (circleCenterX - width * 4.5),
                (int) (circleCenterY - width * 3.5),
                (int) (circleCenterX + width * 4.5),
                (int) (circleCenterY + width * 3.5));
        canvas.drawLine(
                bounds.left,
                bounds.bottom,
                bounds.right,
                bounds.top , mPaint);

        canvas.drawLine(
                bounds.left,
                bounds.top,
                bounds.right,
                bounds.bottom , mPaint);
    }

}
