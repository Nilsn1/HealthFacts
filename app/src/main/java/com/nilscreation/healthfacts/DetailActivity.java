package com.nilscreation.healthfacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    PhotoView photoView;
    String[] permission = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    TextToSpeech textToSpeech;
    TextView main_title, main_text, categoryName;
    ImageView btnBack, speakbtn, favourite, share;
    String mPoster, mTitle, mText, mCategory;
    String title;
    MyDBHelper myDBHelper;
    SwitchCompat switchMode;
    Boolean fav = false;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        photoView = findViewById(R.id.photoView);
        main_title = findViewById(R.id.main_title);
        main_text = findViewById(R.id.main_text);
        categoryName = findViewById(R.id.categoryName);
        btnBack = findViewById(R.id.btnBack);
        speakbtn = findViewById(R.id.speak);
        favourite = findViewById(R.id.favourite);
        share = findViewById(R.id.share);
        switchMode = findViewById(R.id.switchMode);

        nightMode();

        Bundle bundle = getIntent().getExtras();
        mPoster = bundle.getString("poster");
        mCategory = bundle.getString(("category"));
        mTitle = bundle.getString("title");
        mText = bundle.getString("text");

        Glide.with(this).load(mPoster).placeholder(R.drawable.app_logo).into(photoView);
        main_title.setText(mTitle);
        main_text.setText(mText);
        categoryName.setText(mCategory);

        //Banner Ads Admob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9137303962163689/7583844533");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        speakbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = main_title.getText().toString() + "." + main_text.getText().toString();

                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                    ImageViewCompat.setImageTintList(speakbtn, ColorStateList.valueOf
                            (ContextCompat.getColor(DetailActivity.this, R.color.ic_color)));
                } else {
                    textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                    ImageViewCompat.setImageTintList(speakbtn, ColorStateList.valueOf
                            (ContextCompat.getColor(DetailActivity.this, R.color.primary)));
                }
            }
        });

        readData();

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fav) {
                    myDBHelper.deleteData(mTitle);
                    favourite.setImageResource(R.drawable.ic_like);
                    ImageViewCompat.setImageTintList(favourite, ColorStateList.valueOf
                            (ContextCompat.getColor(DetailActivity.this, R.color.ic_color)));
                    Toast.makeText(DetailActivity.this, "Removed from Favourite", Toast.LENGTH_SHORT).show();
                    fav = false;

                } else {
                    myDBHelper.deleteandAdd(mTitle);
                    favourite.setImageResource(R.drawable.ic_favourite);
                    ImageViewCompat.setImageTintList(favourite, ColorStateList.valueOf
                            (ContextCompat.getColor(DetailActivity.this, R.color.red)));
                    Toast.makeText(DetailActivity.this, "Added to Favourite", Toast.LENGTH_SHORT).show();
                    fav = true;
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) photoView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap);
            }
        });
    }

    private void readData() {
        myDBHelper = new MyDBHelper(DetailActivity.this);
        ArrayList<FactsModel> facts = myDBHelper.readData();

        for (int i = 0; i < facts.size(); i++) {
            title = facts.get(i).title;

            if (mTitle.equals(title)) {
                favourite.setImageResource(R.drawable.ic_favourite);
                ImageViewCompat.setImageTintList(favourite, ColorStateList.valueOf
                        (ContextCompat.getColor(DetailActivity.this, R.color.red)));
                fav = true;
            } else {
                favourite.setImageResource(R.drawable.ic_like);
                ImageViewCompat.setImageTintList(favourite, ColorStateList.valueOf
                        (ContextCompat.getColor(DetailActivity.this, R.color.ic_color)));
                fav = false;
            }
        }
    }

    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getmageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_STREAM, uri);

        intent.putExtra(Intent.EXTRA_TEXT, mTitle + "\n" + mText + "\n\n" +
                "For more interesting facts download the app now." +
                "\nhttps://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());

        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        intent.setType("image/png");

        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    // Retrieving the url to share
    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "fact.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, "com.nilscreation.dailyfacts", file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
//            textToSpeech = null;
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
//            textToSpeech = null;
        }
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        nightMode();

        textToSpeech = new TextToSpeech(DetailActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
//                    int lang = textToSpeech.setLanguage(Locale.forLanguageTag("en"));
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        super.onResume();
    }

    private void nightMode() {
        //NIGHT MODE
        // Saving state of our app using SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        // When user reopens the app after applying dark/light mode
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchMode.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchMode.setChecked(false);
        }

        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchMode.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                }
            }
        });
    }
}