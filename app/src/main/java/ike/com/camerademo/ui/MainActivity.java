package ike.com.camerademo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import ike.com.camerademo.R;
import ike.com.camerademo.utils.CameraUtils;
import ike.com.camerademo.widget.RectView;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private SurfaceView sf_view;
    private Camera mCanmera;
    private SurfaceHolder mHolder;
    private static String Tag = "MainActivity";
    private Button btn_take_photo,btn_record_video;
    private ImageView iv_pic;
    private RectView rect_view;
    private int widthPixels;
    private int heightPixels;
    private int picHeight;
    private FrameLayout.LayoutParams params;
    private FrameLayout.LayoutParams rect_params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sf_view = (SurfaceView) findViewById(R.id.sf_view);
        btn_take_photo= (Button) findViewById(R.id.btn_take_photo);
        btn_record_video= (Button) findViewById(R.id.btn_record_video);
        iv_pic= (ImageView) findViewById(R.id.iv_pic);
        rect_view= (RectView) findViewById(R.id.rect_view);
        btn_take_photo.setOnClickListener(this);
        btn_record_video.setOnClickListener(this);
        mHolder = sf_view.getHolder();
        mHolder.addCallback(this);

    }

    private Camera.Parameters parameters;
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启摄像头进行预览界面的操作设定
        mCanmera = Camera.open(0);
        initCameraParam();
    }
    /**
     * 初始化相机参数
     */
    private void initCameraParam() {
        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = getResources().getDisplayMetrics().heightPixels;
        Camera.Parameters p = mCanmera.getParameters();
        Camera.Size optimalPreviewSize =
                //getPropSizeForHeight(p.getSupportedPreviewSizes(),800);
                CameraUtils.getOptimalSize(mCanmera, widthPixels, heightPixels,true);
        Camera.Size pictureSize = CameraUtils.getOptimalSize(mCanmera, widthPixels, heightPixels, false);
        Log.e(Tag,"预览比例：宽："+optimalPreviewSize.width+",optimalPreviewSize.height:"+optimalPreviewSize.height+",比例:"+(optimalPreviewSize.width*1.0f/optimalPreviewSize.height));
        Log.e(Tag,"照片比例：宽："+pictureSize.width+",pictureSize.height:"+pictureSize.height+",比例:"+(pictureSize.width*1.0f/pictureSize.height));
        float peesent=(optimalPreviewSize.width*1.0f/optimalPreviewSize.height);
        p.setPreviewSize(optimalPreviewSize.width,optimalPreviewSize.height);
        p.setPictureSize(pictureSize.width,pictureSize.height);
        mCanmera.setParameters(p);
        CameraUtils.setCameraDisplayOrientation(this,0,mCanmera);
        CameraUtils.setAutoFocusMode(mCanmera);
        try {
            mCanmera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        picHeight = (int) (widthPixels *pictureSize.width)/pictureSize.height;
        Log.e(Tag,"picHeight:"+picHeight);
        mCanmera.startPreview();
        params = new FrameLayout.LayoutParams((int) (widthPixels), (int) (widthPixels *peesent));
        params.gravity= Gravity.TOP;
        sf_view.setLayoutParams(params);
        rect_params = new FrameLayout.LayoutParams(rect_view.getWidth(), rect_view.getHeight());
        rect_params.topMargin=(params.height-rect_view.getHeight())/2;
        rect_params.gravity=Gravity.CENTER_HORIZONTAL;
        rect_view.setLayoutParams(rect_params);
//        Log.e(Tag,"sf_view的宽:"+params.width+"sf_view的高:"+params.height);
//        Log.e(Tag,"屏幕的宽:"+widthPixels+"屏幕的高:"+getResources().getDisplayMetrics().heightPixels);

    }

    /**
     * 完成相机指定区域的拍摄功能：由于在整个相机的使用过程中进行了相机预览界面的旋转，因此在相机拍摄成像的时候需要将生成的bitmap的宽高对调，
     * 因为在进行预览界面旋转的时候原来设置的宽高相互变化，宽变化成了高，高变化成了宽，如果是系统默认的设置（即未进行旋转操作），则不需要宽高对调
     * 特此在此记录这个坑
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_photo://拍摄照片
                mCanmera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.e(Tag,"控件比例:"+(params.width*1.0f/params.height));
                        Log.e(Tag,"生成比例:"+(widthPixels*1.0f/picHeight));
                        Log.e(Tag,"控件宽:"+params.width+"控件高:"+params.height);
                        Log.e(Tag,"top:"+ rect_view.getTop()+"图高:"+bitmap.getHeight()+"拆去狂德高:"+rect_view.getHeight());
                        Log.e(Tag,"left:"+ rect_view.getLeft()+"图宽:"+bitmap.getWidth()+"拆去狂德宽:"+rect_view.getWidth());
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, picHeight, widthPixels, true);
                        Log.e(Tag,"scaledBitmap高:"+scaledBitmap.getHeight()+",scaledBitmap宽:"+scaledBitmap.getWidth());
//                        Log.e(Tag,"x:"+(params.width-rect_params.width)/2+",y:"+(params.height-rect_params.height)/2);
//                        Bitmap clipBitmap = Bitmap.createBitmap(scaledBitmap,(params.width-rect_params.width)/2, (params.height-rect_params.height)/2, rect_view.getWidth(), rect_view.getHeight());
                        //比例计算
//                        float topPersent=rect_view.getTop()*1.0f/params.width;
//                        float leftPersent=rect_view.getLeft()*1.0f/params.height;
//                        float widthPersent=rect_view.getWidth()*1.0f/params.height;
//                        float heightPersent=rect_view.getHeight()*1.0f/params.width;
//                        Log.e(Tag,"topPersent:"+topPersent+",leftPersent:"+leftPersent+",widthPersent:"+widthPersent);
                        Bitmap clipBitmap = Bitmap.createBitmap(scaledBitmap,rect_view.getTop(),rect_view.getLeft(),rect_view.getHeight(),rect_view.getWidth());
                        iv_pic.setImageBitmap(setTakePicktrueOrientation(0,clipBitmap));
                        mCanmera.startPreview();
                    }
                });
                //拍摄小视频
            case R.id.btn_record_video:

                break;
        }
    }
    public Bitmap setTakePicktrueOrientation(int id, Bitmap bitmap) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
        bitmap = rotaingImageView(id, info.orientation, bitmap);
        return bitmap;
    }
    /**
     * 把相机拍照返回照片转正
     *
     * @param angle 旋转角度
     * @return bitmap 图片
     */
    public  Bitmap rotaingImageView(int id, int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //加入翻转 把相机拍照返回照片转正
        if (id == 1) {
            matrix.postScale(-1, 1);
        }
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCanmera.stopPreview();
        mCanmera.release();
        mCanmera = null;
    }



}

