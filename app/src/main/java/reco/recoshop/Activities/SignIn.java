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

import reco.recoshop.R;

/**
 * The SignIn activity, where the users sign in to RecoShop.
 */
public class SignIn extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText edEmail;
    private EditText edpPassword;
    private Button bSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        edEmail = findViewById(R.id.singin_etEmail);
        edpPassword = findViewById(R.id.singin_etPassword);
        bSignIn = findViewById(R.id.singin_bSignIn);

        // Called to notify after the email or password has been changed.
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                enableSignIn();
            }
        };
    }

    /**
     * Enable/Unable the sign in button
     */
    private void enableSignIn() {
        boolean enable;

        // Enable the sign in button if all fields aren't empty.
        if (edEmail.getText().length() > 0 &&
                edpPassword.getText().length() > 0) {
            enable = true;
        } else {
            enable = false;
        }

        bSignIn.setEnabled(enable);
    }

    /**
     * Called when the user clicks on the "Sign In" button.
     *
     * @param view
     */
    public void signIn(View view) {

        // Get email and password.
        String email = edEmail.getText().toString();
        String password = edpPassword.getText().toString();


        final ProgressDialog progressDiaSignIn = ProgressDialog.show(this, "",
                "Logging in...", true);

        // Sign in to firebase with email and password.
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // The sign in task completed successfully.

                progressDiaSignIn.dismiss();

                // Start the Categories activity.
                Intent iUserMenu = new Intent(SignIn.this, Categories.class);
                startActivity(iUserMenu);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // The sign in task failed with an exception.

                progressDiaSignIn.dismiss();

                // Show error message.
                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


}