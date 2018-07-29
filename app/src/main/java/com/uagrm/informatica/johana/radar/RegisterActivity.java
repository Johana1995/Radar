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
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "register";
    private EditText editTextName,editTextEmail,editTextPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.email);
        editTextName=findViewById(R.id.display_name);
        editTextPass=findViewById(R.id.password);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        printLog("onback press");
        gotoLoginActivity();
    }

    private void gotoLoginActivity() {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    private void printLog(String s) {
        Log.i(TAG,s);
    }

    public void registrarUsuario(final View view) {
        final String name=editTextName.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPass.getText().toString().trim();

        view.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                   // .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful())
                                                showToast("Error al actualizar Display name");
                                        }
                                    });

                            gotoMapActivity();
                        } else {
                            showToast("Error al crear cuenta.");
                            view.setEnabled(true);
                        }
                    }

                });
    }

    private void gotoMapActivity() {
        startActivity(new Intent(this,MapsActivity.class));
        finish();
    }

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
