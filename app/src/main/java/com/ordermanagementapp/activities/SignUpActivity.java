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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ordermanagementapp.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    TextInputLayout inputName, inputEmail, inputPassword;
    TextInputEditText etName, etEmail, etPassword;
    CheckBox cbRemember;
    Button btSignup;
    TextView tvLogin;
    private FirebaseAuth mAuth;
    private static final String TAG = "Signup";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        context             = this;
        inputName           = findViewById(R.id.inputName);
        inputEmail          = findViewById(R.id.inputEmail);
        inputPassword       = findViewById(R.id.inputPassword);
        etName              = findViewById(R.id.etName);
        etEmail             = findViewById(R.id.etEmail);
        etPassword          = findViewById(R.id.etPassword);
        cbRemember          = findViewById(R.id.cbRemember);
        btSignup            = findViewById(R.id.btSignup);
        tvLogin             = findViewById(R.id.tvLogin);
        progressDialog      = new ProgressDialog(this);

        btSignup.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

        etName.addTextChangedListener(new MyTextWatcher(inputName));
        etEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        etPassword.addTextChangedListener(new MyTextWatcher(inputPassword));



    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

//        Intent intent = new Intent(context,MainActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();

                    }


                });
    }

    private boolean validateName() {
        if (etName.getText().toString().trim().isEmpty()) {
            inputName.setError(getString(R.string.err_msg_name));
            requestFocus(etName);
            return false;
        } else {
            inputName.setErrorEnabled(false);
        }

        return true;
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
                case R.id.etName:
                    validateName();
                    break;
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
        switch (v.getId()){
            case R.id.btSignup:
                submitForm();
                break;

            case R.id.tvLogin:
                Intent intent = new Intent(context,LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
        }
    }
}
