package com.example.striderproject.ui.goal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.striderproject.databinding.FragmentGoalBinding;

public class GoalFragment extends Fragment {

    private FragmentGoalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GoalViewModel goalViewModel =
                new ViewModelProvider(this).get(GoalViewModel.class);

        binding = FragmentGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGoal;
        goalViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}