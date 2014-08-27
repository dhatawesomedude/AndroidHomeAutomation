package com.example.capstoneapp.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

import org.json.JSONObject;

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
   ListView movieListView;
    String[] movieTitles = {
            "Major Crimes",
            "Twenty Four (24)",
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

        moviePlaying = (TextView)findViewById(R.id.music_now_playing);
        movieListView = (ListView)findViewById(R.id.movie_list_view);
        movieListView.setAdapter(adapter);
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MovieActivity.this, "You selected" +movieTitles[position], Toast.LENGTH_SHORT).show();
                PlayNotificationActivity notification = new PlayNotificationActivity(MovieActivity.this, movieTitles[position],thumbnailID[position]);
                new RecSockets().execute("hello");
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
                                readText(ndefRecord);
                                Toast.makeText(MovieActivity.this, readText(ndefRecord), Toast.LENGTH_SHORT).show();
                            }catch (UnsupportedEncodingException e) {
                                Log.e("NFC", "Unsupported Encoding", e);
                            }
                        }
                    }
                            //Toast.makeText(MovieActivity.this, msgs[i].toString(), Toast.LENGTH_LONG).show();
                }
                Toast.makeText(MovieActivity.this, msgs.toString(), Toast.LENGTH_LONG).show();
                Log.d("NFC", msgs.toString());
            }
        }

    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException{
        byte [] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength+1, payload.length - languageCodeLength - 1, textEncoding);
    }

    class RecSockets extends AsyncTask<String, Integer, String> {

        private SocketIO socket;


        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }*/

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

    }


}
