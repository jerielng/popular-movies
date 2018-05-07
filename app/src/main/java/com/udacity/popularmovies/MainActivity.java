package com.udacity.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mPosterRecycler;
    private GridLayoutManager mLayoutManager;
    private MovieAdapter mMovieAdapter;

    private GridLayout mPosterGrid;

    private String mSortType;

    private String[] mPosterPaths;
    private String[] mTitleList;
    private String[] mDescriptionList;
    private double[] mRatingList;
    private String[] mDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPosterGrid = findViewById(R.id.poster_grid);

        mPosterRecycler = findViewById(R.id.poster_recycler_view);
        mLayoutManager = new GridLayoutManager(this, 2);
        mPosterRecycler.setLayoutManager(mLayoutManager);
        mMovieAdapter = new MovieAdapter(this);
        mPosterRecycler.setAdapter(mMovieAdapter);

        mSortType = this.getString(R.string.popular_sort);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //referred udacity code
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

    public void extractMovieData(String jsonString) {
        try {
            JSONObject moviesObject = new JSONObject(jsonString);
            JSONArray movieResults = moviesObject.getJSONArray("results");
            mPosterPaths = new String[movieResults.length()];
            mTitleList = new String[movieResults.length()];
            mDescriptionList = new String[movieResults.length()];
            mRatingList = new double[movieResults.length()];
            mDateList = new String[movieResults.length()];
            for (int i = 0; i < movieResults.length(); i++) {
                mPosterPaths[i] = movieResults.getJSONObject(i).getString("poster_path");
                mTitleList[i] = movieResults.getJSONObject(i).getString("title");
                mDescriptionList[i] = movieResults.getJSONObject(i).getString("overview");
                mRatingList[i] = movieResults.getJSONObject(i).getDouble("vote_average");
                mDateList[i] = movieResults.getJSONObject(i).getString("release_date");
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateUI() {
        int gridSize = mPosterPaths.length;

        //clear existing poster views

        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w185/";
        for (int i = 0; i < mPosterPaths.length; i++) {
            final String posterUrl = POSTER_BASE_URL + POSTER_SIZE + mPosterPaths[i];
            ImageView poster = new ImageView(this);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            poster.setLayoutParams(layoutParams);
            Picasso.with(this).load(posterUrl).into(poster);
            final String title = mTitleList[i];
            final String description = mDescriptionList[i];
            final double rating = mRatingList[i];
            final String releaseDate = mDateList[i];
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                    detailIntent.putExtra(getString(R.string.poster_url), posterUrl);
                    detailIntent.putExtra(getString(R.string.title), title);
                    detailIntent.putExtra(getString(R.string.description), description);
                    detailIntent.putExtra(getString(R.string.rating), rating);
                    detailIntent.putExtra(getString(R.string.release_date), releaseDate);
                    startActivity(detailIntent);
                }
            });
//            MovieAdapter.MovieAdapterViewHolder posterVh = new MovieAdapter.MovieAdapterViewHolder(poster, title);
            mPosterGrid.addView(poster);
        }
    }
}
