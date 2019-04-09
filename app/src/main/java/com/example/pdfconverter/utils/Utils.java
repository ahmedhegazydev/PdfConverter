package com.example.pdfconverter.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
    private static final Utils ourInstance = new Utils();

    private Utils() {
    }

    public static Utils getInstance() {
        return ourInstance;
    }

    /**
     * Loads an existing PDF and renders it to a Bitmap
     */
    public Bitmap renderFile(Context context, String fileName, File root) {

        final AssetManager assetManager = context.getAssets();
        Bitmap pageImage = null;

        // Render the page and save it to an image file
        try {
            // Load in an already created PDF
            PDDocument document = PDDocument.load(assetManager.open(fileName));
            // Create a renderer for the document
            PDFRenderer renderer = new PDFRenderer(document);
            // Render the image to an RGB Bitmap
            pageImage = renderer.renderImage(0, 1, Bitmap.Config.RGB_565);

            // Save the render result to an image
            String path = root.getAbsolutePath() + "/render.jpg";
            File renderFile = new File(path);
            FileOutputStream fileOut = new FileOutputStream(renderFile);
            pageImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
            fileOut.close();
            Toast.makeText(context, " Successfully rendered image to" + path, Toast.LENGTH_SHORT).show();
            // Optional: display the render result on screen

        } catch (IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while rendering file", e);
        }


        return pageImage;
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
        } catch(IOException e) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while loading document to strip", e);
        }

        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(0);
            pdfStripper.setEndPage(1);
            parsedText = "Parsed text: " + pdfStripper.getText(document);
        }
        catch (IOException e)
        {
            Log.e("PdfBox-Android-Sample", "Exception thrown while stripping text", e);
        } finally {
            try {
                if (document != null) document.close();
            }
            catch (IOException e)
            {
                Log.e("PdfBox-Android-Sample", "Exception thrown while closing document", e);
            }
        }
        return parsedText;
    }
}
