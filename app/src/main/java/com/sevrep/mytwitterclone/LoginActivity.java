package com.sevrep.mytwitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private EditText edtLoginEmail;
    private EditText edtLoginPassword;
    private Button btnLoginLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.log_in));

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutLogin);
        constraintLayout.setOnClickListener(this);

        edtLoginEmail = findViewById(R.id.edtLoginEmail);

        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        edtLoginPassword.setOnKeyListener(this);

        btnLoginLogin = findViewById(R.id.btnLoginLogin);
        btnLoginLogin.setOnClickListener(this);

        Button btnLoginSign = findViewById(R.id.btnLoginSign);
        btnLoginSign.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnLoginSign) {
            goToSign();
        } else if (viewId == R.id.btnLoginLogin) {

            boolean isValid = true;

            String email = edtLoginEmail.getText().toString().trim();
            String password = edtLoginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                edtLoginEmail.setError("Email is required.");
                isValid = false;
            }
            if (TextUtils.isEmpty(password)) {
                edtLoginPassword.setError("Password is required.");
                isValid = false;
            }

            if (isValid) {
                try {
                    ParseUser.logInInBackground(edtLoginEmail.getText().toString().trim(), edtLoginPassword.getText().toString().trim(), (user, e) -> {
                        if (user != null && e == null) {
                            FancyToast.makeText(this, "Welcome, " + user.get("username") + "!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            clearLogin();
                            goToTwitterUsers();
                        } else {
                            FancyToast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                        }
                    });
                } catch (Exception e) {
                    FancyToast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                }
                hideKeypad(v);
            } else {
                FancyToast.makeText(this, "Some fields are missing.", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
            }
        } else if (viewId == R.id.constraintLayoutLogin) {
            hideKeypad(v);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v == edtLoginPassword) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                onClick(btnLoginLogin);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        goToSign();
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

    private void goToSign() {
        Intent nextActivity = new Intent(this, MainActivity.class);
        startActivity(nextActivity);
        this.finish();
    }

    private void clearLogin() {
        edtLoginEmail.setText("");
        edtLoginPassword.setText("");
    }

    private void goToTwitterUsers() {
        Intent nextActivity = new Intent(this, TwitterUsersActivity.class);
        startActivity(nextActivity);
        this.finish();
    }

}
