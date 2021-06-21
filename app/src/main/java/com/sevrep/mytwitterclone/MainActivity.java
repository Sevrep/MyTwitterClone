package com.sevrep.mytwitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private EditText edtSignEmail;
    private EditText edtSignUsername;
    private EditText edtSignPassword;
    private Button btnSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.sign_up));

        ParseInstallation.getCurrentInstallation().saveInBackground();

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutSign);
        constraintLayout.setOnClickListener(this);

        edtSignEmail = findViewById(R.id.edtSignEmail);

        edtSignUsername = findViewById(R.id.edtSignUsername);

        edtSignPassword = findViewById(R.id.edtSignPassword);
        edtSignPassword.setOnKeyListener(this);

        btnSign = findViewById(R.id.btnSign);
        btnSign.setOnClickListener(this);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) {
            goToTwitterUsers();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnSign) {

            boolean isValid = true;

            String email = edtSignEmail.getText().toString().trim();
            String username = edtSignUsername.getText().toString().trim();
            String password = edtSignPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                edtSignEmail.setError("Email is required.");
                isValid = false;
            }

            if (TextUtils.isEmpty(username)) {
                edtSignUsername.setError("Username is required.");
                isValid = false;
            }

            if (TextUtils.isEmpty(password)) {
                edtSignPassword.setError("Password is required.");
                isValid = false;
            }

            if (isValid) {
                try {
                    final ParseUser appUser = new ParseUser();
                    appUser.setEmail(email);
                    appUser.setUsername(username);
                    appUser.setPassword(password);

                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Signing up user: " + username);
                    progressDialog.show();

                    appUser.signUpInBackground(e -> {
                        if (e == null) {
                            FancyToast.makeText(this, appUser.get("username") + " saved successfully!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            clearSignUp();
                            goToTwitterUsers();
                        } else {
                            FancyToast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                        }
                        progressDialog.dismiss();
                    });
                } catch (Exception e) {
                    FancyToast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                }
                hideKeypad(v);
            } else {
                FancyToast.makeText(this, "Some fields are missing.", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
            }
        } else if (viewId == R.id.btnLogin) {
            goToLogin();
        } else if (viewId == R.id.constraintLayoutSign) {
            hideKeypad(v);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v == edtSignPassword) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                onClick(btnSign);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void hideKeypad(View v) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToLogin() {
        Intent nextActivity = new Intent(this, LoginActivity.class);
        startActivity(nextActivity);
        this.finish();
    }

    private void clearSignUp() {
        edtSignEmail.setText("");
        edtSignUsername.setText("");
        edtSignPassword.setText("");
    }

    private void goToTwitterUsers() {
        Intent nextActivity = new Intent(this, TwitterUsersActivity.class);
        startActivity(nextActivity);
        this.finish();
    }

}