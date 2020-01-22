package com.ayusma.pharamacymanagementsystem.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ayusma.pharamacymanagementsystem.AlertDialogHelper;
import com.ayusma.pharamacymanagementsystem.R;
import com.ayusma.pharamacymanagementsystem.StoreActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffHomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {
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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_store, container, false);

        db = FirebaseFirestore.getInstance();
        AlertDialogHelper.createAlertDialog(getContext(), "Please Wait...");

        spinnerCategory = root.findViewById(R.id.spinner_category);
        spinnerDrug = root.findViewById(R.id.spinner_name);
        editTextDrugId = root.findViewById(R.id.edit_text_drug_id);
        editTextDrugId.setEnabled(false);
        editTextDrugName = root.findViewById(R.id.edit_text_drug_name);
        editTextDrugName.setEnabled(false);
        editTextQuantity = root.findViewById(R.id.edit_text_drug_quantity);
        editTextQuantity.setEnabled(false);
        editTextDoage = root.findViewById(R.id.edit_text_drug_dosage);
        editTextDoage.setEnabled(false);
        editTextPrice = root.findViewById(R.id.edit_drug_text_price);
        editTextPrice.setEnabled(false);
        editTextExpiry = root.findViewById(R.id.edit_text_exp_date);
        editTextExpiry.setEnabled(false);
        editTextsellinQuantity = root.findViewById(R.id.edit_text_drug_quanitiy_sold);
        editTextAmount = root.findViewById(R.id.edit_text_amount);
        editTextAmount.setEnabled(false);
        buttonSell = root.findViewById(R.id.btn_sell);

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
                        Toast.makeText(getContext(), "Purchase Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("TAG", "Error adding document", e);
                    });


        });

        spinnerDrug.setOnItemSelectedListener(this);
        spinnerCategory.setOnItemSelectedListener(this);

        loadCategory();
        return root;
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
                        Toast.makeText(getContext(), "Error loading Categories,make sure you have a good connection", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void populateSpinnerCategory() {
        spinnerArrayCategoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
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
                        Toast.makeText(getContext(), "Error loading drug names: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }


    private void populateNameCategory() {
        spinnerArrayNameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, drugName);
        spinnerDrug.setAdapter(spinnerArrayNameAdapter);
        populateEditText();
    }

    private void populateEditText() {
        if(drugName.size() != 0) {
            editTextDrugName.setText(drugName.get(spinnerDrug.getSelectedItemPosition()));
            editTextPrice.setText(price.get(spinnerDrug.getSelectedItemPosition()));
            editTextExpiry.setText(expiry.get(spinnerDrug.getSelectedItemPosition()));
            editTextDoage.setText(dosage.get(spinnerDrug.getSelectedItemPosition()));
            editTextQuantity.setText(quantity.get(spinnerDrug.getSelectedItemPosition()));
            editTextDrugId.setText(String.valueOf(drugId.get(spinnerDrug.getSelectedItemPosition())));
        }


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
                    Toast.makeText(getContext(), "Purchase Successful", Toast.LENGTH_SHORT).show();
                    refresh();
                }).addOnFailureListener(e -> {
            AlertDialogHelper.hideDialog();
            buttonSell.setEnabled(true);
            Toast.makeText(getContext(), "Purchase Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.w("TAG", "Error adding document", e);
        });


    }

    public void refresh(){
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.nav_host_fragment);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
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