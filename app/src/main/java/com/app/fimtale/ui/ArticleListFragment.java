package com.app.fimtale.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.R;
import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleListFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private String category;
    private TopicAdapter topicAdapter;
    private List<TopicViewItem> topicViewItemList = new ArrayList<>();
    private int currentPage = 1;
    private int totalPages = 5;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public static ArticleListFragment newInstance(String category) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
        } else {
            recyclerView = view.findViewById(R.id.recycler_view);
        }
        progressBar = view.findViewById(R.id.progressBar);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topicAdapter = new TopicAdapter(topicViewItemList);
        recyclerView.setAdapter(topicAdapter);

        topicAdapter.setPaginationListener(new TopicAdapter.OnPaginationListener() {
            @Override
            public void onPrevPage() {
                if (currentPage > 1) {
                    currentPage--;
                    loadTopics();
                }
            }

            @Override
            public void onNextPage() {
                if (currentPage < totalPages) {
                    currentPage++;
                    loadTopics();
                }
            }
        });

        loadTopics();
    }

    private void loadTopics() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;

            List<Topic> topicList = new ArrayList<>();
            
            for (int i = 1; i <= 10; i++) {
                Topic topic = new Topic();
                topic.setId(i + (currentPage - 1) * 10);
                topic.setTitle("文章 " + topic.getId());
                topic.setAuthorName("作者 " + topic.getId());
                topic.setBackground("https://dreamlandcon.top/img/sample.jpg");
                topicList.add(topic);
            }

            topicViewItemList.clear();
            topicViewItemList.addAll(topicList.stream().map(TopicViewItem::new).collect(Collectors.toList()));
            topicAdapter.notifyDataSetChanged();
            
            topicAdapter.setPageInfo(currentPage, totalPages);

            progressBar.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        progressBar.setVisibility(View.GONE);
                        progressBar.setAlpha(1f);

                        recyclerView.setAlpha(0f);
                        recyclerView.setScaleX(0.9f);
                        recyclerView.setScaleY(0.9f);
                        recyclerView.setVisibility(View.VISIBLE);

                        android.view.animation.PathInterpolator interpolator = new android.view.animation.PathInterpolator(1.00f, 0.00f, 0.28f, 1.00f);

                        recyclerView.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setInterpolator(interpolator)
                                .setDuration(500)
                                .start();
                    })
                    .start();
        }, 1000);
    }
}
