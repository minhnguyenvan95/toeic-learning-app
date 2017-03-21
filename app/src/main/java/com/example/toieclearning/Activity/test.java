package com.example.toieclearning.Activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.FileCache;
import com.example.toieclearning.Api.ImageGetterHandler;
import com.example.toieclearning.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.example.toieclearning.Api.InputStreamVolleyRequest;

import java.io.File;
import java.io.IOException;

public class test extends Activity {
    private final static String TAG = "TestImageGetter";
    ImageGetterHandler imageGetterHandler;
    MediaPlayer mediaPlayer = new MediaPlayer();
    FileCache fileCache;
    private TextView mTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTv = (TextView) findViewById(R.id.text);
        imageGetterHandler = new ImageGetterHandler(mTv, this);
        fileCache = new FileCache(this);
        ApiHelper.setContext(getApplicationContext());

        String source = "<audio controls><source type=\"audio/mpeg\" src=\"/question_mp3/9.mp3\"></source></audio>";

        if(source.contains("<audio") && source.contains("</audio>")){
            Document doc = Jsoup.parse(source);
            Element src = doc.select("source").first();
            final String audio = src.attr("src");
            if(audio != null){
                File f = fileCache.getFile(audio);
                if(f.exists()){
                    //TODO: test get audio

                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(f.getAbsolutePath());
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();

                }else {
                    //TODO: download audio
                    InputStreamVolleyRequest rq = new InputStreamVolleyRequest(Request.Method.GET, ApiHelper.DOMAIN + audio,
                            new Response.Listener<byte[]>() {
                                @Override
                                public void onResponse(byte[] response) {
                                    fileCache.saveFile(response, audio);
                                }
                            }, null, null);
                    ApiHelper.addToRequestQueue(rq);
                }
            }
        }

        Spanned spanned = Html.fromHtml(source, imageGetterHandler, null);
        mTv.setText(spanned);

    }

}