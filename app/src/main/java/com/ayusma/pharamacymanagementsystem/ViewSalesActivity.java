package com.ayusma.pharamacymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ayusma.pharamacymanagementsystem.adapter.SalesViewAdapter;
import com.ayusma.pharamacymanagementsystem.adapter.StaffRecyclerViewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewSalesActivity extends AppCompatActivity {
    private List<String> category = new ArrayList<>();
    private List<String> drugName = new ArrayList<>();
    private List<String> dosage = new ArrayList<>();
    private List<String> quantity = new ArrayList<>();
    private List<String> amount = new ArrayList<>();
    private String TAG = ViewSalesActivity.class.getSimpleName();
    private FirebaseFirestore db;
    private TextView textView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);
        db = FirebaseFirestore.getInstance();
        AlertDialogHelper.createAlertDialog(this, "");
        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.text_view);

        loadCategory();
    }

    private void loadCategory() {
        AlertDialogHelper.showDialog();
        db.collection("soldDrugs")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    category.add("Name: " +(document.getData().get("category")));
                    drugName.add("Address: " +(document.getData().get("drugName")));
                    dosage.add("Sex: " +(document.getData().get("dosage")));
                    quantity.add("AccessLevel: " +(document.getData().get("quantity")));
                    amount.add("Id:" +(document.getData().get("amount")));
                }
                populateRecyclerView();
            } else {
                Log.d(TAG, "Error getting documents.", task.getException());
                Toast.makeText(getApplicationContext(), "Error loading Categories,make sure you have a good connection", Toast.LENGTH_LONG).show();

            }

        });

    }

    private void populateRecyclerView(){
        SalesViewAdapter salesViewAdapter = new SalesViewAdapter(category,drugName,dosage,quantity,amount);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (salesViewAdapter.getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(salesViewAdapter);
        }
        AlertDialogHelper.hideDialog();
    }

}
