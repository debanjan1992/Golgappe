package com.development.techiefolks.golgappe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class HomePageActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddReviewDialogGlobal dialog=new AddReviewDialogGlobal();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(),"reviewDialogMain");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logout)
        {
            SharedPreferences sharedPreferences=getSharedPreferences("sessionData",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent=new Intent(this,StartingActivity.class);
            startActivity(intent);
            Toast.makeText(this, "You have been successfully logged out.", Toast.LENGTH_SHORT).show();
            finish();
        }
        return true;
    }

//to be deleted
    public static class testFrag extends Fragment
{
    public testFrag()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page,container,false);
    }
}
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
            {
                testFrag fragment=new testFrag();
                return fragment;
            }
            else if(position==1)
            {
                MovieTabFragment fragment=new MovieTabFragment();
                return fragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "FEED";
                case 1:
                    return "MOVIE";
            }
            return null;
        }
    }
}
