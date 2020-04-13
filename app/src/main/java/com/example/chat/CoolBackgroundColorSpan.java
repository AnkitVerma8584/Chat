package com.example.chat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

class CoolBackgroundColorSpan extends ReplacementSpan {

    private final int mBackgroundColor;
    private final int mTextColor;
    private final float mCornerRadius;
    private final float mPaddingStart;
    private final float mPaddingEnd;
    private final float mMarginStart;

    public CoolBackgroundColorSpan(int backgroundColor, int textColor, float cornerRadius, float paddingStart, float paddingEnd, float marginStart) {
        super();
        mBackgroundColor = backgroundColor;
        mTextColor = textColor;
        mCornerRadius = cornerRadius;
        mPaddingStart = paddingStart;
        mPaddingEnd = paddingEnd;
        mMarginStart = marginStart;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) (mPaddingStart + paint.measureText(text.subSequence(start, end).toString()) + mPaddingEnd);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        float width = paint.measureText(text.subSequence(start, end).toString());
        RectF rect = new RectF(x - mPaddingStart + mMarginStart  , top, x + width + mPaddingEnd + mMarginStart, bottom);
        paint.setColor(mBackgroundColor);
        canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, paint);
        paint.setColor(mTextColor);
        canvas.drawText(text, start, end, x + mMarginStart, y, paint);
    }
}