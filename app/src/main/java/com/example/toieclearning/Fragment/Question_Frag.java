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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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
    ImageButton pre, next;
    int current_question = -1;

    public Question_Frag() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.question_fragment, container, false);
        addControl();
        addEvents();
        init();
        return view;
    }

    private void addEvents() {
        pre = (ImageButton) view.findViewById(R.id.previous_question_btn);
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestion(current_question - 1);
            }
        });
        next = (ImageButton) view.findViewById(R.id.next_question_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestion(current_question + 1);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int number = (int) listNumberQuestion.get(position);
                showQuestion(number);
                dialog.dismiss();
            }
        });
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
        imageGetterHandler = new ImageGetterHandler(txtQuestion, getActivity());
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
                                listNumberQuestion.add(i + 1);
                                JSONObject qo = (JSONObject) data.get(i);
                                int id = qo.getInt("id");
                                String s_package_id = qo.getString("package_id");
                                int package_id = s_package_id.equals("null") ? Integer.MAX_VALUE : Integer.parseInt(s_package_id);
                                int question_type_id = qo.getInt("question_type_id");
                                String content = qo.getString("content");
                                Question q = new Question(id, question_type_id, package_id, content);
                                questionHashMap.put(i + 1, q);
                            }
                            DialogAdapter adapter = new DialogAdapter(getActivity(), R.layout.number_adapter, listNumberQuestion);
                            gridView.setAdapter(adapter);
                            showQuestion(1);
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
        if (number == 1) {
            pre.setVisibility(View.INVISIBLE);
        } else if (number == questionHashMap.size()) {
            next.setVisibility(View.INVISIBLE);
        } else {
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }
        current_question = number;
        Question q = questionHashMap.get(number);
        txtNumber.setText(String.valueOf(number));
        Spanned spanned = Html.fromHtml(q.getContent(), imageGetterHandler, null);
        txtQuestion.setText(spanned);
    }
}
