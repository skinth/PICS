package com.is.pics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;

import com.is.pics.login.LoginHandle;
import com.is.pics.login.StatefulRestTemplate;
import com.is.pics.model.Item;

import org.imgscalr.Scalr;
import org.opencv.android.JavaCameraView;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class PhotoShotView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "Sample::Tutorial3View";
    private String mPictureFileName;

    public PhotoShotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);
           // Bitmap blarge = BitmapFactory.decodeByteArray(data, 0, data.length);
            float maxWidth = 1200;
            float maxHeight = 720;
            Bitmap blarge = decodeSampledBitmapFromByteArray(data,Math.round(maxWidth),Math.round(maxHeight));
            System.out.println("BLARGE WIDTH: "+blarge.getWidth());
            System.out.println("BLARGE HEIGHT: "+blarge.getHeight());
            float ratio = 0;
            float width = blarge.getWidth();
            float height = blarge.getHeight();
            float newWidth = width;
            float newHeight = height;
            if(width>maxWidth){
                ratio = maxWidth/width;
                System.out.println("RATIO: "+ratio);
                newWidth = maxWidth;
                newHeight = height*ratio;
                height = height * ratio;
                width = width * ratio;
            }
            if(height>maxHeight){
                ratio = maxHeight/height;
                newWidth = width*ratio;
                newHeight = maxHeight;
            }
            System.out.println("NEW WIDTH: "+newWidth);
            System.out.println("NEW HEIGHT: "+newHeight);
            Bitmap bscaled = Bitmap.createScaledBitmap(blarge,Math.round(newWidth), Math.round(newHeight),true);
            System.out.println("BSCALED WIDTH: "+bscaled.getWidth());
            bscaled.compress(Bitmap.CompressFormat.JPEG,80,fos);
            fos.close();

        } catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        new HttpRequestTask().execute(mPictureFileName);

    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0,res.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, 0,res.length, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }



    private class HttpRequestTask extends AsyncTask<String, Void, Item> {
        StatefulRestTemplate statefulRestTemplate = LoginHandle.getInstance().getStatefulRestTemplate();

        @Override
        protected Item doInBackground(String... params) {
            String name = params[0];
            System.out.println("name: "+name);
            try {
                MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
                //parts.add("Content-Type", MediaType.MULTIPART_FORM_DATA);
                parts.add("name",name);


                parts.add("file", new FileSystemResource(name));
                statefulRestTemplate.initEntity(parts);
                //System.out.println("ph"+statefulRestTemplate.getRequestEntity().getHeaders());
                //System.out.println("User: "+usr.getBody().getUsername());
                //HttpEntity requestEntity = new HttpEntity(parts, statefulRestTemplate.getRequestHeaders());
                //System.out.println(requestEntity.getHeaders());
                //System.out.println("User: "+usr.getBody().getUsername());
                HttpEntity<Item> result = statefulRestTemplate.exchangeForOur(URI.create(LoginHandle.BASE_URL + "/upload"),
                        HttpMethod.POST, Item.class);
                statefulRestTemplate.initEntity(null);
                System.out.println(result.getHeaders());
                System.out.println(result.getBody().getByteImage());
                //clean-up
                //File f = new File(name);
                //f.delete();
        /*this.loggedUser = statefulRestTemplate.exchangeForOur(URI.create(BASE_URL+"/profile"),
                HttpMethod.GET,MyUser.class).getBody();*/
                LoginHandle.getInstance().getLoggedUser().getUserItems().add(result.getBody());
                return result.getBody();

            } catch (Exception e) {
                Log.e("PhotoShotActivity", e.getMessage(), e);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Item item) {
            //Intent intent = new Intent(PhotoShotView.this, HomeActivity.class);
            //intent.putExtra("name", loggedUser.getUsername());
            //startActivity(intent);
        }

    }


}
