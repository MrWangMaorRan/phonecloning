package com.peep.contractbak;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.peep.contractbak.utils.ScreenUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * 全局抽象类
 * */
public abstract class BaseActivity extends AppCompatActivity {

    public Handler uiHandler = new Handler();
    private ProgressDialog loadingDialog = null;  //初始化等待动画
    public static boolean ALLOWED_FLAG = false; //已经授权

    /**
     * 权限组
     */
    private static final String[] permissionsGroup1 = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.CAMERA,
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(ALLOWED_FLAG){
            return;
        }
        requestPermission1();
    }




    public void requestPermission1() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(permissionsGroup1)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        Log.d("tag",permission.name + "权限问题" + permission.granted) ;
                        if (permission.granted) {
                            ALLOWED_FLAG = true;
                        }else {
                            ALLOWED_FLAG = false;
                            Toast.makeText(BaseActivity.this,"没有权限，相关功能无法使用！", Toast.LENGTH_LONG).show();
                            // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.topActivity = this;
        ScreenUtils.initScreenUtils(this);
    }

    @Override
    public void finish() {
        super.finish();
        try {
            ScreenUtils.hideSoftKeyboard(this);
        } catch (Throwable t) {}

    }


    /**
     * 加载动画
     * */
    public void showLoadingAnim(){
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null == loadingDialog){
                    loadingDialog = new ProgressDialog(BaseActivity.this);
                }
                loadingDialog.setMessage("链接中...");
                loadingDialog.show();
            }
        },100L);

    }

    /**
     * 加载动画
     * */
    public void showLoadingAnim(String tipsTxt){
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null == loadingDialog){
                    loadingDialog = new ProgressDialog(BaseActivity.this);
                }
                loadingDialog.setMessage(tipsTxt);
                loadingDialog.show();
            }
        },100L);
    }

    /**
     * 移除动画
     * */
    public void removeLoadingAnim(){
        try{
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null == loadingDialog){
                    return;
                }
                loadingDialog.cancel();
            }
        },100L);}catch (Throwable r){}
    }
}
