package com.example.datnguyen.appenglish;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.datnguyen.appenglish.References.Constant;
import com.example.datnguyen.appenglish.References.Database;
import com.example.datnguyen.appenglish.References.Questions;
import com.example.datnguyen.appenglish.References.Sentences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_main_repeat)
    Button btn_main_repeat;
    @BindView(R.id.btn_main_answer)
    Button btn_main_answer;

    public static Database database;
    ArrayList<Sentences> arrSentences;
    ArrayList<Questions> arrQuestions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //database

        database();
        //Event click
        eventClick();
    }

    //Event click
    private void eventClick(){
        btn_main_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRepeat = new Intent(MainActivity.this,ConfigActivity.class);
                intentRepeat.putExtra(Constant.TYPE_ACTION,Constant.TYPE_REPEAT);
                startActivity(intentRepeat);

            }
        });
        btn_main_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAnswer = new Intent(MainActivity.this,ConfigActivity.class);
                intentAnswer.putExtra(Constant.TYPE_ACTION,Constant.TYPE_ANSWER);
                startActivity(intentAnswer);
            }
        });
    }

    //database
    private void database(){
        //tạo db
        database = new Database(MainActivity.this, "english.sql", null, 1);
        //tạo table
        database.QueryData("CREATE TABLE IF NOT EXISTS Sentences(id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR(500))");
        database.QueryData("CREATE TABLE IF NOT EXISTS Questions(id INTEGER PRIMARY KEY AUTOINCREMENT, question VARCHAR(500), answer VARCHAR(500))");

        //Add data sentences
        Cursor curSentences = database.getData("SELECT * FROM Sentences");
        Cursor curQuestions = database.getData("SELECT * FROM Questions");
        if(curSentences.getCount()<=0){
            //add datas
            database.QueryData("INSERT INTO Sentences VALUES(null, 'hi')");
            database.QueryData("INSERT INTO Sentences VALUES(null, 'good morning')");
            database.QueryData("INSERT INTO Sentences VALUES(null, 'love you')");
            database.QueryData("INSERT INTO Sentences VALUES(null, 'baby')");
            database.QueryData("INSERT INTO Sentences VALUES(null, 'good job')");
        }
        if(curQuestions.getCount()<=0){
            //add datas
            database.QueryData("INSERT INTO Questions VALUES(null, 'Hello','Hi')");
            database.QueryData("INSERT INTO Questions VALUES(null, 'How are you?','I am fine')");
            database.QueryData("INSERT INTO Questions VALUES(null, 'Goodbye?','Bye')");
            database.QueryData("INSERT INTO Questions VALUES(null, 'What is your name?','My name is Jason')");
            database.QueryData("INSERT INTO Questions VALUES(null, 'How old are you?','22')");
        }
    }
}
