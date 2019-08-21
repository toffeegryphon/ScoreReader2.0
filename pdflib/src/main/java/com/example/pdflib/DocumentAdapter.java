package com.example.pdflib;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private PdfRenderer renderer;
    private Point pageSize;

    DocumentAdapter(@NonNull PdfRenderer renderer, @NonNull Point displaySize) {
        this.renderer = renderer;

        // Get Required Values
        PdfRenderer.Page config = renderer.openPage(0);
        float ratio = (float) config.getHeight() / config.getWidth();
        config.close();

        pageSize = new Point(displaySize.x, (int) (displaySize.x * ratio)); // Assuming Same Page Size
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DocumentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        // Render PDF as Bitmap
        PdfRenderer.Page page = renderer.openPage(position);
        Bitmap bitmap = Bitmap.createBitmap(pageSize.x, pageSize.y, Bitmap.Config.ARGB_4444);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        // Set Bitmap as Source of ImageView
        holder.pageView.setImageBitmap(bitmap);
        holder.pageView.invalidate();
    }

    @Override
    public int getItemCount() {
        return renderer.getPageCount();
    }

    Point getPageSize() { // Wanted to put this in the parent class. But page size may change due to rotation
        return pageSize;
    }

    // DocumentViewHolder Sub-Class
    static class DocumentViewHolder extends RecyclerView.ViewHolder {

        ImageView pageView;

        DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            pageView = itemView.findViewById(R.id.pageView);
        }
    }
}
