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

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_HEADER = 2;

    private List<TopicViewItem> topics;
    private OnPaginationListener paginationListener;
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isPaginationEnabled = true;

    public interface OnPaginationListener {
        void onPrevPage();
        void onNextPage();
    }

    public TopicAdapter(List<TopicViewItem> topics) {
        this.topics = topics;
    }

    public void setPaginationListener(OnPaginationListener listener) {
        this.paginationListener = listener;
    }

    public void setPaginationEnabled(boolean enabled) {
        this.isPaginationEnabled = enabled;
        notifyDataSetChanged();
    }

    public void setPageInfo(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPaginationEnabled) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            if (position == getItemCount() - 1) {
                return TYPE_FOOTER;
            }
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER || viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pagination_footer, parent, false);
            return new PaginationViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PaginationViewHolder) {
            PaginationViewHolder paginationHolder = (PaginationViewHolder) holder;
            paginationHolder.tvPageInfo.setText(currentPage + " / " + totalPages);
            paginationHolder.btnPrevPage.setEnabled(currentPage > 1);
            paginationHolder.btnNextPage.setEnabled(currentPage < totalPages);

            paginationHolder.btnPrevPage.setOnClickListener(v -> {
                if (paginationListener != null) paginationListener.onPrevPage();
            });
            paginationHolder.btnNextPage.setOnClickListener(v -> {
                if (paginationListener != null) paginationListener.onNextPage();
            });
        } else if (holder instanceof TopicViewHolder) {
            TopicViewHolder topicHolder = (TopicViewHolder) holder;
            int actualPosition = position - (isPaginationEnabled ? 1 : 0);
            if (actualPosition < 0 || actualPosition >= topics.size()) return;
            
            TopicViewItem topic = topics.get(actualPosition);
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
        return isPaginationEnabled ? topics.size() + 2 : topics.size();
    }

    public static class PaginationViewHolder extends RecyclerView.ViewHolder {
        android.widget.Button btnPrevPage;
        android.widget.Button btnNextPage;
        android.widget.TextView tvPageInfo;

        public PaginationViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPrevPage = itemView.findViewById(R.id.btn_prev_page);
            btnNextPage = itemView.findViewById(R.id.btn_next_page);
            tvPageInfo = itemView.findViewById(R.id.tv_page_info);
        }
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
