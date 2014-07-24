package com.example.capstoneapp.app;

/**
 * Created by Orlando on 7/7/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NFCActivity extends Activity{
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_activity);

        final Button musicButton = (Button) findViewById(R.id.movie_button);

        musicButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent movie_Intent = new Intent(NFCActivity.this, MovieActivity.class);
                startActivity(movie_Intent);
            }
        });
    }
}
