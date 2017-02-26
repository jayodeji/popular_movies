package com.jayodeji.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jayodeji.android.popularmovies.data.Review;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private static String TAG = ReviewListAdapter.class.getSimpleName();

    private Review[] mReviewList = null;
    private final ReviewClickListener mOnReviewClickedListener;

    public ReviewListAdapter(ReviewClickListener listener, Review[] reviewList) {
        mOnReviewClickedListener = listener;
        mReviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediatelyToParent = false;
        View view = inflater.inflate(R.layout.movie_review_item, parent, attachImmediatelyToParent);
        return new ReviewViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        if (mReviewList != null && (mReviewList.length > position)) {
            Review review = mReviewList[position];
            String reviewAuthor = "By: " + review.author;
            holder.mReviewAuthor.setText(reviewAuthor);
        }
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) {
            return 0;
        }
        return mReviewList.length;
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mReviewIcon;
        private TextView mReviewAuthor;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mReviewIcon = (ImageView) itemView.findViewById(R.id.iv_review_icon);
            mReviewIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Review review = mReviewList[clickedPosition];
            mOnReviewClickedListener.onReviewClicked(review);
        }
    }

    public interface ReviewClickListener {
        public void onReviewClicked(Review clickedReview);
    }
}
