package com.ordermanagementapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ordermanagementapp.constants.Constants;
import com.ordermanagementapp.R;
import com.ordermanagementapp.singleton.Singleton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    TextInputLayout inputEmail, inputPassword;
    TextInputEditText etEmail, etPassword;
    CheckBox cbRemember;
    Button btLogin;
    TextView tvSignup;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    private static final String TAG = "Login";
    boolean rememberMe = false;

    @Override
    protected void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        //check the current user
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else if (Singleton.getPrefBoolean(Constants.REMEMBER_ME,this)){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);

        context             = this;
        inputEmail          = findViewById(R.id.inputEmail);
        inputPassword       = findViewById(R.id.inputPassword);
        etEmail             = findViewById(R.id.etEmail);
        etPassword          = findViewById(R.id.etPassword);
        cbRemember          = findViewById(R.id.cbRemember);
        btLogin             = findViewById(R.id.btLogin);
        tvSignup            = findViewById(R.id.tvSignup);
        progressDialog      = new ProgressDialog(this);

        btLogin.setOnClickListener(this);
        tvSignup.setOnClickListener(this);

        etEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        etPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberMe = isChecked;
            }
        });

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

            }
        };
    }

    /**
     * Validating login
     */
    private void submitForm() {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

//        Intent intent = new Intent(context,MainActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        progressDialog.setMessage("Singing in Please Wait...");
        progressDialog.show();
        //authenticate user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // there was an error
                            Log.e(TAG, "signInWithEmail:success");

                            Singleton.setPrefBoolean(Constants.REMEMBER_ME, rememberMe, context);

                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                        } else {
                            Log.e(TAG, "singInWithEmail:Fail");
                            Toast.makeText(context, getString(R.string.failed), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();


                    }

                });
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError(getString(R.string.err_msg_email));
            requestFocus(etEmail);
            return false;
        } else {
            inputEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (etPassword.getText().toString().trim().isEmpty()) {
            inputPassword.setError(getString(R.string.err_msg_password));
            requestFocus(etPassword);
            return false;
        } else {
            inputPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etEmail:
                    validateEmail();
                    break;
                case R.id.etPassword:
                    validatePassword();
                    break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogin:
                mAuth = FirebaseAuth.getInstance();
                submitForm();
                break;

            case R.id.tvSignup:
                Intent intent = new Intent(context, SignUpActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
        }
    }
}
