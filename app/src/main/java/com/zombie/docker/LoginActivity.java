package com.zombie.docker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zombie.docker.models.User;
import com.zombie.docker.network.ApiService;
import com.zombie.docker.network.RetrofitBuilder;
import com.zombie.docker.networksync.CheckInternetConnection;
import com.zombie.docker.usersession.UserSession;
import com.kaopiz.kprogresshud.KProgressHUD;

import retrofit2.Call;
import retrofit2.Callback;


public class LoginActivity extends AppCompatActivity {

    private EditText edtemail,edtpass;
    private String email,pass,sessionmobile;
    private UserSession session;
    private TextView forgotpass,registernow;
    public static final String TAG = "MyTag";
    private int cartcount, wishlistcount;
    private ApiService service;
    private Call<User> loginCall;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e("Login CheckPoint","LoginActivity started");
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);


        edtemail= findViewById(R.id.email);
        edtpass= findViewById(R.id.password);

        Bundle registerinfo=getIntent().getExtras();
        if (registerinfo!=null) {
                edtemail.setText(registerinfo.getString("email"));
        }

        session= new UserSession(getApplicationContext());

        if(session.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this,Home.class));

        }

        //if user wants to register
        registernow= findViewById(R.id.register_now);
        registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,Register.class));
                finish();
            }
        });



        //Validating login details
        Button button=findViewById(R.id.login_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email=edtemail.getText().toString();
                pass=edtpass.getText().toString();

                if (validateUsername(email) && validatePassword(pass)) { //Username and Password Validation

                    //Progress Bar while connection establishes

                          final KProgressHUD progressDialog=  KProgressHUD.create(LoginActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();


            service = RetrofitBuilder.createService(ApiService.class);

            loginCall = service.login(email, pass);
            loginCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                    progressDialog.dismiss();
                    // Response from the server is in the form if a JSON, so we need a JSON Object

                        if(response.code() == 200 && response.body() != null) {
                            user = response.body();
                            String sessionname = user.getName();
                            sessionmobile = user.getMobile();
                            String sessionemail =  user.getEmail();


                            //create shared preference and store data
                            session.createLoginSession(sessionname,sessionemail,sessionmobile, user.isIs_admin());

                            Intent loginSuccess = new Intent(LoginActivity.this, Home.class);
                            startActivity(loginSuccess);
                            finish();
                        } else {
                            if(response.code() == 404)
                                Toast.makeText(LoginActivity.this, "Credentials invalid", Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(LoginActivity.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
                            }
                        }
                }
                // when there is a network or server error
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e(TAG, "onFailure: ", t);
                }
            });


                }

            }
        });


    }


    private boolean validatePassword(String pass) {


        if (pass.length() < 4 || pass.length() > 20) {
            edtpass.setError("Password Must consist of 4 to 20 characters");
            return false;
        }
        return true;
    }

    private boolean validateUsername(String email) {

        if (email.length() < 4 || email.length() > 30) {
            edtemail.setError("Email Must consist of 4 to 30 characters");
            return false;
        } else if (!email.matches("^[A-za-z0-9.@]+")) {
            edtemail.setError("Only . and @ characters allowed");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            edtemail.setError("Email must contain @ and .");
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Login CheckPoint","LoginActivity resumed");
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        }

    @Override
    protected void onStop () {
        super.onStop();
        Log.e("Login CheckPoint","LoginActivity stopped");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
