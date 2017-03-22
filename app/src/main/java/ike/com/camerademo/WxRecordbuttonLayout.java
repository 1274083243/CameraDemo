package ike.com.camerademo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ike.com.camerademo.widget.WxRecodeButton;

/**
作者：ike
时间：' 9:18
功能描述：仿微信发布按钮的动画布局
**/

public class WxRecordbuttonLayout extends RelativeLayout implements View.OnClickListener {
    private View rootView;
    private TextView btn_cancle;//取消按钮
    private TextView btn_finish;//完成按钮
    private WxRecodeButton btn_record;//录制按钮
    private ValueAnimator mAnimator;//平移动画
    private int DEFAULT_TIME=500;//默认动画执行时间
    public WxRecordbuttonLayout(Context context) {
        this(context,null);
    }

    public WxRecordbuttonLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WxRecordbuttonLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView=View.inflate(context,R.layout.wx_record_view_layout,this);
        initView();
        initAnimator();
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        mAnimator=new ValueAnimator();
        mAnimator.setDuration(DEFAULT_TIME);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value= (int) animation.getAnimatedValue();
                btn_cancle.setTranslationX(-value);
                btn_finish.setTranslationX(value);
            }
        });
    }

    /**
     * 展开动画
     */
    private void startExtendAnimation(){
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_record.setVisibility(GONE);
            }
        });
        mAnimator.setIntValues(0,getResources().getDisplayMetrics().widthPixels/3);
        mAnimator.start();
    }

    /**
     * 收起动画
     */
    private void startShrinkageAnimation(){
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_record.setVisibility(VISIBLE);
            }
        });
        mAnimator.setIntValues(getResources().getDisplayMetrics().widthPixels/3,0);
        mAnimator.start();
    }


    /**
     * 初始化试图控件
     */
    private void initView() {
        btn_cancle= (TextView) rootView.findViewById(R.id.btn_cancle);
        btn_finish= (TextView) rootView.findViewById(R.id.btn_finish);
        btn_record= (WxRecodeButton) rootView.findViewById(R.id.btn_record);
        //btn_record.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        btn_record.setRecordButtonTouchListener(new WxRecodeButton.RecordButtonTouchListener() {
            @Override
            public void onUp() {
                //TODO:开始平移动画
               startExtendAnimation();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //取消录制
            case  R.id.btn_cancle:
                startShrinkageAnimation();
                break;
            //录制完成
            case  R.id.btn_finish:
                break;
            //开始录制
            case  R.id.btn_record:
                break;
        }
    }

}
