package com.example.pdfutils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdflib.DocumentRecycler;
import com.example.pdflib.SegmentBuilder;

import java.io.File;

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
                display);

        final SegmentBuilder segmentBuilder = new SegmentBuilder(documentRecycler, (ViewGroup) findViewById(R.id.builderContainer));
    }
}
