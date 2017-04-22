package com.example.toieclearning.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.toieclearning.Adapter.DialogAdapter;
import com.example.toieclearning.Adapter.PackageAdapter;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.ApiRequest;
import com.example.toieclearning.Api.FileCache;
import com.example.toieclearning.Api.ImageGetterHandler;
import com.example.toieclearning.Api.InputStreamVolleyRequest;
import com.example.toieclearning.R;
import com.example.toieclearning.modal.Answer;
import com.example.toieclearning.modal.Question;
import com.example.toieclearning.myInterface.OnPackageRdbSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;

public class QuestionPackage_Fragment extends Fragment implements OnPackageRdbSelectedListener{
    private int package_type;
    private View mView;
    ArrayList<PackageAdapter> packageAdapterArrayList;
    RecyclerView rcvPackage;
    FileCache fileCache;
    ImageButton pre, next;
    private int current_package = -1;
    TextView txtNumber;
    SeekBar media_seekBar;
    MediaPlayer mediaPlayer;
    TextView media_current_txt;
    ImageButton media_play_btn;
    Handler handler;
    TextView txtQuestion;
    private ImageGetterHandler imageGetterHandler;
    private NestedScrollView nestedScrollView;
    Dialog dialog;
    GridView gridView;

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
        packageAdapterArrayList = new ArrayList<>();
        imageGetterHandler = new ImageGetterHandler(txtQuestion, getActivity());
        fileCache = new FileCache(getActivity());
        mediaPlayer = new MediaPlayer();
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
        ApiRequest rq = new ApiRequest(Request.Method.GET, ApiHelper.API_URL + "/practice/" + String.valueOf(package_type),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    final ArrayList<Integer> listNumberQuestion = new ArrayList<>();
                    if (response.get("status").equals("success")) {
                        Boolean isPackage = (Boolean) response.get("isPackage");
                        JSONArray data = response.getJSONArray("message");
                        if (isPackage) {
                            for(int ll=0;ll<data.length();ll++) {
                                listNumberQuestion.add(ll+1);
                                ArrayList<Question> questionArrayList = new ArrayList<>();
                                JSONObject qoi = (JSONObject) data.get(ll);

                                String noidung_goicauhoi = qoi.getString("content");
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

                                PackageAdapter packageAdapter = new PackageAdapter(getActivity(),questionArrayList,QuestionPackage_Fragment.this);
                                packageAdapter.setNoidung_goicauhoi(noidung_goicauhoi);
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

                    DialogAdapter adapter = new DialogAdapter(getActivity(), R.layout.number_adapter, listNumberQuestion);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            int number = listNumberQuestion.get(position);
                            showPackage(position);
                            dialog.dismiss();
                        }
                    });

                    showPackage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        ApiHelper.addToRequestQueue(rq);
    }

