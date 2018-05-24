package com.anima.rxphotograph;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import rx.Subscriber;

/**
 * Created by jianjianhong on 2018/5/22.
 */

public class RxPhotographFragment extends Fragment {

    private static final String TAG = "RxPhotographFragment";
    public static final int ALBUM_REQUEST_CODE = 1001;
    public static final int CAMERA_REQUEST_CODE = 1002;
    private Subscriber<? super Photograph> subscriber;
    private String cameraFilePath;

    public RxPhotographFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Photograph photograph = null;
        if(requestCode == ALBUM_REQUEST_CODE){
            photograph = bitmapFromAlbum(data);
        }else if(requestCode == CAMERA_REQUEST_CODE){
            photograph = bitmapFromCamera(data);
        }
        subscriber.onNext(photograph);
    }

    private Photograph bitmapFromAlbum(Intent data) {
        try {
            ContentResolver contentResolver  = getActivity().getContentResolver();
            //获得图片的uri
            Uri uri = data.getData();
            //将图片内容解析成字节数组
            byte[] mContent = readStream(contentResolver.openInputStream(Uri.parse(uri.toString())));
            //将字节数组转换为ImageView可调用的Bitmap对象
            Bitmap bitmap  = getPicFromBytes(mContent,null);

            File file = new File(uri.getPath());
            return new Photograph(file.getName(), bitmap, file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Photograph bitmapFromCamera(Intent data) {
        if(!TextUtils.isEmpty(cameraFilePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(cameraFilePath);
            File file = new File(cameraFilePath);
            return new Photograph(file.getName(), bitmap, cameraFilePath);
        }else {
            return null;
        }
    }


    private Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    private byte[] readStream(InputStream in) throws Exception{
        byte[] buffer  =new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        while((len=in.read(buffer))!=-1){
            outStream.write(buffer, 0, len);
        }
        byte[] data  =outStream.toByteArray();
        outStream.close();
        in.close();
        return data;
    }

    public void setSubscriber(Subscriber<? super Photograph> subscriber) {
        this.subscriber = subscriber;
    }

    public void setCameraFilePath(String cameraFilePath) {
        this.cameraFilePath = cameraFilePath;
    }
}
