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

public class StaffRecyclerViewAdapter extends RecyclerView.Adapter<StaffRecyclerViewAdapter.ViewHolder> {

    private List<String> names;
    private List<String> ids;
    private List<String> sex;
    private List<String> address;
    private List<String> acceslevel;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPosition = -1;
    private Context context;

    public StaffRecyclerViewAdapter(List<String> names, List<String> ids, List<String> sex, List<String> address, List<String> acceslevel) {
        this.names = names;
        this.ids = ids;
        this.sex = sex;
        this.address = address;
        this.acceslevel = acceslevel;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view =mInflater.inflate(R.layout.layout_staff_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewAddress.setText(address.get(position));
        holder.textViewAccesLevel.setText(acceslevel.get(position));
        holder.textViewId.setText(ids.get(position));
        holder.textViewName.setText(names.get(position));
        holder.textViewSex.setText(sex.get(position));

    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewName,textViewId,textViewSex,textViewAccesLevel,textViewAddress;

        ViewHolder(View itemView) {
            super(itemView);
        textViewName = itemView.findViewById(R.id.text_view_staff_name);
        textViewSex = itemView.findViewById(R.id.text_view_staff_sex);
        textViewId = itemView.findViewById(R.id.text_view_staff_id);
        textViewAccesLevel = itemView.findViewById(R.id.text_view_staff_acces_level);
        textViewAddress = itemView.findViewById(R.id.text_view_staff_address);



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
        return names.size();


    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return names.get(id);
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