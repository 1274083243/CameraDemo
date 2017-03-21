package ike.com.camerademo;

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

import ike.com.camerademo.widget.RectView;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private SurfaceView sf_view;
    private Camera mCanmera;
    private SurfaceHolder mHolder;
    private static String Tag = "MainActivity";
    private Button btn_take_photo;
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
        iv_pic= (ImageView) findViewById(R.id.iv_pic);
        rect_view= (RectView) findViewById(R.id.rect_view);
        btn_take_photo.setOnClickListener(this);
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
                getOptimalPreviewSize(mCanmera, widthPixels, heightPixels,true);
        Camera.Size pictureSize = getOptimalPreviewSize(mCanmera, widthPixels, heightPixels, false);
        Log.e(Tag,"预览比例：宽："+optimalPreviewSize.width+",optimalPreviewSize.height:"+optimalPreviewSize.height+",比例:"+(optimalPreviewSize.width*1.0f/optimalPreviewSize.height));
        Log.e(Tag,"照片比例：宽："+pictureSize.width+",pictureSize.height:"+pictureSize.height+",比例:"+(pictureSize.width*1.0f/pictureSize.height));
        float peesent=(optimalPreviewSize.width*1.0f/optimalPreviewSize.height);
        p.setPreviewSize(optimalPreviewSize.width,optimalPreviewSize.height);
        p.setPictureSize(pictureSize.width,pictureSize.height);
        mCanmera.setParameters(p);
        setCameraDisplayOrientation(0,mCanmera);
        setAutoFocusMode(mCanmera);
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
            case R.id.btn_take_photo:
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
                break;
        }
    }

    /**生成拍照后图片的中间矩形的宽度和高度
     * @param w 屏幕上的矩形宽度，单位px
     * @param h 屏幕上的矩形高度，单位px
     * @return
     */
    private Point createCenterPictureRect(int w, int h){

        int wScreen = widthPixels;
        int hScreen = heightPixels;
        int wSavePicture = mCanmera.getParameters().getPictureSize().height; //因为图片旋转了，所以此处宽高换位
        int hSavePicture = mCanmera.getParameters().getPictureSize().width; //因为图片旋转了，所以此处宽高换位
        float wRate = (float)(wSavePicture) / (float)(wScreen);
        float hRate = (float)(hSavePicture) / (float)(hScreen);
        float rate = (wRate <= hRate) ? wRate : hRate;//也可以按照最小比率计算

        int wRectPicture = (int)( w * wRate);
        int hRectPicture = (int)( h * hRate);
        return new Point(wRectPicture, hRectPicture);

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
    //升序 按照高度
    public class CameraAscendSizeComparatorForHeight implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 查询最适合previewsize，或者是pictureSize
     * @param camera
     * @param width
     * @param height
     * @return
     */
    public static Camera.Size getOptimalPreviewSize(Camera camera, int width, int height,boolean isFindPreviewSize) {
        Camera.Size optimalSize = null;
        double minHeightDiff = Double.MAX_VALUE;
        double minWidthDiff = Double.MAX_VALUE;
        List<Camera.Size> sizes;
                if(isFindPreviewSize){
                    sizes = camera.getParameters().getSupportedPreviewSizes();
                }else {
                    sizes = camera.getParameters().getSupportedPictureSizes();
                }

        if (sizes == null) return null;
        //找到宽度差距最小的
        for(Camera.Size size:sizes){
            if (Math.abs(size.width - width) < minWidthDiff) {
                minWidthDiff = Math.abs(size.width - width);
            }
        }
        //在宽度差距最小的里面，找到高度差距最小的
        for(Camera.Size size:sizes){
            if(Math.abs(size.width - width) == minWidthDiff) {
                if(Math.abs(size.height - height) < minHeightDiff) {
                    optimalSize = size;
                    minHeightDiff = Math.abs(size.height - height);
                }
            }
        }
        return optimalSize;
    }
    /**
     * 查询最适合PictureSize
     * @param camera
     * @param width
     * @param height
     * @return
     */
    public static Camera.Size getOptimalPictureSize(Camera camera, int width, int height) {
        Camera.Size optimalSize = null;
        double minHeightDiff = Double.MAX_VALUE;
        double minWidthDiff = Double.MAX_VALUE;
        List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
        if (sizes == null) return null;
        //找到宽度差距最小的
        for(Camera.Size size:sizes){
            if (Math.abs(size.width - width) < minWidthDiff) {
                minWidthDiff = Math.abs(size.width - width);
            }
        }
        //在宽度差距最小的里面，找到高度差距最小的
        for(Camera.Size size:sizes){
            if(Math.abs(size.width - width) == minWidthDiff) {
                if(Math.abs(size.height - height) < minHeightDiff) {
                    optimalSize = size;
                    minHeightDiff = Math.abs(size.height - height);
                }
            }
        }
        return optimalSize;
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

    /**
     * 保证预览方向正确
     *
     *
     * @param cameraId
     * @param camera
     */
    public void setCameraDisplayOrientation(
                                            int cameraId, Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public  void setAutoFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

