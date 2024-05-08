package com.example.striderproject.ui.track;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.striderproject.databinding.FragmentTrackBinding;

public class TrackFragment extends Fragment {

    private FragmentTrackBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.example.striderproject.ui.track.TrackViewModel accountViewModel =
                new ViewModelProvider(this).get(com.example.striderproject.ui.track.TrackViewModel.class);

        binding = FragmentTrackBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTrack;
        accountViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}