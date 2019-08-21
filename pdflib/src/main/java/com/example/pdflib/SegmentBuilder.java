package com.example.pdflib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class SegmentBuilder {

    private Point displaySize;

    private ArrayList<Integer> bookmarks;
    private ArrayList<Bitmap> segments;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private FrameLayout segmentOutline;

    private int segmentHeight = 440;

    public SegmentBuilder(@NonNull DocumentRecycler documentRecycler, @NonNull ViewGroup outlineContainer) {
        displaySize = documentRecycler.getDisplaySize();
        recyclerView = documentRecycler.getRecyclerView();
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        adapter = recyclerView.getAdapter();

        Context context = recyclerView.getContext();
        segmentOutline = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.segment_outline, outlineContainer, false);
        outlineContainer.addView(segmentOutline);
        segmentOutline.getLayoutParams().height = segmentHeight;

        // TODO Consider: Space vs Speed? Save bookmarks - utilises less space, but Save segments - faster.
        // TODO Change bookmarks to HashMap / Add a bookmarks, segmentHeight HashMap (if it is to be saved)
        bookmarks = new ArrayList<>();
        segments = new ArrayList<>();
    }

    // Get Current Offset Position
    public int bookmarkPosition() {
        int position = recyclerView.computeVerticalScrollOffset();
        bookmarks.add(position); // TODO Include a Check before adding. E.g. if within 5% of another bookmark, prompt user.
        segments.add(calculate(position, ((DocumentAdapter) adapter).getPageSize().y));
        Log.d("BOOKMARKS", bookmarks.toString());
        Log.d("SEGMENTS", segments.toString());

        return position;
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

    public void finish() {
        SegmentAdapter segmentAdapter = new SegmentAdapter(segments);
        recyclerView.setAdapter(segmentAdapter);
    }

    public int getSegmentHeight() {
        return segmentHeight;
    }

    public void setSegmentHeight(int height) {
        segmentHeight = height;
        segmentOutline.setLayoutParams(new FrameLayout.LayoutParams(displaySize.x, height));
    }

    public FrameLayout getSegmentOutline() {
        return segmentOutline;
    }

    public ArrayList<Bitmap> getSegments() {
        return segments;

    }
}

