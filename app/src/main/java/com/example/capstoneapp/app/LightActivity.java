package com.example.capstoneapp.app;

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Created by Orlando on 29/8/2014.
 */
public class LightActivity extends Activity {

    Button OffLightButton;
    Button OnLightButton;
    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.light);


        OnLightButton = (Button) findViewById(R.id.light_on);
        OffLightButton = (Button) findViewById(R.id.light_off);

        OnLightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new HttpGetTask().execute("lampon");
            }
        });

        OffLightButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new HttpGetTask().execute("lampoff");
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        String data;

        try {
            if (getIntent().getExtras().getString("LIGHT") != null) {
                data = getIntent().getExtras().getString("LIGHT");
                Toast.makeText(LightActivity.this, data, Toast.LENGTH_SHORT).show();
                OnLightButton.requestFocusFromTouch();
                OnLightButton.performClick();
            }
        }
        catch(NullPointerException e){
            Log.e("ERROR", e.toString());
            e.printStackTrace();
        }
    }

    private class HttpGetTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "HttpGetTask";

        // Get your own user name at http://www.geonames.org/login
       // private static final String MOVIE_NAME = "movie-major crimes";

        //private static final String URL = "http://mmu-foe-capstone.appspot.com/control?group=22&msg=movie-major%20crimes";
        // + MOVIE_NAME;

        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(String... params) {

            String LIGHT_COMMAND = params[0];
            String LIGHT_URL = "http://mmu-foe-capstone.appspot.com/control?group=22&msg="+LIGHT_COMMAND;
            HttpGet request = new HttpGet(LIGHT_URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            try {

                return mClient.execute(request, responseHandler);

            } catch (ClientProtocolException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            //moviePlaying.setVisibility(TextView.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {

            if (null != mClient)
                mClient.close();

            //moviePlaying.setVisibility(TextView.VISIBLE);
            //moviePlaying.setText(result);
            Toast.makeText(LightActivity.this, "Status : " + result, Toast.LENGTH_SHORT).show();

        }
    }
}
