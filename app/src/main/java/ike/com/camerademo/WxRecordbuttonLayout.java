package ike.com.camerademo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;

import ike.com.camerademo.utils.CameraUtils;
import ike.com.camerademo.widget.WxRecodeButton;

/**
 * 作者：ike
 * 时间：' 9:18
 * 功能描述：仿微信发布按钮的动画布局
 **/

public class WxRecordbuttonLayout extends RelativeLayout implements View.OnClickListener, SurfaceHolder.Callback {
    private String Tag = "WxRecordbuttonLayout";
    private View rootView;
    private TextView btn_cancle;//取消按钮
    private TextView btn_finish;//完成按钮
    private WxRecodeButton btn_record;//录制按钮

    private VideoView sf_view;

    private ValueAnimator mAnimator;//平移动画
    private int DEFAULT_TIME = 500;//默认动画执行时间
    private MediaRecorder mMediaRecorder;//音视频录制器
    private String saveVideoPath = "";
    private String videoFileName = "";
    private SurfaceHolder mHolder;
    private Camera camera;
    private int screenW;
    private int screenH;
    private Camera.Size oPreviewSize;
    private Activity activity;
    private int RECORD_TIME=5*1000;//录像事件
    public WxRecordbuttonLayout(Context context) {
        this(context, null);
    }

    public WxRecordbuttonLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WxRecordbuttonLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof Activity){
            activity= (Activity) context;
        }
        rootView = View.inflate(context, R.layout.wx_record_view_layout, this);
        screenH=getResources().getDisplayMetrics().heightPixels;
        screenW=getResources().getDisplayMetrics().widthPixels;
        initView();
        initAnimator();
    }
    /**
     * 初始化动画
     */
    private void initAnimator() {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(DEFAULT_TIME);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                btn_cancle.setTranslationX(-value);
                btn_finish.setTranslationX(value);
            }
        });
    }

    /**
     * 展开动画
     */
    private void startExtendAnimation() {
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_record.setVisibility(GONE);

            }
        });
        mAnimator.setIntValues(0, getResources().getDisplayMetrics().widthPixels / 3);
        mAnimator.start();
    }

    /**
     * 收起动画
     */
    private void startShrinkageAnimation() {
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_record.setVisibility(VISIBLE);
                startPreView();
            }
        });
        mAnimator.setIntValues(getResources().getDisplayMetrics().widthPixels / 3, 0);
        mAnimator.start();
    }


    /**
     * 初始化试图控件
     */
    private void initView() {
        btn_cancle = (TextView) rootView.findViewById(R.id.btn_cancle);
        btn_finish = (TextView) rootView.findViewById(R.id.btn_finish);
        btn_record = (WxRecodeButton) rootView.findViewById(R.id.btn_record);
        btn_finish.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        btn_record.setRecordButtonTouchListener(new WxRecodeButton.RecordButtonTouchListener() {
            @Override
            public void onTakePicUp() {
                Log.e(Tag,"点击了，拍照片吧");
            }

            @Override
            public void onRecordVedioUp() {
                Log.e(Tag,"手指抬起");
                stopRecord();
                startExtendAnimation();
            }

            @Override
            public void onRecordVedioDown() {
                Log.e(Tag,"开始录制吧");
                startRecord();
            }

            @Override
            public void onRecordVedioFinnish() {
                Log.e(Tag,"录制完成");
                stopRecord();
                startExtendAnimation();
            }


        });
        sf_view= (VideoView) findViewById(R.id.sf_view);
        mHolder=sf_view.getHolder();
        mHolder.addCallback(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //取消录制
            case R.id.btn_cancle:
                startShrinkageAnimation();
                break;
            //录制完成
            case R.id.btn_finish:
                break;
            //开始录制
            case R.id.btn_record:

                startRecord();
                break;
        }
    }

    /**
     * 开始录制视频
     */
    private void startRecord() {
        if (camera == null) {
            return;
        }
        camera.unlock();
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        //初始化mMediaRecorder的参数
        mMediaRecorder.reset();
        //设置数据采集来源
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音视频输出类型
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置音视频编码参数
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置预览图的大小(视频的分辨率)
        mMediaRecorder.setVideoSize(oPreviewSize.width, oPreviewSize.height);
        //调整摄像头角度
        mMediaRecorder.setOrientationHint(90);
        //设置最大录制时间
        mMediaRecorder.setMaxDuration(btn_record.getRecordTime());
        mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        videoFileName = "video_" + System.currentTimeMillis() + ".mp4";
        if (saveVideoPath.equals("")) {
            saveVideoPath = Environment.getExternalStorageDirectory().getPath();
        }
        mMediaRecorder.setOutputFile(saveVideoPath + "/" + videoFileName);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(Tag, "mMediaRecorder:" + saveVideoPath + "/" + videoFileName);
    }

    /**
     * 停止录制视频
     */
    private void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mMediaRecorder.release();
            mMediaRecorder = null;
            releaseCamera();
            FrameLayout.LayoutParams layoutParams=
                    new FrameLayout.LayoutParams(screenW,screenH);
            sf_view.setLayoutParams(layoutParams);
            final String fileName = saveVideoPath + "/" + videoFileName;
            sf_view.setVideoPath(fileName);
            sf_view.start();
            sf_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                 //   isPlay = true;
                    mp.start();
                    mp.setLooping(true);
                }
            });
            sf_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            sf_view.setVideoPath(fileName);
                            sf_view.start();
                        }
                    });
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreView();
    }

    /**
     * 开始预览
     */
    private void startPreView() {
        //停止视频播放
        if (sf_view.isPlaying()){
          sf_view.stopPlayback();
        }
        camera= getCamera(0);
        Camera.Parameters p = camera.getParameters();
        oPreviewSize = CameraUtils.getOptimalSize(camera, screenH, screenW, true);
        p.setPreviewSize(oPreviewSize.width, oPreviewSize.height);
        camera.setParameters(p);
        CameraUtils.setCameraDisplayOrientation(activity,0,camera);
        CameraUtils.setAutoFocusMode(camera);
        try {
            camera.setPreviewDisplay(mHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    /**
     * 获取Camera
     * @param position
     * @return
     */
    private Camera getCamera(int position) {
        Camera camera;
        try {
            camera = Camera.open(position);
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }
}
