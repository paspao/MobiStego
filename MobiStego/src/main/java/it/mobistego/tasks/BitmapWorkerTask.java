package it.mobistego.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.lang.ref.WeakReference;

import it.mobistego.MainActivity;

/**
 * Copyright (C) 2015  Pasquale Paola
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {

    private static final String TAG = BitmapWorkerTask.class.getName();

    private final WeakReference<ImageView> imageViewReference;
    private final WeakReference<ProgressBar> progressBarWeakReference;
    private File data;

    public BitmapWorkerTask(ImageView imageView, ProgressBar progr) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        if (progr != null)
            progressBarWeakReference = new WeakReference<ProgressBar>(progr);
        else
            progressBarWeakReference = null;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(File... params) {
        data = params[0];
        //BitmapFactory.decodeFile(data.getAbsolutePath());
        int maxTextureSize = MainActivity.TEXTURE_SIZE_GL20;

        if (maxTextureSize <= 0)
            maxTextureSize = MainActivity.TEXTURE_SIZE_GL10;
        Log.d(TAG, "Max texture size " + maxTextureSize);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(data.getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        //String imageType = options.outMimeType;

        if (maxTextureSize > 0 && imageWidth > maxTextureSize) {
            imageWidth = maxTextureSize;
        }
        if (maxTextureSize > 0 && imageHeight > maxTextureSize) {
            imageHeight = maxTextureSize;
        }
        options.inSampleSize = calculateInSampleSizeWorstCase(options, imageWidth, imageHeight);

        options.inJustDecodeBounds = false;


        return BitmapFactory.decodeFile(data.getAbsolutePath(), options);
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (progressBarWeakReference != null) {
                final ProgressBar progr = progressBarWeakReference.get();
                if (progr != null)
                    progr.setVisibility(ProgressBar.GONE);
            }
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(ImageView.VISIBLE);
            }
        }
    }

    public static int calculateInSampleSizeWorstCase(
            final BitmapFactory.Options options, final int reqWidth, final int reqHeight) {


        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            do {
                inSampleSize *= 2;
            }
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth);
        }

        return inSampleSize;
    }

}