package com.anima.rxphotographdemo;

import android.Manifest;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.anima.rxphotograph.Photograph;
import com.anima.rxphotograph.RxPhotograph;
import com.anima.rxphotographdemo.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            requestPhoto();
                        } else {
                            Toast.makeText(MainActivity.this, "你取消了文件读写权限。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void requestPhoto() {
        new RxPhotograph(this).request().subscribe(new rx.functions.Action1<Photograph>() {
            @Override
            public void call(Photograph photograph) {
                ImageView imageView = findViewById(R.id.mainAct_imageView);
                imageView.setImageBitmap(photograph.getBitmap());
            }
        });
    }
}
