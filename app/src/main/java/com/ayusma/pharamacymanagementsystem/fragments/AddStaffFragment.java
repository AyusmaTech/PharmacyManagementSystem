package com.ayusma.pharamacymanagementsystem.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ayusma.pharamacymanagementsystem.AdminLoginActivity;
import com.ayusma.pharamacymanagementsystem.AlertDialogHelper;
import com.ayusma.pharamacymanagementsystem.R;
import com.ayusma.pharamacymanagementsystem.SupervisorLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddStaffFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private EditText editTextName, editTextId, editTextAddress, editTextNationality, editTextOrigin, editTextMobile, editTextEmail, editTextPassword, editTextDateEmployed;
    private Spinner spinnerAge, spinnerStatus;
    private RadioGroup radioGroupSex, radioGroupAccessLevel;
    private RadioButton radioButtonSex, radioButtonAccessLevl;
    private FirebaseFirestore db;
    private Button btnAddStaff;
    private ImageButton btnDateEmp;
    private DatePickerDialog dpdPEmployed;
    private View root;
    private FirebaseAuth firebaseAuth;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_add_staff, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        editTextName = root.findViewById(R.id.edit_text_staff_name);
        editTextId = root.findViewById(R.id.edit_text_reg_id);
        editTextAddress = root.findViewById(R.id.edit_text_address);
        editTextNationality = root.findViewById(R.id.edit_text_nationality);
        editTextOrigin = root.findViewById(R.id.edit_text_origin);
        editTextMobile = root.findViewById(R.id.edit_text_mobile);
        editTextEmail = root.findViewById(R.id.edit_text_email);
        editTextPassword = root.findViewById(R.id.edit_text_password);
        editTextDateEmployed = root.findViewById(R.id.edit_date_employed);
        btnDateEmp = root.findViewById(R.id.btn_date_emp);
        btnAddStaff = root.findViewById(R.id.btn_add_staff);
        spinnerAge = root.findViewById(R.id.spinner_age);
        spinnerStatus = root.findViewById(R.id.spinner_status);
        radioGroupAccessLevel = root.findViewById(R.id.radio_group_access);
        radioGroupSex = root.findViewById(R.id.radio_group_sex);


        List<Integer> age = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            age.add(i);
        }

        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_spinner_item, age);
        spinnerAge.setAdapter(spinnerArrayAdapter);

        AlertDialogHelper.createAlertDialog(getContext(), "Creating Staff");


        btnAddStaff.setOnClickListener(view -> addStaff());
        btnDateEmp.setOnClickListener(view -> dpdPEmployed.show(getFragmentManager(), "datePickerDialog"));

        Calendar now = Calendar.getInstance();
        dpdPEmployed = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );


        return root;
    }

    private void addStaff() {
        String textStaffName = editTextName.getText().toString().trim();
        String textStaffId = editTextId.getText().toString().trim();
        String textStaffAddress = editTextAddress.getText().toString().trim();
        String textStaffNationality = editTextNationality.getText().toString().trim();
        String textStaffOrigin = editTextOrigin.getText().toString().trim();
        String textStaffMobile = editTextMobile.getText().toString().trim();
        String textStaffEmail = editTextEmail.getText().toString().trim();
        String textStaffPassword = editTextPassword.getText().toString().trim();
        String textStaffDateEmployed = editTextDateEmployed.getText().toString().trim();
        String textStaffAge = spinnerAge.getSelectedItem().toString();
        String textStaffStatus = spinnerStatus.getSelectedItem().toString();
        String textStaffSex, textStaffAccessLevel;
        radioButtonAccessLevl = root.findViewById(radioGroupAccessLevel.getCheckedRadioButtonId());
        radioButtonSex = root.findViewById(radioGroupSex.getCheckedRadioButtonId());


        if (textStaffName.isEmpty()) {
            editTextName.setError("Enter a Name");
            editTextName.requestFocus();
            return;
        }

        if (textStaffId.isEmpty()) {
            editTextId.setError("Enter Id");
            editTextId.requestFocus();
            return;
        }

        if (textStaffAddress.isEmpty()) {
            editTextAddress.setError("Enter Address");
            editTextAddress.requestFocus();
            return;
        }

        if (textStaffNationality.isEmpty()) {
            editTextNationality.setError("Enter Nationality");
            editTextNationality.requestFocus();
            return;
        }

        if (textStaffOrigin.isEmpty()) {
            editTextOrigin.setError("Enter Origin");
            editTextOrigin.requestFocus();
            return;
        }

        if (textStaffMobile.isEmpty()) {
            editTextMobile.setError("Enter Mobile Number");
            editTextMobile.requestFocus();
            return;
        }

        if (textStaffMobile.length() != 11) {
            editTextMobile.setError("Minimum of 11 digit is allowed ");
            editTextMobile.requestFocus();
            return;
        }

        if (textStaffEmail.isEmpty()) {
            editTextEmail.setError("Enter an Email Address");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(textStaffEmail).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (textStaffPassword.isEmpty()) {
            editTextPassword.setError("Enter Password");
            editTextPassword.requestFocus();
            return;
        }

        if (textStaffPassword.length() < 6) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        if (textStaffDateEmployed.isEmpty()) {
            editTextEmail.setError("Select Employed Date");
            editTextEmail.requestFocus();
            return;
        }

        if (radioButtonSex == null) {
            Toast.makeText(getContext(), "Select a Sex", Toast.LENGTH_SHORT).show();
            return;
        } else {
            textStaffSex = radioButtonSex.getText().toString();
        }

        if (radioButtonAccessLevl == null) {
            Toast.makeText(getContext(), "Select an Access Level", Toast.LENGTH_SHORT).show();
            return;
        } else {
            textStaffAccessLevel = radioButtonAccessLevl.getText().toString();
        }


        btnAddStaff.setEnabled(false);
        AlertDialogHelper.showDialog();

        Map<String, Object> staffDetails = new HashMap<>();
        staffDetails.put("name", textStaffName);
        staffDetails.put("id", textStaffId);
        staffDetails.put("address", textStaffAddress);
        staffDetails.put("nationality", textStaffNationality);
        staffDetails.put("origin", textStaffOrigin);
        staffDetails.put("mobile", textStaffMobile);
        staffDetails.put("email", textStaffEmail);
        staffDetails.put("password", textStaffPassword);
        staffDetails.put("age", textStaffAge);
        staffDetails.put("status", textStaffStatus);
        staffDetails.put("accessLevel", textStaffAccessLevel);
        staffDetails.put("sex", textStaffSex);


        db.collection("staff")
                .add(staffDetails)
                .addOnSuccessListener(documentReference -> {
                    authenticate(textStaffEmail, textStaffPassword);
                    // Toast.makeText(getContext(), "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    // Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    AlertDialogHelper.hideDialog();
                    btnAddStaff.setEnabled(true);
                    Toast.makeText(getContext(), "Account Creation  Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "Error adding document", e);
                });


    }

    private void authenticate(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AlertDialogHelper.hideDialog();
                        btnAddStaff.setEnabled(true);
                        Toast.makeText(getContext(), " Account Created Successfully", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(getContext(), AdminLoginActivity.class));
                        Toast.makeText(getContext(), "Please login again to continue", Toast.LENGTH_SHORT).show();
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


                    }

                }).addOnFailureListener(e -> {
            AlertDialogHelper.hideDialog();
            btnAddStaff.setEnabled(true);
            Toast.makeText(getContext(), "Account Creation  Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        editTextDateEmployed.setText(date);

    }

}