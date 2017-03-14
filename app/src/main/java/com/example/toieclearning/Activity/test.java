package com.example.toieclearning.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.example.toieclearning.Api.ImageGetterHandler;
import com.example.toieclearning.R;

public class test extends Activity {
    private final static String TAG = "TestImageGetter";
    ImageGetterHandler imageGetterHandler;
    private TextView mTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTv = (TextView) findViewById(R.id.text);
        imageGetterHandler = new ImageGetterHandler(mTv, this);
        String source = "this is a test of <b>ImageGetter</b> it contains " +
                "two images: <br/>" +
                "<img src=\"https://i.ytimg.com/vi/DSAJdFPp8Ao/maxresdefault.jpg\"><br/>and<br/>" +
                "<img src=\"https://i.ytimg.com/vi/DSAJdFPp8Ao/maxresdefault.jpg\"><br/>and<br/>";

        Spanned spanned = Html.fromHtml(source, imageGetterHandler, null);
        mTv.setText(spanned);
    }

}