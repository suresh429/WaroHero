package com.waro.hero.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.waro.hero.R;
import com.waro.hero.databinding.ActivityLoginBinding;
import com.waro.hero.helper.UserSessionManager;
import com.waro.hero.helper.Util;
import com.waro.hero.network.ApiInterface;
import com.waro.hero.network.RetrofitService;

import com.google.gson.JsonObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waro.hero.helper.Util.keypadHide;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // login session
        session = new UserSessionManager(LoginActivity.this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        binding.btnContinue.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_continue) {


            if (Objects.requireNonNull(binding.etMobile.getText()).toString().equalsIgnoreCase("warohero@gmail.com")  &&
                    Objects.requireNonNull(binding.etPassword.getText()).toString().equalsIgnoreCase("WaroHero@123")){

                session.createLogin();

                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }else{
                Toast.makeText(this, "Enter Valid Details", Toast.LENGTH_SHORT).show();
            }

        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}