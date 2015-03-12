package com.is.pics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.is.pics.login.LoginHandle;
import com.is.pics.login.StatefulRestTemplate;
import com.is.pics.model.Item;
import com.is.pics.model.MyUser;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class PhotoShotActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "OCVSample::Activity";

    private PhotoShotView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(PhotoShotActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public PhotoShotActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.photo_shot_surface_view);

        mOpenCvCameraView = (PhotoShotView) findViewById(R.id.tutorial3_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<String> effects = mOpenCvCameraView.getEffectList();

        if (effects == null) {
            Log.e(TAG, "Color effects are not supported by device!");
            return true;
        }

        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
           String element = effectItr.next();
           mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
           idx++;
        }

        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
         }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1)
        {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }
        else if (item.getGroupId() == 2)
        {
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String name = "sample_picture_" + currentDateandTime + ".jpg";
        String fileName = Environment.getExternalStorageDirectory().getPath() +"/"+ name;
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        //new HttpRequestTask().execute(name);
        return false;
    }

    private class HttpRequestTask extends AsyncTask<String, Void, Item>{
        StatefulRestTemplate statefulRestTemplate = LoginHandle.getInstance().getStatefulRestTemplate();

        @Override
        protected Item doInBackground(String... params) {
            String name = params[0];
            System.out.println("name: "+name);
            String fileName = Environment.getExternalStorageDirectory().getPath() +"/"+ name;
            System.out.println("filename: "+fileName);
            try {
                MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
                //parts.add("Content-Type", MediaType.MULTIPART_FORM_DATA);
                parts.add("name",name);
                parts.add("file", new FileSystemResource(fileName));
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
            Intent intent = new Intent(PhotoShotActivity.this, HomeActivity.class);
            //intent.putExtra("name", loggedUser.getUsername());
            startActivity(intent);
        }

    }
}
