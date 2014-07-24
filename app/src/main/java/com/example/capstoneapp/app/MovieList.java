/**
 * Created by Orlando on 23/7/2014.
 */
package com.example.capstoneapp.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MovieList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] movieTitles;
    private final Integer[] thumbnailID;

    public MovieList(Activity context, String[] movieTitles, Integer[] thumbnailID){
        super(context, R.layout.movie_single_row, movieTitles);
        this.context = context;
        this.movieTitles = movieTitles;
        this.thumbnailID = thumbnailID;
    }


    @Override
    public View getView(int key, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        ImageView movieThumbnail = null;

        View movieRow = inflater.inflate(R.layout.movie_single_row, null, true);
        //View movieRow = LayoutInflater.from(context).inflate(R.layout.movie_single_row, null);
        TextView movieTitle = (TextView) movieRow.findViewById(R.id.movie_title);
        movieThumbnail = (ImageView) movieRow.findViewById(R.id.movie_thumbnail);
        movieTitle.setText(movieTitles[key]);
        movieThumbnail.setImageResource(thumbnailID[key]);
        return movieRow;



    }


}
