package com.messages.readmms.readsmss.feature.permission;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.messages.readmms.readsmss.databinding.TranslucentActivityMyBinding;

public class MyTranslucentActivity extends AppCompatActivity {

    TranslucentActivityMyBinding binding;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = TranslucentActivityMyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.translayRel.setOnClickListener(view -> finish());
        String stringExtra = getIntent().getStringExtra("autostart");
        if (stringExtra != null) {
            binding.textTrans.setText(stringExtra);
        }
    }
}
