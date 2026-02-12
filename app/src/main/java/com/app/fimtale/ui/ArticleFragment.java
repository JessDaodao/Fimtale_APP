package com.app.fimtale.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.app.fimtale.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabLayoutMediator tabLayoutMediator;
    private final String[] categories = {"分区1", "分区2", "分区3"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.view_pager);

        ArticlePagerAdapter pagerAdapter = new ArticlePagerAdapter(getChildFragmentManager(), getViewLifecycleOwner().getLifecycle());
        viewPager.setAdapter(pagerAdapter);

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(categories[position]);
        });
        tabLayoutMediator.attach();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
            tabLayoutMediator = null;
        }
        viewPager.setAdapter(null);
    }

    private class ArticlePagerAdapter extends FragmentStateAdapter {

        public ArticlePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ArticleListFragment.newInstance(categories[position]);
        }

        @Override
        public int getItemCount() {
            return categories.length;
        }
    }
}
