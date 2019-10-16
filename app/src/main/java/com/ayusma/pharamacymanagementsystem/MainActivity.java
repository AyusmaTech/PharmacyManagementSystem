package com.ayusma.pharamacymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cardViewAdmin,cardViewSupervisor, cardViewStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardViewAdmin = findViewById(R.id.card_view_admin);
        cardViewSupervisor = findViewById(R.id.card_view_supervisor);
        cardViewStaff = findViewById(R.id.card_view_staff);

        cardViewAdmin.setOnClickListener(this);
        cardViewSupervisor.setOnClickListener(this);
        cardViewStaff.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.card_view_admin:
                startActivity(new Intent(this,AdminLoginActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.card_view_supervisor:
                startActivity(new Intent(getApplicationContext(),SupervisorLoginActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.card_view_staff:
                startActivity(new Intent(this,StaffLoginActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;



        }

    }
}
