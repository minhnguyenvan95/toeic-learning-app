package com.example.toieclearning.Api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.example.toieclearning.R;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by MyPC on 3/13/2017.
 *
 */

public class ImageGetterHandler implements Html.ImageGetter{

    public FileCache mCache;
    private TextView mText;
    private Context context;

    /* Bat buoc khoi tao TextView truoc
    * */
    public ImageGetterHandler(TextView mText, Context context) {
        this.mText = mText;
        this.context = context;
        mCache = new FileCache(context);
    }

    @Override
    public Drawable getDrawable(String source) {
        //from SD cache

        Bitmap b = mCache.getImage(source);
        if (b != null) {
            Log.e("CACHEIMAGE", "HIHI");
            return new BitmapDrawable(context.getResources(), b);
        }

        LevelListDrawable d = new LevelListDrawable();
        //TODO: thay the con cu vao
        Drawable empty = context.getResources().getDrawable(R.drawable.user);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new LoadImage().execute(source, d, mText);
        return d;
    }

    private class LoadImage extends AsyncTask<Object, Void, Bitmap> {
        private TextView mTextview;
        private LevelListDrawable mDrawable;
        private String mSource;

        @Override
        protected Bitmap doInBackground(Object... params) {
            mSource = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            mTextview = (TextView) params[2];
            Log.d("GetImage", "doInBackground " + mSource);
            try {
                InputStream is = new URL(mSource).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d("GetImageExecute", "onPostExecute drawable " + mDrawable);
            Log.d("GetImageExecute", "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                mCache.saveImage(bitmap, mSource);
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                CharSequence text = mTextview.getText();
                mTextview.setText(text);
            }
        }
    }
}
