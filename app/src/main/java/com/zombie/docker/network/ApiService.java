package com.zombie.docker.network;


import com.zombie.docker.models.Product;
import com.zombie.docker.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<User> register(@Field("name") String name,
                        @Field("email") String email,
                        @Field("password") String password,
                        @Field("mobile") String mobile
                       );


    @POST("login")
    @FormUrlEncoded
    Call<User> login(@Field("email") String email, @Field("password") String password);


    @GET("product")
    Call<List<Product>> products();

    @POST("product")
    @FormUrlEncoded
    Call<Void> postProduct(@Field("name") String name, @Field("image") String image,
                           @Field("description") String desc, @Field("price") float price);


    @DELETE("product/{id}")
    Call<Void> deleteProduct(@Path("id") String id);
}

