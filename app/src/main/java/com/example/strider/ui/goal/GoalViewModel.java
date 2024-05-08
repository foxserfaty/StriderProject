package com.example.strider.ui.goal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GoalViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GoalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Goal fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}