package com.example.mythreadtimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivityDownload extends AppCompatActivity {
    private static final String TAG = MainActivityDownload.class.getSimpleName();
    private static final String GREEK_BAND = "GreekBand";
    private TextView textView;
    private Button downloadbtn;
    private ProgressBar progressBar;
    public final static String DOWNLOAD_URL = " https://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int progress;
            if(msg.what == 0){
                Log.i(TAG, "handleMessage: " +msg.obj.toString());
                progress = (int)msg.obj;
                progressBar.setProgress(progress);
                textView.setText(String.valueOf(progress));
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_download);
        ActivityCompat.requestPermissions(MainActivityDownload.this,new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        initView();
        initEvent();
    }

    private void initEvent() {
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download(DOWNLOAD_URL);
                    }
                }).start();


            }
        });
    }

    private void download(String downloadUrl) {
        try {
            URL url = new URL(downloadUrl);
            URLConnection urlConnection = url.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            int contentLength = urlConnection.getContentLength();//要下载的文件的大小

            Log.i(TAG, "download: "+contentLength);
            //创建存储路径

//            String downloadFoldersName = Environment.getExternalStorageDirectory() + File.separator
//                    +"Android/data/com.example.mythreadtimer/files/"
//                    + GREEK_BAND +File.separator;
            String downloadFoldersName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+ File.separator;
            Log.i(TAG, "download: "+downloadFoldersName);
            if( ! Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                Log.i(TAG, "SD卡不存在 ");
                return;
            }
            File file = new File(downloadFoldersName);
            boolean ret;
            if(!file.exists()){

                if( file.mkdirs()) {
                    Log.i(TAG, "文件夹创建成功: ");
                }else{
                    Log.i(TAG, "文件夹创建失败: ");
                }

            }
            //重命名下载文件
            String fileName = downloadFoldersName + "test.apk";
            File apkFile = new File(fileName);
            if(apkFile.exists()){
                apkFile.delete();
            }

            int downloadSize = 0;

            byte[]  bytes = new byte[1024];
            int length = 0;

            OutputStream outputStream = new FileOutputStream(fileName);
            while((length = inputStream.read(bytes))!= -1){
               outputStream.write(bytes,0,length);
               downloadSize += length;

               int progress = downloadSize*100 /contentLength;
               Message msg = handler.obtainMessage();
               msg.what = 0;
               msg.obj = progress;
               handler.sendMessage(msg);
                Log.i(TAG, "download progress: ");
            }

            Log.i(TAG, "download: success");
            inputStream.close();
            outputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        textView = findViewById(R.id.textView);
        downloadbtn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}