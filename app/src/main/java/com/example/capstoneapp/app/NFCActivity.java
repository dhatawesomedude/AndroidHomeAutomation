package com.example.capstoneapp.app;

/**
 * Created by Orlando on 7/7/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import java.io.UnsupportedEncodingException;

public class NFCActivity extends Activity{

    NdefMessage msgs[];
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_activity);

        final Button movieButton = (Button) findViewById(R.id.movie_button);
        final Button musicButton = (Button) findViewById(R.id.music_button);
        final Button lightButton = (Button) findViewById(R.id.light_button);

        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent music_intent = new Intent(NFCActivity.this, MusicActivity.class);
                startActivity(music_intent);
            }
        });


        movieButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent movie_Intent = new Intent(NFCActivity.this, MovieActivity.class);
                startActivity(movie_Intent);
            }
        });

        lightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent light_intent = new Intent(NFCActivity.this, LightActivity.class);
                startActivity(light_intent);
           }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        String results;

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    NdefRecord[] records = msgs[i].getRecords();
                    for(NdefRecord ndefRecord : records){
                        if(ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN){
                            try{
                                String receivedText = readText(ndefRecord);
                                sendNFCIntent(receivedText);
                                //Toast.makeText(NFCActivity.this, readText(ndefRecord), Toast.LENGTH_SHORT).show();

                                /*Intent movie_Intent = new Intent(NFCActivity.this, MovieActivity.class);
                                movie_Intent.putExtra("MOVIE", receivedText);
                                startActivity(movie_Intent);*/
                            }catch (UnsupportedEncodingException e) {
                                Log.e("NFC", "Unsupported Encoding", e);
                            }
                        }
                    }
                    //Toast.makeText(MovieActivity.this, msgs[i].toString(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void sendNFCIntent(String receivedText){

        if (receivedText.contains("movie")){
            Intent new_Intent = new Intent(NFCActivity.this, MovieActivity.class);
            new_Intent.putExtra("MOVIE", receivedText);
            startActivity(new_Intent);
        }
        else if (receivedText.contains("music")){
            Intent new_Intent = new Intent(NFCActivity.this, MusicActivity.class);
            new_Intent.putExtra("MUSIC", receivedText);
            startActivity(new_Intent);
        }
        else if(receivedText.contains("lighton")){
            Intent new_Intent = new Intent(NFCActivity.this, LightActivity.class);
            new_Intent.putExtra("LIGHT", receivedText);
            startActivity(new_Intent);
        }
        else if(receivedText.contains("pause")){
            /*Intent new_Intent = new Intent(NFCActivity.this, LightActivity.class);
            new_Intent.putExtra("LIGHT", receivedText);
            startActivity(new_Intent);*/
            new HttpGetTask().execute("pause");
        }
        else if(receivedText.contains("stop")){
           /* Intent new_Intent = new Intent(NFCActivity.this, LightActivity.class);
            new_Intent.putExtra("LIGHT", receivedText);
            startActivity(new_Intent);*/
            new HttpGetTask().execute("stop");
        }
        else if(receivedText.contains("lightoff")){
            new HttpGetTask().execute("lampoff");
        }
        else {
            Toast.makeText(NFCActivity.this, "There is no record of this NFC tag", Toast.LENGTH_SHORT).show();
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
        protected void onPostExecute(String result) {



        }
    }
}
