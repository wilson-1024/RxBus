package com.chemanman.rxbus1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zhangzhang on 2017/1/11.
 */

public class OneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        getFragmentManager().beginTransaction().add(R.id.send, new SendFragment()).commit();
        getFragmentManager().beginTransaction().add(R.id.recv, new RevFragment()).commit();
    }
}
