package com.example.datnguyen.appenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.datnguyen.appenglish.References.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {
    @BindView(R.id.list_review_sentences)
    ListView list_sentences;
    @BindView(R.id.list_review_speeches)
    ListView list_speeches;

    ArrayList<String> arrStringSentences = new ArrayList<>();
    ArrayList<String> arrStringSpeeches = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        getDataIntent();

        //show in list view
        ArrayAdapter<String> adapterSentences = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,arrStringSentences);
        ArrayAdapter<String> adapterSpeeches = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,arrStringSpeeches);

        list_sentences.setAdapter(adapterSentences);
        list_speeches.setAdapter(adapterSpeeches);
    }


    //get data from intent
    private void getDataIntent(){
        Bundle bundle = getIntent().getExtras();
        arrStringSentences = bundle.getStringArrayList(Constant.ARRAY_REPEAT_SENTENCES);
        arrStringSpeeches = bundle.getStringArrayList(Constant.ARRAY_REPEAT_SPEECHES);
    }
}
