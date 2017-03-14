package com.example.toieclearning.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.ApiRequest;
import com.example.toieclearning.Api.ImageGetterHandler;
import com.example.toieclearning.R;
import com.example.toieclearning.modal.Question;
import com.example.toieclearning.modal.QuestionType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageGetterHandler imageGetterHandler;
    private int[] box_labels = {R.id.box_0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageGetterHandler = new ImageGetterHandler(this);

        ApiHelper.setContext(getApplicationContext());
        /*JSONObject object = new JSONObject();
        try {
            object.put("email","minh.nguyenvan95@gmail.com");
            object.put("password","123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Get_QuestionType();
        Get_Question();

    }

    public void Get_QuestionType(){
        ApiRequest apiRequest = new ApiRequest(Request.Method.GET, ApiHelper.API_URL + "/questiontype/all", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String status = (String) response.get("status");
                    JSONObject message = response.getJSONObject("message");
                    JSONArray data = message.getJSONArray("data");
                    Log.e("Get_QuestionType",status);
                    ArrayList<QuestionType> questionTypeArrayList = new ArrayList<>();

                    if(status.equals("success")) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            QuestionType questionType = new QuestionType();
                            questionType.setId((int) object.get("id"));
                            questionType.setMeta((String) object.get("meta"));
                            questionType.setName((String) object.get("name"));
                            questionTypeArrayList.add(questionType);
                        }
                    }
                }catch (Exception e){
                    Log.e("Error_getQuestionType",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getCause();
                //.makeText(MainActivity.this, , Toast.LENGTH_SHORT).show();
            }
        });
        ApiHelper.addToRequestQueue(apiRequest,"load_questionType");
    }

    public void Get_Question(){
        ApiRequest apiRequest = new ApiRequest(Request.Method.GET, ApiHelper.API_URL + "/question/type/3", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String status = (String) response.get("status");
                    JSONObject message = response.getJSONObject("message");
                    JSONArray data = message.getJSONArray("data");
                    Log.e("Get_Question",status);
                    ArrayList<Question> questionArrayList = new ArrayList<>();

                    if(status.equals("success")) {

                            JSONObject object = data.getJSONObject(0);
                            Question question = new Question();
                            question.setId((int) object.get("id"));
                            question.setQuestion_type_id((int) object.get("question_type_id"));
                            question.setContent((String) object.get("content"));
                            questionArrayList.add(question);
                            //Document doc = Jsoup.parse(question.getContent());
                            String html_source = question.getContent();
                        //TextView txtvTest = (TextView) findViewById(R.id.test);
                            //txtvTest.setText(Html.from(doc.toString()));

                            Html.fromHtml(html_source,imageGetterHandler,null);
                            Spanned spanned = Html.fromHtml(html_source, imageGetterHandler, null);
                        //txtvTest.setText(Html.fromHtml(html_source,null, new HtmlTagHandler()));

                    }
                }catch (Exception e){
                    Log.e("Error_getQuestion",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getCause();
                //.makeText(MainActivity.this, , Toast.LENGTH_SHORT).show();
            }
        });
        ApiHelper.addToRequestQueue(apiRequest,"load_question");
    }
}
