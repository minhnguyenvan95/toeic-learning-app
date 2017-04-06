package com.example.toieclearning.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.ApiRequest;
import com.example.toieclearning.Fragment.Question_Frag;
import com.example.toieclearning.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
    View box_1,box_2,box_3,box_4,box_5,box_6,box_7,box_8;
    FragmentTransaction fragmentTransaction;
    Question_Frag question_frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApiHelper.setContext(getApplicationContext());

        addControls();
        addEvents();

        final SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);

        TextView logout = (TextView) findViewById(R.id.txt_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        String name = sharedPreferences.getString("name","name");
        long point = sharedPreferences.getLong("balance",0);
        reloadBalance(name,point);

        String api_token = sharedPreferences.getString("api_token","");
        ApiRequest apiRequest = new ApiRequest(Request.Method.POST, ApiHelper.API_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = (String) response.get("status");
                    if(status == "success"){
                        JSONObject message = (JSONObject) response.get("message");
                        String name = message.getString("name");
                        long balance = message.getLong("balance");
                        reloadBalance(name,balance);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name",name);
                        editor.putLong("balance",balance);
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPackagesFragment(int package_type){
        Toast.makeText(this, "Goi cau hoi, chua lam", Toast.LENGTH_SHORT).show();
    }


    private void showQuestionFragment(int question_type){
        question_frag = new Question_Frag();
        question_frag.setQuestion_type(question_type);

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.showFragment, question_frag, "TAG_FRAG");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void addEvents() {

        box_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestionFragment(1);
            }
        });

        box_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestionFragment(2);
            }
        });

        box_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPackagesFragment(3);
            }
        });

        box_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPackagesFragment(4);
            }
        });
        box_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestionFragment(5);
            }
        });

        box_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPackagesFragment(6);
            }
        });

        box_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPackagesFragment(7);
            }
        });

        box_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Tu dien", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addControls() {
        box_1 = findViewById(R.id.box_1);
        box_2 = findViewById(R.id.box_2);
        box_3 = findViewById(R.id.box_3);
        box_4 = findViewById(R.id.box_4);
        box_5 = findViewById(R.id.box_5);
        box_6 = findViewById(R.id.box_6);
        box_7 = findViewById(R.id.box_7);
        box_8 = findViewById(R.id.box_8);
    }

    public void reloadBalance(String name,long balance){
        TextView txtname = (TextView) findViewById(R.id.txtNameUser);
        txtname.setText(name);
        TextView txtpoint = (TextView) findViewById(R.id.txtPoint);
        txtpoint.setText(String.valueOf(balance));
    }

}
