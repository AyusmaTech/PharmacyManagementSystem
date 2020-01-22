package com.ayusma.pharamacymanagementsystem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DrugState extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String TAG = DrugState.class.getSimpleName();
    private FirebaseFirestore db;
    private ArrayList<String> categoryList;
    private ArrayAdapter<String> spinnerArrayCategoryAdapter;
    private ArrayAdapter<String> spinnerArrayNameAdapter;
    private ArrayList<String> quantity = new ArrayList<>();
    private ArrayList<Integer> drugId = new ArrayList<>();
    private ArrayList<String> expiry = new ArrayList<>();
    private ArrayList<String> drugName = new ArrayList<>();
    private Map<String, Object> documents = new HashMap<>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();

    private EditText editTextDrugId, editTextQuantity, editTextExpiry, editTextDaysLeft;
    private Spinner spinnerCategory,spinnerDrug;
    private int load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_state);

        db = FirebaseFirestore.getInstance();

        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDrug = findViewById(R.id.spinner_name);
        editTextDrugId = findViewById(R.id.edit_text_drug_id);
        editTextDrugId.setEnabled(false);
        editTextExpiry = findViewById(R.id.edit_text_exp_date);
        editTextExpiry.setEnabled(false);
        editTextQuantity = findViewById(R.id.edit_text_drug_available_quanitiy);
        editTextQuantity.setEnabled(false);
        editTextDaysLeft = findViewById(R.id.edit_text_days_left);
        editTextDaysLeft.setEnabled(false);

        spinnerCategory.setOnItemSelectedListener(this);
        spinnerDrug.setOnItemSelectedListener(this);

        AlertDialogHelper.createAlertDialog(this,"");

        loadCategory();


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
       // editTextPrice.setText(price.get(spinnerDrug.getSelectedItemPosition()));
        if(drugId.size() != 0) {
            editTextExpiry.setText(expiry.get(spinnerDrug.getSelectedItemPosition()));
            editTextQuantity.setText(quantity.get(spinnerDrug.getSelectedItemPosition()));
            editTextDrugId.setText(String.valueOf(drugId.get(spinnerDrug.getSelectedItemPosition())));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY,0);
            try {
               Date day = dateFormat.parse(expiry.get(spinnerDrug.getSelectedItemPosition()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   long days =  betweenDates(day,today.getTime());
                    editTextDaysLeft.setText((String.valueOf((int) days)));
                }else {
                    long days = getDifferenceDays(day,today.getTime());
                    editTextDaysLeft.setText((String.valueOf((int) days)));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }




    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long betweenDates(java.util.Date firstdate, java.util.Date secondDate){
        return ChronoUnit.DAYS.between(firstdate.toInstant(),secondDate.toInstant());
    }

    public  long getDifferenceDays(java.util.Date date1, java.util.Date date2){
        long diff = date1.getTime() - date2.getTime();
        return TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
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
                  //  editTextPrice.setText(price.get(spinnerDrug.getSelectedItemPosition()));
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
