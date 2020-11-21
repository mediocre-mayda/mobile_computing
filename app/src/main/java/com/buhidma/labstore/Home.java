package com.buhidma.labstore;

import android.content.Intent;
import android.os.Bundle;

import com.buhidma.labstore.adapters.ProductsAdapter;
import com.buhidma.labstore.models.Product;
import com.buhidma.labstore.network.ApiService;
import com.buhidma.labstore.network.RetrofitBuilder;
import com.buhidma.labstore.usersession.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity implements ProductsAdapter.OverflowMenuClickHandler {
    Picasso picasso;
    private RecyclerView rv;
    private ApiService service;
    private static final String TAG = "Home";
    private ProductsAdapter adapter;
    private List<Product> list= new ArrayList<>();
    private Call<List<Product>> productCall;
    private Call<Void> deleteCall;
    UserSession session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new UserSession(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        if(session.isAdmin()) {
            fab.show();
            // the callback of the onclick listener
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Home.this, AddProduct.class));
                }
            });

        } else {
            fab.hide();
        }
        service = RetrofitBuilder.createService(ApiService.class);
        picasso = Picasso.with(this);
        rv = findViewById(R.id.rv);
        setupUpRecyclerView();
        getProducts();
    }

    public void setupUpRecyclerView() {

            GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rv.setLayoutManager(mLayoutManager);
            adapter = new ProductsAdapter(this, list, picasso, this, session);
            rv.setAdapter(adapter);
    }


    public void getProducts() {
        productCall = service.products();
        productCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.code() == 200 && response.body() != null){
                    list = response.body();
                    adapter.swapProducts(list);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }


    @Override
    public void onOverflowMenuClick(View view, Product product) {
        PopupMenu popup = new PopupMenu(this, view);


            popup.inflate(R.menu.list_card_overflow_menu);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.list_overflow_delete:
                        deleteProduct(product.getId());
                        break;
                }
                return false;
            });

        //popup.inflate(R.menu.list_card_overflow_menu);

        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            session.logoutUser();
            startActivity(new Intent(Home.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteProduct(String id) {
        deleteCall = service.deleteProduct(id);
        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 204) {
                    Toasty.success(Home.this, "Product deleted successfully!").show();
                    getProducts();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }


}


