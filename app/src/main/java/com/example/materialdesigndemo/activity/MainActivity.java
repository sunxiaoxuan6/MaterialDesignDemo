package com.example.materialdesigndemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.materialdesigndemo.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    //    聚合数据
    private static final String WEATHER_URL="http://v.juhe.cn/toutiao/index";
    private static final String WEATHER_APP_KEY="234f1a253352054c294ef16692c7011b";
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//设置标题栏的标题
        toolbar=findViewById(R.id.to_toolbar);
        toolbar.setTitle("Android 5.0新特性");
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setSubtitle("Material Design Support控件");

        setSupportActionBar(toolbar);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_recyclerView:
                Intent intent=new Intent(MainActivity.this,RecycleViewActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_coordinatorLayout:
                intent=new Intent(MainActivity.this,CoordinatorActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_tabLayout:
                intent=new Intent(MainActivity.this,TabLayoutActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_navigation:
//                intent=new Intent(MainActivity.this,NavigationActivity.class);
//                startActivity(intent);
                break;
            case R.id.btn_notification:
                intent=new Intent(MainActivity.this,NotifiCationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
