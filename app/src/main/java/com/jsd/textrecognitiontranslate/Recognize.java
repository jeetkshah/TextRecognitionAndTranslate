package com.jsd.textrecognitiontranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions;

import java.util.Locale;

public class Recognize extends AppCompatActivity {

    TextView txt, lang;
    String Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        txt = findViewById(R.id.txt);
        lang = findViewById(R.id.lang);

        Bundle extras = getIntent().getExtras();
        if(extras==null){
            Text = "Did not get data.";
        }
        else {
            Text = extras.getString("strText");
            if(Text.isEmpty()){
                Text = "Empty text was passed";
            }
        }

        txt.setText(Text);

        final FirebaseLanguageIdentification languageIdentifier =
                FirebaseNaturalLanguage.getInstance().getLanguageIdentification(
                        new FirebaseLanguageIdentificationOptions.Builder()
                                .setConfidenceThreshold(0.3f)
                                .build()
                );

        languageIdentifier.identifyLanguage(Text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(!s.equals("und")){
                    Locale loc = new Locale(s);
                    lang.setText(loc.getDisplayLanguage());
                }
                else{
                    lang.setText("Language not found.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
