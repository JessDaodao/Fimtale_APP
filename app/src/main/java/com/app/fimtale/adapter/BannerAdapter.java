package com.app.fimtale.adapter;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.R;
import com.app.fimtale.model.RecommendedTopic;
import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<RecommendedTopic> bannerItems;

    public BannerAdapter(List<RecommendedTopic> bannerItems) {
        this.bannerItems = bannerItems;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banner_item_topic, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        RecommendedTopic topic = bannerItems.get(position);
        holder.titleTextView.setText(topic.getTitle());
        holder.introTextView.setText(topic.getRecommendWord());

        RenderEffect blurEffect = RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.CLAMP);
        holder.imageView.setRenderEffect(blurEffect);

        Glide.with(holder.itemView.getContext())
                .load(topic.getBackground())
                .placeholder(R.drawable.ic_default_article_cover)
                .error(R.drawable.ic_default_article_cover)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (bannerItems == null) return 0;
        return bannerItems.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView introTextView;
        TextView authorTextView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImageView);
            titleTextView = itemView.findViewById(R.id.bannerTitleTextView);
            introTextView = itemView.findViewById(R.id.bannerIntroTextView);
        }
    }
}