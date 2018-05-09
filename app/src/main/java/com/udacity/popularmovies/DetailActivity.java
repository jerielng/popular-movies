package com.udacity.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mRatingView;
    private TextView mReleaseDateView;
    private Button mFavoriteButton;

    private LinearLayout mTrailerList;
    private LinearLayout mReviewList;

    private String mPosterUrl;
    private String mTitle;
    private String mDescription;
    private double mRating;
    private String mReleaseDate;
    private String mId;

    private final String TRAILER_BASE_URL = "http://youtube.com/watch?v=";
    private final String PARAM_RESULTS = "results";
    private final String PARAM_KEY = "key";
    private final String PARAM_NAME = "name";

    private final String PARAM_AUTHOR = "author";
    private final String PARAM_CONTENT = "content";

    private String[] mTrailerKeys;
    private String[] mTrailerNames;

    private String[] mReviewAuthors;
    private String[] mReviewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterView = findViewById(R.id.poster_image);
        mTitleView = findViewById(R.id.title_view);
        mDescriptionView = findViewById(R.id.description_text);
        mRatingView = findViewById(R.id.rating_text);
        mReleaseDateView = findViewById(R.id.date_text);
        mFavoriteButton = findViewById(R.id.favorite_button);

        mTrailerList = findViewById(R.id.trailer_list);
        mReviewList = findViewById(R.id.review_list);

        /* Extract movie data from MainActivity Intent */
        Intent mainIntent = getIntent();
        Bundle movieDetails = mainIntent.getExtras();
        if (movieDetails != null) {
            mPosterUrl = movieDetails.getString(getString(R.string.poster_url));
            mTitle = movieDetails.getString(getString(R.string.title));
            mDescription = movieDetails.getString(getString(R.string.description));
            mRating = movieDetails.getDouble(getString(R.string.rating));
            mReleaseDate = movieDetails.getString(getString(R.string.release_date));
            mId = movieDetails.getString(getString(R.string.movie_id));
        }

        populateUI();
        new FetchTrailersTask().execute();
        new FetchReviewsTask().execute();
    }

    private void populateUI() {
        Picasso.with(this).load(mPosterUrl).into(mPosterView);
        mPosterView.setAdjustViewBounds(true);

        mTitleView.setText(mTitle);
        mDescriptionView.setText(mDescription);
        mRatingView.setText(getString(R.string.out_of_ten, mRating));
        mReleaseDateView.setText(mReleaseDate.substring(0, 4));

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL trailersRequestUrl = NetworkUtils.buildTrailersUrl(mId);
                return NetworkUtils.getResponseFromHttpUrl(trailersRequestUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            extractTrailerData(result);
            loadTrailerUI();
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL reviewsRequestUrl = NetworkUtils.buildReviewsUrl(mId);
                return NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            extractReviews(result);
            loadReviewUI();
        }
    }

    public void extractTrailerData(String trailersResponse) {
        try {
            JSONObject jsonTrailersObject = new JSONObject(trailersResponse);
            JSONArray trailersResults = jsonTrailersObject.getJSONArray(PARAM_RESULTS);
            mTrailerKeys = new String[trailersResults.length()];
            mTrailerNames = new String[trailersResults.length()];
            for (int i = 0; i < trailersResults.length(); i++)
            {
                mTrailerKeys[i] = trailersResults.getJSONObject(i).optString(PARAM_KEY);
                mTrailerNames[i] = trailersResults.getJSONObject(i).optString(PARAM_NAME);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadTrailerUI() {
        if (mTrailerKeys.length == 0) {
            TextView noTrailers = new TextView(this);
            noTrailers.setText(R.string.no_trailers);
            noTrailers.setPadding(0, 0, 0, 50);
            noTrailers.setTextSize(15);
            mTrailerList.addView(noTrailers);
        }
        else {
            for (int i = 0; i < mTrailerKeys.length; i++) {
                Button trailerItem = new Button(this);
                trailerItem.setText(mTrailerNames[i]);
                trailerItem.setPadding(0, 30, 0, 30);
                trailerItem.setTextSize(15);
                final String trailerUrl = TRAILER_BASE_URL + mTrailerKeys[i];
                trailerItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Referenced from Udacity's Exercises lesson on Webpages, Maps, and Sharing
                        Uri youtubeLink = Uri.parse(trailerUrl);
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeLink);
                        if (youtubeIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(youtubeIntent);
                        }
                    }
                });
                mTrailerList.addView(trailerItem);
            }
        }
    }

    public void extractReviews(String reviewsResponse) {
        try {
            JSONObject jsonReviewsObject = new JSONObject(reviewsResponse);
            JSONArray reviewsResults = jsonReviewsObject.getJSONArray(PARAM_RESULTS);
            mReviewAuthors = new String[reviewsResults.length()];
            mReviewContent = new String[reviewsResults.length()];
            for (int i = 0; i < reviewsResults.length(); i++)
            {
                mReviewAuthors[i] = reviewsResults.getJSONObject(i).optString(PARAM_AUTHOR);
                mReviewContent[i] = reviewsResults.getJSONObject(i).optString(PARAM_CONTENT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadReviewUI() {
        if (mReviewContent.length == 0) {
            TextView noReviews = new TextView(this);
            noReviews.setText(R.string.no_reviews);
            noReviews.setPadding(0, 0, 0, 50);
            noReviews.setTextSize(15);
            mReviewList.addView(noReviews);
        }
        else {
            for (int i = 0; i < mReviewContent.length; i++)
            {
//                LinearLayout reviewItem = new LinearLayout(this, R.layout.review_item);
//                TextView x = (TextView) reviewItem.findViewById(R.id.author_text);
//                x.setText(mReviewAuthors[i]);
//                mReviewList.addView(reviewItem);
            }
        }
    }
}
