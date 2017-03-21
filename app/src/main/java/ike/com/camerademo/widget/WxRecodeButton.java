package ike.com.camerademo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import ike.com.camerademo.R;

/**
 * 作者：ike
 * 时间：2017/3/21 15:39
 * 功能描述：视频录制按钮，没有图自己画
 **/
public class WxRecodeButton extends View {
    private String Tag="WxRecodeButton";
    private ValueAnimator mAnimator;
    private ValueAnimator progressAnimator;//进度动画
    private Paint mProgressPaint;//录制进度画笔
    private Paint mBgPaint;//背景图画笔
    private Paint textPaint;//文本画笔
    private int height;//控件的高
    private int width;//控件的宽
    private int DEFAULT_TIME = 300;
    private int STROKE_WIDTH=10;//进度条宽度
    private boolean canDrawProgress;//是否可以绘制进度条
    private RectF mRect;//进度条所在的绘制矩形
    private int current_progress;//当前进度
    private float progress;

    public WxRecodeButton(Context context) {
        this(context, null);
    }

    public WxRecodeButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WxRecodeButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化参数
     *
     * @param context
     */
    private void init(Context context) {
        initPaint();
        initAnimator();
    }

    /**
     * 初始化动画参数
     */
    private void initAnimator() {
        mAnimator=new ValueAnimator();
        mAnimator.setDuration(DEFAULT_TIME);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                setScaleY(animatedValue);
                setScaleX(animatedValue);
            }
        });
        progressAnimator=new ValueAnimator();
        progressAnimator.setDuration(3000);
        progressAnimator.setIntValues(1,100);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current_progress= (int) animation.getAnimatedValue();
                progress = current_progress*1.0f/100*360;
                invalidate();
            }
        });
    }

    /**
     * 放大动画
     */
    private void bigAnimator(){
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                canDrawProgress=true;
                progressAnimator.start();
                invalidate();
            }
        });
        mAnimator.setFloatValues(1.0f,1.2f);
        mAnimator.start();
    }

    /**
     * 恢复动画
     */
    private void resumeAnimator(){
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                canDrawProgress=false;
               invalidate();
            }
        });
        mAnimator.setFloatValues(1.2f,1.0f);
        mAnimator.start();
    }



    /**
     * 初始化画笔
     */
    private void initPaint() {
        //录制进度画笔
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(getResources().getColor(R.color.colorAccent));
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(STROKE_WIDTH);
        mProgressPaint.setAntiAlias(true);
        //背景图画笔
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setAntiAlias(true);
        //文本画笔
        textPaint=new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(19);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景圆

        canvas.drawCircle(width / 2, height / 2, width / 4, mBgPaint);
        if (canDrawProgress){

            //绘制进度条T
            canvas.drawArc(mRect,-90,progress,false,mProgressPaint);
            //canvas.drawCircle(width / 2, height / 2, width / 4, mProgressPaint);
            String text=current_progress+"%";
            Rect textRect=new Rect();
            textPaint.getTextBounds(text,0,text.length(),textRect);
            canvas.drawText(text,width / 2-textRect.width()/2,height / 2+textRect.height()/2,textPaint);
        }


    }

    /**
     * 获取控件的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mRect=new RectF(width / 2-width / 4+STROKE_WIDTH/2,height / 2-width / 4+STROKE_WIDTH/2,height / 2+width / 4-STROKE_WIDTH/2,height / 2+width / 4-STROKE_WIDTH/2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //执行放大动画
                if (!mAnimator.isRunning()){
                    bigAnimator();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                resumeAnimator();
                break;
        }
        return true;
    }


}
