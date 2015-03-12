package com.is.pics;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;

import com.is.pics.login.LoginHandle;
import com.is.pics.login.StatefulRestTemplate;
import com.is.pics.model.Item;

import org.opencv.android.JavaCameraView;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileOutputStream;
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

            fos.write(data);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        new HttpRequestTask().execute(mPictureFileName);

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
