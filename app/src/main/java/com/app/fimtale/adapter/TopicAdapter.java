package com.app.fimtale.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.fimtale.R;
import com.app.fimtale.TopicDetailActivity;
import com.app.fimtale.model.TopicViewItem;
import com.bumptech.glide.Glide;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<TopicViewItem> topics;

    public TopicAdapter(List<TopicViewItem> topics) {
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicViewItem topic = topics.get(position);
        holder.titleTextView.setText(topic.getTitle());
        holder.authorTextView.setText(topic.getAuthorName());
        
        // 绑定统计数据
        if (holder.wordCountTextView != null) {
            holder.wordCountTextView.setText(topic.getWordCount());
        }
        if (holder.viewCountTextView != null) {
            holder.viewCountTextView.setText(topic.getViewCount());
        }
        if (holder.commentCountTextView != null) {
            holder.commentCountTextView.setText(topic.getCommentCount());
        }
        if (holder.favoriteCountTextView != null) {
            holder.favoriteCountTextView.setText(topic.getFavoriteCount());
        }

        Glide.with(holder.itemView.getContext())
                .load(topic.getBackground())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.coverImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TopicDetailActivity.class);
            intent.putExtra(TopicDetailActivity.EXTRA_TOPIC_ID, topic.getId());
            v.getContext().startActivity(intent);
        });
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

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.coverImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            wordCountTextView = itemView.findViewById(R.id.wordCountTextView);
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            favoriteCountTextView = itemView.findViewById(R.id.favoriteCountTextView);
        }
    }
}
