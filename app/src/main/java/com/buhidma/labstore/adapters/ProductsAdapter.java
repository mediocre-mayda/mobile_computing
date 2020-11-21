package com.buhidma.labstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buhidma.labstore.R;
import com.buhidma.labstore.models.Product;
import com.buhidma.labstore.models.User;
import com.buhidma.labstore.usersession.UserSession;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductVH> {

    private List<Product> productList;
    private Picasso mPicasso;
    private Context context;
    private static final String TAG = "ProductsAdapter";
    private OverflowMenuClickHandler listener;
    private UserSession session;

    public ProductsAdapter(Context context, List<Product> productList, Picasso mPicasso, OverflowMenuClickHandler clickHandler, UserSession session) {
        this.context = context;
        this.productList = productList;
        this.mPicasso = mPicasso;
        this.listener = clickHandler;
        this.session = session;


    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.cards_cardview_layout, parent, false);
        view.setFocusable(true);
        return new ProductVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        Product currProduct = productList.get(position);

        holder.name.setText(currProduct.getName());
        holder.price.setText(String.valueOf(currProduct.getPrice()));
        mPicasso.load(currProduct.getImage()).into(holder.image);

        if(session.isAdmin()) {
            holder.ovm.setVisibility(View.VISIBLE);
            holder.ovm.setOnClickListener(view ->
                    listener.onOverflowMenuClick(view, currProduct));
        }


    }


    public interface OverflowMenuClickHandler {
        void onOverflowMenuClick(View view, Product product);
    }

    @Override
    public int getItemCount() {
        if(productList != null)
            return productList.size();
        else
            return 0;
    }

    public void swapProducts(List<Product> list) {
        productList = list;
        notifyDataSetChanged();
    }
}
