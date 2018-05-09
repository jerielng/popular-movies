package com.udacity.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mPosterGrid;

    private String mSortType;

    /* String values for JSON parsing */
    private final String PARAM_MOVIE_RESULTS = "results";
    private final String PARAM_POSTER_PATH = "poster_path";
    private final String PARAM_TITLE = "title";
    private final String PARAM_OVERVIEW = "overview";
    private final String PARAM_VOTE_AVERAGE = "vote_average";
    private final String PARAM_RELEASE_DATE = "release_date";
    private final String PARAM_ID = "id";

    /* URL components for retrieving poster images */
    private final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private final String POSTER_SIZE = "w185/";

    /* Storage for parsed JSON data */
    private String[] mPosterPaths;
    private String[] mTitleList;
    private String[] mDescriptionList;
    private double[] mRatingList;
    private String[] mDateList;
    private String[] mIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPosterGrid = findViewById(R.id.poster_grid);
        mSortType = this.getString(R.string.popular_sort); //Sets "Most Popular" as default sort
        new FetchMoviesTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case (R.id.sort_most_pop):
                mSortType = this.getString(R.string.popular_sort);
                new FetchMoviesTask().execute();
                return true;
            case (R.id.sort_highest_rated):
                mSortType = this.getString(R.string.highest_rated_sort);
                new FetchMoviesTask().execute();
                return true;
            case (R.id.sort_favorites):
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL movieRequestUrl = NetworkUtils.buildMovieUrl(mSortType);
                return NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            extractMovieData(result);
            populateUI();
        }
    }

    private void extractMovieData(String jsonString) {
        try {
            JSONObject moviesObject = new JSONObject(jsonString);
            JSONArray movieResults = moviesObject.getJSONArray(PARAM_MOVIE_RESULTS);
            mPosterPaths = new String[movieResults.length()];
            mTitleList = new String[movieResults.length()];
            mDescriptionList = new String[movieResults.length()];
            mRatingList = new double[movieResults.length()];
            mDateList = new String[movieResults.length()];
            mIdList = new String[movieResults.length()];
            for (int i = 0; i < movieResults.length(); i++) {
                mPosterPaths[i] = movieResults.getJSONObject(i).optString(PARAM_POSTER_PATH);
                mTitleList[i] = movieResults.getJSONObject(i).optString(PARAM_TITLE);
                mDescriptionList[i] = movieResults.getJSONObject(i).optString(PARAM_OVERVIEW);
                mRatingList[i] = movieResults.getJSONObject(i).optDouble(PARAM_VOTE_AVERAGE);
                mDateList[i] = movieResults.getJSONObject(i).optString(PARAM_RELEASE_DATE);
                mIdList[i] = movieResults.getJSONObject(i).optString(PARAM_ID);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateUI() {
        mPosterGrid.removeAllViews();

        LinearLayout.LayoutParams rowLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        rowLayoutParams.weight = 1;
        LinearLayout.LayoutParams posterLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        posterLayoutParams.weight = 1;

        //Generate new grid row
        LinearLayout gridRow = new LinearLayout(this);
        gridRow.setLayoutParams(rowLayoutParams);
        gridRow.setOrientation(LinearLayout.HORIZONTAL);
        mPosterGrid.addView(gridRow);

        for (int i = 0; i < mPosterPaths.length; i++) {
            if (gridRow.getChildCount() >= 2) { //If grid row is already full, generate new one.
                gridRow = new LinearLayout(this);
                gridRow.setLayoutParams(rowLayoutParams);
                gridRow.setOrientation(LinearLayout.HORIZONTAL);
                mPosterGrid.addView(gridRow);
            }

            final String posterUrl = POSTER_BASE_URL + POSTER_SIZE + mPosterPaths[i];
            ImageView poster = new ImageView(this);
            poster.setLayoutParams(posterLayoutParams);
            Picasso.with(this).load(posterUrl).into(poster);
            poster.setAdjustViewBounds(true);

            /* Sends movie data into DetailActivity via Intent */
            final String title = mTitleList[i];
            final String description = mDescriptionList[i];
            final double rating = mRatingList[i];
            final String releaseDate = mDateList[i];
            final String id = mIdList[i];
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                    detailIntent.putExtra(getString(R.string.poster_url), posterUrl);
                    detailIntent.putExtra(getString(R.string.title), title);
                    detailIntent.putExtra(getString(R.string.description), description);
                    detailIntent.putExtra(getString(R.string.rating), rating);
                    detailIntent.putExtra(getString(R.string.release_date), releaseDate);
                    detailIntent.putExtra(getString(R.string.movie_id), id);
                    startActivity(detailIntent);
                }
            });

            gridRow.addView(poster);
        }
    }
}
