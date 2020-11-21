package com.buhidma.labstore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.buhidma.labstore.network.ApiService;
import com.buhidma.labstore.network.RetrofitBuilder;
import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProduct extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText cardName;
    private TextInputEditText cardimage;
    private TextInputEditText carddesc;
    private TextInputEditText cardprice;
    private Button bSubmit;
    private ApiService service;
    Call<Void> postProduct;
    private static final String TAG = "AddProduct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        cardName =  findViewById(R.id.tiet_movie_name);
        cardimage = findViewById(R.id.tiet_movie_logo);
        cardprice = findViewById(R.id.price);
        carddesc =  findViewById(R.id.description);
        bSubmit =   findViewById(R.id.b_submit);

        bSubmit.setOnClickListener(this);

        service = RetrofitBuilder.createService(ApiService.class);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.b_submit:
                if(!isEmpty(cardName) && !isEmpty(cardimage) && !isEmpty(cardprice) && !isEmpty(carddesc)){
                    postProduct(cardName.getText().toString().trim(),cardimage.getText().toString(),carddesc.getText().toString(),Float.parseFloat(cardprice.getText().toString()));
                }else{
                    if(isEmpty(cardName)){
                        Toast.makeText(this, "Please enter a name!", Toast.LENGTH_SHORT).show();
                    }else if(isEmpty(cardimage)){
                        Toast.makeText(this, "Please specify a url for the image", Toast.LENGTH_SHORT).show();
                    }else if(isEmpty(cardprice)){
                        Toast.makeText(this, "Please enter a price !", Toast.LENGTH_SHORT).show();
                    }else if(isEmpty(carddesc)){
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

//    private void postProduct(String trim, String toString, String toString1, float parseFloat) {
//    }

    private void postProduct(String name, String image, String desc, float price) {
        postProduct = service.postProduct(name, image, desc, price);
        postProduct.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toasty.success(AddProduct.this, "Product Posted successfully").show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toasty.error(AddProduct.this, t.getMessage()).show();
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }


    //check if edittext is empty
    private boolean isEmpty(TextInputEditText textInputEditText) {
        if (textInputEditText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
