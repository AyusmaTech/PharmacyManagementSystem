package com.ayusma.pharamacymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ManageDrugs extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseFirestore db;
    private ArrayList<String> quantity = new ArrayList<>();
    private ArrayList<Integer> drugId = new ArrayList<>();
    private ArrayList<String> expiry = new ArrayList<>();
    private ArrayList<String> drugName = new ArrayList<>();
    private Map<String, Object> documents = new HashMap<>();
    private ArrayList<String> price = new ArrayList<>();
    private EditText editTextDrugId, editTextQuantity, editTextExpiry, editTextRestock, editTextPrice;
    private Button restockButton, deleteButton, updatePriceButton;
    private Spinner spinnerCategory, spinnerDrug;
    private List<String> categoryList;
    private String TAG = StoreActivity.class.getSimpleName();
    private ArrayAdapter<String> spinnerArrayCategoryAdapter;
    private ArrayAdapter<String> spinnerArrayNameAdapter;
    private ArrayList<String> ids = new ArrayList<>();
    private int load = 0;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_drugs);

        db = FirebaseFirestore.getInstance();

        editTextDrugId = findViewById(R.id.edit_text_drug_id);
        editTextDrugId.setEnabled(false);
        editTextQuantity = findViewById(R.id.edit_text_drug_available_quanitiy);
        editTextQuantity.setEnabled(false);
        editTextExpiry = findViewById(R.id.edit_text_exp_date);
        editTextExpiry.setEnabled(false);
        editTextRestock = findViewById(R.id.edit_text_restock);
        editTextPrice = findViewById(R.id.edit_drug_text_price);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDrug = findViewById(R.id.spinner_name);
        restockButton = findViewById(R.id.btn_restock);
        deleteButton = findViewById(R.id.btn_delete_drug);
        updatePriceButton = findViewById(R.id.btn_update_price);

        spinnerDrug.setOnItemSelectedListener(this);
        spinnerCategory.setOnItemSelectedListener(this);

        AlertDialogHelper.createAlertDialog(this, "");
        loadCategory();

        updatePriceButton.setOnClickListener(view -> updatePrice());
        restockButton.setOnClickListener(view -> restock());
        deleteButton.setOnClickListener(view -> deleteDrug());

    }

    private void loadCategory() {
        AlertDialogHelper.showDialog();
        db.collection("category")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoryList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    categoryList.add(document.getId());
                }
                populateSpinnerCategory();
                loadNames();

            } else {
                Log.d(TAG, "Error getting documents.", task.getException());
                Toast.makeText(getApplicationContext(), "Error loading Categories,make sure you have a good connection", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void populateSpinnerCategory() {
        spinnerArrayCategoryAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, categoryList);
        spinnerCategory.setAdapter(spinnerArrayCategoryAdapter);
    }

    private void loadNames() {
        String name = spinnerCategory.getSelectedItem().toString();
        db.collection("drugs")
                .whereEqualTo("Category", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            documents.putAll(document.getData());
                            ids.add(document.getId());
                            drugId.add(Integer.valueOf(documents.get("drugId").toString()));
                            quantity.add(documents.get("Quantity").toString());
                            drugName.add(documents.get("drugName").toString());
                            price.add(documents.get("costPrice").toString());
                            expiry.add(documents.get("expiryDate").toString());
                        }
                        AlertDialogHelper.hideDialog();
                        populateNameCategory();
                        load = 1;
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(getApplicationContext(), "Error loading drug names: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }


    private void populateNameCategory() {
        spinnerArrayNameAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, drugName);
        spinnerDrug.setAdapter(spinnerArrayNameAdapter);
        populateEditText();
    }

    public void populateEditText() {
        editTextPrice.setText(price.get(spinnerDrug.getSelectedItemPosition()));
        editTextExpiry.setText(expiry.get(spinnerDrug.getSelectedItemPosition()));
        editTextQuantity.setText(quantity.get(spinnerDrug.getSelectedItemPosition()));
        editTextDrugId.setText(String.valueOf(drugId.get(spinnerDrug.getSelectedItemPosition())));


    }

    public void updatePrice() {
        String price = editTextPrice.getText().toString().trim();
        if (price.isEmpty()) {
            editTextPrice.setError("Enter Price");
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("costPrice", price);

        updatePriceButton.setEnabled(false);
        AlertDialogHelper.showDialog();

        DocumentReference documentReference = db.collection("drugs")
                .document(ids.get(spinnerDrug.getSelectedItemPosition()));

        documentReference.update(hashMap)
                .addOnSuccessListener(aVoid -> {
                    AlertDialogHelper.hideDialog();
                    updatePriceButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Price Updated Successfully", Toast.LENGTH_LONG).show();
                    recreate();

                }).addOnFailureListener(e -> {
            AlertDialogHelper.hideDialog();
            updatePriceButton.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Price Updating Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.w("TAG", "Error adding document", e);
        });

    }

    private void restock() {
        String Quantity = editTextRestock.getText().toString().trim();
        if (Quantity.isEmpty()) {
            editTextRestock.setError("Enter Quantity");
            return;
        }

        int newQuantity = Integer.valueOf(quantity.get(spinnerDrug.getSelectedItemPosition())) + Integer.valueOf(Quantity);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Quantity", newQuantity);

        restockButton.setEnabled(false);
        AlertDialogHelper.showDialog();

        DocumentReference documentReference = db.collection("drugs")
                .document(ids.get(spinnerDrug.getSelectedItemPosition()));
        documentReference.update(hashMap)
                .addOnSuccessListener(aVoid -> {
                    AlertDialogHelper.hideDialog();
                    editTextPrice.setText("");
                    restockButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Quantity Updated Successfully", Toast.LENGTH_LONG).show();
                    load = 0;
                    recreate();
                }).addOnFailureListener(e -> {
            AlertDialogHelper.hideDialog();
            restockButton.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Quantity Updating Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.w("TAG", "Error adding document", e);

        });

    }

    private void deleteDrug() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage(R.string.delete_notice);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            deleteButton.setEnabled(false);
            AlertDialogHelper.showDialog();
            DocumentReference documentReference = db.collection("drugs")
                    .document(ids.get(spinnerDrug.getSelectedItemPosition()));
            documentReference.delete()
                    .addOnSuccessListener(aVoid -> {
                        deleteButton.setEnabled(true);
                        ids.remove(spinnerDrug.getSelectedItemPosition());
                        categoryList.remove(spinnerDrug.getSelectedItemPosition());
                        drugName.remove(spinnerDrug.getSelectedItemPosition());
                        spinnerArrayCategoryAdapter.notifyDataSetChanged();
                        spinnerArrayNameAdapter.notifyDataSetChanged();
                        AlertDialogHelper.hideDialog();
                        recreate();
                        Toast.makeText(getApplicationContext(), "Drug deleted", Toast.LENGTH_LONG).show();

                    }).addOnFailureListener(e -> {
                deleteButton.setEnabled(true);
                AlertDialogHelper.hideDialog();
                Toast.makeText(getApplicationContext(), "Drug deleting Failed " + e.getMessage(), Toast.LENGTH_LONG).show();

            });
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> alertDialog.hide());
        alertDialog = builder.create();
        alertDialog.show();



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (load != 0) {
            switch (adapterView.getId()) {
                case R.id.spinner_category:
                    AlertDialogHelper.showDialog();
                    loadNames();
                    break;

                case R.id.spinner_name:
                    editTextPrice.setText(price.get(spinnerDrug.getSelectedItemPosition()));
                    editTextExpiry.setText(expiry.get(spinnerDrug.getSelectedItemPosition()));
                    editTextQuantity.setText(quantity.get(spinnerDrug.getSelectedItemPosition()));
                    editTextDrugId.setText(String.valueOf(drugId.get(spinnerDrug.getSelectedItemPosition())));
                    break;


            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
