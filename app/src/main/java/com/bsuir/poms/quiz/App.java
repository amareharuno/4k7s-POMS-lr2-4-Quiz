package com.bsuir.poms.quiz;

import android.app.Application;

import com.google.firebase.auth.FirebaseUser;

public class App extends Application {

    private FirebaseUser user;
    private boolean[] answers = new boolean[10];
    private boolean[] checkedAnswers = new boolean[10];

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public boolean[] getAnswers() {
        return answers;
    }

    public boolean[] getCheckedAnswers() {
        return checkedAnswers;
    }
}