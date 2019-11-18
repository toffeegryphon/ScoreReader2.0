package com.example.pdflib;

import android.content.Context;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DocumentRecycler {

    private Point displaySize;

    private RecyclerView recyclerView;

    public DocumentRecycler(@NonNull File document, @NonNull RecyclerView recyclerView, @NonNull Point displaySize) {


        this.displaySize = displaySize;

        try {
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(document, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(descriptor);

            // Initialise RecyclerView
            Context context = recyclerView.getContext();
            this.recyclerView = recyclerView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            // Initialise and Attach Adapter to RecyclerView
            DocumentAdapter adapter = new DocumentAdapter(Objects.requireNonNull(renderer), displaySize);
            recyclerView.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Point getDisplaySize() {
        return displaySize;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
