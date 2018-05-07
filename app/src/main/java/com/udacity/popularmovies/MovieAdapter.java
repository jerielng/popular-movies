package com.udacity.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Context mContext;
    private Image[] mPosterList;
    private String[] mIdList;

    public MovieAdapter(Context context, Image[] posterList, String[] idList) {
        mContext = context;
        mPosterList = posterList;
        mIdList = idList;
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        //public final ImageView mPosterImageView;
        //public final String movieId;
        public MovieAdapterViewHolder(View view) {
            super(view);
        }
    }

    @NonNull
    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}