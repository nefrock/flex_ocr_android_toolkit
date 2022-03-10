package com.nefrock.flex_ocr_android_toolkit.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;

public class S3ImageUploader {

    private static final String MY_BUCKET = "flex-ocr";

    private final UploaderListener listener;

    public S3ImageUploader(UploaderListener listener) {
        this.listener = listener;
    }

    public void uploadImage(Context context, ImageBullet bullet) {
        UploadAsyncTask asyncTask = new UploadAsyncTask(context, bullet);
        asyncTask.execute();
    }

    class UploadAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private final Context context;
        private final ImageBullet bullet;

        UploadAsyncTask(Context context, ImageBullet bullet) {
            this.context = context;
            this.bullet = bullet;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            listener.onComplete(result);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            File root = context.getFilesDir();
            String tmpFileName = "tmp.jpg";
            String bucket = bullet.getBucketName();
            String keyName = bullet.getFileName();
            try {
                FileOutputStream fos = new FileOutputStream(new File(root, tmpFileName));
                bullet.getImage().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (Exception e) {
                Log.d("doInBackground", "#############");
                e.printStackTrace();
                return false;
            }
            try {
                File f = new File(root, tmpFileName);
                AmazonS3Client s3Client = new AmazonS3Client(
                        new BasicAWSCredentials(
                                bullet.getKey1(), bullet.getKey2()));
                PutObjectRequest por = new PutObjectRequest(
                        bucket,
                        keyName,
                        f);
                s3Client.putObject(por);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}