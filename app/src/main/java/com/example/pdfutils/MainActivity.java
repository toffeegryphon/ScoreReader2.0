package com.example.pdfutils;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdflib.DocumentRecycler;
import com.example.pdflib.SegmentAdapter;
import com.example.pdflib.SegmentBuilder;
import com.example.pdflib.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.example.pdflib.Utils.convert;
import static com.example.pdflib.Utils.generate;

public class MainActivity extends AppCompatActivity implements SegmentBuilder.OnSavedListener {

    private Point display;
    private String currentFilePath;

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

        Log.d("DATA", Arrays.toString(getFilesDir().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("txt");
            }
        })));
        Utils.Config result = convert(getFilesDir().getAbsolutePath() + "/bookmarks.txt");
        if (result.getSource() != null && result.getBookmarks() != null) {
            Log.d("CREATE", result.toString());
            ArrayList<Bitmap> bookmarks = generate(result);

            if (bookmarks != null) {
                final DocumentRecycler documentRecycler = new DocumentRecycler(
                        new File(result.getSource()),
                        (RecyclerView) findViewById(R.id.documentsRecycler),
                        display);

                SegmentAdapter segmentAdapter = new SegmentAdapter(bookmarks);
                ((RecyclerView) findViewById(R.id.documentsRecycler)).setAdapter(segmentAdapter);
            }
        }
//        search();
//        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Magic Fuckery to obtain file path from downloads provider
            Uri uri = data.getData();
            if (uri != null && Objects.equals(uri.getAuthority(), "com.android.providers.downloads.documents")) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(DocumentsContract.getDocumentId(uri)));

                try (Cursor cursor = getContentResolver().query(contentUri, new String[]{"_data"}, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                        Log.d("PATH", path);
                        currentFilePath = path;
                        start(new File(path));
                    }
                }
            }
        }
    }

    public void search() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        startActivityForResult(intent, 1);
    }

    public void start() {
        String storage = Environment.getExternalStorageDirectory().getPath();

        File demo = new File(storage + "/Download/[Free-scores.com]_chopin-frederic-nocturnes-opus-9-no-2-1508.pdf");
        currentFilePath = storage + "/Download/[Free-scores.com]_chopin-frederic-nocturnes-opus-9-no-2-1508.pdf";

        final DocumentRecycler documentRecycler = new DocumentRecycler(
                demo,
                (RecyclerView) findViewById(R.id.documentsRecycler),
                display);

        final SegmentBuilder segmentBuilder = new SegmentBuilder(this, documentRecycler, (ViewGroup) findViewById(R.id.builderContainer));
    }

    public void start(File file) {
        final DocumentRecycler documentRecycler = new DocumentRecycler(
                file,
                (RecyclerView) findViewById(R.id.documentsRecycler),
                display);

        final SegmentBuilder segmentBuilder = new SegmentBuilder(this, documentRecycler, (ViewGroup) findViewById(R.id.builderContainer));
    }

    @Override
    public void onSaved(LinkedHashMap<Double, Double> bookmarks) {
        // TODO implement SQL of source file to saved file
        StringBuilder data = new StringBuilder(String.format(Locale.getDefault(), "%s\n", currentFilePath));
        for (Map.Entry<Double, Double> bookmark : bookmarks.entrySet()) {
            data.append(String.format(Locale.getDefault(), "%s:%s\n", bookmark.getKey(), bookmark.getValue()));
        }
        Log.d("DATA", data.toString());

        File file = new File(getFilesDir(), "bookmarks.txt");
        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("bookmarks.txt", Context.MODE_PRIVATE));
            writer.write(data.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO return to home page
        // TODO convert home page with list of created .txt
    }
}
