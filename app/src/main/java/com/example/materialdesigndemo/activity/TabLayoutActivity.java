package com.example.materialdesigndemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.materialdesigndemo.R;
import com.example.materialdesigndemo.adapter.NewsFragmentAdapter;
import com.example.materialdesigndemo.fragment.NewsFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TabLayoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabs;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        initView();
        initViewPager();
    }

    private void initViewPager() {
//        1、组装tab的标题集合
        List<String> titles = new ArrayList<>();
        titles.add("精选");
        titles.add("新闻");
        titles.add("体育");
        titles.add("购物");
        titles.add("视频");
        titles.add("健康");
//        2、根据tab的个数初始化Fragment的集合
        List<Fragment> fragments = new ArrayList<>();
        for(String title : titles) {
            tabs.addTab(tabs.newTab().setText(title));
            fragments.add(NewsFragment.newInstance());
        }
//        3、创建ViewPage的adapter
        NewsFragmentAdapter adapter = new NewsFragmentAdapter(
                getSupportFragmentManager(), fragments, titles);
//        4、ViewPage设置adapter
        viewPager.setAdapter(adapter);
//        5、关联TabLayot
        tabs.setupWithViewPager(viewPager);
    }

    private void initView() {

    }
}