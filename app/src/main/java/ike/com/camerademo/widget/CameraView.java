package ike.com.camerademo.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
作者：ike
时间：2017/3/17 10:26
功能描述：相机布局
**/

public class CameraView extends FrameLayout{
    private float mAspectRatio =9.0f/16;
    public CameraView(@NonNull Context context) {
        this(context,null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//
//        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        if(widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
//            heightSpecSize = (int)(widthSpecSize / mAspectRatio);
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize,
//                    MeasureSpec.EXACTLY);
//        } else if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
//            widthSpecSize = (int)(heightSpecSize * mAspectRatio);
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize,
//                    MeasureSpec.EXACTLY);
//        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
