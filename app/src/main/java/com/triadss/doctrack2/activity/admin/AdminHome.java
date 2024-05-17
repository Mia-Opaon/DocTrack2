package com.triadss.doctrack2.activity.admin;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.LoginActivity;
import com.triadss.doctrack2.activity.admin.fragments.AdminHomeFragment;
import com.triadss.doctrack2.activity.admin.fragments.AdminManageUserAccount;
import com.triadss.doctrack2.activity.admin.fragments.AdminGenerateReportsPage;
import com.triadss.doctrack2.databinding.ActivityAdminHomeBinding;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        replaceFragment(new AdminHomeFragment());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        ActivityAdminHomeBinding binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.healthprof_menu) {
                replaceFragment(new AdminManageUserAccount());
            }
            if (item.getItemId() == R.id.report_menu) {
                replaceFragment(new AdminGenerateReportsPage());
            }
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
                boolean isCurrentlyAtHomepage = currentFragment instanceof AdminHomeFragment;
                if (!isCurrentlyAtHomepage) {
                    fragmentManager.popBackStack();
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
        boolean isCurrentlyAtHomepage = currentFragment instanceof AdminHomeFragment;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        if (isCurrentlyAtHomepage) {
            fragmentTransaction.addToBackStack("toHome");
        }
        fragmentTransaction.commit();
    }
}