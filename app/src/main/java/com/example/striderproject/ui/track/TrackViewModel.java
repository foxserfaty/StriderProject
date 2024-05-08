package com.example.striderproject.ui.track;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrackViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TrackViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is track fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}