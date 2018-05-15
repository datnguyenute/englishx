package com.example.datnguyen.appenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.datnguyen.appenglish.References.Constant;
import com.example.datnguyen.appenglish.References.Sentences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultActivity extends AppCompatActivity {
    @BindView(R.id.txtv_result_point)
    TextView txtv_point;
    @BindView(R.id.btn_result_again)
    Button btn_again;
    @BindView(R.id.btn_result_review)
    Button btn_review;
    @BindView(R.id.btn_result_home)
    Button btn_home;

    public int total_sentences = 0;
    public int point = 0;
    public int type_action = 0;

    ArrayList<String> arrStringSentences = new ArrayList<>();
    ArrayList<String> arrStringSpeeches = new ArrayList<>();
    ArrayList<String> arrStringAnswers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        //get data from sentences
        getDataIntent();

        //click event
        eventClick();

        //show data
        txtv_point.setText(point+"/"+total_sentences);
    }

    //event click()
    private void eventClick(){
        btn_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this,ReviewActivity.class);
                intent.putExtra(Constant.ARRAY_REPEAT_SENTENCES,arrStringSentences);
                intent.putExtra(Constant.ARRAY_REPEAT_SPEECHES,arrStringSpeeches);
                startActivity(intent);
            }
        });
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //get data intent
    private void getDataIntent(){
        Bundle bundle = getIntent().getExtras();
        type_action = bundle.getInt(Constant.TYPE_ACTION);
        switch (type_action){
            case Constant.TYPE_REPEAT:
                total_sentences = bundle.getInt(Constant.TOTAL_SENTENCES);
                point = bundle.getInt(Constant.POINT);
                arrStringSentences = bundle.getStringArrayList(Constant.ARRAY_REPEAT_SENTENCES);
                arrStringSpeeches = bundle.getStringArrayList(Constant.ARRAY_REPEAT_SPEECHES);
                break;
            case Constant.TYPE_ANSWER:
                total_sentences = bundle.getInt(Constant.TOTAL_SENTENCES);
                point = bundle.getInt(Constant.POINT);
                arrStringSentences = bundle.getStringArrayList(Constant.ARRAY_ANSWER_SENTENCES);
                arrStringSpeeches = bundle.getStringArrayList(Constant.ARRAY_ANSWER_SPEECHES);
                arrStringAnswers = bundle.getStringArrayList(Constant.ARRAY_ANSWER_ANSWERS);

                for(int i=0;i<arrStringSentences.size();i++){
                    arrStringSentences.set(i,arrStringSentences.get(i)+" (Answer: "+arrStringAnswers.get(i)+")");
                }
                break;
            default:
                break;
        }

    }
}
