package com.example.toieclearning.Api;

/**
 * Created by Admin on 3/14/2017.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class FileCache {
    private Context context;
    private File cacheDir;

    public FileCache(Context context) {
        this.context = context;
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TempFiles");
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    public Bitmap getImage(String url) {
        File f = getFile(url);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Log.e("get_image",f.getAbsolutePath());
        return BitmapFactory.decodeFile(f.getAbsolutePath(), options);
    }


    public boolean saveFile(Bitmap b, String url) {
        File f = getFile(url);
        if(f.exists())
            return true;
        Log.e("save_image",f.getAbsolutePath());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean saveFile(byte[] data,String url){
        File f = getFile(url);
        if(f.exists())
            return true;
        Log.e("save_file",f.getAbsolutePath());

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(f);
            outputStream.write(data);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    public byte[] getAudio(String url){
        File file = getFile(url);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytes;
    }*/


    public Bitmap resizeImage(Bitmap b){
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        float aspectRatio = b.getWidth()/ (float) b.getHeight();
        /*
        if(b.getWidth() > displayMetrics.widthPixels){
            int scaledheight =  Math.round(displayMetrics.widthPixels / aspectRatio);
            return Bitmap.createScaledBitmap(b, displayMetrics.widthPixels, scaledheight, false);
        }
        return b;*/
        int scaledheight = Math.round(displayMetrics.widthPixels / aspectRatio);
        if (scaledheight > displayMetrics.heightPixels * 2 / 3) {
            return Bitmap.createScaledBitmap(b, displayMetrics.widthPixels * 2 / 3, scaledheight * 2 / 3, false);
        }
        return Bitmap.createScaledBitmap(b, displayMetrics.widthPixels, scaledheight, false);

        //return b;
    }

}
