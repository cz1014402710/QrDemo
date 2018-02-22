package com.chenz.qrdemo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.lib_zxing.activity.CaptureActivity
import com.lib_zxing.activity.CodeUtils
import com.tbruyelle.rxpermissions.RxPermissions

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1001
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn: Button = findViewById(R.id.btn)
        btn.setOnClickListener(View.OnClickListener {
            checkPermission()
        })

    }

    private fun checkPermission() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (granted) { // Always true pre-M
                        val intent = Intent(this@MainActivity, CaptureActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE)
                    } else {
                        Toast.makeText(this@MainActivity, "请打开相机权限！", Toast.LENGTH_LONG).show()
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle = data.extras ?: return
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val result = bundle.getString(CodeUtils.RESULT_STRING)
                    Log.e(TAG, result)
                    Toast.makeText(this@MainActivity, result, Toast.LENGTH_LONG).show()

                }
            }
        }
    }
}