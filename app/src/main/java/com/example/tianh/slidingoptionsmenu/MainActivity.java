package com.example.tianh.slidingoptionsmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.menu.mylibrary.SlidingOptionsMenu;

public class MainActivity extends AppCompatActivity {

    private SlidingOptionsMenu slidingOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slidingOptionsMenu = findViewById(R.id.slidingOptionsMenu);
        slidingOptionsMenu.setCurrentItem(3);
    }
}
