package com.mponeff.orunner.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mponeff.orunner.R;
import com.mponeff.orunner.utils.Errors;
import com.mponeff.orunner.utils.Network;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.tv_error)
    TextView mTvError;
    @BindView(R.id.et_email)
    EditText mEtEmail;
    @BindView(R.id.et_password)
    TextInputEditText mEtPassword;
    @BindView(R.id.sign_in_button)
    Button mEmailPasswordSignInButton;
    @BindView(R.id.tv_create_account)
    TextView mTvCreateAccount;
    @BindView(R.id.google_sign_in_button)
    Button mGoogleSignInButton;

    private static final int RC_SIGN_IN = 1001;
    private ProgressDialog mProgressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mEmailPasswordSignInButton.setOnClickListener(this);
        mTvCreateAccount.setOnClickListener(this);
        mGoogleSignInButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        /** Hide the soft input keyboard by default */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("166364071777-4rrv39qveaadoniarb8kdksouq7sa852.apps.googleusercontent.com") // TODO Export
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                mTvError.setText("");
            }

            // TODO Auto-generated method stubs
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

            // TODO Auto-generated method stubs
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
        if (i == R.id.google_sign_in_button) {
            signInWithGoogle();
        } else if (i == R.id.sign_in_button) {
            loginWithEmail(
                    mEtEmail.getText().toString().trim(),
                    mEtPassword.getText().toString().trim());
        } else if (i == R.id.tv_create_account) {
            createAccount();
        }
    }

    public void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Signing in...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void startHomeActivity() {
        Intent homeActivity = new Intent(this, HomeActivity.class);
        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeActivity);
        finish();
    }

    private void createAccount() {
        Intent createAccActivity = new Intent(this, CreateAccountActivity.class);
        startActivity(createAccActivity);
    }

    /**
     * Assume that if there is no Internet connection the login failed due to connection problems.
     * Otherwise the credentials provided are wrong.
     */
    private void showLoginError() {
        if (!Network.hasNetworkConnection(this)) {
            mTvError.setText(Errors.E_NO_CONNECTION);
        } else {
            mTvError.setText(Errors.E_INVALID_CREDENTIALS);
        }
    }

    /**************************
     * Google sing-in methods *
     **************************/
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "errorCode[G]: " + e.getStatusCode());
                showLoginError();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgress();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startHomeActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showLoginError();
                        }
                        hideProgress();
                    }
                });
    }

    /*************************
     * Email sign-in methods *
     *************************/
    private void loginWithEmail(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            mTvError.setText(Errors.E_MISSING_FIELDS);
            return;
        }

        showProgress();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgress();
                        if (task.isSuccessful()) {
                            startHomeActivity();
                        } else {
                            showLoginError();
                        }
                    }
                });
    }
}
