package com.example.capstoneapp.app;

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Created by Orlando on 28/8/2014.
 */
public class MusicActivity extends Activity{

    private TextView musicPlaying;
    private String data = " ";
    ListView musicListView;
    String[] movieTitles = {
            "music",
            "music",
            "music",
            "music",
            "music"
    };

    Integer[] thumbnailID = {
            R.drawable.music_icon,
            R.drawable.music_logo,
            R.drawable.music_icon,
            R.drawable.music_logo,
            R.drawable.music_icon
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);
        MovieList adapter = new MovieList(MusicActivity.this, movieTitles, thumbnailID);


        Button stopButton = (Button)findViewById(R.id.music_stop);
        Button pauseButton = (Button)findViewById(R.id.music_pause);
        musicPlaying = (TextView)findViewById(R.id.movie_now_playing);
        musicListView = (ListView)findViewById(R.id.music_list_view);
        musicListView.setAdapter(adapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MusicActivity.this, "You selected" + movieTitles[position], Toast.LENGTH_SHORT).show();
                PlayNotificationActivity notification = new PlayNotificationActivity(MusicActivity.this, movieTitles[position],thumbnailID[position]);
                //new RecSockets().execute("hello");
                //String movieName = movieTitles[position].split(" ", 2)[0] + "%20" + movieTitles[position].split(" ", 2)[1];
                String movieName = convertMusicTitle(movieTitles[position]);
                new HttpGetTask().execute("music");
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
        playIntent(getIntent());
    }

    public void playIntent(Intent intent){
        try {
            if (intent.getExtras().getString("MUSIC") != null) {
                data = intent.getExtras().getString("MUSIC");
                Toast.makeText(MusicActivity.this, data, Toast.LENGTH_SHORT).show();
                musicListView.requestFocusFromTouch();
                musicListView.setSelection(0);
                musicListView.performItemClick(musicListView.getAdapter().getView(0, null, null), 0, musicListView.getItemIdAtPosition(3));
            }
        }
        catch(NullPointerException e){
            Log.e("ERROR", e.toString());
            e.printStackTrace();
        }
    }

   /* @Override
    public void onResume(){
        super.onResume();

        try {
            if (getIntent().getExtras().getString("MUSIC") != null) {
                data = getIntent().getExtras().getString("MUSIC");
                Toast.makeText(MusicActivity.this, data, Toast.LENGTH_SHORT).show();
                musicListView.requestFocusFromTouch();
                musicListView.setSelection(0);
                musicListView.performItemClick(musicListView.getAdapter().getView(0, null, null), 0, musicListView.getItemIdAtPosition(3));
            }
        }
        catch(NullPointerException e){
            Log.e("ERROR", e.toString());
            e.printStackTrace();
        }
    }*/

    private String convertMusicTitle(String name){
        String movieName = name;
        if (movieName.indexOf(' ') != -1){
            return movieName.split(" ", 2)[0] + "%20" + movieName.split(" ", 2)[1];
            //return originalName;
        }
        else return movieName;
    }

    private class HttpGetTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "HttpGetTask";

        // Get your own user name at http://www.geonames.org/login
        private static final String MOVIE_NAME = "movie-major crimes";

        private static final String URL = "http://mmu-foe-capstone.appspot.com/control?group=22&msg=music";
        // + MOVIE_NAME;

        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(String... params) {

            String MUSIC = params[0];
            String MUSIC_URL = "http://mmu-foe-capstone.appspot.com/control?group=22&msg="+ MUSIC;
            HttpGet request = new HttpGet(MUSIC_URL);
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
            musicPlaying.setVisibility(TextView.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {

            if (null != mClient)
                mClient.close();

            musicPlaying.setVisibility(TextView.VISIBLE);
            musicPlaying.setText(result);

        }
    }
}
