package com.nilscreation.healthfacts;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FactsAdapter extends RecyclerView.Adapter<FactsAdapter.MovieHolder> {

    Context context;
    private List<FactsModel> factList;
    FragmentActivity activity;
    private InterstitialAd mInterstitialAd;
    private int mCounter = 0;
    Random r = new Random();

    String title, mTitle;
    MyDBHelper myDBHelper;

    public FactsAdapter(Context context, List<FactsModel> movies, FragmentActivity activity) {
        this.context = context;
        factList = movies;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);

        FactsModel fact = factList.get(position);

        //set title
        holder.title.setText(fact.getTitle());
        holder.relativeLayout.setBackgroundColor(Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256)));

        String text = fact.getTitle();
//        readData();

        holder.itemView.startAnimation(animation);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.relativeLayout.setBackgroundColor(Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256)));

            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.likeButton.setSelected(!holder.likeButton.isSelected());

                myDBHelper = new MyDBHelper(holder.likeButton.getContext());
                mTitle = fact.getTitle();

                if (!holder.likeButton.isSelected()) {
                    myDBHelper.deleteData(mTitle);
                    holder.favourite.setImageResource(R.drawable.ic_like);
                    ImageViewCompat.setImageTintList(holder.favourite, ColorStateList.valueOf
                            (ContextCompat.getColor(holder.likeButton.getContext(), R.color.ic_color)));
                    Toast.makeText(holder.likeButton.getContext(), "Removed from Favourite", Toast.LENGTH_SHORT).show();

                } else {
                    myDBHelper.deleteandAdd(mTitle);
                    holder.favourite.setImageResource(R.drawable.ic_favourite);
                    ImageViewCompat.setImageTintList(holder.favourite, ColorStateList.valueOf
                            (ContextCompat.getColor(holder.likeButton.getContext(), R.color.red)));
                    Toast.makeText(holder.likeButton.getContext(), "Added to Favourite", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboardManager = (ClipboardManager) holder.copyButton.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("simple text", text);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(holder.copyButton.getContext(), "Text copied", Toast.LENGTH_SHORT).show();

            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (holder.saveButton.getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
                        ActivityCompat.requestPermissions((Activity) holder.saveButton.getContext(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        return;
                    }
                }

                Bitmap bitmap = Bitmap.createBitmap(holder.relativeLayout.getWidth(), holder.relativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                holder.relativeLayout.draw(canvas);

                saveImageToGallery(bitmap);
            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Replace your own action here
                String appUrl = text + " For More Health Facts & Tips download the app now " + "https://play.google.com/store/apps/details?id=" + holder.shareButton.getContext().getPackageName();

                Intent sharing = new Intent(Intent.ACTION_SEND);
                sharing.setType("text/plain");
                sharing.putExtra(Intent.EXTRA_SUBJECT, "Download Now");
                sharing.putExtra(Intent.EXTRA_TEXT, appUrl);
                holder.shareButton.getContext().startActivity(Intent.createChooser(sharing, "Share via"));

            }
        });

    }

    private void saveImageToGallery(Bitmap imageBitmap) {
        String savedImagePath;
        String imageFileName = "Photo_" + System.currentTimeMillis() + ".jpg";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoEditor");
            Uri imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream os = activity.getContentResolver().openOutputStream(imageUri)) {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                Toast.makeText(activity, "Image Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }
            savedImagePath = imageUri.toString();
        } else {
            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Sunglasses_PhotoEditor");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Toast.makeText(activity, "Image Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        galleryAddPic(savedImagePath);
//        EditActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    private void mInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, "ca-app-pub-9137303962163689/2088238601", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
//                        Toast.makeText(activity, "loaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }

    @Override
    public int getItemCount() {
        return factList.size();
    }

    public class MovieHolder extends RecyclerView.ViewHolder {
        CardView likeButton, copyButton, shareButton, saveButton;
        TextView title;
        LinearLayout constraintLayout;
        RelativeLayout relativeLayout;

        ImageView favourite;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.main_title);
            likeButton = itemView.findViewById(R.id.likebutton);
            copyButton = itemView.findViewById(R.id.copybutton);
            shareButton = itemView.findViewById(R.id.sharebutton);
            saveButton = itemView.findViewById(R.id.savebutton);
            constraintLayout = itemView.findViewById(R.id.main_layout);
            relativeLayout = itemView.findViewById(R.id.content);
            favourite = itemView.findViewById(R.id.imgfav);
        }
    }
}
