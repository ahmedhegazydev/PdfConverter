package com.example.pdfconverter.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pdfconverter.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final Utils ourInstance = new Utils();
    private static final String TAG = Utils.class.getName();
    private int count = 0;

    private Utils() {
    }

    public static Utils getInstance() {
        return ourInstance;
    }

    /**
     * Loads an existing PDF and renders it to a Bitmap
     */
    public List<Bitmap> renderFile(Context context, String fileName, File root) {

        final AssetManager assetManager = context.getAssets();
        Bitmap pageImage = null;
        List<Bitmap> bitmaps = new ArrayList<>();
        // Render the page and save it to an image file
        try {
            // Load in an already created PDF
            PDDocument document = PDDocument.load(assetManager.open(fileName));
            // Create a renderer for the document
            PDFRenderer renderer = new PDFRenderer(document);
            // Render the image to an RGB Bitmap
//            pageImage = renderer.renderImage(0, 1, Bitmap.Config.RGB_565);
            for (; true; ) {
                try {
//                    pageImage = renderer.renderImage(count, 1, Bitmap.Config.RGB_565);
                    pageImage = renderer.renderImage(count, 2, Bitmap.Config.ARGB_8888);

                    bitmaps.add(pageImage);
                    count++;
                } catch (Exception e) {
                    break;
                }

            }
            // Save the render result to an image
//            String path = root.getAbsolutePath() + "/render.jpg";
//            File renderFile = new File(path);
//            FileOutputStream fileOut = new FileOutputStream(renderFile);
//            pageImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
//            fileOut.close();
//            Toast.makeText(context, " Successfully rendered image to" + path, Toast.LENGTH_SHORT).show();
            // Optional: display the render result on screen

        } catch (IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while rendering file", e);
        }


        return bitmaps;
    }

    /**
     * Strips the text from a PDF and displays the text on screen
     */
    public String stripText(Context context, String fileName) {

        final AssetManager assetManager = context.getAssets();
        Bitmap pageImage = null;

        String parsedText = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(assetManager.open(fileName));
        } catch (IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while loading document to strip", e);
        }

        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(0);
            pdfStripper.setEndPage(1);
            parsedText = "Parsed text: " + pdfStripper.getText(document);
        } catch (IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while stripping text", e);
        } finally {
            try {
                if (document != null) document.close();
            } catch (IOException e) {
                Log.e("PdfBox-Android-Sample", "Exception thrown while closing document", e);
            }
        }
        return parsedText;
    }

    public String extractTextFromBitmap(Context context, Bitmap bitmap) {
//        Log.e(TAG, "Eliwa");
        StringBuilder stringBuilder = null;
        // imageBitmap is the Bitmap image you're trying to process for text
        if (bitmap != null) {
            Log.e(TAG, "Eliwa");
            TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
            if (!textRecognizer.isOperational()) {
                // Note: The first time that an app using a Vision API is installed on a
                // device, GMS will download a native libraries to the device in order to do detection.
                // Usually this completes before the app is run for the first time.  But if that
                // download has not yet completed, then the above call will not detect any text,
                // barcodes, or faces.
                // isOperational() can be used to check if the required native libraries are currently
                // available.  The detectors will automatically become operational once the library
                // downloads complete on device.
                Log.w(TAG, "Detector dependencies are not yet available.");
                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null;
                if (hasLowStorage) {
                    Toast.makeText(context, "Low Storage", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Low Storage");
                }
            }
            Frame imageFrame = new Frame.Builder()
                    .setBitmap(bitmap)
                    .build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
            stringBuilder = new StringBuilder();

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                String text = textBlock.getValue();
//                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                Log.e(TAG, text);
                stringBuilder.append(text);
            }
        }
        assert stringBuilder != null;
        return stringBuilder.toString();
    }


}
