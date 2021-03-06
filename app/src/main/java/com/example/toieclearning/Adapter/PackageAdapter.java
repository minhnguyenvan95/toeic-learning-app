package com.example.toieclearning.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.toieclearning.Api.ImageGetterHandler;
import com.example.toieclearning.R;
import com.example.toieclearning.modal.Answer;
import com.example.toieclearning.modal.Question;
import com.example.toieclearning.myInterface.OnPackageRdbSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 4/14/2017.
 */

public class PackageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<Question> questionArrayList;
    public HashMap<Integer, Answer> answeredHashMap;
    public HashMap<Integer, Answer> danhsachcauhoi;
    private String noidung_goicauhoi;
    public Boolean diem_flag = false;
    private OnPackageRdbSelectedListener listener;

    public ArrayList<Question> getQuestionArrayList() {
        return questionArrayList;
    }

    public HashMap<Integer, Answer> getDanhsachcauhoi() {
        return danhsachcauhoi;
    }

    public void setDanhsachcauhoi(HashMap<Integer, Answer> danhsachcauhoi) {
        this.danhsachcauhoi = danhsachcauhoi;
    }

    public void setQuestionArrayList(ArrayList<Question> questionArrayList) {
        this.questionArrayList = questionArrayList;
    }

    public Boolean getDiem_flag() {
        return diem_flag;
    }

    public void setDiem_flag(Boolean diem_flag) {
        this.diem_flag = diem_flag;
    }

    public String getNoidung_goicauhoi() {
        return noidung_goicauhoi;
    }

    public void setNoidung_goicauhoi(String noidung_goicauhoi) {
        this.noidung_goicauhoi = noidung_goicauhoi;
    }

    public HashMap<Integer, Answer> getAnsweredHashMap() {
        return answeredHashMap;
    }

    public PackageAdapter(Context context, ArrayList<Question> questions,OnPackageRdbSelectedListener l) {
        this.context = context;
        this.questionArrayList = questions;
        this.answeredHashMap = new HashMap<>();
        this.listener = l;
        this.danhsachcauhoi = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.package_item_adapter,parent,false);
        return new QuestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Question q = questionArrayList.get(position);
        if(q != null){
            QuestionViewHolder mHolder = (QuestionViewHolder) holder;
            String html = q.getContent();

            Spanned spanned = Html.fromHtml(html, new ImageGetterHandler(mHolder.question, context), null);
            mHolder.question.setText(spanned);


            final HashMap<Integer, Answer> answerArrayList = q.getAnswers();

            mHolder.answers.removeAllViews();

            for (int i = 0; i < answerArrayList.size(); i++) {
                mHolder.answers.addView(new View(context));
            }

            for (Answer a : answerArrayList.values()) {
                danhsachcauhoi.put(a.getId(),a);
                final RadioButton rdbtn = new RadioButton(context);
                rdbtn.setId(a.getId());
                rdbtn.setText(a.getContent());
                if (diem_flag){
                    if (a.isChecked()){
                        rdbtn.setTextColor(Color.BLUE);
                    }
                }

                Answer da_chon = answeredHashMap.get(q.getId());

                if (da_chon != null && a.getId() == da_chon.getId()) {
                    rdbtn.setChecked(true);
                    if (diem_flag){
                        if (da_chon.isChecked()){
                            rdbtn.setTextColor(Color.BLUE);
                        }
                        else {
                            rdbtn.setTextColor(Color.RED);
                        }
                    }
                }

                rdbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int a_id = v.getId();
                        Answer c = answerArrayList.get(a_id);
                        answeredHashMap.put(c.getQuestion_id(), c);
                        /*
                        if (c.isChecked()){
                            diem++;
                        }*/
                        if(answeredHashMap.size() == questionArrayList.size()){
                            listener.onAllRdbInPackageSelected();
                        }

                        if(listener != null)
                            listener.onPackageRdbSelected();

                    }
                });
                try {
                    if (a.getContent().equals("A")) {
                        mHolder.answers.removeViewAt(0);
                        mHolder.answers.addView(rdbtn, 0);
                    } else if (a.getContent().equals("B")) {
                        mHolder.answers.removeViewAt(1);
                        mHolder.answers.addView(rdbtn, 1);
                    } else if (a.getContent().equals("C")) {
                        mHolder.answers.removeViewAt(2);
                        mHolder.answers.addView(rdbtn, 2);
                    } else if (a.getContent().equals("D")) {
                        mHolder.answers.removeViewAt(3);
                        mHolder.answers.addView(rdbtn, 3);
                    } else {
                        mHolder.answers.addView(rdbtn);
                    }
                } catch (Exception ex) {
                    mHolder.answers.addView(rdbtn);
                }
            }

        }
        //mListener

    }

    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder{
        private TextView question;
        private RadioGroup answers;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            question = (TextView) itemView.findViewById(R.id.txtQuestion);
            answers = (RadioGroup) itemView.findViewById(R.id.rdbGroupAnswer);
        }
    }
    public void refreshEvents(ArrayList<Question> questionArrayList) {
        ArrayList<Question> temp = new ArrayList<>();
        temp = questionArrayList;
        questionArrayList.clear();
        questionArrayList.addAll(temp);
        notifyDataSetChanged();
    }
}
