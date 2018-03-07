package com.winsion.dispatch.media.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.view.TitleView;
import com.winsion.dispatch.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.winsion.component.basic.constants.Intents.Media.MEDIA_FILE;

/**
 * Created by wyl on 2016/8/27.
 * TODO 动态权限-相机
 */
public class TakePhotoActivity extends BaseActivity {
    private SurfaceView svPreview;
    private ImageView ivButton;
    private LinearLayout llButton;
    private TitleView tvTitle;

    private SurfaceHolder holder;
    private Camera mCamera;
    private File mFile;
    private boolean isPermissionDenied;

    @Override
    protected int setContentView() {
        return R.layout.activity_take_photo;
    }

    @Override
    protected void start() {
        initView();
        initData();
        initListener();
    }

    private void initView() {
        svPreview = findViewById(R.id.sv_preview);
        ivButton = findViewById(R.id.iv_shutter);
        llButton = findViewById(R.id.ll_button);
        tvTitle = findViewById(R.id.tv_title);
    }

    private void initData() {
        svPreview.setKeepScreenOn(true);
        svPreview.setFocusable(true);
        mFile = (File) getIntent().getSerializableExtra(MEDIA_FILE);
        holder = svPreview.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isPermissionDenied = false;
                try {
                    initPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (!isPermissionDenied) {
                    releaseCamera();
                }
            }
        });
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> {
            deleteFile();
            finish();
        });
        addOnClickListeners(R.id.iv_shutter, R.id.btn_confirm, R.id.btn_cancel);
    }

    /**
     * 初始化预览
     */
    private void initPreview() throws IOException {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters params = mCamera.getParameters();

        List<Camera.Size> sps = params.getSupportedPictureSizes();
        if (sps != null && sps.size() != 0) {
            Camera.Size size = sps.get(sps.size() / 2);
            params.setPictureSize(size.width, size.height);
        }

        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        mCamera.setPreviewDisplay(holder);
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        mCamera.startPreview();
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix m = new Matrix();
                m.setRotate(90, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                File file = mFile.getParentFile();
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    if (!mkdirs) {
                        return;
                    }
                }
                FileOutputStream fos = new FileOutputStream(mFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                bm.recycle();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(mFile);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                } else {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                }
                ivButton.setVisibility(View.GONE);
                llButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 释放相机
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_shutter:
                mCamera.takePicture(null, null, mPictureCallback);
                ivButton.setEnabled(false);
                break;
            case R.id.btn_confirm:
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case R.id.btn_cancel:
                ivButton.setEnabled(true);
                try {
                    if (mFile.exists()) {
                        mFile.delete();
                    }
                    ivButton.setVisibility(View.VISIBLE);
                    llButton.setVisibility(View.GONE);
                    mCamera.reconnect();
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void deleteFile() {
        if (mFile.exists()) {
            mFile.delete();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            deleteFile();
        }
        return super.onKeyDown(keyCode, event);
    }
}
