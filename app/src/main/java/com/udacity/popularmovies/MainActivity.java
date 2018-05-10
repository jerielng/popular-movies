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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.data.FavoritesProvider;
import com.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mPosterGrid;
    private LinearLayout mFavoritesList;
    private LinearLayout mFavoritesHolder;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPosterGrid = findViewById(R.id.poster_grid);
        mFavoritesList = findViewById(R.id.favorites_list);
        mFavoritesHolder = findViewById(R.id.favorites_holder);
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
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSortType = savedInstanceState.getString(savedSort);
            if (mSortType.equals(this.getString(R.string.favorites_sort))) {
                loadFavorites();
            } else {
                new FetchMoviesTask().execute();
            }
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

    /* Creates grid rows and add them to existing LinearLayout. Each grid row should contain
     * no more than 2 movie posters. This function clears out all pre-existing views from the
     * UI before re-populating them each time.
     */
    private void populateUI() {
        /* Toggles between fetched movies and favorites list */
        mFavoritesList.setVisibility(View.GONE);
        mPosterGrid.setVisibility(View.VISIBLE);
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
            Picasso
                    .with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(poster);
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

    private void loadFavorites() {
        /* Toggles between fetched movies and favorites list */
        mPosterGrid.setVisibility(View.GONE);
        mFavoritesList.setVisibility(View.VISIBLE);
        mFavoritesHolder.removeAllViews();

        Uri uri = FavoritesProvider.CONTENT_URI;
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) { //If there are existing favorited items
            while (!cursor.isAfterLast()) {
                String id = cursor
                        .getString(cursor.getColumnIndex(FavoritesProvider.COLUMN_MOVIE_ID));
                String title = cursor
                        .getString(cursor.getColumnIndex(FavoritesProvider.COLUMN_TITLE));
                String favoriteText = id + ": " + title;
                TextView favoriteItem = new TextView(this);
                favoriteItem.setText(favoriteText);
                favoriteItem.setTextSize(18);
                favoriteItem.setTextColor(getResources().getColor(R.color.fontLight));
                favoriteItem.setPadding( 0, 15, 0, 15);
                mFavoritesHolder.addView(favoriteItem);
                cursor.moveToNext();
            }
            cursor.close();
        } else { //Create placeholder text if there are no favorited items
            TextView noFavorites = new TextView(this);
            noFavorites.setText(getString(R.string.no_favorites));
            noFavorites.setTextSize(18);
            noFavorites.setTextColor(getResources().getColor(R.color.fontLight));
            mFavoritesHolder.addView(noFavorites);
        }
    }
}
