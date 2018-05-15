package com.example.datnguyen.appenglish;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datnguyen.appenglish.References.Constant;
import com.example.datnguyen.appenglish.References.Sentences;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswerActivity extends AppCompatActivity {
    @BindView(R.id.txtv_answer_time)
    TextView txtv_duration;
    @BindView(R.id.txtv_answer_question)
    TextView txtv_question;
    @BindView(R.id.txtv_answer_speech)
    TextView txtv_speech;
    @BindView(R.id.imgview_answer_mic)
    ImageView imageview_mic;
    @BindView(R.id.txtv_answer_status)
    TextView txtv_status;


    //Recognize speech
    public SpeechRecognizer mSpeechRecognizer;
    public Intent mSpeechRecognizerIntent;


    public int duration;
    public ArrayList<Sentences> arrSentences;

    //some variables
    public int total_sentences = 0;
    public int current_sentence_pos = 0;
    public String current_speech_content = "";
    public String current_sentence_content = "";
    public String current_answer_content = "";

    public CountDownTimer mCountDownTimer;
    public long millisRemaining =  0;

    //User point
    public int point;
    //array speech
    public ArrayList<String> arrStringSentences;
    public ArrayList<String> arrStringAnswers;
    public ArrayList<String> arrStringSpeeches;


    //Sound correct
    public SoundPool soundPool;
    public AudioManager audioManager;
    private int mStreamVolume = 0;
    // Số luồng âm thanh phát ra tối đa.
    public static final int MAX_STREAMS = 5;
    // Chọn loại luồng âm thanh để phát nhạc.
    public static final int streamType = AudioManager.STREAM_MUSIC;
    public float volume;
    public boolean loaded;
    public int soundCorrect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        //check permission
        checkPermission();
        //
        ButterKnife.bind(this);
        //sound
        playSound();
        //set up
        txtv_status.setText(Constant.SETUP_RESULT);
        txtv_speech.setText("");

        //get sentences from database to ArrayList<Sentences>
        arrStringSentences = new ArrayList<>();
        arrStringAnswers = new ArrayList<>();
        arrStringSpeeches = new ArrayList<>();
        getSentences();
        duration +=1;
        total_sentences = arrStringSentences.size();

        //active speech
        activeSpeech();

        //run time
        activeDuration(duration*1000);
        //txtv_speech.setText("Listening...");
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        mStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // getting system volume into var for later un-muting
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0); // setting system volume to zero, muting


        //test
        showSentence(0);

        //point
        point = 0;


        //event click
        eventClick();
    }


    //get sentences
    private void getSentences(){
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        duration = bundle.getInt(Constant.DURATION_TIME);

        ArrayList<String> arrTemp = bundle.getStringArrayList(Constant.ARRAY_ANSWER_SENTENCES);
        ArrayList<String> arrTempAns = bundle.getStringArrayList(Constant.ARRAY_ANSWER_ANSWERS);
        if (arrTemp.size()>0){
            arrStringSentences = arrTemp;
            arrStringAnswers = arrTempAns;
        }else{
            Cursor curSentences = MainActivity.database.getData("SELECT * FROM Questions");
            while (curSentences.moveToNext()){
                String content = curSentences.getString(1);
                String answer = curSentences.getString(2);
                arrStringSentences.add(content);
                arrStringAnswers.add(answer);
            }
        }
    }

    //Apply duration
    public void activeDuration(long duration){

        mCountDownTimer = new CountDownTimer(duration, 500) {

            public void onTick(long millisUntilFinished) {
                Log.e("Dur:",millisUntilFinished+"");
                millisRemaining = millisUntilFinished;
                //set 0 value
                txtv_duration.setText(millisUntilFinished / 1000+"");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                if(mCountDownTimer != null){
                    //stop Counter
                    mCountDownTimer.cancel();
                }
                //save to array
                saveSpeechToArray(txtv_speech.getText().toString(),current_sentence_pos);
                //delay
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //set up
                        txtv_status.setText(Constant.SETUP_RESULT);
                        //next sentences
                        nextSentence();
                    }
                };
                txtv_status.setText(Constant.INCORRECT_RESULT);
                Handler handler = new Handler();
                handler.postDelayed(runnable, 1000);
            }
        }.start();

    }
    //resume time
    private void timerResume(long timeResume) {
        activeDuration(timeResume);
    }

    //Active API speech to text google
    public void activeSpeech(){
        //mute the beep sound
        //muteBeepSound();
        //
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getBaseContext());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0); // again setting the system volume back to the original, un-mutting
            }

            @Override
            public void onBeginningOfSpeech() {
                imageview_mic.setImageResource(R.drawable.ic_mic_black_24dp);
                mCountDownTimer.cancel();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                imageview_mic.setImageResource(R.drawable.ic_mic_none_black_24dp);
                timerResume(millisRemaining);
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                //check result
                if(!checkSpeechResult(current_speech_content)){
                    restartSpeechRecognizer();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                //speech results to text
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matches!=null){
                    current_speech_content=matches.get(0);
                    txtv_speech.setText(current_speech_content);
                }
                if(checkSpeechResult(current_speech_content)){
                    if(mCountDownTimer != null){
                        //stop Counter
                        mCountDownTimer.cancel();
                    }
                    //point +1
                    point ++;
                    //save to array
                    saveSpeechToArray(current_speech_content,current_sentence_pos);
                    //delay
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            //set up
                            txtv_status.setText(Constant.SETUP_RESULT);
                            //next sentences
                            nextSentence();
                        }
                    };
                    //play sound
                    if(loaded)  {
                        float leftVolumn = volume;
                        float rightVolumn = volume;
                        // Phát âm thanh tiếng súng. Trả về ID của luồng mới phát ra.
                        soundPool.play(soundCorrect,leftVolumn, rightVolumn, 1, 0, 1f);
                    }

                    txtv_status.setText(Constant.CORRECT_RESULT);
                    Handler handler = new Handler();
                    handler.postDelayed(runnable, 1000);
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    //Transfer next sentences
    public void nextSentence(){
        current_speech_content = "";
        txtv_speech.setText("");
        if(mCountDownTimer != null){
            //stop Counter
            mCountDownTimer.cancel();
        }
        current_sentence_pos ++;


        if(current_sentence_pos>total_sentences-1){
            //
            mSpeechRecognizer.stopListening();
            if(mSpeechRecognizer != null){
                mSpeechRecognizer.cancel();
            }

            //go to result
            Intent intent = new Intent(AnswerActivity.this,ResultActivity.class);
            //
            intent.putExtra(Constant.TOTAL_SENTENCES,total_sentences);
            intent.putExtra(Constant.POINT,point);
            intent.putExtra(Constant.TYPE_ACTION,Constant.TYPE_ANSWER);
            intent.putExtra(Constant.ARRAY_ANSWER_SENTENCES,arrStringSentences);
            intent.putExtra(Constant.ARRAY_ANSWER_SPEECHES,arrStringSpeeches);
            intent.putExtra(Constant.ARRAY_ANSWER_ANSWERS,arrStringAnswers);
            startActivity(intent);

            mSpeechRecognizer.destroy();
            this.finish();
        }else{
            restartSpeechRecognizer();
            activeDuration(duration*1000);
            showSentence(current_sentence_pos);
            //clear old result
        }

    }

    //Show current sentences
    public boolean showSentence(int curPos){
        if(curPos > total_sentences-1){
            return false;
        } else{
            String content = arrStringSentences.get(curPos);
            String answer = arrStringAnswers.get(curPos);
            txtv_question.setText(content);
            current_sentence_content = content;
            current_answer_content = answer;
            return true;
        }
    }

    //checkPermission
    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    //Mute the beep sound
    private void muteBeepSound(){
        AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        amanager.setStreamMute(AudioManager.STREAM_RING, true);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
    }

    //check speech with result
    private boolean checkSpeechResult(String speech){
        if(speech.trim().toLowerCase().equals(current_answer_content.trim().toLowerCase())){
            return true;
        }
        return false;
    }

    //restart speech listener
    private void restartSpeechRecognizer(){
        mSpeechRecognizer.stopListening();
        if(mSpeechRecognizer!=null)
        {
            mSpeechRecognizer.cancel();
        }
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    //save result speeches to array
    private void saveSpeechToArray(String content, int pos){
        arrStringSpeeches.add(pos,content);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCountDownTimer.cancel();
        mCountDownTimer = null;
        mSpeechRecognizer.cancel();
        mSpeechRecognizer.destroy();
        finish();
    }

    //event click
    private void eventClick(){
        imageview_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartSpeechRecognizer();
            }
        });
    }

    //sound correct
    private void playSound(){
        // Đối tượng AudioManager sử dụng để điều chỉnh âm lượng.
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Chỉ số âm lượng hiện tại của loại luồng nhạc cụ thể (streamType).
        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);

        // Chỉ số âm lượng tối đa của loại luồng nhạc cụ thể (streamType).
        float maxVolumeIndex  = (float) audioManager.getStreamMaxVolume(streamType);

        // Âm lượng  (0 --> 1)
        this.volume = currentVolumeIndex / maxVolumeIndex;
        // Cho phép thay đổi âm lượng các luồng kiểu 'streamType' bằng các nút
        // điều khiển của phần cứng.
        this.setVolumeControlStream(streamType);

        // Với phiên bản Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21 ) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        } // Với phiên bản Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // Sự kiện SoundPool đã tải lên bộ nhớ thành công.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        // Tải file nhạc correct
        this.soundCorrect = this.soundPool.load(this, R.raw.crow,1);

    }
}
