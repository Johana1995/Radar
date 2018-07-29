package com.uagrm.informatica.johana.radar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "login";
    private EditText editTextEmail;
    private EditText editTextPass;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail=findViewById(R.id.email);
        editTextPass=findViewById(R.id.password);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            printLog("Usuario identificado "+currentUser.getDisplayName()+ " "+currentUser.getEmail());
            gotoMapActivity();
        }
    }

    private void gotoMapActivity() {
        startActivity(new Intent(this,MapsActivity.class));
        finish();
    }

    public void identificarse(View view) {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPass.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            printLog(user.getDisplayName()+ "login OK.");
                            gotoMapActivity();
                        } else {
                            showToast("Email o password incorrectos.");
                            limpiarCampos();
                        }
                    }
                });
    }

    private void limpiarCampos() {
        editTextEmail.setText("");
        editTextPass.setText("");
    }

    private void printLog(String s) {
        Log.i(TAG,s);
    }

    public void gotoRegisterActivity(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
        finish();
    }
    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

}
