package com.example.materialdesigndemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.materialdesigndemo.R;
import com.example.materialdesigndemo.adapter.NewsAdapter;
import com.example.materialdesigndemo.model.News;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CoordinatorActivity extends AppCompatActivity
    implements SwipeRefreshLayout.OnRefreshListener {

    private static final String NEWS_URL="http://v.juhe.cn/toutiao/index";
    private static final int GET_NEWS=1;

    private List<News> newsList;
    private NewHandler handler;
    private NewsAdapter adapter;

    private Toolbar toolbar;
    private RecyclerView rvNews;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private SwipeRefreshLayout refresh;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator1);

        handler=new NewHandler(this);

        initView();
        initData();

    }

    private void initView() {
        toolbar=findViewById(R.id.tool_bar);
        collapsingToolbarLayout=findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("新闻头条");
        setSupportActionBar(toolbar);

        rvNews=findViewById(R.id.rv_new);
        refresh=findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);

        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowPos=(rvNews==null||rvNews.getChildCount()==0)?0:rvNews.getChildAt(0).getTop();
                refresh.setEnabled(topRowPos>=0);
            }
        });

        appBarLayout=findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                refresh.setEnabled(i>=0);
            }
        });
    }


    private void initData() {
        String key="234f1a253352054c294ef16692c7011b";
        String url=NEWS_URL+"?key="+key+"&type=top";
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
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
                            Message msg = handler.obtainMessage();
                            msg.what = GET_NEWS;
                            msg.obj = data.toJSONString();  //
                            handler.sendMessage(msg);
                        }
                    }
            }
        });
    }

    @Override
    public void onRefresh() {
        initData();
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(false);
            }
        },3000);
    }

    public void sendEmail(View view) {
    }

    static class NewHandler extends Handler{
        private WeakReference<Activity> ref;

        public NewHandler(Activity activity){
            this.ref=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CoordinatorActivity activity= (CoordinatorActivity) this.ref.get();
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
