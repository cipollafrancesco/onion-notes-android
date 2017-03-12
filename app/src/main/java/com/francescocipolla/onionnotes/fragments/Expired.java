package com.francescocipolla.onionnotes.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.francescocipolla.onionnotes.R;

/**
 * Created by Ciccio on 12/03/2017.
 */

public class Expired extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expired, container, false);
    }
}
