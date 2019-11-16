package com.example.pdflib;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SegmentAdapter extends RecyclerView.Adapter<SegmentAdapter.SegmentViewHolder> {

    private ArrayList<Bitmap> segments;

    public SegmentAdapter(@NonNull ArrayList<Bitmap> segments) {
        this.segments = segments;
    }

    @NonNull
    @Override
    public SegmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SegmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SegmentViewHolder holder, int position) {
        holder.segmentView.setImageBitmap(segments.get(position));
        holder.segmentView.invalidate();
    }

    @Override
    public int getItemCount() {
        return segments.size();
    }

    class SegmentViewHolder extends RecyclerView.ViewHolder {
        ImageView segmentView;

        SegmentViewHolder(@NonNull View itemView) {
            super(itemView);
            segmentView = itemView.findViewById(R.id.pageView);
        }
    }
}
