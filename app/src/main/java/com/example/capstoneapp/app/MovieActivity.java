package com.example.capstoneapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Orlando on 23/7/2014.
 */
public class MovieActivity extends Activity {
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

        movieListView = (ListView)findViewById(R.id.movie_list_view);
        movieListView.setAdapter(adapter);
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MovieActivity.this, "You selected" +movieTitles[position], Toast.LENGTH_SHORT).show();
                PlayNotificationActivity notification = new PlayNotificationActivity(MovieActivity.this, movieTitles[position],thumbnailID[position]);
            }
        });
    }


}
