package dspanah.sensor_based_har;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final int N_SAMPLES = 100;
    private static int prevIdx = -1;

    private static List<Float> ax;
    private static List<Float> ay;
    private static List<Float> az;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;

    private static List<Float> ma;
    private static List<Float> ml;
    private static List<Float> mg;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;


    private TextView bikingTextView;
    private TextView downstairsTextView;
    private TextView joggingTextView;
    private TextView sittingTextView;
    private TextView standingTextView;
    private TextView upstairsTextView;
    private TextView walkingTextView;

    private TableRow bikingTableRow;
    private TableRow downstairsTableRow;
    private TableRow joggingTableRow;
    private TableRow sittingTableRow;
    private TableRow standingTableRow;
    private TableRow upstairsTableRow;
    private TableRow walkingTableRow;

    private TextToSpeech textToSpeech;
    private float[] results;
    private HARClassifier classifier;

    private final String[] labels = {"Biking", "Downstairs", "Jogging", "Sitting", "Standing", "Upstairs", "Walking"};
    private long[]  arrytimer ={0,0,0,0,0,0,0};
    long starttime , endtime ;
    Boolean onoff = false;
    int  lastidx = -1 ;
    ImageButton startbtn;
    private int bg_color = R.color.colorTransparent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();
        ma = new ArrayList<>(); ml = new ArrayList<>(); mg = new ArrayList<>();

        bikingTextView = findViewById(R.id.biking_prob);
        downstairsTextView = findViewById(R.id.downstairs_prob);
        joggingTextView = findViewById(R.id.jogging_prob);
        sittingTextView = findViewById(R.id.sitting_prob);
        standingTextView = findViewById(R.id.standing_prob);
        upstairsTextView = findViewById(R.id.upstairs_prob);
        walkingTextView = findViewById(R.id.walking_prob);

        bikingTableRow = findViewById(R.id.biking_row);
        downstairsTableRow = findViewById(R.id.downstairs_row);
        joggingTableRow = findViewById(R.id.jogging_row);
        sittingTableRow = findViewById(R.id.sitting_row);
        standingTableRow = findViewById(R.id.standing_row);
        upstairsTableRow = findViewById(R.id.upstairs_row);
        walkingTableRow = findViewById(R.id.walking_row);

        startbtn = findViewById(R.id.starttime_id);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mLinearAcceleration , SensorManager.SENSOR_DELAY_FASTEST);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_FASTEST);

        classifier = new HARClassifier(getApplicationContext());

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
        startbtn.setOnClickListener(view -> {
            if(onoff)
            {
                startbtn.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.start));
                endtime = System.currentTimeMillis();
                long diff = (endtime-starttime)/1000;
                arrytimer[lastidx] = arrytimer[lastidx]+diff;
                onoff = false;
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("arrayoftime", arrytimer);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }else{
                bg_color = R.color.colorBlue;
                startbtn.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.stop));
                onoff = true;
                starttime = System.currentTimeMillis();
              //  Toast.makeText(MainActivity.this, ""+starttime/1000, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (results == null || results.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

                if(max > 0.50 && idx != prevIdx) {
                    textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null,
                            Integer.toString(new Random().nextInt()));
                    prevIdx = idx;
                }
            }
        }, 1000, 3000);
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        activityPrediction();

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);

        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lx.add(event.values[0]);
            ly.add(event.values[1]);
            lz.add(event.values[2]);

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {

        List<Float> data = new ArrayList<>();

        if (ax.size() >= N_SAMPLES && ay.size() >= N_SAMPLES && az.size() >= N_SAMPLES
                && lx.size() >= N_SAMPLES && ly.size() >= N_SAMPLES && lz.size() >= N_SAMPLES
                && gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES
        ) {
            double maValue; double mgValue; double mlValue;

            for( int i = 0; i < N_SAMPLES ; i++ ) {
                maValue = Math.sqrt(Math.pow(ax.get(i), 2) + Math.pow(ay.get(i), 2) + Math.pow(az.get(i), 2));
                mlValue = Math.sqrt(Math.pow(lx.get(i), 2) + Math.pow(ly.get(i), 2) + Math.pow(lz.get(i), 2));
                mgValue = Math.sqrt(Math.pow(gx.get(i), 2) + Math.pow(gy.get(i), 2) + Math.pow(gz.get(i), 2));

                ma.add((float)maValue);
                ml.add((float)mlValue);
                mg.add((float)mgValue);
            }

            data.addAll(ax.subList(0, N_SAMPLES));
            data.addAll(ay.subList(0, N_SAMPLES));
            data.addAll(az.subList(0, N_SAMPLES));

            data.addAll(lx.subList(0, N_SAMPLES));
            data.addAll(ly.subList(0, N_SAMPLES));
            data.addAll(lz.subList(0, N_SAMPLES));

            data.addAll(gx.subList(0, N_SAMPLES));
            data.addAll(gy.subList(0, N_SAMPLES));
            data.addAll(gz.subList(0, N_SAMPLES));

            data.addAll(ma.subList(0, N_SAMPLES));
            data.addAll(ml.subList(0, N_SAMPLES));
            data.addAll(mg.subList(0, N_SAMPLES));

            results = classifier.predictProbabilities(toFloatArray(data));

            float max = -1;
            int idx = -1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    idx = i;
                    max = results[i];
                }
            }

            setProbabilities();
            setRowsColor(idx, bg_color);

            ax.clear(); ay.clear(); az.clear();
            lx.clear(); ly.clear(); lz.clear();
            gx.clear(); gy.clear(); gz.clear();
            ma.clear(); ml.clear(); mg.clear();
        }
    }

    private void setProbabilities() {
        bikingTextView.setText(Float.toString(round(results[0], 2)));
        downstairsTextView.setText(Float.toString(round(results[1], 2)));
        joggingTextView.setText(Float.toString(round(results[2], 2)));
        sittingTextView.setText(Float.toString(round(results[3], 2)));
        standingTextView.setText(Float.toString(round(results[4], 2)));
        upstairsTextView.setText(Float.toString(round(results[5], 2)));
        walkingTextView.setText(Float.toString(round(results[6], 2)));
    }

    private void setRowsColor(int idx, int bg_color) {

        bikingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        downstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        joggingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        upstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));

        if(idx == 0)
        {
            if(idx!=lastidx){
                arraytimesetup();
               lastidx = idx;
            }

            bikingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), bg_color, null));

        }
            //bikingTextView.setBackgroundColor(Color.parseColor("#33B5E5"));
        else if (idx == 1){
            if(idx!=lastidx){
                arraytimesetup();
                lastidx = idx;
            }
            downstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), bg_color, null));

        }
        else if (idx == 2){
            if(idx!=lastidx){
                arraytimesetup();
                lastidx = idx;
            }
            joggingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), bg_color, null));
        }
        else if (idx == 3){
            if(idx!=lastidx){
                arraytimesetup();
                lastidx = idx;
            }
            sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), bg_color, null));

        }
        else if (idx == 4)
        {
            if(idx!=lastidx){
                arraytimesetup();
                lastidx = idx;
            }
            standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), bg_color, null));

        }
        else if (idx == 5)
        {
            if(idx!=lastidx){
                arraytimesetup();
                lastidx = idx;
            }
            upstairsTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), bg_color, null));

        }
        else if (idx == 6)
        {
            if(idx!=lastidx){
                arraytimesetup();
                lastidx = idx;
            }
            walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), bg_color, null));

        }
    }

    private void arraytimesetup()
    {
        endtime = System.currentTimeMillis();
        long diff = (endtime-starttime)/1000;
        starttime =endtime;
        if(lastidx>=0)
        {
            arrytimer[lastidx] = arrytimer[lastidx]+diff;
        }
    }


    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Log.d("STATTT", String.valueOf(user));
            SharedPreferences prefs = getSharedPreferences("Profile", MODE_PRIVATE);
            int idName = prefs.getInt("Height", 0); //0 is the default value.
            if(idName==0) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                //     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            Log.d("STATTT", "not_signed_in");
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        }
}
