package com.bsuir.poms.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class QuizMainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    public static final int RC_SIGN_IN = 1;

    private ViewPager mViewPager;

    private int currentQuestionId;
    private int questionsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_pager);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                currentUser = user;
            } else {
                // User is signed out
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                .setTheme(R.style.LoginTheme)
//                                .setLogo(R.mipmap.logo)
                                .build(),
                        RC_SIGN_IN);
            }
        };

//        ((App) getApplication()).setUser(currentUser);
        database = FirebaseDatabase.getInstance();

        // У тебя было так. У меня так не работает (хотя, когда я запускала прям твое - работало).
        // Потому что сначала идет quiz-блабла, а под ним уже questions в бд
//        DatabaseReference referenceToQuestionsInDb = database.getReference("questions");
        DatabaseReference referenceToQuestionsInDb = database.getReference().child("questions");

        referenceToQuestionsInDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentQuestionId = 0;
                questionsCount = (int) dataSnapshot.getChildrenCount();
                System.out.println("currentQuestionId: " + currentQuestionId  + " questionsCount: " + questionsCount);
                // выводит все верно - 0 позиция, 10 вопросов
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

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
                return questionsCount;
            }
        });

        mViewPager.setCurrentItem(currentQuestionId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) { // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Signed in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}