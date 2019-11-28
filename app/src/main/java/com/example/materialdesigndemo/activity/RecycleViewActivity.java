package com.example.materialdesigndemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.materialdesigndemo.R;
import com.example.materialdesigndemo.adapter.NewsAdapter;
import com.example.materialdesigndemo.adapter.NewsGridAdapter;
import com.example.materialdesigndemo.model.News;
import com.example.materialdesigndemo.utils.HttpsUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class RecycleViewActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener {
    //    聚合数据的url网址和handle的消息
    private static final String NEWS_URL="http://v.juhe.cn/toutiao/index";
    private static final int GET_NEWS=1;

//    控件对象
    private Toolbar toolbar;
    private RecyclerView rvNews;
    private SwipeRefreshLayout refreshLayout;

//    数据处理的对象
    private List<News> newsList;
    private NewsHandler handler;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        handler=new NewsHandler(this);

        initView();
        initData();
    }

    private void initData() {
        String key="234f1a253352054c294ef16692c7011b";
        String url=NEWS_URL+"?key="+key+"&type=top";
        Request request=new Request.Builder().url(url).build();
        HttpsUtil.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("NewsListActivity",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String json = response.body().string();
                    JSONObject obj = JSON.parseObject(json);
                    JSONObject result = obj.getJSONObject("result");
                    if (result != null) {
                        JSONArray data = result.getJSONArray("data");
                        if(data != null && !data.isEmpty()) {
                            Message msg = handler.obtainMessage();
                            msg.what = GET_NEWS;
                            msg.obj = data.toJSONString();  //
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    Log.e("NewsListActivity",response.message());
                }
            }
        });

    }

    private void initView() {
        toolbar=findViewById(R.id.to_recycle_toolbar);
        toolbar.setTitle("RecycleverView示例");
        setSupportActionBar(toolbar);

        rvNews=findViewById(R.id.rv_news);
        refreshLayout=findViewById(R.id.refresh);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_linear:
                rvNews.setLayoutManager(new LinearLayoutManager(this));
                adapter = new NewsAdapter(newsList);
                rvNews.setAdapter(adapter);
                break;
            case R.id.item_grid:
                rvNews.setLayoutManager(new GridLayoutManager(this, 2));
                NewsGridAdapter adapter = new NewsGridAdapter(newsList);
                rvNews.setAdapter(adapter);
                break;
            case R.id.item_stagger:
                rvNews.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
                adapter = new NewsGridAdapter(newsList);
                rvNews.setAdapter(adapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        initData();
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        },3000);
    }


    static class NewsHandler extends Handler{
        private WeakReference<Activity> ref;

        public NewsHandler(Activity activity){
            this.ref=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final RecycleViewActivity activity= (RecycleViewActivity) this.ref.get();
            if(msg.what==GET_NEWS){
//                获取数据
                String json= (String) msg.obj;
                activity.newsList=JSON.parseArray(json,News.class);
//                设置RecyclerView的分割线和布局
                activity.rvNews.setLayoutManager(new LinearLayoutManager(activity));
//                设置Adapter
                activity.adapter=new NewsAdapter(activity.newsList);
                activity.rvNews.setAdapter(activity.adapter);

//                设置事件监听
                activity.adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent=new Intent(activity,NewsDetailActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("news",activity.newsList.get(position));
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                });
            }
        }
    }
}
