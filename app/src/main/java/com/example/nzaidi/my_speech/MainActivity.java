 package com.example.nzaidi.my_speech;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nzaidi.my_speech.Model.CountryDataSource;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SPEAK_REQUEST = 10;
    TextView txt_value;
    Button btn_voice_intent;

    public static CountryDataSource countryDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_value = (TextView)findViewById(R.id.txtValue);
        btn_voice_intent = (Button)findViewById(R.id.btnVoiceIntent);


        btn_voice_intent.setOnClickListener(MainActivity.this);

        Hashtable<String, String> countriesAndMessages = new Hashtable<>();
        countriesAndMessages.put("Canada","Welcome to Canada. Happy Visiting");
        countriesAndMessages.put("France","Welcome to France. Happy Visiting");
        countriesAndMessages.put("Brazil","Welcome to Brazil. Happy Visiting");
        countriesAndMessages.put("United States","Welcome to United States. Happy Visiting");
        countriesAndMessages.put("Japan", "Welcome to Japan. Happy Visiting");

        countryDataSource = new CountryDataSource(countriesAndMessages);

        PackageManager packageManager = this.getPackageManager();

        List<ResolveInfo> listOfInformation = packageManager.queryIntentActivities(new Intent
                (RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (listOfInformation.size() > 0)
        {
            Toast.makeText(MainActivity.this,"Your device does support Speech Recognition" ,
                    Toast.LENGTH_SHORT).show();

            listenToTheUserVoice();

        }
        else
        {
            Toast.makeText(MainActivity.this,"Your device does not support Speech Recognition" ,
                    Toast.LENGTH_SHORT ).show();
        }


    }

    private void listenToTheUserVoice()
    {
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to Me!" );
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10 );

        startActivityForResult(voiceIntent,SPEAK_REQUEST );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEAK_REQUEST && resultCode == RESULT_OK)
        {
            ArrayList<String> voiceWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            float[] confidLevels = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

            /*int index = 0;
            for(String useWord : voiceWords)
            {
                if (confidLevels != null && index < confidLevels.length)
                {
                    txt_value.setText(useWord + " - " + confidLevels[index] );
                }
            }*/

            String countryMatchedWithUserWord = countryDataSource.matchWithMinimumConfidenceLevelOfUserWords
                                                         (voiceWords,confidLevels );

            Intent myMapActivity = new Intent(MainActivity.this,MapsActivity.class);
 ;           myMapActivity.putExtra(countryDataSource.COUNTRY_KEY,countryMatchedWithUserWord);
            startActivity(myMapActivity);

        }



    }

    @Override
    public void onClick(View view) {

        listenToTheUserVoice();

    }
}
