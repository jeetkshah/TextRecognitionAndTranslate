package com.jsd.textrecognitiontranslate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    TextView txt_tip;
    Button recognize, translate;
    Button choose;

    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.img);
        txt_tip = findViewById(R.id.txt_tip);
        choose = findViewById(R.id.btn_detect);
        recognize = findViewById(R.id.btn_Recognize);
        translate = findViewById(R.id.btn_Translate);

        recognize.setEnabled(false);
        translate.setEnabled(false);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "select an image.."), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null) {
            txt_tip.setVisibility(View.GONE);
            img.setImageURI(data.getData());

            recognize.setEnabled(true);
            recognize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, Recognize.class);
                    i.putExtra("strText", text);
                    startActivity(i);
                }
            });

            translate.setEnabled(true);
            translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, Translate.class);
                    i.putExtra("strText", text);
                    startActivity(i);
                }
            });

            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());

                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                text = "";
                                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                    text = text.concat(block.getText());
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
