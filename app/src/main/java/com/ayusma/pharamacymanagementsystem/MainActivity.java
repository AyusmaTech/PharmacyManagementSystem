package com.ayusma.pharamacymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private MaterialCardView cardViewAdmin, cardViewSupervisor, cardViewStaff;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        cardViewAdmin = findViewById(R.id.card_view_admin);
        cardViewSupervisor = findViewById(R.id.card_view_supervisor);
        cardViewStaff = findViewById(R.id.card_view_staff);

        cardViewAdmin.setOnClickListener(view -> {
            startActivity(new Intent(this,AdminLoginActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        });

        cardViewSupervisor.setOnClickListener(view -> {
            startActivity(new Intent(this,SupervisorLoginActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        cardViewStaff.setOnClickListener(view -> {
            startActivity(new Intent(this,StaffLoginActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });





    }


}
