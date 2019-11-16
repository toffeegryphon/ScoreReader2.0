package com.example.pdflib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;

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
            Log.d("CREATE", source);
            LinkedHashMap<Double, Double> bookmarks = new LinkedHashMap<>();
            String bookmark = reader.readLine();
            while (bookmark != null) {
                Log.d("CREATE", bookmark);
                String[] bookmarkArr = bookmark.split(":");
                bookmarks.put(Double.parseDouble(bookmarkArr[0]), Double.parseDouble(bookmarkArr[1]));
                bookmark = reader.readLine();
            }
            Log.d("CREATE", String.valueOf(bookmarks));
            return new Config(source, bookmarks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Config(null, null);
    }

    // TODO return bitmaps
    public static ArrayList<Bitmap> generate(Config config) {
        ArrayList<Bitmap> bookmarks = new ArrayList<>();
        File source = new File(config.getSource());
        try {
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(source, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(descriptor);

            // Get Required Values
            PdfRenderer.Page check = renderer.openPage(0);
            Point pageSize = new Point(check.getWidth(), check.getHeight());
            check.close();
            Log.d("CONVERT_PAGE_SIZE", pageSize.toString());

            for (Map.Entry<Double, Double> bookmark : config.getBookmarks().entrySet()) {
                int pageNum = (int) Math.floor(bookmark.getKey());
                Log.d("CONVERT", String.valueOf(pageNum));
                PdfRenderer.Page page = renderer.openPage(pageNum);
                //TODO MAJOR just get the final bitmap instead of this two step process
                Bitmap bitmap = Bitmap.createBitmap(pageSize.x, pageSize.y, Bitmap.Config.ARGB_4444);
                Rect area = new Rect(
                        0,
                        ((int) ((bookmark.getKey() - pageNum) * pageSize.y)),
                        pageSize.x,
                        ((int) (pageSize.y * bookmark.getValue()))
                );
                Log.d("CONVERT_AREA", area.toShortString());
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                bookmarks.add(Bitmap.createBitmap(bitmap, 0, ((int) ((bookmark.getKey() - pageNum) * pageSize.y)), pageSize.x, ((int) (pageSize.y * bookmark.getValue()))));
            }

            Log.d("CONVERT_BOOKMARKS", bookmarks.toString());
            return bookmarks;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
