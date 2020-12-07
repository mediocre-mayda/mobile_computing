package com.zombie.docker.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zombie.docker.R;

class ProductVH extends RecyclerView.ViewHolder {
    TextView name, price, description;
    ImageView image, ovm;
    public ProductVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.cardcategory);
        price = itemView.findViewById(R.id.cardprice);
        description = itemView.findViewById(R.id.tv_description);
        image = itemView.findViewById(R.id.image);
        ovm = itemView.findViewById(R.id.mylist_overflow_menu);
    }
}
