package com.bsuir.poms.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bsuir.poms.quiz.constant.Const;
import com.bsuir.poms.quiz.model.Question;
import com.bsuir.poms.quiz.model.Result;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuestionFragment extends Fragment {
    private Question mQuestion;
    private boolean[] mAnswers;
    private boolean[] mCheckedAnswers;

    private RadioGroup mRadioGroup;

    private RadioButton mAnswerOne;
    private RadioButton mAnswerTwo;
    private RadioButton mAnswerThree;
    private RadioButton mAnswerFour;

    private Button mConfirmButton;

    private TextView mQuestionText;

    FirebaseDatabase database;
    DatabaseReference resultsDatabaseReference;
    DatabaseReference questionsDatabaseReference;

    public QuestionFragment() {
    }

    public static QuestionFragment newInstance(int id) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(Const.QUESTION_ID_DB_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnswers = ((App) getActivity().getApplication()).getAnswers();
        mCheckedAnswers = ((App) getActivity().getApplication()).getCheckedAnswers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        database = FirebaseDatabase.getInstance();
        resultsDatabaseReference = database.getReference().child(Const.RESULTS_DB_KEY);
        questionsDatabaseReference = database.getReference().child(Const.QUESTIONS_DB_KEY);

        mRadioGroup = view.findViewById(R.id.answer_group);
        mConfirmButton = view.findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(
                view1 -> {
                    int answer = mQuestion.getNumOfRightAnswer();
                    if (answer == getAnswerNum(mRadioGroup.getCheckedRadioButtonId())) {
                        view.setBackgroundColor(getResources().getColor(R.color.green));
                        mAnswers[mQuestion.getId()] = true;
                    } else {
                        view.setBackgroundColor(getResources().getColor(R.color.red));
                        mAnswers[mQuestion.getId()] = false;
                    }
                    mCheckedAnswers[mQuestion.getId()] = true;

                    int count = 0;
                    for (boolean checkedAnswer : mCheckedAnswers) {
                        if (checkedAnswer) count++;
                    }

                    setRadioGroupNotClickable();

                    if (count == mAnswers.length) {
                        Result result = new Result();
                        result.setId(resultsDatabaseReference.push().getKey());
                        result.setEmail(((App) getActivity().getApplication()).getUser().getEmail());
                        int score = 0;
                        for (boolean rightAnswer : mAnswers) {
                            if (rightAnswer) score++;
                        }
                        result.setScore(score);
                        resultsDatabaseReference.child(result.getId()).setValue(result);
                        Intent intent = new Intent(getActivity(), ResultActivity.class);
                        startActivity(intent);
                    }
                });

        mAnswerOne = view.findViewById(R.id.answer_one);
        mAnswerTwo = view.findViewById(R.id.answer_two);
        mAnswerThree = view.findViewById(R.id.answer_three);
        mAnswerFour = view.findViewById(R.id.answer_four);

        mQuestionText = view.findViewById(R.id.question_text);

        questionsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String questionId = String.valueOf(QuestionFragment.this.getArguments()
                        .getInt(Const.QUESTION_ID_DB_KEY, 0));

                mQuestion = dataSnapshot.child(questionId).getValue(Question.class);

                if (mQuestion != null) {
                    mAnswerOne.setText(mQuestion.getFirstAnswer());
                    mAnswerTwo.setText(mQuestion.getSecondAnswer());
                    mAnswerThree.setText(mQuestion.getThirdAnswer());
                    mAnswerFour.setText(mQuestion.getFourthAnswer());

                    mQuestionText.setText(mQuestion.getQuestion());
                    if (mCheckedAnswers[mQuestion.getId()]) {
                        setRadioGroupNotClickable();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;
    }

    private void setRadioGroupNotClickable() {
        mAnswerOne.setClickable(false);
        mAnswerTwo.setClickable(false);
        mAnswerThree.setClickable(false);
        mAnswerFour.setClickable(false);
        mConfirmButton.setClickable(false);
    }

    private int getAnswerNum(int id) {
        switch (id) {
            case R.id.answer_one:
                return 1;
            case R.id.answer_two:
                return 2;
            case R.id.answer_three:
                return 3;
            case R.id.answer_four:
                return 4;
            default:
                return 0;
        }
    }
}