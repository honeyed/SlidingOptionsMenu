package com.menu.mylibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlidingOptionsMenu extends View {

    private Paint whitePaint, textPaint2;
    private int height, width;
    private CharSequence[] title = {};
    private float boderWidth = 3;
    private float textSize = 50;
    private float touchPoint = 0;
    private ValueAnimator downAnimator;
    private int stroke_color;
    private int count;
    private OnItemSelectListener onItemSelectListener;
    private int currentItemPostion = 0;

    public SlidingOptionsMenu(Context context) {
        super(context);
        initPaint();
    }

    public SlidingOptionsMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideButton);
        stroke_color = ta.getColor(R.styleable.SlideButton_stroke_color, Color.RED);//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        boderWidth = ta.getFloat(R.styleable.SlideButton_stroke_width, 3);//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        textSize = ta.getDimension(R.styleable.SlideButton_text_size, 50);//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        title = ta.getString(R.styleable.SlideButton_indicatorText).split(",");//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        count = title.length * 2;
        ta.recycle();
        initPaint();
    }

    public SlidingOptionsMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideButton);
        stroke_color = ta.getColor(R.styleable.SlideButton_stroke_color, Color.RED);//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        boderWidth = ta.getFloat(R.styleable.SlideButton_stroke_width, 3);//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        textSize = ta.getDimension(R.styleable.SlideButton_text_size, 50);//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        title = ta.getString(R.styleable.SlideButton_indicatorText).split(",");//第二个参数是设置的默认值，当你不设置这个属性时会使用这个值
        count = title.length * 2;
        ta.recycle();
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        touchPoint = width / count * ((currentItemPostion + 1) * 2 - 1);
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

//=========================================================================================================================
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
            canvas.drawText(title[i].toString(), location, (height - 12) / 2 + 25, textPaint2);
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
                if(downAnimator != null && downAnimator.isRunning())
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
                if(downAnimator != null && downAnimator.isRunning())
                    downAnimator.cancel();
                if (title.length * 2 - 1 == count)
                    upPoint += 2;
                startDownAnimal(touchPoint, upPoint);
                if (onItemSelectListener != null)
                    onItemSelectListener.onItemClick(count % 2 + 1, title[count % 2 + 1].toString());
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

    public void setCurrentItem(int item) {
        if (item + 1 == title.length) {
            throw new IndexOutOfBoundsException("默认标签坐标越界");
        }
        currentItemPostion = item;
        if (getMeasuredWidth() != 0) {
            touchPoint = getMeasuredWidth() / count * (item * 2 - 1);
            invalidate();
        } else {

        }
    }

    public OnItemSelectListener getOnItemSelectListener() {
        return onItemSelectListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }
}
