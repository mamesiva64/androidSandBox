package com.example.drawsample;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/*
*
* https://developer.android.com/reference/android/hardware/Camera
* */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int REQUEST_CODE = 1;
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.CAMERA
        }, REQUEST_CODE);

        // カメラ数を取得
        /*
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                int defaultCameraId = i;
            }
        }*/

        Camera mainCamera = Camera.open();
        mainCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;

                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

                byte[] bytes = out.toByteArray();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }
        });
        mainCamera.startPreview();


        Button button = findViewById(R.id.button);
        button.setOnClickListener(onButton);
    }

    private SurfaceView mySurfaceView;
    private Camera myCamera; //hardware

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //SurfaceView
        mySurfaceView = (SurfaceView)findViewById(R.id.mySurfaceVIew);

        //SurfaceHolder(SVの制御に使うInterface）
        SurfaceHolder holder = mySurfaceView.getHolder();
        //コールバックを設定
        holder.addCallback(callback);

    }
*/


    private View.OnClickListener onButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bitmap bitmap = Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint p = new Paint();

            p.setColor(0xffff00ff);
            canvas.drawRect(0, 0, 32, 64, p);
            p.setColor(0xffffff00);
            canvas.drawRect(32, 0, 64, 64, p);

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        }
    };

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            //CameraOpen
            myCamera = Camera.open();

            //出力をSurfaceViewに設定
            try{
                myCamera.setPreviewDisplay(surfaceHolder);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

            //プレビュースタート（Changedは最初にも1度は呼ばれる）
            myCamera.startPreview();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            //片付け
            myCamera.release();
            myCamera = null;
        }
    };

}
