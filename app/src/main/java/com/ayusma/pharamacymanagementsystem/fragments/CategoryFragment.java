package com.ayusma.pharamacymanagementsystem.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayusma.pharamacymanagementsystem.AlertDialogHelper;
import com.ayusma.pharamacymanagementsystem.R;
import com.ayusma.pharamacymanagementsystem.adapter.CategoryRecyclerViewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CategoryFragment extends Fragment implements View.OnClickListener {
    AlertDialog alertDialog;
    String textCategoryName;
    Button btnCreate;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);

        db = FirebaseFirestore.getInstance();

         recyclerView = root.findViewById(R.id.recyclerView);


        CategoryRecyclerViewAdapter recyclerViewAdapter = new CategoryRecyclerViewAdapter(operation.getAllSavedText());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (recyclerViewAdapter.getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(recyclerViewAdapter);
        }









        View addCategory = View.inflate(getContext(),R.layout.layout_add_category,null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(addCategory);

       btnCreate = addCategory.findViewById(R.id.btn_create_category);
       btnCreate.setOnClickListener(this);
        addCategory.findViewById(R.id.btn_cancel).setOnClickListener(this);
        EditText categoryName = addCategory.findViewById(R.id.edit_text_category_name);
        textCategoryName = categoryName.getText().toString();
        alertDialog = alertDialogBuilder.create();

        AlertDialogHelper.createAlertDialog(getContext(),"Creating Category");






        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                alertDialog.hide();
                break;
            case  R.id.btn_create_category:
                if(textCategoryName.isEmpty()){
                    Toast.makeText(getContext(),"Category Name can not be empty",Toast.LENGTH_SHORT).show();
                }else {
                    Map<String, Object> category = new HashMap<>();
                    category.put("categoryName",textCategoryName);

                    btnCreate.setEnabled(false);
                    db.collection("category")
                            .add(category)
                            .addOnSuccessListener(documentReference -> {
                                btnCreate.setEnabled(true);
                                AlertDialogHelper.hideDialog();
                                Toast.makeText(getContext(),"DocumentSnapshot added with ID: " + documentReference.getId(),Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                            })
                            .addOnFailureListener(e -> {
                                AlertDialogHelper.hideDialog();
                                btnCreate.setEnabled(true);
                                Toast.makeText(getContext(),"Error adding document "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error adding document", e);
                            });


                }


        }

    }
}