package com.example.pennywise.fragments;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.pennywise.R;
import com.example.pennywise.service.NotificationHelper;

public class SettingsFragment extends Fragment {

    private Switch switchDark, switchNotifications;

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchDark = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);

        setupDarkModeSwitch();
        setupNotificationSwitch();

        return view;
    }

    private void setupDarkModeSwitch() {
        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void setupNotificationSwitch() {
        // Set initial state
        boolean notificationsEnabled = NotificationHelper.isNotificationEnabled(requireContext());
        switchNotifications.setChecked(notificationsEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationHelper.setNotificationEnabled(requireContext(), isChecked);
        });
    }
}