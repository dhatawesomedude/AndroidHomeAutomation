package com.example.capstoneapp.app;

/**
 * Created by Orlando on 7/7/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class NFCActivity extends Activity{

    NdefMessage msgs[];
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_activity);

        final Button movieButton = (Button) findViewById(R.id.movie_button);
        final Button musicButton = (Button) findViewById(R.id.music_button);

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
                ;
            }
        }

    }

    private void sendNFCIntent(String receivedText){

        if (receivedText.contains("music")){
            Intent new_Intent = new Intent(NFCActivity.this, MovieActivity.class);
            new_Intent.putExtra("MOVIE", receivedText);
            startActivity(new_Intent);
        }
        else if (receivedText.contains("music")){
            Intent new_Intent = new Intent(NFCActivity.this, MusicActivity.class);
            new_Intent.putExtra("MUSIC", receivedText);
            startActivity(new_Intent);
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
}
