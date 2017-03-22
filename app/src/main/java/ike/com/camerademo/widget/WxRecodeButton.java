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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import ike.com.camerademo.R;

/**
 * 作者：ike
 * 时间：2017/3/21 15:39
 * 功能描述：视频录制按钮，没有图自己画
 **/
public class WxRecodeButton extends View {
    private String Tag = "WxRecodeButton";
    private ValueAnimator mBigAnimator;
    private ValueAnimator mRestoreAnimator;
    private ValueAnimator progressAnimator;//进度动画
    private Paint mProgressPaint;//录制进度画笔
    private Paint mBgPaint;//背景图画笔
    private Paint textPaint;//文本画笔
    private int height;//控件的高
    private int width;//控件的宽
    private int DEFAULT_TIME = 300;
    private int STROKE_WIDTH = 10;//进度条宽度
    private boolean canDrawProgress;//是否可以绘制进度条
    private RectF mRect;//进度条所在的绘制矩形
    private float current_progress;//当前进度
    private float progress;
    private Rect textRect;//文本绘制矩形
    private int radios;//圆的半径

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
        mBigAnimator=creatValueAnimator();
        mBigAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canDrawProgress = true;
                progressAnimator.start();
                invalidate();
            }
        });
        mBigAnimator.setFloatValues(1.0f, 1.2f);

        mRestoreAnimator=creatValueAnimator();
        mRestoreAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canDrawProgress = false;
                setVisibility(GONE);
                if (listener != null) {
                    listener.onUp();
                }
            }
        });
        mRestoreAnimator.setFloatValues(1.2f, 1.0f);

        progressAnimator = new ValueAnimator();
        progressAnimator.setDuration(3000);
        progressAnimator.setFloatValues(1, 100);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current_progress = (float) animation.getAnimatedValue();
                progress = current_progress * 1.0f / 100 * 360;
                invalidate();
            }
        });
    }

    private ValueAnimator creatValueAnimator() {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(DEFAULT_TIME);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                setScaleY(animatedValue);
                setScaleX(animatedValue);
            }
        });
        return animator;
    }

    /**
     * 放大动画
     */
    private void bigAnimator() {
        mBigAnimator.start();
    }

    /**
     * 恢复动画
     */
    private void resumeAnimator() {
        mRestoreAnimator.start();
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
        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(19);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景圆
        canvas.drawCircle(width / 2, height / 2, width / 2, mBgPaint);
        if (canDrawProgress) {
            //绘制进度条T
            canvas.drawArc(mRect, -90, progress, false, mProgressPaint);
            String text = current_progress + "%";
            textRect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), textRect);
            canvas.drawText(text, width / 2 - textRect.width() / 2, height / 2 + textRect.height() / 2, textPaint);
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
        //进度条绘制区域
        int left = STROKE_WIDTH / 2;
        int top = STROKE_WIDTH / 2;
        int right = width - STROKE_WIDTH / 2;
        int boottom = height - STROKE_WIDTH / 2;
        mRect = new RectF(left, top, right, boottom);
        radios = width / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //执行放大动画

                bigAnimator();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                resumeAnimator();
                break;
        }
        return true;
    }

    /**
     * 该按钮的按下与松开的事件监听
     */
    public interface RecordButtonTouchListener {
//        /**
//         * 按钮按下
//         */
//        void onDown();

        /**
         * 按钮松开
         */
        void onUp();
    }

    private RecordButtonTouchListener listener;

    public void setRecordButtonTouchListener(RecordButtonTouchListener listener) {
        this.listener = listener;
    }


}
