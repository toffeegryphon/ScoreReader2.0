package com.example.pdflib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class SegmentBuilder {

    private Point displaySize;

    private ArrayList<Integer> bookmarks;
    private ArrayList<Bitmap> segments;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private ViewGroup segmentBuilder;

    private int segmentHeight = 440;

    // TODO allow own implementation of segment building
    public SegmentBuilder(@NonNull DocumentRecycler documentRecycler, @NonNull ViewGroup container) {
        displaySize = documentRecycler.getDisplaySize();
        recyclerView = documentRecycler.getRecyclerView();
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        adapter = recyclerView.getAdapter();

        Context context = recyclerView.getContext();
        segmentBuilder = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.segment_builder, container, false);
        container.addView(segmentBuilder);

        ViewGroup outlineContainer = segmentBuilder.findViewById(R.id.outlineContainer);
        final FrameLayout segmentOutline = (FrameLayout) LayoutInflater.from(context).inflate(
                R.layout.segment_outline,
                outlineContainer,
                false);
        outlineContainer.addView(segmentOutline);
        segmentOutline.getLayoutParams().height = segmentHeight;

        // Bookmark Button
        FloatingActionButton bookmarkButton = segmentBuilder.findViewById(R.id.bookmarkButton);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarkPosition();
            }
        });

        // Finish Button
        FloatingActionButton finishButton = segmentBuilder.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // SeekBar
        SeekBar segmentHeightSeeker = segmentBuilder.findViewById(R.id.segmentHeightSeeker);

        // Initiate SeekBar to the correct Progress
        int progress = (int) (100 * (float) segmentHeight / displaySize.y);
        segmentHeightSeeker.setProgress(progress, true);

        segmentHeightSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Animate changes in SegmentHeight
                int segmentHeight = (int) (displaySize.y * seekBar.getProgress() / 100.0f);
                Log.d("SEGMENT_HEIGHT", String.valueOf(segmentHeight));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(displaySize.x, segmentHeight);
                segmentOutline.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Save SegmentHeight on release only to prevent constantly changing the value and causing excess memory load
                segmentHeight = (int) (displaySize.y * seekBar.getProgress() / 100.0f);
                Log.d("STOP_TRACK", String.valueOf(segmentHeight));
            }
        });

        // TODO Consider: Space vs Speed? Save bookmarks - utilises less space, but Save segments - faster.
        // TODO Change bookmarks to HashMap / Add a bookmarks, segmentHeight HashMap (if it is to be saved)
        bookmarks = new ArrayList<>();
        segments = new ArrayList<>();
    }

    // Add current Offset Position to Bookmarks
    private void bookmarkPosition() {
        int position = recyclerView.computeVerticalScrollOffset();
        bookmarks.add(position); // TODO Include a Check before adding. E.g. if within 5% of another bookmark, prompt user.
        segments.add(calculate(position, ((DocumentAdapter) adapter).getPageSize().y));
        Log.d("BOOKMARKS", bookmarks.toString());
        Log.d("SEGMENTS", segments.toString());

    }

    private Bitmap calculate(int bookmark, int pageHeight) { // TODO segmentHeight should probably be an argument
        int page = (int) Math.floor(bookmark / pageHeight);
        Log.d("PAGE_POSITION", String.valueOf(page));

        // View assumed to exist since it needs to be on screen when you bookmark it.
        ImageView pageView = Objects.requireNonNull(layoutManager.findViewByPosition(page)).findViewById(R.id.pageView);
        Bitmap original = ((BitmapDrawable) pageView.getDrawable()).getBitmap();

        int tempHeight = segmentHeight;
        int y = bookmark - page * pageHeight;
        if (y + tempHeight > pageHeight) tempHeight = pageHeight - y;
        return Bitmap.createBitmap(original, 0, bookmark - page * pageHeight, displaySize.x, tempHeight);
    }

    private void finish() {
        EditText bpmEdit = segmentBuilder.findViewById(R.id.bpmEdit);
        EditText bpbEdit = segmentBuilder.findViewById(R.id.bpbEdit);
        EditText bplEdit = segmentBuilder.findViewById(R.id.bplEdit);
        String bpmString = bpmEdit.getText().toString();
        String bpbString = bpbEdit.getText().toString();
        String bplString = bplEdit.getText().toString();
        if (bpmString.equals("") || bpbString.equals("") || bplString.equals("")) {
            Toast.makeText(recyclerView.getContext(), "Enter Beats Per Minute, Beats Per Bar, and Bars Per Line", Toast.LENGTH_LONG).show();
            return;
        }

        int bpm = Integer.parseInt(bpmString);
        int bpb = Integer.parseInt(bpbString);
        int bpl = Integer.parseInt(bplString);
        int dps = (int) (60.0f / bpm * bpb * bpl);
        Log.d("DURATION", String.valueOf(dps));

        SegmentAdapter segmentAdapter = new SegmentAdapter(segments);
        recyclerView.setAdapter(segmentAdapter);
        segmentBuilder.setVisibility(View.GONE);
    }
}

