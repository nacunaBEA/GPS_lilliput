package com.example.google.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.google.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecycleViewFragamet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecycleViewFragamet extends Fragment {


    public RecycleViewFragamet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);
        return  view;
    }

}
