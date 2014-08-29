package com.example.capstoneapp.app;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by Orlando on 23/7/2014.
 */
public class MovieActivity extends Activity {

    private TextView moviePlaying;

    NdefMessage msgs[];
    private String data = " ";
   ListView movieListView;
    String[] movieTitles = {
            "Major Crimes",
            "Twenty Four(24)",
            "Suits",
            "Power",
            "Hello Cupid"
    };

    Integer[] thumbnailID = {
            R.drawable.major_crimes,
            R.drawable.twenty_four,
            R.drawable.suits,
            R.drawable.power,
            R.drawable.hello_cupid
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies);
        MovieList adapter = new MovieList(MovieActivity.this, movieTitles, thumbnailID);

        Button stopButton = (Button)findViewById(R.id.movie_stop);
        Button pauseButton = (Button)findViewById(R.id.movie_pause);

        moviePlaying = (TextView)findViewById(R.id.music_now_playing);
        movieListView = (ListView)findViewById(R.id.movie_list_view);
        movieListView.setAdapter(adapter);
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MovieActivity.this, "You selected" +movieTitles[position], Toast.LENGTH_SHORT).show();
                PlayNotificationActivity notification = new PlayNotificationActivity(MovieActivity.this, movieTitles[position],thumbnailID[position]);
                //new RecSockets().execute("hello");
                //String movieName = movieTitles[position].split(" ", 2)[0] + "%20" + movieTitles[position].split(" ", 2)[1];
                String movieName = convertMovieTitle(movieTitles[position]);
                new HttpGetTask().execute(movieName);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpGetTask().execute("pause");
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpGetTask().execute("stop");
            }
        });
    }

    private String convertMovieTitle(String name){
        String movieName = name;
        if (movieName.indexOf(' ') != -1){
            return movieName.split(" ", 2)[0] + "%20" + movieName.split(" ", 2)[1];
            //return originalName;
        }
        else return movieName;
    }

    @Override
    public void onResume(){
        super.onResume();

        try {
            if (getIntent().getExtras().getString("MOVIE") != null) {
                data = getIntent().getExtras().getString("MOVIE");
                Toast.makeText(MovieActivity.this, data, Toast.LENGTH_SHORT).show();
                movieListView.requestFocusFromTouch();
                movieListView.setSelection(0);
                movieListView.performItemClick(movieListView.getAdapter().getView(0, null, null), 0, movieListView.getItemIdAtPosition(3));
            }
        }
        catch(NullPointerException e){
            Log.e("ERROR", e.toString());
            e.printStackTrace();
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException{
        byte [] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength+1, payload.length - languageCodeLength - 1, textEncoding);
    }

    private class HttpGetTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "HttpGetTask";

        // Get your own user name at http://www.geonames.org/login
        private static final String MOVIE_NAME = "movie-major crimes";

        private static final String URL = "http://mmu-foe-capstone.appspot.com/control?group=22&msg=movie-major%20crimes";
               // + MOVIE_NAME;

        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(String... params) {

            String MOVIE = params[0];
            String MOVIE_URL = "http://mmu-foe-capstone.appspot.com/control?group=22&msg="+MOVIE;
            HttpGet request = new HttpGet(MOVIE_URL);
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
            moviePlaying.setVisibility(TextView.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {

            if (null != mClient)
                mClient.close();

            moviePlaying.setVisibility(TextView.VISIBLE);
            moviePlaying.setText(result);

        }
    }
/*    class RecSockets extends AsyncTask<String, Integer, String> {

        private SocketIO socket;


        *//*@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }*//*

        @Override
        protected String doInBackground(String... params) {
            String data = params[0];
            //final String jsonmsg = new String();

            try {
                SocketIO.setDefaultSSLSocketFactory(SSLContext.getDefault());
                socket = new SocketIO("http://192.168.0.112:3000/");
                Log.d("SOCKET SUCCESS", "socket created");



                socket.connect(new IOCallback() {


                    @Override
                    public void onDisconnect() {
                        Log.e("ERROR", "Disconnected");

                    }

                    @Override
                    public void onConnect() {
                        Log.d("SUCCESS", "Connection Established");
                    }

                    @Override
                    public void onMessage(String s, IOAcknowledge ioAcknowledge) {
                        Log.d("SUCCESS", "Message Received");
                    }

                    @Override
                    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
                        Log.d("SUCCESS", "Message Received");

                    }

                    @Override
                    public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {

                        Object[] arguments = objects;
                        String jsonmsg = arguments[0].toString();
                        Log.d("SUCCESS", "Message Received");

                        if (s.equals("play")) {
                            String myVar = jsonmsg;
                        }

                        int values = 1;
                        publishProgress(values);
                    }

                    @Override
                    public void onError(SocketIOException error) {
                        Log.e("ERROR", error.toString());
                    }
                });


            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            catch (NoSuchAlgorithmException ALGO_ERROR){
                Log.d("ERROR", ALGO_ERROR.toString());
            }
            return data;
        }



        @Override
        protected void onPreExecute(){
            moviePlaying.setVisibility(TextView.VISIBLE);
        }

        protected void onPostExecute(String result) {
            moviePlaying.setVisibility(TextView.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //UPdate UI...


        }


        private void sleep() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.e("ERROR", e.toString());
            }
        }

    }*/


}
