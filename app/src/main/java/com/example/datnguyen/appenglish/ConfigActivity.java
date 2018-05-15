package com.example.datnguyen.appenglish;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.FilePicker;
import com.applandeo.constants.FileType;
import com.applandeo.listeners.OnSelectFileListener;
import com.example.datnguyen.appenglish.References.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Recerve: Value type of action: 0 or 1
 * Send: to 2 case:
 *      - Duaration: User input
 *      - File or not
 *          + File example: I go to school, I go die, I want to go, ...
 *      - If don't use file: send database
 *          Class: Sentences or Questions class
 */
public class ConfigActivity extends AppCompatActivity {
    @BindView(R.id.btn_config_start)
    Button btn_start;
    @BindView(R.id.edt_config_duration)
    EditText edt_duration;
    @BindView(R.id.btn_config_open_file)
    Button btn_open_file;
    @BindView(R.id.txtv_config_filepath)
    TextView txtv_filepath;
    private int typeAction = 0;

    public ArrayList<String> arrSentences = new ArrayList<>();
    public ArrayList<String> arrQuestions = new ArrayList<>();
    public ArrayList<String> arrAnswers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        //get type action
        Bundle bundle = getIntent().getExtras();
        typeAction = bundle.getInt(Constant.TYPE_ACTION);
        Toast.makeText(this, "Type action:"+typeAction, Toast.LENGTH_SHORT).show();
        //event click
        eventClick();
    }

    //event Click
    private void eventClick(){
        btn_start.setOnClickListener(new View.OnClickListener() {
            int duration = 0;
            @Override
            public void onClick(View v) {
                if(edt_duration.getText().toString().equals("")){
                    Toast.makeText(ConfigActivity.this, "Input your time!", Toast.LENGTH_SHORT).show();
                }else{
                    duration = Integer.parseInt(edt_duration.getText().toString());
                    switch (typeAction){
                        case Constant.TYPE_REPEAT:
                            Intent intentRep = new Intent(ConfigActivity.this,RepeatActivity.class);
                            intentRep.putExtra(Constant.DURATION_TIME,duration);
                            intentRep.putExtra(Constant.ARRAY_REPEAT_SENTENCES,arrSentences);
                            startActivity(intentRep);
                            break;
                        case Constant.TYPE_ANSWER:
                            Intent intentAns = new Intent(ConfigActivity.this,AnswerActivity.class);
                            intentAns.putExtra(Constant.DURATION_TIME,duration);
                            intentAns.putExtra(Constant.ARRAY_ANSWER_SENTENCES,arrQuestions);
                            intentAns.putExtra(Constant.ARRAY_ANSWER_ANSWERS,arrAnswers);
                            startActivity(intentAns);
                            break;
                        default:
                            break;
                    }
                    finish();
                }
            }
        });

        btn_open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
    }

    //Open file picker
    private void openFilePicker(){
        new FilePicker.Builder(this, new OnSelectFileListener() {
            @Override
            public void onSelect(File file) {
                //handle
                txtv_filepath.setText(file.getPath());
                //get text from file
                String text = readTextFile(file.getPath());
                //split text
                String[] datas = text.split(",");
                Log.e("TEXT GET: ",text);

                arrQuestions.clear();
                arrSentences.clear();
                arrAnswers.clear();

                //if type = REPEAT or type = ANSWER
                switch (typeAction){
                    case Constant.TYPE_REPEAT:
                        for(int i=0;i<datas.length;i++){
                            arrSentences.add(datas[i].trim());
                        }
                        break;
                    case Constant.TYPE_ANSWER:
                        for(int i=0;i<datas.length/2;i++){
                            arrQuestions.add(datas[i*2].trim());
                            arrAnswers.add(datas[i*2+1].trim());
                        }
                        break;
                    default:
                        break;
                }

            }
        })
                //this method let you decide how far user can go up in the directories tree
                .mainDirectory(Environment.getExternalStorageDirectory().getPath())
                //this method let you choose what types of files user will see in the picker
                .fileType(FileType.TEXT)
                //this method let you hide files, only directories will be visible for user
                .hideFiles(false)
                //this method let you decide which directory user will see after picker opening
                .directory(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS)
                .show();
    }

    //Read TextDocument file .txt
    public String readTextFile(String filePath){
        File file = new File(filePath);
        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && requestCode == FilePicker.STORAGE_PERMISSIONS) {
            openFilePicker();
        }
    }
}
