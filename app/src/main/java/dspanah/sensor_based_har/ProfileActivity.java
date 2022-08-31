package dspanah.sensor_based_har;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class ProfileActivity extends AppCompatActivity {
      EditText heightedt, weightedt , ageedt ;
      RadioButton malebtn , femalebtn ;
      Button savebtn;
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
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String heightinput = heightedt.getText().toString();
                String weightinput = weightedt.getText().toString();
                String ageinput = ageedt.getText().toString();
                if(TextUtils.isEmpty(heightinput)){
                    heightedt.setError("Enter height");
                }else if (TextUtils.isEmpty(weightinput)){
                    weightedt.setError("Enter Weight");
                }else if (TextUtils.isEmpty(ageinput)){
                    ageedt.setError("Enter age");
                }else{
                    SharedPreferences.Editor editor = getSharedPreferences("Profile", MODE_PRIVATE).edit();
                    editor.putInt("Height", Integer.parseInt(heightinput));
                    editor.putInt("Wight", Integer.parseInt(weightinput));
                    editor.putInt("age",Integer.parseInt(ageinput));
                    if(malebtn.isChecked()){
                        editor.putInt("Gender",0);
                    }else{
                        editor.putInt("Gender",1);
                    }
                    editor.apply();
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

}