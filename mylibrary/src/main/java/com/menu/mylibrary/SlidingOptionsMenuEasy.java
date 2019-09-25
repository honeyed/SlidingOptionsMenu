package com.menu.mylibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class SlidingOptionsMenuEasy extends View {

    private Paint whitePaint, textPaint2;
    private int height, width;
    private String[] title = {"你好", "他好", "我好", "都好", "都好", "都好"};
    private float boderWidth = 3;
    private float textSize = 50;
    private float touchPoint = 0;
    private ValueAnimator downAnimator;
    private int stroke_color = Color.RED;
    private int count = title.length * 2;

    public SlidingOptionsMenuEasy(Context context) {
        super(context);
        initPaint();
    }

    public SlidingOptionsMenuEasy(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SlidingOptionsMenuEasy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        touchPoint = width / count;
    }

    public void initPaint() {
        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setColor(stroke_color);
        whitePaint.setStrokeWidth(boderWidth);
        whitePaint.setStyle(Paint.Style.STROKE);


        textPaint2 = new Paint();
        textPaint2.setAntiAlias(true);
        textPaint2.setColor(stroke_color);
        textPaint2.setStrokeWidth(2);
        textPaint2.setTextSize(textSize);
        textPaint2.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 255, 255);

        canvas.drawRoundRect(new RectF(boderWidth / 2, boderWidth, width - boderWidth, height - boderWidth), width / 2, width / 2, whitePaint);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        int r = canvasWidth / 3;
        //绘制红色椭圆
        canvas.drawRoundRect(new RectF(touchPoint - width / count, boderWidth / 2, touchPoint + width / count, height - boderWidth / 2), width / 2, width / 2, textPaint2);
        //
        textPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
//        paint.setColor(0xFF66AAFF);
        for (int i = 0; i < title.length; i++) {
            float txtWidth = textPaint2.measureText(title[i].toString());
            float location = width / count * (i * 2 + 1) - txtWidth / 2;
            canvas.drawText(title[i], location, (height - 12) / 2 + 25, textPaint2);
        }
        //最后将画笔去除Xfermode
        textPaint2.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downPoint;
        float upPoint;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 只有单指按在屏幕上移动时，为拖动状态
                if (downAnimator != null && downAnimator.isRunning())
                    downAnimator.cancel();
                touchPoint = event.getX();
                if (touchPoint < width / title.length / 2) {
                    touchPoint = width / title.length / 2;
                }
                if (touchPoint > width - width / title.length / 2) {
                    touchPoint = width - width / title.length / 2;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开屏幕时将临时值还原
                upPoint = event.getX();
                if (upPoint < width / title.length / 2) {
                    upPoint = width / title.length / 2;
                }
                if (upPoint > width - width / title.length / 2) {
                    upPoint = width - width / title.length / 2;
                }
                int count = ((int) upPoint) / (width / title.length) * 2 + 1;
                upPoint = (width / title.length / 2) * count;
                if (downAnimator != null && downAnimator.isRunning())
                    downAnimator.cancel();
                if (title.length * 2 - 1 == count)
                    upPoint += 2;
                startDownAnimal(touchPoint, upPoint);
                break;
            case MotionEvent.ACTION_DOWN:
                downPoint = event.getX();
                if (downPoint < width / title.length / 2) {
                    downPoint = width / title.length / 2;
                }
                if (downPoint > width - width / title.length / 2) {
                    downPoint = width - width / title.length / 2;
                }
                startDownAnimal(touchPoint, downPoint);
                break;
            default:
                break;
        }
        return true;
    }


    public void startDownAnimal(float start, float end) {
        downAnimator = ValueAnimator.ofFloat(start, end);
        downAnimator.setDuration(100);//播放时长
        downAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                touchPoint = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        downAnimator.start();
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled
     * pixel" units.  This size is adjusted based on the current density and
     * user font size preference.
     *
     * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
     *
     * @param size The scaled pixel size.
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Set the default text size to a given unit and value. See {@link
     * TypedValue} for the possible dimension units.
     *
     * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(int unit, float size) {
        setTextSizeInternal(unit, size, true /* shouldRequestLayout */);
    }

    private void setTextSizeInternal(int unit, float size, boolean shouldRequestLayout) {
        Context c = getContext();
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }

        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()),
                shouldRequestLayout);
    }

    private void setRawTextSize(float size, boolean shouldRequestLayout) {
        if (size != textPaint2.getTextSize()) {
            textPaint2.setTextSize(size);
            invalidate();
        }
    }

}
