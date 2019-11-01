package com.ayusma.pharamacymanagementsystem.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ayusma.pharamacymanagementsystem.AlertDialogHelper;
import com.ayusma.pharamacymanagementsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

public class AddDrugFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private EditText editTextName, editTextId, editTextManufacturer, editTextBatch, editTextProd, editTextExp, editTextDosage, editTextReg, editTextQuan, editTextCostP;
    private ImageButton btnProd, btnReg, btnExp;
    private FirebaseFirestore db;
    private DatePickerDialog dpdProd, dpdExp, dpdReg;
    private Spinner spinnerCategory;
    private Button btnSumbit;
    private int calType;
    Map<String, Object> hashMap = new HashMap<>();
    private String TAG = AddDrugFragment.class.getSimpleName();
    private List<String> categoryList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_drug, container, false);

        db = FirebaseFirestore.getInstance();

        editTextName = root.findViewById(R.id.edit_text_name);
        editTextId = root.findViewById(R.id.edit_text_id);
        editTextManufacturer = root.findViewById(R.id.edit_text_manufacturer);
        editTextBatch = root.findViewById(R.id.edit_text_batch);
        editTextProd = root.findViewById(R.id.edit_text_prod_date);
        editTextExp = root.findViewById(R.id.edit_text_expiry_date);
        editTextDosage = root.findViewById(R.id.edit_text_dosage);
        editTextReg = root.findViewById(R.id.edit_text_reg_date);
        editTextQuan = root.findViewById(R.id.edit_text_quantity);
        editTextCostP = root.findViewById(R.id.edit_text_cost_price);
        btnProd = root.findViewById(R.id.btn_prod_date);
        btnReg = root.findViewById(R.id.btn_reg_date);
        btnExp = root.findViewById(R.id.btn_exp_date);
        btnSumbit = root.findViewById(R.id.btn_submit);
        spinnerCategory = root.findViewById(R.id.spinner_category);

        btnProd.setOnClickListener(this);
        btnExp.setOnClickListener(this);
        btnReg.setOnClickListener(this);
        btnSumbit.setOnClickListener(this);


        Calendar now = Calendar.getInstance();
        dpdProd = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpdExp = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpdReg = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        AlertDialogHelper.createAlertDialog(getContext(), "Adding drug....");


        loadCategory();

        return root;
    }

    private void loadCategory() {
        db.collection("category")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categoryList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categoryList.add(document.getId());
                    }
                    populateSpinner();
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(getContext(), "Error loading Categories,make sure you have a good connection", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void populateSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        spinnerCategory.setAdapter(spinnerArrayAdapter);
    }


    private void addDrug() {
        String textName = editTextName.getText().toString().trim();
        String textId = editTextId.getText().toString().trim();
        String textManufacturer = editTextManufacturer.getText().toString().trim();
        String textBatch = editTextBatch.getText().toString().trim();
        String textProd = editTextProd.getText().toString().trim();
        String textExp = editTextExp.getText().toString().trim();
        String textDosage = editTextDosage.getText().toString().trim();
        String textReg = editTextReg.getText().toString().trim();
        String textQuan = editTextQuan.getText().toString().trim();
        String textCostP = editTextCostP.getText().toString().trim();
        String textCategory;
        if (spinnerCategory.getSelectedItem() != null) {
            textCategory = spinnerCategory.getSelectedItem().toString();
        } else {
            Toast.makeText(getContext(), "Select Drug Category: Make sure you have a good connection to load categories,or go to the category section if you havent created one", Toast.LENGTH_LONG).show();
            return;
        }


        if (textName.isEmpty()) {
            editTextName.setError("Enter a Drug Name");
            editTextName.requestFocus();
            return;
        }

        if (textId.isEmpty()) {
            editTextId.setError("Enter an Id");
            editTextId.requestFocus();
            return;
        }

        if (textManufacturer.isEmpty()) {
            editTextManufacturer.setError("Enter Manufacturer Name");
            editTextManufacturer.requestFocus();
            return;
        }

        if (textBatch.isEmpty()) {
            editTextBatch.setError("Enter Batch Number");
            editTextBatch.requestFocus();
            return;
        }
        if (textProd.isEmpty()) {
            editTextProd.setError("Set Production Date");
            editTextProd.requestFocus();
            return;
        }
        if (textExp.isEmpty()) {
            editTextExp.setError("Set Expiry Date");
            editTextExp.requestFocus();
            return;
        }

        if (textDosage.isEmpty()) {
            editTextDosage.setError("Enter Dosage");
            editTextDosage.requestFocus();
            return;
        }

        if (textReg.isEmpty()) {
            editTextReg.setError("Set Registration Date");
            editTextReg.requestFocus();
            return;
        }

        if (textQuan.isEmpty()) {
            editTextQuan.setError("Set Quantity");
            editTextQuan.requestFocus();
            return;
        }
        if (textCostP.isEmpty()) {
            editTextCostP.setError("Set Cost Price");
            editTextCostP.requestFocus();
            return;
        }

        AlertDialogHelper.showDialog();
        btnSumbit.setEnabled(false);

        Map<String, Object> drug = new HashMap<>();
        drug.put("drugName", textName);
        drug.put("drugId", textId);
        drug.put("Manufacturer", textManufacturer);
        drug.put("batchNo", textBatch);
        drug.put("productionDate", textProd);
        drug.put("expiryDate", textExp);
        drug.put("registrationDate", textReg);
        drug.put("Dosage", textDosage);
        drug.put("Category", textCategory);
        drug.put("Quantity", textQuan);
        drug.put("costPrice", textCostP);

        db.collection("drugs")
                .add(drug)
                .addOnSuccessListener(documentReference -> {
                    btnSumbit.setEnabled(true);
                    AlertDialogHelper.hideDialog();
                    editTextName.setText("");
                    editTextId.setText("");
                    editTextManufacturer.setText("");
                    editTextBatch.setText("");
                    editTextProd.setText("");
                    editTextExp.setText("");
                    editTextDosage.setText("");
                    editTextReg.setText("");
                    editTextQuan.setText("");
                    editTextCostP.setText("");
                    Toast.makeText(getContext(), "Drug Added Successfully", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    AlertDialogHelper.hideDialog();
                    btnSumbit.setEnabled(true);
                    Toast.makeText(getContext(), "Error adding drug " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "Error adding document", e);
                });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prod_date:
                if (getFragmentManager() != null) {
                    calType = 1;
                    dpdProd.show(getFragmentManager(), "datePickerDialog");
                }
                break;

            case R.id.btn_exp_date:
                if (getFragmentManager() != null) {
                    calType = 2;
                    dpdExp.show(getFragmentManager(), "datePickerDialog");
                }
                break;
            case R.id.btn_reg_date:
                if (getFragmentManager() != null) {
                    calType = 3;
                    dpdReg.show(getFragmentManager(), "datePickerDialog");
                }
                break;

            case R.id.btn_submit:
                addDrug();
                break;

        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        switch (calType) {
            case 1:
                editTextProd.setText(date);
                break;
            case 2:
                editTextExp.setText(date);
                break;
            case 3:
                editTextReg.setText(date);
                break;
        }

    }

}