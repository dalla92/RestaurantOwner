package it.polito.group2.restaurantowner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Date;

/**
 * Created by Filippo on 14/04/2016.
 */
public class Photo {
    private Bitmap bitmap;
    private String name;
    private String description;
    private String uploadTime;
    private Restaurant photoOwner;

    Photo(String imageByte) {
        byte[] b = Base64.decode(imageByte, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        name = "";
        description = "";
        uploadTime = "";
        photoOwner = null;
    }

    Photo(Bitmap image) {
        bitmap = image;
        name = "";
        description = "";
        uploadTime = "";
        photoOwner = null;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Restaurant getPhotoOwner() {
        return photoOwner;
    }

    public void setPhotoOwner(Restaurant photoOwner) {
        this.photoOwner = photoOwner;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}
