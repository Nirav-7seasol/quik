package com.messages.readmms.readsmss.callendservice.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.messages.readmms.readsmss.callendservice.MainCallActivity;
import com.messages.readmms.readsmss.callendservice.model.ContactCDO;

import com.messages.readmms.readsmss.databinding.FragmentAfterCallBinding;
import com.messages.readmms.readsmss.feature.compose.ComposeActivity;

public final class AfterCallFragment extends Fragment {
    public static final Companion Companion = new Companion();
    public FragmentAfterCallBinding binding;
    private ContactCDO contact;
    private String contactNumber = "";
    private String contactName = "";
    private String contactID = "";

    public final void setContactNumber(String str) {
        this.contactNumber = str;
    }

    public final void setContactName(String str) {
        this.contactName = str;
    }

    public final void setContactID(String str) {
        this.contactID = str;
    }

    public void setContact(ContactCDO contactCDO) {
        this.contact = contactCDO;
    }

    public static final class Companion {
        public AfterCallFragment getInstance(String str, String str2, String str3, ContactCDO contactCDO) {
            AfterCallFragment afterCallFragment = new AfterCallFragment();
            afterCallFragment.setContactNumber(str);
            afterCallFragment.setContactName(str2);
            afterCallFragment.setContactID(str3);
            afterCallFragment.setContact(contactCDO);
            return afterCallFragment;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentAfterCallBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initView();
    }

    public void initView() {
        bindObjects();
        bindListener();
    }

    public void bindObjects() {
        // Show help layout if contact ID is not valid
        if (this.contactID == null || this.contactID.isEmpty()) {
            binding.helpLayout.setVisibility(View.VISIBLE);
        }
    }

    public final void bindListener() {
        binding.helpLayout.setOnClickListener(view -> ((MainCallActivity) getActivity()).setSecondPage());
        binding.cvSend.setOnClickListener(view -> sendMessage());
        binding.cvSchedule.setOnClickListener(view -> sendMessage());
    }

    public void sendMessage() {
        try {
            Intent intent = new Intent(requireContext(), ComposeActivity.class);
            intent.putExtra("ComAdShow", false);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception unused) {
            unused.getMessage();
        }
    }
}
