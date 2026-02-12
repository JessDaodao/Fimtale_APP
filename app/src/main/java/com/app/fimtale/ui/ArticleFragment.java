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
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleFragment extends Fragment {

    private TopicAdapter topicAdapter;
    private List<TopicViewItem> topicViewItemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topicAdapter = new TopicAdapter(topicViewItemList);
        recyclerView.setAdapter(topicAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("分区1"));
        tabLayout.addTab(tabLayout.newTab().setText("分区2"));
        tabLayout.addTab(tabLayout.newTab().setText("分区3"));

        loadTopics();

        return view;
    }

    private void loadTopics() {
        List<Topic> topicList = new ArrayList<>();
        
        Topic topic1 = new Topic();
        topic1.setId(1);
        topic1.setTitle("文章1");
        topic1.setAuthorName("作者1");
        topic1.setBackground("https://pic.imgdb.cn/item/6698e7b7d9c38f524e35b54d.png");
        topicList.add(topic1);

        Topic topic2 = new Topic();
        topic2.setId(2);
        topic2.setTitle("文章2");
        topic2.setAuthorName("作者2");
        topic2.setBackground("https://pic.imgdb.cn/item/6698e7b7d9c38f524e35b54d.png");
        topicList.add(topic2);

        Topic topic3 = new Topic();
        topic3.setId(3);
        topic3.setTitle("文章3");
        topic3.setAuthorName("作者3");
        topic3.setBackground("https://pic.imgdb.cn/item/6698e7b7d9c38f524e35b54d.png");
        topicList.add(topic3);

        topicViewItemList.clear();
        topicViewItemList.addAll(topicList.stream().map(TopicViewItem::new).collect(Collectors.toList()));
        topicAdapter.notifyDataSetChanged();
    }
}
