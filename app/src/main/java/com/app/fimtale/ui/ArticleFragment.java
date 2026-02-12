package com.app.fimtale.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.R;
import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.Tags;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicViewItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ArticleFragment extends Fragment {

    private TabLayout tabLayout;
    private FrameLayout contentContainer;
    private ProgressBar progressBar;
    private final String[] categories = {"分区1", "分区2", "分区3"};
    private List<CategoryData> categoryDataList = new ArrayList<>();

    private class CategoryData {
        String name;
        RecyclerView recyclerView;
        TopicAdapter adapter;
        List<TopicViewItem> dataList = new ArrayList<>();
        int currentPage = 1;
        int totalPages = 5;
        boolean isLoaded = false;

        CategoryData(String name) {
            this.name = name;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabs);
        contentContainer = view.findViewById(R.id.content_container);
        progressBar = view.findViewById(R.id.progressBar);

        for (String category : categories) {
            CategoryData data = new CategoryData(category);
            categoryDataList.add(data);
            
            TabLayout.Tab tab = tabLayout.newTab().setText(category);
            tabLayout.addTab(tab);

            RecyclerView rv = new RecyclerView(getContext());
            rv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            int padding = (int)(8 * getResources().getDisplayMetrics().density);
            rv.setPadding(padding, 0, padding, 0);
            rv.setClipToPadding(false);
            rv.setVisibility(View.GONE);
            
            data.adapter = new TopicAdapter(data.dataList);
            data.adapter.setPaginationListener(new TopicAdapter.OnPaginationListener() {
                @Override
                public void onPrevPage() {
                    if (data.currentPage > 1) {
                        data.currentPage--;
                        loadCategoryData(data, false);
                    }
                }

                @Override
                public void onNextPage() {
                    if (data.currentPage < data.totalPages) {
                        data.currentPage++;
                        loadCategoryData(data, false);
                    }
                }
            });
            rv.setAdapter(data.adapter);
            
            data.recyclerView = rv;
            contentContainer.addView(rv);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                showCategory(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        showCategory(0);
    }

    private void showCategory(int position) {
        for (int i = 0; i < categoryDataList.size(); i++) {
            CategoryData data = categoryDataList.get(i);
            if (i == position) {
                if (data.recyclerView.getVisibility() != View.VISIBLE) {
                    data.recyclerView.setVisibility(View.VISIBLE);
                    if (!data.isLoaded) {
                        loadCategoryData(data, true);
                    }
                }
            } else {
                data.recyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void loadCategoryData(CategoryData data, boolean animate) {
        if (animate) {
            progressBar.setVisibility(View.VISIBLE);
            data.recyclerView.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            data.recyclerView.setVisibility(View.INVISIBLE);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;

            List<Topic> topicList = new ArrayList<>();
            Random random = new Random();
            String[] authors = {"作者A", "作者B", "作者C", "作者D", "作者E"};
            
            for (int i = 1; i <= 10; i++) {
                Topic topic = new Topic();
                topic.setId(i + (data.currentPage - 1) * 10);
                topic.setTitle(data.name + " 文章 " + topic.getId());
                topic.setAuthorName(authors[random.nextInt(authors.length)]);
                topic.setBackground("https://dreamlandcon.top/img/sample.jpg");
                topic.setIntro("这是 " + data.name + " 的文章简介 " + topic.getId());
                
                Tags topicTags = new Tags();
                topicTags.setType("类型" + (i % 3 + 1));
                topicTags.setSource("来源" + (i % 2 + 1));
                topicTags.setRating("评级" + (i % 3 + 1));
                topic.setTags(topicTags);
                
                topicList.add(topic);
            }

            data.dataList.clear();
            data.dataList.addAll(topicList.stream().map(TopicViewItem::new).collect(Collectors.toList()));
            data.adapter.notifyDataSetChanged();
            data.adapter.setPageInfo(data.currentPage, data.totalPages);
            data.isLoaded = true;

            progressBar.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        progressBar.setVisibility(View.GONE);
                        progressBar.setAlpha(1f);

                        data.recyclerView.setAlpha(0f);
                        data.recyclerView.setScaleX(0.9f);
                        data.recyclerView.setScaleY(0.9f);
                        data.recyclerView.setVisibility(View.VISIBLE);

                        android.view.animation.PathInterpolator interpolator = new android.view.animation.PathInterpolator(1.00f, 0.00f, 0.28f, 1.00f);

                        data.recyclerView.animate()
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
