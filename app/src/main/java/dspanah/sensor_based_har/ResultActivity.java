package dspanah.sensor_based_har;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    private long[]  arrytimer ;
    private final String [] tag = {"Running","Downstrairs","Jogging","Sitting","Standing","Upstairs","Walking"};
    long totaltime =0;
    PieChart pieChart;
    Button getresultbtn;
    EditText inputcaledt;
    double BMR,totalcalburn;
      TextView resulttxt , resulttextview2;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        pieChart = findViewById(R.id.peichartview_id);
        getresultbtn = findViewById(R.id.getcalorydeficitbrn_id);
        inputcaledt = findViewById(R.id.inputcaloryedt_id);
        resulttxt = findViewById(R.id.textviewlastvalueid);
        resulttextview2 = findViewById(R.id.textview2lastvalueid);


        arrytimer = Objects.requireNonNull(getIntent().
                getExtras()).getLongArray("arrayoftime");
        for(int i =0;i<7;i++){
            totaltime =totaltime+arrytimer[i];
        }
        Map<String, Object> activity_map = new HashMap<String, Object>();
        for(int i =0;i<tag.length;i++){
            activity_map.put(tag[i], arrytimer[i]);
        }
        activity_map.put("uid", user.getUid());
        Date date;
        date = Calendar.getInstance().getTime();
        Timestamp datetime = new Timestamp(date);
        activity_map.put("date", datetime);
        CollectionReference docRefdocRef = db.collection("userActivityData");
        Task future = db.collection("userActivityData").add(activity_map)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                            Log.d("TAG_ZZ", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG_ZZ", "Error writing document", e);
                    }
                });
        setuppiechart();
        ArrayList<PieEntry>entries = new ArrayList<>();
        for(int j =0;j<7;j++){
            entries.add(new PieEntry(arrytimer[j],tag[j]));
        }


        ArrayList<Integer>colors = new ArrayList<>();
            for(int color: ColorTemplate.MATERIAL_COLORS){
                colors.add(color);
            }
        for(int color: ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);

        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        calcualtebmi();
        calorieburncalculate();
        getresultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputcals = inputcaledt.getText().toString();

                if (TextUtils.isEmpty(inputcals)){
                    inputcaledt.setError("Enter Calorie");
                }else{

                    double deficit = totalcalburn - Double.parseDouble(inputcals);
                    resulttxt.setText(String.valueOf(round(deficit, 2)));
                    resulttextview2.setText(String.valueOf(round(totalcalburn,2)));
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } catch(Exception ignored) {
                    }
                }
            }
        });
    }

    private void calorieburncalculate()
    {
         totalcalburn =((arrytimer[0]*288 + arrytimer[1]*275 + arrytimer[2]*216 +arrytimer[3]*80
                +arrytimer[4]*88+arrytimer[5]*470+arrytimer[6]*133)/3600)+BMR;
       // Toast.makeText(this, "totalcalburn"+totalcalburn, Toast.LENGTH_SHORT).show();
    }

    private void calcualtebmi()
    {
        SharedPreferences prefs = getSharedPreferences("Profile", MODE_PRIVATE);
        int idName = prefs.getInt("Gender", 0);
        int idheght = prefs.getInt("Height",165);
        int idweight = prefs.getInt("Wight",52);
        int idage = prefs.getInt("age",22);
        if(idName==0){
            BMR = 88.362 +(13.397*idweight)+(4.799*idheght)-(5.677*idage);

        }else{
            BMR = 447.593 +(9.247*idweight)+(3.098*idheght)-(4.330*idage);
        }
        Toast.makeText(this, "BMR Value is"+BMR+"Calories/day", Toast.LENGTH_LONG).show();
    }

    private void setuppiechart(){
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setCenterText("Spending Activities");
        pieChart.setCenterTextSize(10);
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences prefs = getSharedPreferences("Profile", MODE_PRIVATE);
                prefs.edit().clear().apply();
                return true;
            case R.id.about:
                // do your code
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}