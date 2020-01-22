package com.ayusma.pharamacymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ayusma.pharamacymanagementsystem.adapter.StaffRecyclerViewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffActivity extends AppCompatActivity {
    private List<String> names = new ArrayList<>();
    private List<String> id = new ArrayList<>();
    private List<String> sex = new ArrayList<>();
    private List<String> address = new ArrayList<>();
    private List<String> accessLevel = new ArrayList<>();
    private HashMap<String, Object> hashMap = new HashMap<>();
    private String TAG = StaffActivity.class.getSimpleName();
    private FirebaseFirestore db;
    private TextView textView;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_ativity);

        db = FirebaseFirestore.getInstance();


        AlertDialogHelper.createAlertDialog(this, "");

        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.text_view);
        loadCategory();


    }

    private void loadCategory() {
        AlertDialogHelper.showDialog();
        db.collection("staff")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    names.add("Name: " +(document.getData().get("name")));
                    address.add("Address: " +(document.getData().get("address")));
                    sex.add("Sex: " +(document.getData().get("sex")));
                    accessLevel.add("AccessLevel: " +(document.getData().get("accessLevel")));
                    id.add("Id:" +(document.getData().get("id")));
                }
                populateRecyclerView();
            } else {
                Log.d(TAG, "Error getting documents.", task.getException());
                Toast.makeText(getApplicationContext(), "Error loading Categories,make sure you have a good connection", Toast.LENGTH_LONG).show();

            }

        });

    }

    private void populateRecyclerView(){
        StaffRecyclerViewAdapter staffRecyclerViewAdapter = new StaffRecyclerViewAdapter(names,id,sex,address,accessLevel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (staffRecyclerViewAdapter.getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(staffRecyclerViewAdapter);
        }
        AlertDialogHelper.hideDialog();
    }
}
