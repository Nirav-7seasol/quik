package com.moez.QKSMS.feature.permission;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.octoshrimpy.quik.databinding.TranslucentActivityMyBinding;

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
