package com.udacity.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.data.FavoritesProvider;
import com.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private GridLayout mPosterGrid;
    private ScrollView mScrollView;

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

    private final String savedSort = "Saved Sort Type";
    private final String xCoord = "X-Coordinates";
    private final String yCoord = "Y-Coordinates";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScrollView = findViewById(R.id.scroll_view);
        mPosterGrid = findViewById(R.id.poster_grid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Menu for selecting movie sort type */
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
                mSortType = this.getString(R.string.favorites_sort);
                loadFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(savedSort, mSortType);
        outState.putStringArray(PARAM_POSTER_PATH, mPosterPaths);
        outState.putStringArray(PARAM_TITLE, mTitleList);
        outState.putStringArray(PARAM_ID, mIdList);
        outState.putStringArray(PARAM_OVERVIEW, mDescriptionList);
        outState.putDoubleArray(PARAM_VOTE_AVERAGE, mRatingList);
        outState.putStringArray(PARAM_RELEASE_DATE, mDateList);
        outState.putInt(xCoord, mScrollView.getScrollX());
        outState.putInt(yCoord, mScrollView.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSortType = savedInstanceState.getString(savedSort);
            mPosterPaths = savedInstanceState.getStringArray(PARAM_POSTER_PATH);
            mTitleList = savedInstanceState.getStringArray(PARAM_TITLE);
            mIdList = savedInstanceState.getStringArray(PARAM_ID);
            mDescriptionList = savedInstanceState.getStringArray(PARAM_OVERVIEW);
            mRatingList = savedInstanceState.getDoubleArray(PARAM_VOTE_AVERAGE);
            mDateList = savedInstanceState.getStringArray(PARAM_RELEASE_DATE);
            populateUI();
            mScrollView.scrollTo(savedInstanceState.getInt(xCoord),
                    savedInstanceState.getInt(yCoord));
        }
    }

    /* Ensures a default sort value if app opens for first time, without interfering
     * with onRestoreInstanceState() logic.
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mSortType == null) {
            mSortType = getString(R.string.popular_sort);
            new FetchMoviesTask().execute();
        }
    }

    /* Makes REST call to online movie database. Returns with data for each movie. */
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

    /* Parses movie data out of JSON response from HTTP request */
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

    private void loadFavorites() {
        Uri uri = FavoritesProvider.CONTENT_URI;
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) { //If there are existing favorited items
            int i = 0;
            int resultsLength = cursor.getCount();
            mIdList = new String[resultsLength];
            mTitleList = new String[resultsLength];
            mPosterPaths = new String[resultsLength];
            mDescriptionList = new String[resultsLength];
            mRatingList = new double[resultsLength];
            mDateList = new String[resultsLength];
            while (!cursor.isAfterLast()) {
                mIdList[i] = cursor
                        .getString(cursor.getColumnIndex(FavoritesProvider.COLUMN_MOVIE_ID));
                mTitleList[i] = cursor
                        .getString(cursor.getColumnIndex(FavoritesProvider.COLUMN_TITLE));
                mPosterPaths[i] = cursor.
                        getString(cursor.getColumnIndex(FavoritesProvider.COLUMN_POSTER));
                mDescriptionList[i] = cursor
                        .getString(cursor.getColumnIndex(FavoritesProvider.COLUMN_DESCRIPTION));
                mRatingList[i] = cursor
                        .getDouble(cursor.getColumnIndex(FavoritesProvider.COLUMN_RATING));
                mDateList[i] = cursor.
                        getString(cursor.getColumnIndex(FavoritesProvider.COLUMN_RELEASE_DATE));
                i++;
                cursor.moveToNext();
            }
            cursor.close();
            populateUI();
        } else { //Create placeholder text if there are no favorited items
            TextView noFavorites = new TextView(this);
            noFavorites.setText(getString(R.string.no_favorites));
            noFavorites.setTextSize(18);
            noFavorites.setPadding(30, 30, 0, 0);
            noFavorites.setTextColor(getResources().getColor(R.color.fontLight));
            mPosterGrid.removeAllViews();
            mPosterGrid.addView(noFavorites);
        }
    }

    /* Creates grid items and add them to existing GridLayout. Each grid row should contain
     * no more than 2 movie posters. This function clears out all pre-existing views from the
     * UI before re-populating them each time.
     */
    private void populateUI() {
        mPosterGrid.removeAllViews();

        for (int i = 0; i < mPosterPaths.length; i++) {
            final String posterUrl = POSTER_BASE_URL + POSTER_SIZE + mPosterPaths[i];
            ImageView poster = new ImageView(this);
            Picasso
                    .with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(poster);
            poster.setAdjustViewBounds(true);

            GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
            gridLayoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            gridLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            gridLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            gridLayoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            poster.setLayoutParams(gridLayoutParams);

            /* Sends movie data into DetailActivity via Intent */
            final String posterKey = mPosterPaths[i];
            final String title = mTitleList[i];
            final String description = mDescriptionList[i];
            final double rating = mRatingList[i];
            final String releaseDate = mDateList[i];
            final String id = mIdList[i];
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent =
                            new Intent(MainActivity.this, DetailActivity.class);
                    detailIntent.putExtra(getString(R.string.poster_url), posterKey);
                    detailIntent.putExtra(getString(R.string.title), title);
                    detailIntent.putExtra(getString(R.string.description), description);
                    detailIntent.putExtra(getString(R.string.rating), rating);
                    detailIntent.putExtra(getString(R.string.release_date), releaseDate);
                    detailIntent.putExtra(getString(R.string.movie_id), id);
                    startActivity(detailIntent);
                }
            });

            mPosterGrid.addView(poster);
        }
    }
}
