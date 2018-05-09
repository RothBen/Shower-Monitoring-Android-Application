package roth.ben.ShowerMonitoringApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tempValue;
    private TextView waterValue;
    private TextView timeValue;
    private TextView flowValue;
    private Button waterButton;
    private Button tempButton;
    private Button calcButton;
    private Button powerCalcButton;
    private Button btButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        tempValue = findViewById(R.id.TempValue);
        waterValue = findViewById(R.id.WaterValue);
        timeValue = findViewById(R.id.TimeValue);
        flowValue = findViewById(R.id.FlowValue);

        waterButton = findViewById(R.id.FlowButton);
        tempButton = findViewById(R.id.HeatButton);
        calcButton = findViewById(R.id.CalcButton);
        powerCalcButton = findViewById(R.id.PowerCalcButton);
        btButton = findViewById(R.id.BTButton);

        final MyDBHandler db = new MyDBHandler(this, null, null, 1);
        DecimalFormat df = new DecimalFormat("#.#");
        int showerCount = db.getShowerCount();

        //Temperature
        double totalTemp = db.getTotalTemp();
        final double avgTemp = Double.parseDouble(df.format(totalTemp/showerCount));

        //Gallons
        double totalGallons = db.getTotalGallons();
        final double avgGallons = Double.parseDouble(df.format(totalGallons/showerCount));

        //Time
        int totalTime = db.getTotalTime();
        int avgShowerTime = 0;
        if(showerCount > 0)
            avgShowerTime = totalTime/showerCount;
        final double avgTimeMinutes = Double.parseDouble(df.format(((double) avgShowerTime/60)));
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        String sAvgShowerTime = formatter.format(avgShowerTime*1000);

        //Flow
        final double avgFlow = Double.parseDouble(df.format(totalGallons/(totalTime/60)));

        tempValue.setText(Double.toString(avgTemp) + " degrees (F)");
        waterValue.setText(Double.toString(avgGallons) + " gallons");
        timeValue.setText(sAvgShowerTime);
        flowValue.setText(Double.toString(avgFlow) + " gal/min");

        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaterChart.class)
                        .putExtra("avgGallons", avgGallons));
            }
        });

        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TemperatureChart.class)
                        .putExtra("avgTemp", avgTemp));
            }
        });

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaterCalculator.class)
                        .putExtra("avgFlow", avgFlow)
                        .putExtra("avgTime", avgTimeMinutes));
            }
        });

        powerCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ElectricityCalculator.class)
                        .putExtra("avgFlow", avgFlow)
                        .putExtra("avgTime", avgTimeMinutes)
                        .putExtra("avgTemp", avgTemp));
            }
        });

        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BluetoothMainActivity.class));
            }
        });
    }
}
