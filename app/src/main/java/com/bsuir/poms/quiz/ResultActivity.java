package com.bsuir.poms.quiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private RecyclerView mCrimeRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mCrimeRecyclerView = findViewById(R.id.score_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("results");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Result> results = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Result result = snapshot.getValue(Result.class);
                    results.add(result);
                }
                mCrimeRecyclerView.setAdapter(new ResultAdapter(results));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private class ResultHolder extends RecyclerView.ViewHolder {
        private TextView mEmailTextView;
        private TextView mScoreTextView;
        private Result mResult;

        public ResultHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.result_list_item, parent, false));

            mEmailTextView = itemView.findViewById(R.id.email_text_view);
            mScoreTextView = itemView.findViewById(R.id.score_text_view);
        }

        public void bind(Result result) {
            mResult = result;
            mEmailTextView.setText(mResult.getEmail());
            mScoreTextView.setText(String.valueOf(mResult.getScore()));
        }
    }

    private class ResultAdapter extends RecyclerView.Adapter<ResultHolder> {
        private List<Result> mResults;

        public ResultAdapter(List<Result> results) {
            mResults = results;
        }

        @Override
        public ResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ResultActivity.this);
            return new ResultHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ResultHolder holder, int position) {
            Result result = mResults.get(position);
            holder.bind(result);
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }
    }
}