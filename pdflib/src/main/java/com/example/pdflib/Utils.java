package com.example.pdflib;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Utils {

    public static class Config {
        private String source;
        private LinkedHashMap<Double, Double> bookmarks;

        Config(String source, LinkedHashMap<Double, Double> bookmarks) {
            this.source = source;
            this.bookmarks = bookmarks;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public LinkedHashMap<Double, Double> getBookmarks() {
            return bookmarks;
        }

        public void setBookmarks(LinkedHashMap<Double, Double> bookmarks) {
            this.bookmarks = bookmarks;
        }

        @NotNull
        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%s: %s", source, bookmarks.toString());
        }
    }

    public static Config convert(String fileName){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String source = reader.readLine();
            LinkedHashMap<Double, Double> bookmarks = new LinkedHashMap<>();
            String bookmark = reader.readLine();
            while (bookmark != null) {
                String[] bookmarkArr = bookmark.split(":");
                bookmarks.put(Double.parseDouble(bookmarkArr[0]), Double.parseDouble(bookmarkArr[1]));
                bookmark = reader.readLine();
            }
            return new Config(source, bookmarks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Config(null, null);
    }

    // TODO be async
    public static ArrayList<Bitmap> generate(Config config, int width) {
        ArrayList<Bitmap> bookmarks = new ArrayList<>();
        File source = new File(config.getSource());
        try {
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(source, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(descriptor);

            // Get Required Values
            PdfRenderer.Page measure = renderer.openPage(0);
            float ratio = (float) measure.getHeight() / measure.getWidth();
            measure.close();
            Point pageSize = new Point(width, (int) (width * ratio));

            SparseArray<Bitmap> pages = new SparseArray<>();

            for (Map.Entry<Double, Double> bookmark : config.getBookmarks().entrySet()) {
                int pageNum = (int) Math.floor(bookmark.getKey());
                if (pages.get(pageNum) == null) {
                    PdfRenderer.Page page = renderer.openPage(pageNum);
                    Bitmap bitmap = Bitmap.createBitmap(pageSize.x, pageSize.y, Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    page.close();
                    pages.put(pageNum, bitmap);
                }

                bookmarks.add(Bitmap.createBitmap(
                        pages.get(pageNum),
                        0,
                        ((int) ((bookmark.getKey() - pageNum) * pageSize.y)),
                        pageSize.x, (int) (bookmark.getValue() * pageSize.y)));
            }
            return bookmarks;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
