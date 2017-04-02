package com.example.toieclearning.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.toieclearning.Adapter.DialogAdapter;
import com.example.toieclearning.Api.ApiHelper;
import com.example.toieclearning.Api.ApiRequest;
import com.example.toieclearning.Api.FileCache;
import com.example.toieclearning.Api.ImageGetterHandler;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Question_Frag extends Fragment {

    HashMap<Integer, Question> questionHashMap = new HashMap<>();
    ImageGetterHandler imageGetterHandler;
    MediaPlayer mediaPlayer;
    FileCache fileCache;
    View view;
    Dialog dialog;
    TextView txtNumber, txtQuestion;
    RadioGroup rdbGroup;
    GridView gridView;
    ArrayList<Integer> listNumberQuestion;
    ImageButton pre, next;
    int current_question = -1;
    Handler handler;
    SeekBar media_seekBar;
    TextView media_current_txt;
    ImageButton media_play_btn;

    HashMap<Integer, Answer> answeredHashMap;

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
                int number = listNumberQuestion.get(position);
                showQuestion(number);
                dialog.dismiss();
            }
        });

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

    private void addControl() {
        rdbGroup = (RadioGroup) view.findViewById(R.id.rdbGroupAnswer);
        media_play_btn = (ImageButton) view.findViewById(R.id.media_play_btn);
        media_current_txt = (TextView) view.findViewById(R.id.media_current_txt);
        media_seekBar = (SeekBar) view.findViewById(R.id.media_seekbar);
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
        answeredHashMap = new HashMap<>();
        mediaPlayer = new MediaPlayer();
        imageGetterHandler = new ImageGetterHandler(txtQuestion, getActivity());
        fileCache = new FileCache(getActivity());
        ApiHelper.setContext(getActivity());

        ApiRequest rq = new ApiRequest(Request.Method.GET, ApiHelper.API_URL + "/practice/" + String.valueOf(5),
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
                                questionHashMap.put(i + 1, q);
                            }
                            DialogAdapter adapter = new DialogAdapter(getActivity(), R.layout.number_adapter, listNumberQuestion);
                            gridView.setAdapter(adapter);
                            showQuestion(1);

                            for (Question q : questionHashMap.values()) {
                                Document sour = Jsoup.parse(q.getContent());
                                Element audio_src = sour.select("source").first();
                                if (audio_src != null) {
                                    final String audio = audio_src.attr("src");
                                    InputStreamVolleyRequest rq = new InputStreamVolleyRequest(Request.Method.GET, ApiHelper.DOMAIN + audio,
                                            new Response.Listener<byte[]>() {
                                                @Override
                                                public void onResponse(byte[] response) {
                                                    fileCache.saveFile(response, audio);
                                                }
                                            }, null, null);
                                    ApiHelper.addToRequestQueue(rq);
                                }
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
        } else if (number >= questionHashMap.size()) {
            next.setVisibility(View.INVISIBLE);
        } else {
            pre.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }

        Question q = questionHashMap.get(number);

        //Tim cau hoi chua tl
        if (q == null) {
            for (int key : questionHashMap.keySet()) {
                //Cau hoi nay chua duoc tra loi
                Question qu = questionHashMap.get(key);
                if (answeredHashMap.get(qu.getId()) == null) {
                    q = qu;
                    number = key;
                }
            }
        }

        //TODO: Neu q la null cham diem

        current_question = number;
        txtNumber.setText(String.valueOf(number));
        String html = q.getContent();
        Spanned spanned = Html.fromHtml(html, imageGetterHandler, null);
        if (html.contains("<audio") && html.contains("</audio>")){
            playMediaFromHtml(html);
        }else{
            View v = view.findViewById(R.id.media_player);
            v.setVisibility(View.GONE);
        }
        txtQuestion.setText(spanned);

        rdbGroup.removeAllViews();
        final HashMap<Integer, Answer> answerArrayList = q.getAnswers();

        for (int i = 0; i < answerArrayList.size(); i++) {
            rdbGroup.addView(new View(getActivity()));
        }

        for (Answer a : answerArrayList.values()) {
            RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setId(a.getId());
            rdbtn.setText(a.getContent());

            Answer da_chon = answeredHashMap.get(q.getId());

            if (da_chon != null && a.getId() == da_chon.getId()) {
                rdbtn.setChecked(true);
            }

            rdbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int a_id = v.getId();
                    Answer c = answerArrayList.get(a_id);
                    answeredHashMap.put(c.getQuestion_id(), c);
                    showQuestion(current_question + 1);
                }
            });
            try {
                if (a.getContent().equals("A")) {
                    rdbGroup.removeViewAt(0);
                    rdbGroup.addView(rdbtn, 0);
                } else if (a.getContent().equals("B")) {
                    rdbGroup.removeViewAt(1);
                    rdbGroup.addView(rdbtn, 1);
                } else if (a.getContent().equals("C")) {
                    rdbGroup.removeViewAt(2);
                    rdbGroup.addView(rdbtn, 2);
                } else if (a.getContent().equals("D")) {
                    rdbGroup.removeViewAt(3);
                    rdbGroup.addView(rdbtn, 3);
                } else {
                    rdbGroup.addView(rdbtn);
                }
            } catch (Exception ex) {
                rdbGroup.addView(rdbtn);
            }
        }

        //Neu cau tra loi da duoc chon thi giu lai trang thai
        /*
        Answer da_chon = answeredHashMap.get(q.getId());
        if(da_chon != null) {
            Toast.makeText(getActivity(), "Da chon : " + da_chon.getId(), Toast.LENGTH_SHORT).show();
            rdbGroup.check(da_chon.getId());
        }*/
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
}
