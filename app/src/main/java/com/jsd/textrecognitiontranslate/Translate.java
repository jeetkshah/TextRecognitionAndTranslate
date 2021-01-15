package com.jsd.textrecognitiontranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


public class Translate extends AppCompatActivity {

    TextView txt_label, txt, translated;
    Spinner spinner;
    String Text;
    String langCode;
    String lang[] = {"Select a language to translate", "English", "Hindi","Gujarati"};

    FirebaseTranslatorOptions options;
    FirebaseTranslator englishGermanTranslator;
    ProgressDialog pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        txt_label = findViewById(R.id.txt_label);
        txt = findViewById(R.id.txt);
        translated = findViewById(R.id.translated);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Text = "Did not get data.";
        } else {
            Text = extras.getString("strText");
            if (Text.isEmpty()) {
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
                if (!s.equals("und")) {
//                    loc = new Locale(s);
//                    txt.setText(loc.getDisplayLanguage());
                    langCode = s;
                } else {
                    //txt.setText("Language not found.");
                    Toast.makeText(Translate.this, "Could not identify language.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lang);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 1) {
                    Toast.makeText(Translate.this, "English selected.", Toast.LENGTH_SHORT).show();

                    options = new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(langCode))
                            .setTargetLanguage(FirebaseTranslateLanguage.EN)
                            .build();
                    translate();

                } else if (i == 2) {
                    Toast.makeText(Translate.this, "Hindi selected.", Toast.LENGTH_SHORT).show();

                    options = new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(langCode))
                            .setTargetLanguage(FirebaseTranslateLanguage.HI)
                            .build();
                    translate();
                } else if (i == 3) {
                    Toast.makeText(Translate.this, "Gujarati selected.", Toast.LENGTH_SHORT).show();

                    options = new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(langCode))
                            .setTargetLanguage(FirebaseTranslateLanguage.GU)
                            .build();
                    translate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void translate(){
        englishGermanTranslator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        pb = new ProgressDialog(this);
        pb.setTitle("Please wait while we download the model or translate.");
        pb.show();
        pb.setCancelable(false);
        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                pb.cancel();
                                englishGermanTranslator.translate(Text)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(@NonNull String translatedText) {
                                                        translated.setText(translatedText);
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Error.
                                                        // ...
                                                    }
                                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                            }
                        });
    }
}
