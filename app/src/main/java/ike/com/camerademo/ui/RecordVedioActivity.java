package ike.com.camerademo.ui;

import android.hardware.Camera;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import ike.com.camerademo.R;
import ike.com.camerademo.utils.CameraUtils;

/**
作者：ike
时间：2017/3/21 15:08
功能描述：仿微信视频录制界面
**/

public class RecordVedioActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static String Tag="RecordVedioActivity";
    private SurfaceView sf_view;
    private SurfaceHolder mHolder;
    private Camera camera;
    private int screenW;
    private int screenH;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_vedio);
        screenH=getResources().getDisplayMetrics().heightPixels;
        screenW=getResources().getDisplayMetrics().widthPixels;
        sf_view= (SurfaceView) findViewById(R.id.sf_view);
        mHolder=sf_view.getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera=Camera.open(0);
        Camera.Parameters p = camera.getParameters();

        Camera.Size optimalOPreviewSize = CameraUtils.getOptimalSize(camera, screenH, screenW, true);

        p.setPreviewSize(optimalOPreviewSize.width,optimalOPreviewSize.height);
        camera.setParameters(p);
        CameraUtils.setCameraDisplayOrientation(this,0,camera);
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
        camera.stopPreview();
        camera.release();
        camera=null;
    }
}
