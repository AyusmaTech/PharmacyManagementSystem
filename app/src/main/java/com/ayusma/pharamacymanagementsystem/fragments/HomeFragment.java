package com.ayusma.pharamacymanagementsystem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ayusma.pharamacymanagementsystem.R;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {
MaterialCardView cardViewGotoStore,cardViewViewDrug,cardViewViewStaff,cardViewViewSales,cardViewDrugState;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        cardViewGotoStore = root.findViewById(R.id.card_view_goto_store);
        cardViewViewDrug = root.findViewById(R.id.card_view_view_drugs);
        cardViewViewStaff = root.findViewById(R.id.card_view_view_staff);
        cardViewDrugState = root.findViewById(R.id.card_view_drug_state);
        cardViewViewSales = root.findViewById(R.id.card_view_view_sales);

        return root;
    }
}