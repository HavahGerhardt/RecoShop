package reco.recoshop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import reco.recoshop.R;

/**
 * The Main activity
 */
public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user clicks on the "Sign in" button. It opens the SignIn activity.
     * @param view
     */
    public void signIn(View view)
    {
        Intent inSignIn = new Intent(this,SignIn.class);
        startActivity(inSignIn);
    }

    /**
     * Called when the user clicks on the "Register" button. It opens the Register activity.
     * @param view
     */
    public void register(View view)
    {
        Intent inRegister = new Intent(this,Register.class);
        startActivity(inRegister);
    }



}
