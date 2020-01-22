package com.ayusma.pharamacymanagementsystem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ayusma.pharamacymanagementsystem.DrugState;
import com.ayusma.pharamacymanagementsystem.ManageDrugs;
import com.ayusma.pharamacymanagementsystem.R;
import com.ayusma.pharamacymanagementsystem.StoreActivity;
import com.ayusma.pharamacymanagementsystem.ViewSalesActivity;
import com.google.android.material.card.MaterialCardView;

public class SupervisorHomeFragment extends Fragment {
   private MaterialCardView cardViewGotoStore,cardViewViewDrug,cardViewViewSales,cardViewDrugState;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_supervisor, container, false);
        cardViewGotoStore = root.findViewById(R.id.card_view_goto_store);
        cardViewViewDrug = root.findViewById(R.id.card_view_view_drugs);
        cardViewDrugState = root.findViewById(R.id.card_view_drug_state);
        cardViewViewSales = root.findViewById(R.id.card_view_view_sales);

        cardViewViewSales.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ViewSalesActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        cardViewViewDrug.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ManageDrugs.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        cardViewDrugState.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), DrugState.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        cardViewGotoStore.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), StoreActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        return root;
    }
}