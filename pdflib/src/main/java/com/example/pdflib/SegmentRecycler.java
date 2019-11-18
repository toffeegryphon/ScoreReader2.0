package com.example.pdflib;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SegmentRecycler {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SegmentAdapter segmentAdapter;

    private int mode; // Edit or View

    public SegmentRecycler(RecyclerView recyclerView, ArrayList<Bitmap> segments) {
        this.recyclerView = recyclerView;
        this.layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        this.segmentAdapter = new SegmentAdapter(segments);

        recyclerView.setAdapter(segmentAdapter);
    }

    public void scroll() {
        if (layoutManager.findLastCompletelyVisibleItemPosition() != segmentAdapter.getItemCount()-1) {
            layoutManager.scrollToPositionWithOffset(layoutManager.findFirstVisibleItemPosition()+1, 0);
        }
    }
}
