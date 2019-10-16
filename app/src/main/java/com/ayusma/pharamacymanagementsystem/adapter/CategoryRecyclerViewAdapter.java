package com.ayusma.pharamacymanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayusma.pharamacymanagementsystem.R;

import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private List<String> mCategories;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPosition = -1;
    private Context context;

    // data is passed into the constructor
    public CategoryRecyclerViewAdapter(Context context, List<String> categories) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mCategories = categories;
    }

    // inflates the row layout from xml when needed


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = mCategories.get(position);


        holder.textViewCategory.setText(category);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewCategory;
        ImageView btnDelete, btnRename;

        ViewHolder(View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.edit_text_category_name);
            btnDelete = itemView.findViewById(R.id.btn_delete_category);
            btnRename = itemView.findViewById(R.id.btn_rename_category);



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
        return mCategories.size();


    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mCategories.get(id);
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