package com.anima.rxphotograph;

import android.graphics.Bitmap;

/**
 * Created by jianjianhong on 2018/5/22.
 */

public class Photograph {
    private String name;
    private Bitmap bitmap;
    private String filePath;

    public Photograph(String name, Bitmap bitmap, String filePath) {
        this.name = name;
        this.bitmap = bitmap;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
