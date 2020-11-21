package com.buhidma.labstore.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buhidma.labstore.R;

class ProductVH extends RecyclerView.ViewHolder {
    TextView name, price;
    ImageView image, ovm;
    public ProductVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.cardcategory);
        price = itemView.findViewById(R.id.cardprice);
        image = itemView.findViewById(R.id.image);
        ovm = itemView.findViewById(R.id.mylist_overflow_menu);
    }
}
