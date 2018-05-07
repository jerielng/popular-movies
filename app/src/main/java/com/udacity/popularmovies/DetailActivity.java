package com.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mRatingView;
    private TextView mReleaseDateView;

    private String mPosterUrl;
    private String mTitle;
    private String mDescription;
    private double mRating;
    private String mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterView = findViewById(R.id.poster_image);
        mTitleView = findViewById(R.id.title_view);
        mDescriptionView = findViewById(R.id.description_text);
        mRatingView = findViewById(R.id.rating_text);
        mReleaseDateView = findViewById(R.id.date_text);

        Intent mainIntent = getIntent();
        Bundle movieDetails = mainIntent.getExtras();
        if (movieDetails != null) {
            mPosterUrl = movieDetails.getString(getString(R.string.poster_url));
            mTitle = movieDetails.getString(getString(R.string.title));
            mDescription = movieDetails.getString(getString(R.string.description));
            mRating = movieDetails.getDouble(getString(R.string.rating));
            mReleaseDate = movieDetails.getString(getString(R.string.release_date));
        }

        populateUI();
    }

    public void populateUI() {
        Picasso.with(this).load(mPosterUrl).into(mPosterView);

        mTitleView.setText(mTitle);
        mDescriptionView.setText(mDescription);
        mRatingView.setText(Double.toString(mRating));
        mReleaseDateView.setText(mReleaseDate);
    }
}
