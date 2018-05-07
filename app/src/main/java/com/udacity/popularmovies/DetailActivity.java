package com.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView posterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        posterView = findViewById(R.id.poster_image);
        populateUI();

        Intent mainIntent = getIntent();
        //mainIntent.getExtras();
    }

    public void populateUI() {
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w185/";
        String posterUrl = POSTER_BASE_URL + POSTER_SIZE + "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
        Picasso.with(this).load(posterUrl).into(posterView);
    }
}
