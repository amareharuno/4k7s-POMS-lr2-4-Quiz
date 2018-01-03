package com.bsuir.poms.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class QuizPagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private static final String QUESTION_ID = "com.qwerfghi.autismquiz.question_id";
    private static final String QUESTION_COUNT = "com.qwerfghi.autismquiz.question_count";

    public static Intent newIntent(Context packageContext, int id, int count) {
        Intent intent = new Intent(packageContext, QuizPagerActivity.class);
        intent.putExtra(QUESTION_ID, id);
        intent.putExtra(QUESTION_COUNT, count);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_pager);

        int id = getIntent().getIntExtra(QUESTION_ID, 0);
        int count = getIntent().getIntExtra(QUESTION_COUNT, 0);

        mViewPager = findViewById(R.id.question_view_pager);
        mViewPager.setOffscreenPageLimit(2);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return QuestionFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return count;
            }
        });
        mViewPager.setCurrentItem(id);
    }
}