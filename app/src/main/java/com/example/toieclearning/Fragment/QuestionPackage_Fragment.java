package com.example.toieclearning.Fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.example.toieclearning.Adapter.DialogAdapter;
import com.example.toieclearning.Adapter.PackageAdapter;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.ApiRequest;
import com.example.toieclearning.Api.FileCache;
import com.example.toieclearning.Api.InputStreamVolleyRequest;
import com.example.toieclearning.R;
import com.example.toieclearning.modal.Answer;
import com.example.toieclearning.modal.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.SCROLL_AXIS_VERTICAL;

public class QuestionPackage_Fragment extends Fragment {
    private int package_type;
    private View mView;
    ArrayList<PackageAdapter> packageAdapterArrayList;
    RecyclerView rcvPackage;
    FileCache fileCache;

    public void setPackage_type(int package_type) {
        this.package_type = package_type;
    }

    public QuestionPackage_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_question_package, container, false);
        addControls();
        initData();
        /*
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question(1,2,3,"hihihi",null));
        questions.add(new Question(1,2,3,"hihihi",null));
        questions.add(new Question(1,2,3,"hihihi",null));
        questions.add(new Question(1,2,3,"hihihi",null));
        questions.add(new Question(1,2,3,"hihihi",null));
        PackageAdapter packageAdapter = new PackageAdapter(getActivity(),questions);
        rcvPackage.setAdapter(packageAdapter);*/
        addEvents();
        return mView;
    }

    private void addEvents() {

    }

    private void initData() {
        packageAdapterArrayList = new ArrayList<>();

        ApiRequest rq = new ApiRequest(Request.Method.GET, ApiHelper.API_URL + "/practice/" + String.valueOf(package_type),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    /*
                    spin_kit.setVisibility(GONE);
                    container.setVisibility(View.VISIBLE);
                    */
                    if (response.get("status").equals("success")) {
                        Boolean isPackage = (Boolean) response.get("isPackage");
                        JSONArray data = response.getJSONArray("message");
                        if (isPackage) {
                            for(int ll=0;ll<data.length();ll++) {
                                ArrayList<Question> questionArrayList = new ArrayList<>();
                                JSONObject qoi = (JSONObject) data.get(ll);
                                JSONArray danhsach_cauhoi = qoi.getJSONArray("questions");


                                for (int i = 0; i < danhsach_cauhoi.length(); i++) {

                                    JSONObject qo = (JSONObject) danhsach_cauhoi.get(i);
                                    int question_id = qo.getInt("id");
                                    String s_package_id = qo.getString("package_id");
                                    int package_id = s_package_id.equals("null") ? Integer.MAX_VALUE : Integer.parseInt(s_package_id);
                                    int question_type_id = qo.getInt("question_type_id");
                                    String content = qo.getString("content");

                                    HashMap<Integer, Answer> answerArrayList = new HashMap<>();
                                    JSONArray ao = (JSONArray) qo.get("answers");
                                    for (int j = 0; j < ao.length(); j++) {
                                        JSONObject a = (JSONObject) ao.get(j);
                                        int a_id = (int) a.get("id");
                                        int a_checked = (int) a.get("checked");
                                        String a_content = (String) a.get("content");
                                        Answer c = new Answer(a_id, a_checked == 1, a_content, question_id);
                                        answerArrayList.put(a_id, c);
                                    }

                                    Question q = new Question(question_id, question_type_id, package_id, content, answerArrayList);
                                    questionArrayList.add(q);
                                }

                                PackageAdapter packageAdapter = new PackageAdapter(getActivity(),questionArrayList);
                                packageAdapterArrayList.add(packageAdapter);

                                for (Question q : questionArrayList) {
                                    Document sour = Jsoup.parse(q.getContent());
                                    Elements imgs = sour.select("img");
                                    if (imgs != null) {
                                        for (Element e : imgs) {
                                            final String img_src = e.attr("src");
                                            ImageRequest ir = new ImageRequest(ApiHelper.DOMAIN + img_src, new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap response) {
                                                    fileCache.saveFile(response, img_src);
                                                }
                                            }, 0, 0, null, null);
                                            ApiHelper.addToRequestQueue(ir);
                                        }
                                    }
                                }
                            }
                        }
                    }


                    showPackage(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        ApiHelper.addToRequestQueue(rq);
    }

    private void addControls() {
        rcvPackage = (RecyclerView) mView.findViewById(R.id.rcvPackage);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcvPackage.setLayoutManager(layoutManager);

    }

    private void showPackage(int pos){
        rcvPackage.setAdapter(packageAdapterArrayList.get(pos));
    }
}
