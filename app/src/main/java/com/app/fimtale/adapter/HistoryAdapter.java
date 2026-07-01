package com.app.fimtale.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.R;
import com.app.fimtale.model.ReadProgress;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<ReadProgress> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ReadProgress progress);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setReadProgress(List<ReadProgress> data) {
        this.items = data;
        notifyDataSetChanged();
    }

    public void addReadProgress(List<ReadProgress> data) {
        int startPos = this.items.size();
        this.items.addAll(data);
        notifyItemRangeInserted(startPos, data.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadProgress item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private LinearProgressIndicator progressIndicator;
        private TextView tvProgress;
        private TextView tvDate;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            progressIndicator = itemView.findViewById(R.id.progressIndicator);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(ReadProgress progress, OnItemClickListener listener) {
            // title 字段：章节进度取章节标题，作品进度取作品标题
            String displayTitle = progress.getTitle();
            if (displayTitle == null || displayTitle.isEmpty()) {
                displayTitle = "作品 #" + progress.getWorkId();
            }
            tvTitle.setText(displayTitle);

            int progressPercent = Math.round(progress.getProgress() * 100);
            int progressValue = Math.round(progress.getProgress() * 1000);
            progressIndicator.setProgress(progressValue);
            tvProgress.setText(progressPercent + "%");

            // 解析 ISO 8601 时间
            String dateStr = "";
            try {
                String updatedAt = progress.getUpdatedAt();
                if (updatedAt != null) {
                    // 处理 ISO 8601 格式: "2026-01-01T00:00:00Z"
                    String normalized = updatedAt.replace("Z", "+0000")
                            .replace("T", " ");
                    if (normalized.length() >= 19) {
                        dateStr = normalized.substring(0, 16);
                    }
                }
            } catch (Exception e) {
                dateStr = "";
            }
            tvDate.setText(dateStr);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(progress);
                }
            });
        }
    }
}
