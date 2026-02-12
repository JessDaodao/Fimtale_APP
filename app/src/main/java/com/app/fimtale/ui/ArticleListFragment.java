package com.app.fimtale.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topicAdapter = new TopicAdapter(topicViewItemList);
        recyclerView.setAdapter(topicAdapter);

        loadTopics();
    }

    private void loadTopics() {
        List<Topic> topicList = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            Topic topic = new Topic();
            topic.setId(i);
            topic.setTitle("文章 " + i);
            topic.setAuthorName("作者 " + i);
            topic.setBackground("https://dreamlandcon.top/img/sample.jpg");
            topicList.add(topic);
        }

        topicViewItemList.clear();
        topicViewItemList.addAll(topicList.stream().map(TopicViewItem::new).collect(Collectors.toList()));
        topicAdapter.notifyDataSetChanged();
    }
}
