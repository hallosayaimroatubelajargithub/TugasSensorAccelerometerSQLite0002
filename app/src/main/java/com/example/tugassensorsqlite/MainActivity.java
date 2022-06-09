package com.example.tugassensorsqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView txX, txY, txZ;
    Sensor mySensor;
    SensorManager SM;
    String SQLiteQuery;
    Button View;
    float x, y, z;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txX = (TextView) findViewById(R.id.txX);
        txY = (TextView) findViewById(R.id.txY);
        txZ = (TextView) findViewById(R.id.txZ);

        SM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mySensor == null) {
            if (SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                createDatabase();
                int MINUTES = 1; // The delay in minutes
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        addData(); // If the function you wanted was static
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }, 0, 1000 * 60 * MINUTES);
                mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            } else {
                txY.setText("SmartPhone Tidak Ditemukan");
            }
        }

        View = findViewById(R.id.view);
        View.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onResume() {
        super.onResume();
        SM.registerListener(accelListener, mySensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        SM.unregisterListener(accelListener);
    }

    SensorEventListener accelListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        @SuppressLint("SetTextI18n")
        public void onSensorChanged(SensorEvent event) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 10 seconds
                    txX.setText("X: " + (int) x);
                    txY.setText("Y: " + (int) y);
                    txZ.setText("Z: " + (int) z);
                    handler.postDelayed(this, 2000);
                }
            }, 2000);  //the time is in miliseconds
        }
    };

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }

    private void createDatabase() {
        sqLiteDatabase = openOrCreateDatabase("Nama_Database_Baru", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Nama_Tabel (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title VARCHAR, x VARCHAR, y VARCHAR, z VARCHAR);");
    }

    private void addData() {
        SQLiteQuery = "INSERT INTO Nama_Tabel (title,x,y,z) VALUES ('" + getCurrentDate() + "', '" + x + "', '" + y + "', '" + z + "');";
        sqLiteDatabase.execSQL(SQLiteQuery);
    }
}
