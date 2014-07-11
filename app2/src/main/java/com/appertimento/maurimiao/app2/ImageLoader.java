package com.appertimento.maurimiao.app2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by mcolombo on 03/05/14.
 */
public class ImageLoader {
    private final static String TAG="ImageLoader";
    private final LruCache<String, Bitmap> mMemoryCache;
    //Set<SoftReference<Bitmap>> mReusableBitmaps;

    int maxWidth ;
    int maxHeight;

    public ImageLoader(){
        // Get the screen size.
        Display display = ((WindowManager) MaurimiaoApp.getContext().getSystemService(MaurimiaoApp.getContext().WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        //get screen size
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //get display size
        final int screenWidth = metrics.widthPixels;
        final int screenHeight =metrics.heightPixels;

        // Set the sample size so that we scale down any image that is larger than twice the
        // width/height of the screen.
        // The goal is to never make an image that is actually larger than the screen end up appearing
        // smaller than the screen.

        maxWidth =  2*screenWidth;
        maxHeight = 2*screenHeight;

        Log.i(TAG,"ImageLoader - maxWidth "+maxWidth);
        Log.i(TAG,"ImageLoader - maxHeigth "+maxHeight);

        maxWidth =  900;
        maxHeight = 900;


        //setting parameter for cache

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.i(TAG,"onCreate - maxMemory val "+maxMemory);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache=new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
            /**************
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                //super.entryRemoved(evicted, key, oldValue, newValue);
                if(RecyclingBitmapDrawable.class.isInstance(oldValue))
                // The removed entry is a recycling drawable, so notify it
                // that it has been removed from the memory cache.
                    ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
                else{
                    // The removed entry is a standard BitmapDrawable.
                    if (Utils.hasHoneycomb()) {
                        // We're running on Honeycomb or later, so add the bitmap
                        // to a SoftReference set for possible use with inBitmap later.
                        mReusableBitmaps.add
                                (new SoftReference<Bitmap>(oldValue.getBitmap()));
                    }
                }
            }
            ****************************/
        };



    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    static class AsyncDrawable extends ColorDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(BitmapWorkerTask bitmapWorkerTask) {
            super(Color.GREEN);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        Log.i(TAG,"getBitmapWorkerTask - start");
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if(drawable!=null)
                Log.i(TAG,"getBitmapWorkerTask - drawable istanceof"+drawable.getClass().toString());
            else
                Log.i(TAG,"getBitmapWorkerTask - drawable is null");

            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        Log.i(TAG,"getBitmapWorkerTask - not associated with AsyncDrawable");
        return null;
    }

    public  void loadBitmap(InputStream inputImageStream, ImageView imageView,String imageKey,int orientation) {
        Log.i(TAG,"LoadBitmap - start "+imageKey);

        //verifico se esiste immagine nella cache
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else   if (cancelPotentialWork(inputImageStream, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView,imageKey,orientation);
            final AsyncDrawable asyncDrawable =  new AsyncDrawable( task);
            imageView.setImageDrawable(asyncDrawable);
            imageView.setMinimumHeight(156);
            task.execute(inputImageStream);
        }
    }

    public static boolean cancelPotentialWork(InputStream data, ImageView imageView) {
        Log.i(TAG,"cancelPotentialWork - start");
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final InputStream bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || bitmapData != data) {
                // Cancel previous task
                Log.i(TAG,"cancelPotentialWork - AsyncDrawable task cancelled");
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                Log.i(TAG,"cancelPotentialWork - AsyncDrawable task already in progress");
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }


    public class BitmapWorkerTask extends AsyncTask<InputStream,Void,Bitmap> {
        //private final String TAG="BitmapWorkerTask";
        private final WeakReference<ImageView> imageViewReference;
        private InputStream data;
        private String imageKey;
        private int orientation;

        public BitmapWorkerTask(ImageView imageView,String imageKey,int orientation) {
            this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.imageKey=imageKey;
            this.orientation=orientation;
        }


        @Override
        protected Bitmap doInBackground(InputStream... is) {
            data=is[0];
            Bitmap bmp=decodeSampledBitmapFromStreaming(data);
            addBitmapToMemoryCache(String.valueOf(imageKey),bmp );
            return bmp;
        }

        Bitmap decodeSampledBitmapFromStreaming(InputStream inputImageStream ){
            Log.i(TAG, "decodeSampledBitmapFromStreaming - start " );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = inputImageStream.read(buffer)) > -1 ) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //create options
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            //get image size
            BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, options);
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;

            int sampleSize = 1;
            while ((imageWidth / sampleSize > maxWidth) && (imageHeight / sampleSize > maxHeight)) {
                sampleSize *= 2;
            }

            options.inJustDecodeBounds=false;
            options.inSampleSize = sampleSize;
            Log.i(TAG, "decodeSampledBitmapFromStreaming - SAMPLE SIZE " + sampleSize);

            //Uri imageURI=Uri.parse(getArguments().getString(IMAGE));
            //InputStream is=container.getContext().getContentResolver().openInputStream(imageURI);
            //return BitmapFactory.decodeStream(inputImageStream,null,options);


            Log.i(TAG,"width after resize "+imageWidth/ sampleSize);
            //return BitmapFactory.decodeStream(inputImageStream, null, options);
            Bitmap sampledImage=BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, options);
            imageWidth=(imageWidth / sampleSize)<450 ?imageWidth / sampleSize:450;
            Log.i(TAG,"width after resize b "+imageWidth);
            //return  ThumbnailUtils.extractThumbnail(sampledImage, imageWidth, imageWidth);
            //ExifInterface exif=new ExifInterface()
            if(orientation>0){
                Matrix matrix=new Matrix();
                matrix.postRotate(orientation);
                sampledImage=Bitmap.createBitmap(sampledImage,0,0,imageWidth,imageWidth,matrix,true);
            }

            return sampledImage;//Bitmap.createBitmap(sampledImage,0,0,imageWidth,imageWidth);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i(TAG,"onPostExecute - Start");
            super.onPostExecute(bitmap);
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                //mi accerto che il task è proprio quello associato all'immagine
                if (imageView != null && this == bitmapWorkerTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }



    }

}
