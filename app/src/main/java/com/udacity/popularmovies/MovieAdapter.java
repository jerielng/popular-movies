package com.udacity.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Context mContext;

    public MovieAdapter(Context context) {
        mContext = context;
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        //public final ImageView mPosterImageView;
        public final String mTitle;
        public MovieAdapterViewHolder(View view, String title) {
            super(view);
            mTitle = title;
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