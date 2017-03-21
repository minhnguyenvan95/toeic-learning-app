package com.example.toieclearning.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.ApiRequest;
import com.example.toieclearning.R;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    TextView txtChuaCoTK, txtQuenMK;
    EditText edtEmail, edtPassword;
    Button btnDangNhap;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ApiHelper.setContext(getApplicationContext());
        AnhXa();

        /*edtEmail.setFocusable(false);
        edtPassword.setFocusable(false);*/

        txtChuaCoTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.activity_regiss);
                dialog.show();
                final EditText edtMail = (EditText) dialog.findViewById(R.id.edtEmailRegis);
                final EditText edtPass = (EditText) dialog.findViewById(R.id.edtPassRegis);
                final EditText edtPassRe = (EditText) dialog.findViewById(R.id.edtPassRegisRe);

                Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = edtMail.getText().toString().trim();
                        String pass = edtPass.getText().toString().trim();
                        if (!pass.equals(edtPassRe.getText().toString().trim())) {
                            Toast.makeText(LoginActivity.this, "Password do not match!!", Toast.LENGTH_SHORT).show();
                        } else {
                            register(email, pass);
                        }
                    }
                });
            }
        });

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String pass = edtPassword.getText().toString();
                login(email, pass);
            }
        });

        txtQuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:QUEN MK
            }
        });

    }

    private void register(String username, String password) {
        loadingDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Loading...");
        JSONObject object = new JSONObject();
        try {
            object.put("email", username);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest apiRequest = new ApiRequest(Request.Method.POST, ApiHelper.API_URL + "/user/create", object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "trail@gmail.com");
        String password = sharedPreferences.getString("password", "123456");
        String token = sharedPreferences.getString("api_token", "");

        if (!token.isEmpty()) {
            edtEmail.setText(email);
            edtPassword.setText(password);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void login(String username, String password) {
        loadingDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Loading...");
        JSONObject object = new JSONObject();
        try {
            object.put("email", username);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest apiRequest = new ApiRequest(Request.Method.POST, ApiHelper.API_URL + "/user/get_token", object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
                try {
                    String status = (String) response.get("status");

                    if (status.equals("success")) {
                        JSONObject message = (JSONObject) response.get("message");

                        String email = edtEmail.getText().toString();
                        String password = edtPassword.getText().toString();
                        String api_token = (String) message.get("api_token");
                        String name = (String) message.get("name");
                        ApiHelper.API_TOKEN = api_token;

                        SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putString("api_token", api_token);
                        editor.putString("name", name);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Thanh cong", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        String message = (String) response.get("message");
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                error.getCause();
                Toast.makeText(LoginActivity.this, getResources().getText(android.R.string.httpErrorUnsupportedScheme), Toast.LENGTH_SHORT).show();
            }
        });
        ApiHelper.addToRequestQueue(apiRequest, "dang_nhap");
    }

    private void AnhXa() {
        txtChuaCoTK = (TextView) findViewById(R.id.txtChuaCoTk);
        edtEmail = (EditText) findViewById(R.id.edtEmailLogin);
        edtPassword = (EditText) findViewById(R.id.edtPassLogin);
        txtQuenMK = (TextView) findViewById(R.id.txtQuenMK);
        btnDangNhap = (Button) findViewById(R.id.btnDangNhap);
    }
}