package com.example.toieclearning.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.toieclearning.Adapter.DialogAdapter;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.ApiRequest;
import com.example.toieclearning.Api.FileCache;
import com.example.toieclearning.Api.ImageGetterHandler;
import com.example.toieclearning.R;
import com.example.toieclearning.modal.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Question_Frag extends Fragment {

    HashMap<Integer, Question> questionHashMap = new HashMap<>();
    ImageGetterHandler imageGetterHandler;
    MediaPlayer mediaPlayer;
    FileCache fileCache;
    View view;
    Dialog dialog;
    TextView txtNumber, txtQuestion;
    RadioGroup rdbGroup;
    RadioButton rdbA, rdbB, rd;
    GridView gridView;
    ArrayList listNumberQuestion;
    private TextView mTv;

    public Question_Frag() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.question_fragment, container, false);
        mTv = new TextView(getActivity());
        init();
        addControl();
        return view;
    }

    private void addControl() {
        txtQuestion = (TextView) view.findViewById(R.id.txtQuestion);
        txtNumber = (TextView) view.findViewById(R.id.txtNumber);

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.select_question);
        gridView = (GridView) dialog.findViewById(R.id.grNumber);

        txtNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void init() {
        mediaPlayer = new MediaPlayer();
        //TODO: init mTv
        imageGetterHandler = new ImageGetterHandler(mTv, getActivity());
        fileCache = new FileCache(getActivity());
        ApiHelper.setContext(getActivity());

        ApiRequest rq = new ApiRequest(Request.Method.GET, ApiHelper.API_URL + "/practice/" + String.valueOf(1),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("status").equals("success")) {
                        Boolean isPackage = (Boolean) response.get("isPackage");
                        JSONArray data = (JSONArray) response.get("message");
                        if (!isPackage) {
                            listNumberQuestion = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                listNumberQuestion.add(i);
                                JSONObject qo = (JSONObject) data.get(i);
                                int id = qo.getInt("id");
                                String s_package_id = qo.getString("package_id");
                                int package_id = s_package_id.equals("null") ? Integer.MAX_VALUE : Integer.parseInt(s_package_id);
                                int question_type_id = qo.getInt("question_type_id");
                                String content = qo.getString("content");
                                Question q = new Question(id, question_type_id, package_id, content);
                                questionHashMap.put(i, q);
                            }
                            DialogAdapter adapter = new DialogAdapter(getActivity(), R.layout.number_adapter, listNumberQuestion);
                            gridView.setAdapter(adapter);
                            showQuestion(0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        ApiHelper.addToRequestQueue(rq);
        //TODO: giai quyet loi xay ra khi rq
    }

    private void showQuestion(int number) {
        Question q = questionHashMap.get(number);
        txtNumber.setText(String.valueOf(number));
        Spanned spanned = Html.fromHtml(q.getContent(), imageGetterHandler, null);
        txtQuestion.setText(spanned);
    }
}
