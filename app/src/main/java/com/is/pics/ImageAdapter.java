package com.is.pics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.is.pics.login.LoginHandle;
import com.is.pics.model.Item;


/**
 * Created by stefano on 27/02/15.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {

        return LoginHandle.getInstance().getLoggedUser().getUserItems().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        try {
            Item items[] = new Item[this.getCount()];
            LoginHandle.getInstance().getLoggedUser().getUserItems().toArray(items);
            byte[] imgbyte = items[position].getByteImage();
            String imgString = Base64.encodeToString((byte[]) imgbyte, Base64.NO_WRAP);
            byte[] encodeByte = Base64.decode(imgString, Base64.DEFAULT);
            Bitmap blarge = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Bitmap bscaled = Bitmap.createScaledBitmap(blarge, 300, 300,true);
            imageView.setImageBitmap(bscaled);
        }catch (Exception e){
            e.printStackTrace();
        }

        return imageView;
    }
}