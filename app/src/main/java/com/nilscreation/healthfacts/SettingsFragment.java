package com.nilscreation.healthfacts;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nilscreation.healthfacts.R;


public class SettingsFragment extends Fragment {

    LinearLayout btnSpeech, btnShare, btnRate, btnPrivacy, btnContact, btnMore;

    public SettingsFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnSpeech = view.findViewById(R.id.btnSpeech);
        btnShare = view.findViewById(R.id.btnShare);
        btnRate = view.findViewById(R.id.btnRate);
        btnPrivacy = view.findViewById(R.id.btnPrivacy);
        btnContact = view.findViewById(R.id.btnContact);
        btnMore = view.findViewById(R.id.btnMore);

        // Saving state of our app using SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.android.settings.TTS_SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appUrl = "For daily interesting facts download the app now." + "\nhttps://play.google.com/store/apps/details?id=" + getActivity().getApplicationContext().getPackageName();

                Intent sharing = new Intent(Intent.ACTION_SEND);
                sharing.setType("text/plain");
                sharing.putExtra(Intent.EXTRA_SUBJECT, "Download Now");
                sharing.putExtra(Intent.EXTRA_TEXT, appUrl);
                startActivity(Intent.createChooser(sharing, "Share via"));
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName()));
                startActivity(intent);
            }
        });

        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://thenilscreation.blogspot.com/p/daily-facts-privacy.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    String[] to = {"nilssonawanen1@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, to);
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
//                    intent.putExtra(Intent.EXTRA_TEXT, "");

                    startActivity(Intent.createChooser(intent, "Send Email"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://play.google.com/store/apps/developer?id=Nils+Creation";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        return view;
    }
}