package com.changhong.ttfileplore.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.changhong.ttfileplore.R;

/**
 * Created by tangli on 2015/11/10。
 */
public class ColorCursorView extends ImageView {
    //选中颜色出现的方向
    private int mDirection = DIRECTION_LEFT;
    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_RIGHT = 1;
    private float mProgress;
    //默认未选中时的颜色
    private int mOriginColor = 0x00000000;
    //默认选中时的颜色
    private int mChangeColor = 0xff00aa00;

    public ColorCursorView(Context context, AttributeSet attrs) {
        super(context, attrs);



        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.ColorCursorView);
        mOriginColor = ta.getColor(
                R.styleable.ColorCursorView_origin_color, mOriginColor);
        mChangeColor = ta.getColor(
                R.styleable.ColorCursorView_change_color, mChangeColor);
        mProgress = ta.getFloat(R.styleable.ColorCursorView_progress, 0);

        mDirection = ta
                .getInt(R.styleable.ColorCursorView_direction, mDirection);

        ta.recycle();
    }

    public ColorCursorView(Context context) {
        super(context, null);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int r = (int) (mProgress * getWidth());

        if (mDirection == DIRECTION_LEFT) {
            drawChangeLeft(canvas, r);
            drawOriginLeft(canvas, r);
        } else if (mDirection == DIRECTION_RIGHT) {
            drawOriginRight(canvas, r);
            drawChangeRight(canvas, r);
        }
    }

    private void drawChangeLeft(Canvas canvas, int r) {
        drawText_h(canvas, mChangeColor, 0,
                r);
    }

    private void drawOriginLeft(Canvas canvas, int r) {
        drawText_h(canvas, mOriginColor, r, getWidth());
    }

    private void drawChangeRight(Canvas canvas, int r) {
        drawText_h(canvas, mChangeColor,
                (int) ( (1 - mProgress) * getWidth()), getWidth());
    }

    private void drawOriginRight(Canvas canvas, int r) {
        drawText_h(canvas, mOriginColor, 0,
                (int) (  (1 - mProgress) * getWidth()));
    }


    private void drawText_h(Canvas canvas, int color, int startX, int endX) {
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX, 0, endX, getHeight());// left, top,right, bottom
        canvas.drawColor(color);
        canvas.restore();
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }
    public void setDirection(int direction) {
        mDirection = direction;
    }
    private static final String KEY_STATE_PROGRESS = "key_progress";
    private static final String KEY_DEFAULT_STATE = "key_default_state";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putFloat(KEY_STATE_PROGRESS, mProgress);
        bundle.putParcelable(KEY_DEFAULT_STATE, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getFloat(KEY_STATE_PROGRESS);
            super.onRestoreInstanceState(bundle
                    .getParcelable(KEY_DEFAULT_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
