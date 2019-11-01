package com.ayusma.pharamacymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseFirestore db;
    private List<String> categoryList;
    private String TAG = StoreActivity.class.getSimpleName();
    private Map<String, Object> documents = new HashMap<>();
    private Spinner spinnerCategory, spinnerDrug;
    private ArrayList<String> drugName = new ArrayList<>();
    private ArrayList<Integer> drugId = new ArrayList<>();
    private ArrayList<String> expiry = new ArrayList<>();
    private ArrayList<String> dosage = new ArrayList<>();
    private ArrayList<String> quantity = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private Button buttonSell;
    private EditText editTextDrugId, editTextDrugName, editTextDoage, editTextQuantity, editTextPrice, editTextExpiry, editTextsellinQuantity, editTextAmount;
    private String id;
    private int load = 0;
    private ArrayAdapter<String> spinnerArrayCategoryAdapter;
    private ArrayAdapter<String> spinnerArrayNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        db = FirebaseFirestore.getInstance();
        AlertDialogHelper.createAlertDialog(this, "Please Wait...");

        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDrug = findViewById(R.id.spinner_name);
        editTextDrugId = findViewById(R.id.edit_text_drug_id);
        editTextDrugId.setEnabled(false);
        editTextDrugName = findViewById(R.id.edit_text_drug_name);
        editTextDrugName.setEnabled(false);
        editTextQuantity = findViewById(R.id.edit_text_drug_quantity);
        editTextQuantity.setEnabled(false);
        editTextDoage = findViewById(R.id.edit_text_drug_dosage);
        editTextDoage.setEnabled(false);
        editTextPrice = findViewById(R.id.edit_drug_text_price);
        editTextPrice.setEnabled(false);
        editTextExpiry = findViewById(R.id.edit_text_exp_date);
        editTextExpiry.setEnabled(false);
        editTextsellinQuantity = findViewById(R.id.edit_text_drug_quanitiy_sold);
        editTextAmount = findViewById(R.id.edit_text_amount);
        editTextAmount.setEnabled(false);
        buttonSell = findViewById(R.id.btn_sell);

        editTextsellinQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  editTextAmount.setText(Integer.valueOf(price.get(spinnerDrug.getSelectedItemPosition()))*Integer.valueOf(charSequence.toString()));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    int Price = Integer.valueOf(price.get(spinnerDrug.getSelectedItemPosition()));
                    int amount = Price * Integer.valueOf(charSequence.toString());
                    editTextAmount.setText(String.valueOf(amount));
                } else {
                    editTextAmount.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    int Price = Integer.valueOf(price.get(spinnerDrug.getSelectedItemPosition()));
                    int amount = Price * Integer.valueOf(editable.toString().trim());
                    editTextAmount.setText(String.valueOf(amount));
                } else {
                    editTextAmount.setText("");
                }
            }
        });

        buttonSell.setOnClickListener(view -> {
            if (editTextsellinQuantity.getText().toString().trim().isEmpty()) {
                editTextsellinQuantity.setError("Enter quantity to be sold");
                return;
            }
            if (Integer.valueOf(editTextsellinQuantity.getText().toString()) > Integer.valueOf(quantity.get(spinnerDrug.getSelectedItemPosition()))) {
                editTextsellinQuantity.setError("Quantity of product to be purchased is higher than available quantities");
                return;
            }
            String category = spinnerCategory.getSelectedItem().toString();
            String drugName = spinnerDrug.getSelectedItem().toString();
            String dosage = editTextDoage.getText().toString().trim();
            String quantity = editTextsellinQuantity.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();
            buttonSell.setEnabled(false);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("category", category);
            hashMap.put("drugName", drugName);
            hashMap.put("dosage", dosage);
            hashMap.put("quantity", quantity);
            hashMap.put("amount", amount);

            AlertDialogHelper.showDialog();
            db.collection("soldDrugs")
                    .add(hashMap)
                    .addOnSuccessListener(documentReference -> {
                        deductQuantity(Integer.valueOf(quantity));
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        AlertDialogHelper.hideDialog();
                        Toast.makeText(getApplicationContext(), "Purchase Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "Error adding document", e);
                    });


        });

        spinnerDrug.setOnItemSelectedListener(this);
        spinnerCategory.setOnItemSelectedListener(this);

        loadCategory();

    }

    private void loadCategory() {
        AlertDialogHelper.showDialog();
        db.collection("category")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                            drugName.add(documents.get("drugName").toString());
                            drugId.add(Integer.valueOf(documents.get("drugId").toString()));
                            dosage.add(documents.get("Dosage").toString());
                            quantity.add(documents.get("Quantity").toString());
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
        editTextDrugName.setText(drugName.get(spinnerDrug.getSelectedItemPosition()));
        editTextPrice.setText(price.get(spinnerDrug.getSelectedItemPosition()));
        editTextExpiry.setText(expiry.get(spinnerDrug.getSelectedItemPosition()));
        editTextDoage.setText(dosage.get(spinnerDrug.getSelectedItemPosition()));
        editTextQuantity.setText(quantity.get(spinnerDrug.getSelectedItemPosition()));
        editTextDrugId.setText(String.valueOf(drugId.get(spinnerDrug.getSelectedItemPosition())));


    }

    public void deductQuantity(int Quantity) {
        int quan = Integer.valueOf(quantity.get(spinnerDrug.getSelectedItemPosition()));
        int deduct = quan - Quantity;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Quantity", deduct);

        DocumentReference documentReference = db.collection("drugs")
                .document(ids.get(spinnerDrug.getSelectedItemPosition()));

        documentReference.update(hashMap)
                .addOnSuccessListener(aVoid -> {
                    AlertDialogHelper.hideDialog();
                    editTextsellinQuantity.setText("");
                    editTextAmount.setText("");
                    buttonSell.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Purchase Successful", Toast.LENGTH_SHORT).show();
                    recreate();
                }).addOnFailureListener(e -> {
            AlertDialogHelper.hideDialog();
            buttonSell.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Purchase Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.w("TAG", "Error adding document", e);
        });


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
                    editTextDrugName.setText(drugName.get(spinnerDrug.getSelectedItemPosition()));
                    editTextPrice.setText(price.get(spinnerDrug.getSelectedItemPosition()));
                    editTextExpiry.setText(expiry.get(spinnerDrug.getSelectedItemPosition()));
                    editTextDoage.setText(dosage.get(spinnerDrug.getSelectedItemPosition()));
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
