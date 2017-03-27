package ike.com.camerademo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ike.com.camerademo.R;
import ike.com.camerademo.WxRecordbuttonLayout;

/**
作者：ike
时间：2017/3/21 15:08
功能描述：仿微信视频录制界面
**/

public class RecordVedioActivity extends AppCompatActivity  {
    private static String Tag="RecordVedioActivity";

    private WxRecordbuttonLayout record_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_vedio);
    }


}
