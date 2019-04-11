package com.example.pdfconverter.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
//import android.support.annotation.Nullable;
//import android.support.constraint.solver.widgets.Rectangle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pdfconverter.R;
import com.example.pdfconverter.utils.Utils;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_CAMERA_AND_LOCATION = 11;
    File root;
    @BindView(R.id.ll)
    LinearLayout linearLayout;
    TessBaseAPI mTess;
    String datapath, language;
    String ocrResult = null;
    private AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        methodRequiresTwoPermission();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setup();
//        displayRenderedImage(Utils.getInstance().renderFile(this, "fateha.pdf", root));
//        displayRenderedImage(Utils.getInstance()
//                .renderFile(this, "short_pdf.pdf", root).get(0));
//        Log.e(TAG, Utils.getInstance().stripText(this, "fateha.pdf"));
//        displayRenderedImage(Utils.getInstance()
//                .renderFile(this, "short_pdf.pdf", root));
//        List<Bitmap> bitmaps = Utils.getInstance()
//                .renderFile(this, "pdf_ar_more_pages.pdf", root);
//        displayRenderedImage(Utils.getInstance()
//                .renderFile(this, "pdf_ar_more_pages.pdf", root));
////        extractTextFromImage(bitmaps.get(0));
        initTessData();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.curious);
//        extractTextFromImage(bitmap);

    }

    private void initTessData() {
        //initialize Tesseract API
        language = "eng";
//        language = "ara";
        datapath = getFilesDir() + "/tesseract/";
        //Log.e("fileDriectPath", datapath);
        mTess = new TessBaseAPI();
        checkFile(new File(datapath + "tessdata/"));
//        try {
        mTess.init(datapath, language);
//        mTess.init(Environment.getExternalStorageDirectory().toString(), “eng”);
//        //processImage(null);
    }

    List<Bitmap> bitmaps = new ArrayList<>();

    private String extractTextUsingGoogleVision(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);
        StringBuilder stringBuilder = new StringBuilder();

        if (!textRecognizer.isOperational()) {
        } else {
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.get(i);
                if (textBlock.getValue() != null && textBlock != null) {
                    stringBuilder.append(textBlock.getValue());
                    stringBuilder.append("\n");
                }
            }

        }
        String s = stringBuilder.toString();
        Log.e("ahmed201300", s);
        return s;
    }

    private String extractTextFromImage(Bitmap bitmap) {
        //init image
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.test_imag1);
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image2);
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image3);

        mTess.setImage(bitmap);
        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        ocrResult = mTess.getUTF8Text();
//        tvExtractedText.setText(ocrResult);
//        Log.e(TAG, ocrResult);
        mTess.end();
        Toast.makeText(this, ocrResult, Toast.LENGTH_SHORT).show();
        return ocrResult;
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//        }
//        return "";
    }

    private void checkFile(File dir) {


        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(getApplicationContext());
            Log.e(TAG, "copyFiles");
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = datapath + "tessdata/eng.traineddata";
            Log.e(TAG, datafilepath);
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(getApplicationContext());
                Log.e(TAG, "Don't exist");
            }
        }
    }

    private void copyFiles(Context context) {
      /*  We need to do a little extra to get a proper value for datapath because of the way Android
        handles assets. At runtime, assets may only be accessed with raw byte streams via an AssetManager,
                meaning files in our asset folder are not accessible by filepath. To get around this, we need
        to copy the language data file into the device's
        internal or external storage at runtime, and then use that path to initialize Tesseract.*/

        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = context.getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes variables used for convenience
     */
    private void setup() {
        // Enable Android-style asset loading (highly recommended)
        PDFBoxResourceLoader.init(getApplicationContext());
        // Find the root of the external storage.
        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        assetManager = getAssets();

        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method for drawing the result of renderFile() on screen
     */
    private void displayRenderedImage(final List<Bitmap> bitmapList) {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Bitmap bitmap1 :
                                bitmapList) {

                            bitmaps.add(bitmap1);
                            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_item, null);
                            ImageView imageView = new ImageView(getApplicationContext());

//                            ImageView imageView = view.findViewById(R.id.iv);
//                            TextView textView = view.findViewById(R.id.tv);

//                            imageView.setImageBitmap(bitmap1);
//                            textView.setText(extractTextFromImage(bitmap1));
                            imageView.setImageBitmap(bitmap1);
                            imageView.setOnClickListener(v -> {
//                                    AlertDialog.Builder builder;
//                                    AlertDialog alertDialog;
//
//                                    builder = new AlertDialog.Builder(MainActivity.this)
//                                            .setMessage(extractTextFromImage(bitmap1))
//                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                }
//                                            });
//                                    alertDialog = builder.create();
//                                    if (alertDialog.isShowing()) {
//                                        alertDialog.show();
//                                    }
                                extractTextUsingGoogleVision(bitmap1);
//                                extractTextFromImage(bitmap1);
//                                new AsyncTasExtractTextFromImage(null, null).execute(bitmap1);

                            });
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                            linearLayout.addView(imageView, layoutParams);
                        }

//                        extractTextFromImage(bitmaps.get(0));
                    }
                });
            }
        }.start();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        displayRenderedImage(Utils.getInstance()
                .renderFile(this, "pdf_ar_more_pages.pdf", root));
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        finish();
    }

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            displayRenderedImage(Utils.getInstance()
                    .renderFile(this, "short_pdf.pdf", root));
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.storage),
                    RC_CAMERA_AND_LOCATION, perms);
        }
    }

    public class AsyncTasExtractTextFromImage extends AsyncTask<Bitmap, Void, String> {

        ProgressBar progressBar = null;
        Bitmap bitmap = null;

        public AsyncTasExtractTextFromImage(ProgressBar progressBar, Bitmap bitmap) {
            this.progressBar = progressBar;
            this.bitmap = bitmap;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            return extractTextFromImage(bitmaps[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

}