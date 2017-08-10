package com.example.savindusanjana.hackathontest1;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by savindusanjana on 8/10/17.
 */

public class home extends AppCompatActivity{

    TabLayout tabLayout;
    ViewPager viewPager;
    viewPagerAdapter vpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        vpa = new viewPagerAdapter(getSupportFragmentManager()) ;

        vpa.addFragments(new HomeFragment(),"Home");
        vpa.addFragments(new JournelFragment(),"Journal");
        vpa.addFragments(new TodoFragment(),"ToDo");

        viewPager.setAdapter(vpa);
        tabLayout.setupWithViewPager(viewPager);

    }
}
