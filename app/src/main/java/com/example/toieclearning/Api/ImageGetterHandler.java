package com.example.toieclearning.Api;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;

import java.util.HashMap;

/**
 * Created by MyPC on 3/13/2017.
 *
 */

public class ImageGetterHandler implements Html.ImageGetter{

    private Context context;
    private HashMap<String, Drawable> mImageCache;

    /*
    * Cach su dung :
     * Truyen context va view vao
     * Download anh luu vao cache
     * Goi lai ham de lay cache ra
    * */
    public ImageGetterHandler(Context c) {
        this.mImageCache = new HashMap<>();
        this.context = c;
    }

    public Drawable getDrawable(final String source) {
        final String url = ApiHelper.DOMAIN + source;
        Log.d("IMG_SRC",source);
        Drawable drawable = mImageCache.get(url);
        if (drawable != null) {
            return drawable;
        } else {
            ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    Drawable d = new BitmapDrawable(Resources.getSystem(), response);
                    mImageCache.put(url,d);
                }
            }, 0, 0, null, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO: buoc nay bat buoc phai set cache khong se tao ra vong lap
                    //CO THE SET TU DRAWLE TRONG MAY
                mImageCache.put(url,null);
                }
            });

        }
        return null;
    }
}
