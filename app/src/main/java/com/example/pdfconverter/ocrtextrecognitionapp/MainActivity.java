package com.example.pdfconverter.ocrtextrecognitionapp;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.pdfconverter.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by suhasbachewar on 10/5/16.
 */

public class MainActivity extends AppCompatActivity implements IOCRCallBack {

    private String mAPiKey = "03fc011fe088957"; //TODO Add your own Registered API key
    private boolean isOverlayRequired;
    private String mImageUrl;
    private String mLanguage;
    private TextView mTxtResult;
    private IOCRCallBack mIOCRCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mLanguage = "ara"; //Language
        mIOCRCallBack = this;
//               mImageUrl = "http://dl.a9t9.com/blog/ocr-online/screenshot.jpg"; // Image url to apply OCR API
//        mLanguage = "eng"; //Language

//        mImageUrl = "https://files.fm/u/kecxaa22#/view/fateh_1.png";
//        mImageUrl = "https://files.fm/u/2fpkpcka#/view/Screenshot_2.png";


//        mImageUrl = "https://iskandarfateh.com/wp-content/uploads/2018/02/islamic-wallpaper-hd-1080p-awesome-islamic-hd-wallpapers-1080p-wallpapersafari-of-islamic-wallpaper-hd-1080p.jpg";
//        mImageUrl = "https://i2.wp.com/www.photoarby.com/wp-content/uploads/2019/01/1-134.png?fit=604%2C605&ssl=1";
//        mImageUrl = "https://files.fm/u/rx3pw6x8#/view/Screenshot_1.png";
//        mImageUrl = "https://ladybirdar.com/wp-content/uploads/2017/05/%D8%A3%D8%AC%D9%85%D9%84-%D8%B9%D8%A8%D8%A7%D8%B1%D8%A7%D8%AA-%D8%B4%D9%88%D9%82.jpg";
//        mImageUrl = "https://wallimpex.com/data/out/451/hd-islamic-wallpaper-8204185.jpg";
//        mImageUrl = "https://files.fm/u/6kp2ssfw#/view/Screenshot_1.jpg";
        mImageUrl = "https://pasteboard.co/I9tG0VX.jpg";
        mImageUrl = " https://i.ibb.co/CQsRzHw/Screenshot-6.jpg";
        mImageUrl = "https://i.ibb.co/r5pMQL2/who-us.jpg";

        isOverlayRequired = true;
        init();

    }

    private void init() {
        mTxtResult = (TextView) findViewById(R.id.actual_result);
        TextView btnCallAPI = (TextView) findViewById(R.id.btn_call_api);
        if (btnCallAPI != null) {
            btnCallAPI.setOnClickListener(v -> {
                OCRAsyncTask oCRAsyncTask = new OCRAsyncTask(MainActivity.this,
                        mAPiKey, isOverlayRequired, mImageUrl, mLanguage, mIOCRCallBack);
                oCRAsyncTask.execute();

            });
        }
    }

    @Override
    public void getOCRCallBackResult(String response) {
        mTxtResult.setText(response);
    }
}
