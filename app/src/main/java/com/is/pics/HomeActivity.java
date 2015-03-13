package com.is.pics;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.is.pics.login.LoginHandle;
import com.is.pics.login.StatefulRestTemplate;
import com.is.pics.model.Item;
import com.is.pics.model.MyUser;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.net.URI;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GridView gridview = (GridView) findViewById(R.id.gridview);

        gridview.setAdapter(new ImageAdapter(this));

        System.out.println(LoginHandle.getInstance().getLoggedUser().getUserItems());

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(HomeActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, ShowImageActivity.class);
                intent.putExtra("position", position);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.shot){
            Intent i = new Intent(this,PhotoShotActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        if(id == R.id.refresh){
            new HttpRequestTask().execute();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
       
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, MyUser> {

        StatefulRestTemplate statefulRestTemplate = LoginHandle.getInstance().getStatefulRestTemplate();

        @Override
        protected MyUser doInBackground(Void... params) {
            try {
                HttpEntity<MyUser> result = statefulRestTemplate.exchangeForOur(
                                        URI.create(LoginHandle.BASE_URL + "/profile"),
                        HttpMethod.GET, MyUser.class);
                System.out.println("USERNAME"+result.getBody().getUsername());
                LoginHandle.getInstance().setLoggedUser(result.getBody());

            } catch (Exception e) {
                Log.e("HomeActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(MyUser user) {
            finish();
            startActivity(getIntent());
        }

    }

}
