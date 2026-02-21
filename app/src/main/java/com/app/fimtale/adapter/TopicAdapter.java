package com.app.fimtale.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.app.fimtale.R;
import com.app.fimtale.TopicDetailActivity;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.ui.ParallaxImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TopicViewItem> topics;

    public TopicAdapter(List<TopicViewItem> topics) {
        this.topics = topics;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TopicViewHolder) {
            TopicViewHolder topicHolder = (TopicViewHolder) holder;
            TopicViewItem topic = topics.get(position);
            topicHolder.titleTextView.setText(topic.getTitle());
            topicHolder.authorTextView.setText(topic.getAuthorName());
            
            // 绑定标签
            if (topic.getTags() != null) {
                if (topicHolder.tagType != null) topicHolder.tagType.setText(topic.getTags().getType());
                if (topicHolder.tagSource != null) topicHolder.tagSource.setText(topic.getTags().getSource());
                if (topicHolder.tagLength != null) topicHolder.tagLength.setText(topic.getTags().getLength());
                if (topicHolder.tagRate != null) {
                    String rating = topic.getTags().getRating();
                    topicHolder.tagRate.setText(rating);

                    int backgroundColor = 0x80000000;
                    if (rating != null) {
                        if (rating.equalsIgnoreCase("Everyone") || rating.equalsIgnoreCase("E")) {
                            backgroundColor = 0xFF4CAF50;
                        } else if (rating.equalsIgnoreCase("Teen") || rating.equalsIgnoreCase("T")) {
                            backgroundColor = 0xFFFFC107;
                        } else if (rating.equalsIgnoreCase("Restricted") || rating.equalsIgnoreCase("Mature") || rating.equalsIgnoreCase("M")) {
                            backgroundColor = 0xFFF44336;
                        }
                    }

                    Drawable background = topicHolder.tagRate.getBackground();
                    if (background instanceof android.graphics.drawable.GradientDrawable) {
                        ((android.graphics.drawable.GradientDrawable) background.mutate()).setColor(backgroundColor);
                    }
                }
            }

            // 绑定统计数据
            if (topicHolder.wordCountTextView != null) {
                topicHolder.wordCountTextView.setText(topic.getWordCount());
            }
            if (topicHolder.viewCountTextView != null) {
                topicHolder.viewCountTextView.setText(topic.getViewCount());
            }
            if (topicHolder.commentCountTextView != null) {
                topicHolder.commentCountTextView.setText(topic.getCommentCount());
            }
            if (topicHolder.favoriteCountTextView != null) {
                topicHolder.favoriteCountTextView.setText(topic.getFavoriteCount());
            }

            ParallaxImageView parallaxView = null;
            if (topicHolder.coverImageView instanceof ParallaxImageView) {
                parallaxView = (ParallaxImageView) topicHolder.coverImageView;
                parallaxView.setParallaxEnabled(false);
            }
            
            final ParallaxImageView finalParallaxView = parallaxView;

            Glide.with(topicHolder.itemView.getContext())
                    .load(topic.getBackground())
                    .placeholder(R.drawable.ic_default_article_cover)
                    .error(R.drawable.ic_default_article_cover)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (finalParallaxView != null) {
                                finalParallaxView.setParallaxEnabled(false);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (finalParallaxView != null) {
                                finalParallaxView.setParallaxEnabled(true);
                            }
                            return false;
                        }
                    })
                    .into(topicHolder.coverImageView);

            topicHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), TopicDetailActivity.class);
                intent.putExtra(TopicDetailActivity.EXTRA_TOPIC_ID, topic.getId());
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (topics == null) return 0;
        return topics.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView wordCountTextView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView favoriteCountTextView;
        TextView tagType, tagSource, tagLength, tagRate;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.coverImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            wordCountTextView = itemView.findViewById(R.id.wordCountTextView);
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            favoriteCountTextView = itemView.findViewById(R.id.favoriteCountTextView);
            
            tagType = itemView.findViewById(R.id.tagType);
            tagSource = itemView.findViewById(R.id.tagSource);
            tagLength = itemView.findViewById(R.id.tagLength);
            tagRate = itemView.findViewById(R.id.tagRate);
        }
    }
}