    private void addControls() {
        nestedScrollView= (NestedScrollView) mView.findViewById(R.id.nestedscrollview);
        rcvPackage = (RecyclerView) mView.findViewById(R.id.rcvPackage);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcvPackage.setLayoutManager(layoutManager);
        pre = (ImageButton) mView.findViewById(R.id.previous_question_btn);
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPackage(current_package-1);
            }
        });

        next = (ImageButton) mView.findViewById(R.id.next_question_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPackage(current_package+1);
            }
        });

        txtNumber = (TextView) mView.findViewById(R.id.txtNumber);

        media_play_btn = (ImageButton) mView.findViewById(R.id.media_play_btn);
        media_current_txt = (TextView) mView.findViewById(R.id.media_current_txt);
        media_seekBar = (SeekBar) mView.findViewById(R.id.media_seekbar);
        txtQuestion = (TextView) mView.findViewById(R.id.txtQuestion);

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

    private void showPackage(int pos){

        nestedScrollView.scrollTo(0, 0);


        current_package = pos;
        if(pos == 0){
            pre.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }else if(pos == packageAdapterArrayList.size() - 1) {
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        }
        else if(pos >= packageAdapterArrayList.size()){
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
            return;
        }else{
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }
        txtNumber.setText((pos+1)+"");

        PackageAdapter packageAdapter = packageAdapterArrayList.get(pos);
        rcvPackage.setAdapter(packageAdapter);

        String html = packageAdapter.getNoidung_goicauhoi();


        Spanned spanned = Html.fromHtml(html, imageGetterHandler, null);
        if(spanned.toString().length() < 10 && !html.contains("<img")){
            txtQuestion.setVisibility(GONE);
        }
        if (html.contains("<audio") && html.contains("</audio>")){
            playMediaFromHtml(html);
        }else{
            View v = mView.findViewById(R.id.media_player);
            v.setVisibility(GONE);
        }
        txtQuestion.setText(spanned);
        txtQuestion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Handler handler2 = new Handler();
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        handler2.post(new Runnable() {
                            public void run() {
                                int pos_start = txtQuestion.getSelectionStart();
                                int pos_end = txtQuestion.getSelectionEnd();
                                String selected_txt = txtQuestion.getText().toString().substring(pos_start, pos_end);

                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("Translating : " + selected_txt);
                                progressDialog.show();

                                String rq_url = "http://www.transltr.org/api/translate?text="+selected_txt+"&to=vi&from=en";
                                ApiRequest translate = new ApiRequest(Request.Method.GET, rq_url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String text = response.getString("text");
                                            String translationText = response.getString("translationText");
                                            progressDialog.dismiss();

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                                    .setTitle("Translated")
                                                    .setMessage(text + " : " + translationText);
                                            builder.show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Please check your network connection.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                ApiHelper.addToRequestQueue(translate);
                            }
                        });
                    }
                }, 10);
                return false;

            }
        });
    }

    public void playMediaFromHtml(final String html) {
        Document doc = Jsoup.parse(html);
        Element src = doc.select("source").first();
        final String audio = src.attr("src");
        if (audio != null) {
            File f = fileCache.getFile(audio);
            Log.e("file_audio", f.getAbsolutePath());
            if (f.exists()) {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(f.getAbsolutePath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                UpdateTimeSongCurrent();
                media_play_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            handler.removeCallbacksAndMessages(null);
                            UpdateTimeSongCurrent();
                        } else {
                            mediaPlayer.start();
                            UpdateTimeSongCurrent();
                        }
                    }
                });

            } else {
                InputStreamVolleyRequest rq = new InputStreamVolleyRequest(Request.Method.GET, ApiHelper.DOMAIN + audio,
                        new Response.Listener<byte[]>() {
                            @Override
                            public void onResponse(byte[] response) {
                                fileCache.saveFile(response, audio);
                                playMediaFromHtml(html);
                            }
                        }, null, null);
                ApiHelper.addToRequestQueue(rq);
            }
        }

    }

    private void UpdateTimeSongCurrent() {
        if (mediaPlayer.isPlaying())
            media_play_btn.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        else {
            media_play_btn.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            return;
        }

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isDetached() || !isAdded()){
                    mediaPlayer.release();
                    return;
                }

                int c_seekbar_pos = 100 * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
                media_seekBar.setProgress(c_seekbar_pos);
                SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
                media_current_txt.setText(timeFormat.format(mediaPlayer.getCurrentPosition()));
                if (mediaPlayer.getCurrentPosition() + 500 >= mediaPlayer.getDuration()) {
                    media_play_btn.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                } else
                    handler.postDelayed(this, 500);
            }
        }, 1);
    }

    @Override
    public void onPackageRdbSelected() {
        int socauhoi = 0;
        int socautraloi = 0;
        for (int i=0;i<packageAdapterArrayList.size();i++){
            socautraloi += packageAdapterArrayList.get(i).getAnsweredHashMap().size();
            socauhoi += packageAdapterArrayList.get(i).getQuestionArrayList().size();
        }
        int sodiem = 0;
        if(socautraloi == socauhoi){
            for (int i=0;i<packageAdapterArrayList.size();i++){
                PackageAdapter packageAdapter = packageAdapterArrayList.get(i);

                HashMap<Integer, Answer> answeredHashMap = packageAdapter.getAnsweredHashMap();
                HashMap<Integer, Answer> danhsachcauhoi = packageAdapter.getDanhsachcauhoi();
                for (Answer a:answeredHashMap.values()){
                    Answer correct = danhsachcauhoi.get(a.getId());
                    if(correct.isChecked())
                        sodiem++;
                }
            }


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Xem kết quả bài test của bạn");
            final int finalSodiem = sodiem;

            final int finalSocauhoi = socauhoi;
            alertDialogBuilder.setPositiveButton("Xem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Bài test của bạn đạt: " + finalSodiem + "/" + finalSocauhoi);
                    alertDialogBuilder.setPositiveButton("Xem đáp án", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            for (int i=0;i<packageAdapterArrayList.size();i++){
                                PackageAdapter packageAdapter = packageAdapterArrayList.get(i);
                                packageAdapter.setDiem_flag(true);
                                packageAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            alertDialogBuilder.setNegativeButton("Làm lại", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    @Override
    public void onAllRdbInPackageSelected() {
        showPackage(current_package+1);
    }
}
