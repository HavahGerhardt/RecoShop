package reco.recoshop.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import reco.recoshop.R;

/**
 * The Register activity, where the users register to RecoShop.
 */
public class Register extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private Button bRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.register_etEmail);
        etPassword = findViewById(R.id.register_etPassword);
        etName = findViewById(R.id.register_etName);
        bRegister = findViewById(R.id.register_bRegister);

        // Called to notify after the email, name or password has been changed.
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                enableRegister();
            }
        };

        etEmail.addTextChangedListener(tw);
        etName.addTextChangedListener(tw);
        etPassword.addTextChangedListener(tw);
    }

    /**
     * Enable/Unable the register button
     */
    private void enableRegister() {

        boolean enable;

        // Enable the register button if all fields aren't empty.
        if (etEmail.getText().length() > 0 &&
                etName.getText().length() > 0 &&
                etPassword.getText().length() > 0) {
            enable = true;
        } else {
            enable = false;
        }

        bRegister.setEnabled(enable);
    }

    /**
     * Called when the user clicks on the "Register" button.
     * @param view
     */
    public void register(View view) {

        // Get email, name and password.
        String email = etEmail.getText().toString();
        final String name = etName.getText().toString();
        String password = etPassword.getText().toString();

        final ProgressDialog progressBarRegister = ProgressDialog.show(this, "",
                "Registering...", true);

        // Register to firebase with email and password.
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>(){

            @Override
            public void onSuccess(AuthResult authResult) {
                // The registration task has succeed.

                // Set user's name (display name).
                final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                // Upload user's name to firebase (update user's profile).
                firebaseAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // The user's profile updating task has been completed.
                        if(task.isSuccessful()){

                        progressBarRegister.dismiss();

                        firebaseAuth.signOut();

                        // Return to Main activity.
                        finish();
                        }

                        // The user's profile updating task failed with an exception.
                        else
                        {

                        progressBarRegister.dismiss();

                        // Show error message.
                        Toast.makeText(getApplicationContext(), "Error: " +  task.getException().toString(), Toast.LENGTH_LONG).show();

                        // Clear all fields.
                        etEmail.setText("");
                        etName.setText("");
                        etPassword.setText("");
                        bRegister.setEnabled(false);
                    }}});
            }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // The registration task failed with an exception.

                        progressBarRegister.dismiss();

                        // Show error message.
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();                    }
                });
    }
}
