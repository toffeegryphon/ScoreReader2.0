package com.example.pdfutils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdflib.DocumentRecycler;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Point display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        display = new Point();
        getWindowManager().getDefaultDisplay().getSize(display);
        Log.d("DISPLAY_WINDOW", display.toString());

        String storage = Environment.getExternalStorageDirectory().getPath();

        File demo = new File(storage + "/Download/[Free-scores.com]_chopin-frederic-nocturnes-opus-9-no-2-1508.pdf");

        final DocumentRecycler documentRecycler = new DocumentRecycler(
                demo,
                (RecyclerView) findViewById(R.id.documentsRecycler),
                display,
                (ViewGroup) findViewById(R.id.outlineContainer));

        Button getPositionButton = findViewById(R.id.getPositionButton);
        getPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OFFSET", String.valueOf(documentRecycler.bookmarkPosition()));
            }
        });

        Button scrollButton = findViewById(R.id.scrollButton);
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Bitmap> segments = documentRecycler.getSegments();
                Log.d("SEGMENTS", segments.toString());
                documentRecycler.finish();
            }
        });

        // TODO Move to different class
        SeekBar segmentHeightSeek = findViewById(R.id.segmentHeightSeek);

        // Initiate SeekBar to the correct Progress
        int progress = (int) (100 * (float) documentRecycler.getSegmentHeight() / display.y);
        segmentHeightSeek.setProgress(progress, true);

        segmentHeightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Animate changes in SegmentHeight
                int segmentHeight = (int) (display.y * seekBar.getProgress() / 100.0f);
                Log.d("SEGMENT_HEIGHT", String.valueOf(segmentHeight));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(display.x, segmentHeight);
                documentRecycler.getSegmentOutline().setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Save SegmentHeight on release only to prevent constantly changing the value and causing excess memory load
                int segmentHeight = (int) (display.y * seekBar.getProgress() / 100.0f);
                documentRecycler.setSegmentHeight(segmentHeight);
                Log.d("STOP_TRACK", String.valueOf(segmentHeight));
            }
        });
    }
}
