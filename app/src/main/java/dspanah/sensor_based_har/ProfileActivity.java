package dspanah.sensor_based_har;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import dspanah.sensor_based_har.data.bioData;

public class ProfileActivity extends AppCompatActivity {
    EditText heightedt, weightedt, ageedt;
    RadioButton malebtn, femalebtn;
    Button savebtn;
    TextView welcome_text;
    FirebaseFirestore db;
    String TAG = "ProfileActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        heightedt = findViewById(R.id.heightedtid);
        weightedt = findViewById(R.id.weightedtid);
        ageedt = findViewById(R.id.ageedtid);
        malebtn = findViewById(R.id.maleradiobtnid);
        femalebtn = findViewById(R.id.femaleradiobtnid);
        savebtn = findViewById(R.id.savebtnid);
        welcome_text = findViewById(R.id.user_name_welcome);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String u_name = user.getDisplayName();
        String welcome_str = "Welcome, " + u_name;
        welcome_text.setText(welcome_str);
        savebtn.setOnClickListener(view -> {
            db = FirebaseFirestore.getInstance();
            String heightinput = heightedt.getText().toString();
            String weightinput = weightedt.getText().toString();
            String ageinput = ageedt.getText().toString();
            if (TextUtils.isEmpty(heightinput)) {
                heightedt.setError("Enter height");
            } else if (TextUtils.isEmpty(weightinput)) {
                weightedt.setError("Enter Weight");
            } else if (TextUtils.isEmpty(ageinput)) {
                ageedt.setError("Enter age");
            } else {
                SharedPreferences.Editor editor = getSharedPreferences("Profile", MODE_PRIVATE).edit();
                editor.putInt("Height", Integer.parseInt(heightinput));
                editor.putInt("Wight", Integer.parseInt(weightinput));
                editor.putInt("age", Integer.parseInt(ageinput));
                if (malebtn.isChecked()) {
                    editor.putInt("Gender", 0);
                } else {
                    editor.putInt("Gender", 1);
                }
                editor.apply();
                CollectionReference docRef = db.collection("userBioData");
                String Gender;
                if (malebtn.isChecked()){Gender="Male";}else{Gender="Female";}
                addDataToFirestore(docRef, user.getUid().trim(), u_name, heightinput, weightinput, ageinput, Gender, Calendar.getInstance().getTime());
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void addDataToFirestore(CollectionReference docRef, String Uid, String Username, String Height, String Weight, String Age, String Gender, Date time_stamp) {

        // creating a collection reference
        // for our Firebase Firetore database.
        // adding our data to our biodata object class.

        bioData biodata = new bioData(Uid, Username, Weight, Height, Age, Gender, time_stamp);
        Log.d("RESUU", biodata.getUid());
        // below method is use to add data to Firebase Firestore.
        docRef.document(Uid)
                .set(biodata)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}