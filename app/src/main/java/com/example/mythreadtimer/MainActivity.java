package com.example.mythreadtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "KIN";
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, " Thread run: ");
                //textView.setText("Thread running");
                //第一种方式
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText("Thread running 1");
//                    }
//                });
                //第二种方式
//                textView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText("Thread running 2");
//                    }
//                });
                //第三种
//                textView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText("Thread running 3");
//                    }
//                },1000);
                //第四种
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText("Thread running 4");
//                    }
//                });

            }

        }).start();

        new TestClass().execute(2,3,4);
    }

    class TestClass extends AsyncTask<Integer,Integer,String>{
        @Override
        protected void onPreExecute() {
            //main Thread ,before doInBackground
            textView.setText("loading...");
            Log.e(TAG, "onPreExecute: ");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            //main Thread ,after doInBackground
            super.onPostExecute(s);
            textView.setText("finished..."+s);//s 是 doInBackground 返回的值
            Log.e(TAG, "onPostExecute: "+s );
        }

        @Override
        protected String doInBackground(Integer... integers) {
            //work Thread
            Log.e(TAG, "doInBackground: ");
            return String.valueOf(integers[0] * 2+8); //integers[] 为execute()调用时传的参数
        }
    }
}