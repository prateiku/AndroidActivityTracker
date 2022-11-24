
package dspanah.sensor_based_har;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    Button btn2_signup, signin_btn;
    EditText user_name, pass_word, confirm_pass_word, email;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email=findViewById(R.id.et_email);
        user_name=findViewById(R.id.et_username);
        pass_word=findViewById(R.id.et_password);
        confirm_pass_word=findViewById(R.id.et_confirm_password);
        btn2_signup=findViewById(R.id.button_signin);
        signin_btn=findViewById(R.id.button2_signin);
        mAuth=FirebaseAuth.getInstance();
        btn2_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_str = email.getText().toString().trim();
                String username= user_name.getText().toString().trim();
                String password= pass_word.getText().toString().trim();
                String confirm_password= confirm_pass_word.getText().toString().trim();
                if(email_str.isEmpty())
                {
                    email.setError("Email is empty");
                    email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email_str).matches())
                {
                    user_name.setError("Enter the valid email address");
                    user_name.requestFocus();
                    return;
                }
                if(password.isEmpty())
                {
                    pass_word.setError("Enter the password");
                    pass_word.requestFocus();
                    return;
                }
                if(password.length()<6)
                {
                    pass_word.setError("Length of the password should be more than 6");
                    pass_word.requestFocus();
                    return;
                }
                if(confirm_password.isEmpty()){
                    confirm_pass_word.setError("Confirm the password");
                    confirm_pass_word.requestFocus();
                    return;
                }
                if(!confirm_password.equals(password)){
                    confirm_pass_word.setError("Password not matching!");
                    confirm_pass_word.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email_str,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();

                        assert user != null;
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("USER_NAME", "User name set.");
                                        }
                                    }
                                });
                        Toast.makeText(SignupActivity.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(SignupActivity.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        signin_btn.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class )));
    }

}