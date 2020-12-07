package com.zombie.docker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import com.zombie.docker.models.User;
import com.zombie.docker.network.ApiService;
import com.zombie.docker.network.RetrofitBuilder;
import com.zombie.docker.usersession.UserSession;

import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zombie.docker.networksync.CheckInternetConnection;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.kaopiz.kprogresshud.KProgressHUD;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private EditText edtname, edtemail, edtpass, edtcnfpass, edtnumber;
    private String check,name,email,password,mobile,profile;
//    ImageView upload;
    RequestQueue requestQueue;
    boolean IMAGE_STATUS = false;
    Bitmap profilePicture;
    public static final String TAG = "MyTag";
    private ApiService service;
    private Call<User> registerCall;
    private User user;
    private UserSession session;
    private TextView login_tv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        session = new UserSession(getApplicationContext());
        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

//        upload=findViewById(R.id.uploadpic);

        edtname = findViewById(R.id.name);
        edtemail = findViewById(R.id.email);
        edtpass = findViewById(R.id.password);
        edtcnfpass = findViewById(R.id.confirmpassword);
        edtnumber = findViewById(R.id.number);

        edtname.addTextChangedListener(nameWatcher);
        edtemail.addTextChangedListener(emailWatcher);
        edtpass.addTextChangedListener(passWatcher);
        edtcnfpass.addTextChangedListener(cnfpassWatcher);
        edtnumber.addTextChangedListener(numberWatcher);

        requestQueue = Volley.newRequestQueue(Register.this);

        //validate user details and register user

        Button button = findViewById(R.id.register);

        login_tv =findViewById(R.id.login_now);
        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,LoginActivity.class));
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: reached onClick");
                //TODO AFTER VALDATION
                if (validateName() && validateEmail() && validatePass() && validateCnfPass() && validateNumber()) {
                    Log.d(TAG, "onClick: if statement is true");
                    name = edtname.getText().toString();
                    email = edtemail.getText().toString();
                    password = edtcnfpass.getText().toString();
                    mobile = edtnumber.getText().toString();


                    final KProgressHUD progressDialog = KProgressHUD.create(Register.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();

                    Log.d(TAG, "onClick: progree dialog is showing? " + progressDialog.isShowing());

                    service = RetrofitBuilder.createService(ApiService.class);

                    registerCall = service.register(name, email, password, mobile);

                    registerCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            progressDialog.dismiss();


                            if(response.code() == 200 && response.body() != null) {
                                user = response.body();
                                Toasty.success(Register.this,"Registered Successfully",Toast.LENGTH_SHORT,true).show();
                                //create shared preference and store data
                                session.createLoginSession(user.getName(),user.getEmail(),user.getMobile(), user.isIs_admin());

                                Intent loginSuccess = new Intent(Register.this, Home.class);
                                startActivity(loginSuccess);
                                finish();
                            } else {
                                if(response.code() == 404)
                                    Toast.makeText(Register.this, "Credentials invalid", Toast.LENGTH_SHORT).show();
                                else{
                                    Toast.makeText(Register.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
                                }
                            }
                            Log.d(TAG, "onResponse: " + response);
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });
                    //take user to reset password


//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//
//                Dexter.withActivity(Register.this)
//                        .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        .withListener(new MultiplePermissionsListener() {
//                            @Override
//                            public void onPermissionsChecked(MultiplePermissionsReport report) {
//                                // check if all permissions are granted
//                                if (report.areAllPermissionsGranted()) {
//                                    // do you work now
//                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                    intent.setType("image/*");
//                                    startActivityForResult(intent, 1000);
//                                }
//
//                                // check for permanent denial of any permission
//                                if (report.isAnyPermissionPermanentlyDenied()) {
//                                    // permission is denied permenantly, navigate user to app settings
//                                    Snackbar.make(view, "Kindly grant Required Permission", Snackbar.LENGTH_LONG)
//                                            .setAction("Allow", null).show();
//                                }
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                                token.continuePermissionRequest();
//                            }
//                        })
//                        .onSameThread()
//                        .check();
//
//
//
//                //result will be available in onActivityResult which is overridden
//            }
//        });
                }

            }

        });

    }

    private void sendRegistrationEmail(final String name, final String emails) {


                BackgroundMail.newBuilder(Register.this)
                        .withSendingMessage("Sending Welcome Greetings to Your Email !")
                        .withSendingMessageSuccess("Kindly Check Your Email now !")
                        .withSendingMessageError("Failed to send password ! Try Again !")
                        .withUsername("beingdevofficial@gmail.com")
                        .withPassword("Singh@30")
                        .withMailto(emails)
                        .withType(BackgroundMail.TYPE_PLAIN)
                        .withSubject("Greetings from Magic Print")
                        .withBody("Hello Mr/Miss, "+ name + "\n " + getString(R.string.registermail1))
                        .send();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
//            //Image Successfully Selected
//            try {
//                //parsing the Intent data and displaying it in the ImageView
//                Uri imageUri = data.getData();//Getting uri of the data
//                InputStream imageStream = getContentResolver().openInputStream(imageUri);//creating an InputArea
//                profilePicture = BitmapFactory.decodeStream(imageStream);//decoding the input stream to bitmap
//
//                IMAGE_STATUS = true;//setting the flag
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private boolean validateProfile() {
        if (!IMAGE_STATUS)
            Toasty.info(Register.this,"Select A Profile Picture",Toast.LENGTH_LONG).show();
        return IMAGE_STATUS;
    }

    private boolean validateNumber() {

        check = edtnumber.getText().toString();
        Log.e("inside number",check.length()+" ");
        if (check.length()>10) {
           return false;
        }else if(check.length()<10){
            return false;
        }
        return true;
    }

    private boolean validateCnfPass() {

        check = edtcnfpass.getText().toString();

        return check.equals(edtpass.getText().toString());
    }

    private boolean validatePass() {


        check = edtpass.getText().toString();

        if (check.length() < 4 || check.length() > 20) {
           return false;
        } else if (!check.matches("^[A-za-z0-9@]+")) {
            return false;
        }
        return true;
    }

    private boolean validateEmail() {

        check = edtemail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
                return false;
        }

        return true;
    }

    private boolean validateName() {

        check = edtname.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtname.setError("Name Must consist of 4 to 20 characters");
            }
        }

    };

    //TextWatcher for Email -----------------------------------------------------

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                edtemail.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                edtemail.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                edtemail.setError("Enter Valid Email");
            }

        }

    };

    //TextWatcher for pass -----------------------------------------------------

    TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtpass.setError("Password Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9@]+")) {
                edtemail.setError("Only @ special character allowed");
            }
        }

    };

    //TextWatcher for repeat Password -----------------------------------------------------

    TextWatcher cnfpassWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (!check.equals(edtpass.getText().toString())) {
                edtcnfpass.setError("Both the passwords do not match");
            }
        }

    };


    //TextWatcher for Mobile -----------------------------------------------------

    TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length()>10) {
                edtnumber.setError("Number cannot be grated than 10 digits");
            }else if(check.length()<10){
                edtnumber.setError("Number should be 10 digits");
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    @Override
    protected void onStop () {
        super.onStop();
    }
}


