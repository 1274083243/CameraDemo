package ike.com.camerademo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import ike.com.camerademo.R;

/**
作者：ike
时间：2017/3/20 14:09
功能描述：一个正方形的
**/

public class RectView extends View{
    private String Tag="RectView";
    private Paint mPaint;
    public RectView(Context context) {
        this(context,null);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setStyle(Paint.Style.STROKE);
    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawCircle(getWidth()/2,getHeight()/2,60,mPaint);
        canvas.drawRect(0,0,getWidth(),getHeight(),mPaint);
    }
}
