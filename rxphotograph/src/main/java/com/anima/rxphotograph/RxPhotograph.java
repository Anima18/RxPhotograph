package com.anima.rxphotograph;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.anima.rxphotograph.RxPhotographFragment.ALBUM_REQUEST_CODE;
import static com.anima.rxphotograph.RxPhotographFragment.CAMERA_REQUEST_CODE;

/**
 * Created by jianjianhong on 2018/5/22.
 */

public class RxPhotograph {
    private static final String TAG = "RxPhotograph";
    private RxPhotographFragment mRxPhotographFragment;
    private Context mContext;
    private Observable<Photograph> observable;
    private Subscriber<? super Photograph> subscriber;

    public RxPhotograph(Activity activity) {
        mContext = activity;
        mRxPhotographFragment = getRxPhotographFragment(activity);
    }

    private RxPhotographFragment getRxPhotographFragment(Activity activity) {
        RxPhotographFragment rxPermissionsFragment = findRxPhotographFragment(activity);
        boolean isNewInstance = rxPermissionsFragment == null;
        if (isNewInstance) {
            rxPermissionsFragment = new RxPhotographFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return rxPermissionsFragment;
    }

    private RxPhotographFragment findRxPhotographFragment(Activity activity) {
        return (RxPhotographFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public Observable<Photograph> request() {
        observable = Observable.create(new Observable.OnSubscribe<Photograph>() {
            @Override
            public void call(final Subscriber<? super Photograph> subscriber) {
                setSubscriber(subscriber);
                requestPhoto();
            }
        });
        return observable;
    }

    private void requestPhoto() {
        new AlertDialog.Builder(mContext).setTitle(R.string.image_select_title).setItems(R.array.image_select_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                if(which == 0) {
                    photoFromCamera();
                }else if(which == 1) {
                   photoFromAlbum();
                }
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                subscriber.unsubscribe();
            }
        }).create().show();
    }

    private void photoFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        request(intent);
    }

    private void photoFromCamera() {
        File file = new File(getFileStorageDir(), getUniqueImageName());
        Uri fileUri = FileProvider.getUriForFile(mContext, "com.anima.rxphotograph.fileprovider", file);

        Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra("CAMERA_FILE_PATH", file.getPath());
        request(intent);
    }

    private File getFileStorageDir() {
        File file = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
        if (!file.mkdirs()) {}
        return file;
    }

    private String getUniqueImageName() {
        SimpleDateFormat format0 = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format0.format(new Date().getTime());

        StringBuilder builder = new StringBuilder("IMG_");
        builder.append(time);
        builder.append(".");
        builder.append("jpg");
        return builder.toString();
    }

    private void request(final Intent intent) {
        mRxPhotographFragment.setSubscriber(subscriber);
        mRxPhotographFragment.setCameraFilePath(intent.getStringExtra("CAMERA_FILE_PATH"));
        mRxPhotographFragment.startActivityForResult(intent, getRequestCode(intent));
    }

    private int getRequestCode(Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_GET_CONTENT)) {
            return ALBUM_REQUEST_CODE;
        }else if(intent.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)) {
            return CAMERA_REQUEST_CODE;
        }else {
            return 0;
        }
    }

    private void setSubscriber(Subscriber<? super Photograph> subscriber) {
        this.subscriber = subscriber;
    }

    private Observable<Photograph> getObservable() {
        return observable;
    }
}
