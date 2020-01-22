package com.ayusma.pharamacymanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayusma.pharamacymanagementsystem.R;

import java.util.List;

public class SalesViewAdapter extends RecyclerView.Adapter<SalesViewAdapter.ViewHolder> {

    private List<String> category;
    private List<String> drugName;
    private List<String> dosage;
    private List<String> quantity;
    private List<String> amount;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPosition = -1;
    private Context context;


    public SalesViewAdapter(List<String> category, List<String> drugName, List<String> dosage, List<String> quantity, List<String> amount) {
        this.category = category;
        this.drugName = drugName;
        this.dosage = dosage;
        this.quantity = quantity;
        this.amount = amount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view =mInflater.inflate(R.layout.layout_sales_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewAmount.setText(amount.get(position));
        holder.textViewQuantity.setText(quantity.get(position));
        holder.textViewDosage.setText(dosage.get(position));
        holder.textViewCategory.setText(category.get(position));
        holder.textViewDrugName.setText(drugName.get(position));

    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewCategory, textViewDosage, textViewDrugName, textViewQuantity, textViewAmount;

        ViewHolder(View itemView) {
            super(itemView);
        textViewCategory = itemView.findViewById(R.id.text_view_category);
        textViewDrugName = itemView.findViewById(R.id.text_view_drug_name);
        textViewDosage = itemView.findViewById(R.id.text_view_dosage);
        textViewQuantity = itemView.findViewById(R.id.text_view_quantity);
        textViewAmount = itemView.findViewById(R.id.text_view_amount);



            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return category.size();


    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return category.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}