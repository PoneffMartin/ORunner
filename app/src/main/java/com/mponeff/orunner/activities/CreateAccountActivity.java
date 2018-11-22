package com.mponeff.orunner.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mponeff.orunner.R;
import com.mponeff.orunner.utils.DataValidation;
import com.mponeff.orunner.utils.Errors;
import com.mponeff.orunner.utils.Network;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    @BindView(R.id.et_email)
    EditText mEtEmail;
    @BindView(R.id.et_password)
    TextInputEditText mEtPassword;
    @BindView(R.id.tv_error)
    TextView mTvError;
    @BindView(R.id.sign_up_button)
    Button mBtnSignUp;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mBtnSignUp.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        /* Hide the soft input keyboard by default */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                mTvError.setText("");
            }

            /** TODO Auto-generated method stubs */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                mTvError.setText("");
            }

            /** TODO Auto-generated method stubs */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onStop() {
        hideProgress();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_up_button) {
            createAccount(
                    mEtEmail.getText().toString().trim(),
                    mEtPassword.getText().toString().trim());
        }
    }

    private void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Creating account...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void startLoginActivity() {
        Intent loginActivity = new Intent(CreateAccountActivity.this, LoginActivity.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
    }

    private void showCreateAccountError() {
        if (!Network.hasNetworkConnection(this)) {
            mTvError.setText(Errors.E_NO_CONNECTION);
        } else {
            Toast.makeText(CreateAccountActivity.this, "Sign up failed",
                    Toast.LENGTH_SHORT).show(); // TODO Add another error
        }
    }

    private void createAccount(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            mTvError.setText(Errors.E_MISSING_FIELDS);
        } else if (!DataValidation.isValidEmail(email)) {
            mTvError.setText(Errors.E_EMAIL_INVALID);
        } else if (!DataValidation.isValidPassword(password)) {
            mTvError.setText(Errors.E_PASSWORD_INVALID);
        } else {
            firebaseCreateAccount(email, password);
        }
    }

    private void firebaseCreateAccount(String email, String password) {
        showProgress();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgress();
                        if (task.isSuccessful()) {
                            startLoginActivity();
                        } else {
                            showCreateAccountError();
                        }
                    }
                });
    }
}
