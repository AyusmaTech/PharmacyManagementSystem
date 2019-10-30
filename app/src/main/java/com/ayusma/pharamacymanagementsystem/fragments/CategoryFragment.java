package com.ayusma.pharamacymanagementsystem.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayusma.pharamacymanagementsystem.AlertDialogHelper;
import com.ayusma.pharamacymanagementsystem.R;
import com.ayusma.pharamacymanagementsystem.adapter.CategoryRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment implements View.OnClickListener {
    private  AlertDialog alertDialog;
    private String textCategoryName;
    private Button btnCreate;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView textViewAddCategory;
    private  List<String> categories = new ArrayList<>();
    private View addCategory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);

        db = FirebaseFirestore.getInstance();

         recyclerView = root.findViewById(R.id.recyclerView);
         textViewAddCategory = root.findViewById(R.id.text_view_add_category);
         floatingActionButton = root.findViewById(R.id.fab_create_category);

         categories.add("Parectamol");
         categories.add("Asinlomabimn");
         categories.add("potato level");


        CategoryRecyclerViewAdapter recyclerViewAdapter = new CategoryRecyclerViewAdapter(getContext(),categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (recyclerViewAdapter.getItemCount() == 0) {
            textViewAddCategory.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(recyclerViewAdapter);
        }


        addCategory = View.inflate(getContext(),R.layout.layout_add_category,null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(addCategory);

       btnCreate = addCategory.findViewById(R.id.btn_create_category);
       btnCreate.setOnClickListener(this);
        addCategory.findViewById(R.id.btn_cancel).setOnClickListener(this);
        progressBar = addCategory.findViewById(R.id.progress_bar);
        alertDialog = alertDialogBuilder.create();

       // AlertDialogHelper.createAlertDialog(getContext(),"Creating Category");
        floatingActionButton.setOnClickListener(view -> {
            alertDialog.show();
        });







        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                alertDialog.hide();
                break;
            case  R.id.btn_create_category:
                EditText categoryName = addCategory.findViewById(R.id.edit_text_category_name);
                textCategoryName = categoryName.getText().toString();
                if(textCategoryName.isEmpty()){
                    Toast.makeText(getContext(),"Category Name can not be empty",Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String, Object> category = new HashMap<>();
                    category.put("categoryName",textCategoryName);
                    categoryName.setText("");
                    btnCreate.setEnabled(false);
                    db.collection("category")
                            .add(category)
                            .addOnSuccessListener(documentReference -> {
                                btnCreate.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
//                                AlertDialogHelper.hideDialog();
                                Toast.makeText(getContext(),"Category Created Successfully",Toast.LENGTH_LONG).show();
                                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                            })
                            .addOnFailureListener(e -> {
                                AlertDialogHelper.hideDialog();
                                btnCreate.setEnabled(true);
                                Toast.makeText(getContext(),"Error creating Category "+e.getMessage(),Toast.LENGTH_LONG).show();
                                Log.w("TAG", "Error adding document", e);
                            });


                }


        }

    }
}