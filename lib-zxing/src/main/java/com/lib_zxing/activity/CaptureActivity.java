package com.lib_zxing.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib_zxing.ImageUtil;
import com.lib_zxing.R;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

/**
 * Initial the camera
 * <p>
 * 默认的二维码扫描Activity
 */
public class CaptureActivity extends AppCompatActivity {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private ImageView iv_back, iv_flash;
    private TextView tv_photo;
    private static final int REQUEST_IMAGE = 3;
    private boolean isFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();
        captureFragment.setCameraInitCallBack(new CaptureFragment.CameraInitCallBack() {
            @Override
            public void callBack(Exception e) {
                if (e == null) {

                } else {
                    Log.e("TAG", "callBack: ", e);
                }
            }
        });

        iv_flash = (ImageView) findViewById(R.id.iv_flash);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_photo = (TextView) findViewById(R.id.tv_photo);
        iv_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeUtils.isLightEnable(!isFlash);
                isFlash = !isFlash;
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });

        tv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到系统相册选择图片
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            Intent wrapperIntent = Intent.createChooser(intent, "选择二维码图片");
                            startActivityForResult(wrapperIntent, REQUEST_IMAGE);
                        } else {
                            Toast.makeText(CaptureActivity.this, "请打开相机权限！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            analyzeSuccess(mBitmap, result);
        }

        @Override
        public void onAnalyzeFailed() {
            analyzeFailed();
        }
    };

    private void analyzeSuccess(Bitmap mBitmap, String result) {
        Log.e(TAG, result);
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
        bundle.putString(CodeUtils.RESULT_STRING, result);
        resultIntent.putExtras(bundle);
        CaptureActivity.this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }

    private void analyzeFailed() {
        Log.e(TAG, "解析二维码失败");
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
        bundle.putString(CodeUtils.RESULT_STRING, "");
        resultIntent.putExtras(bundle);
        CaptureActivity.this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片

                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            analyzeSuccess(mBitmap, result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            analyzeFailed();
                        }
                    });

                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}