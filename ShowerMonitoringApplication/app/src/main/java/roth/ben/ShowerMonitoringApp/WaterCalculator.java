package roth.ben.ShowerMonitoringApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class WaterCalculator extends AppCompatActivity {

    private EditText calcTimeValue;
    private EditText calcFlowValue;
    private EditText calcCostValue;
    private TextView monthlyWaterCost;
    private TextView yearlyWaterCost;
    private TextView monthlyWaterUsage;
    private TextView yearlyWaterUsage;
    private Button calcButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_calculator);

        Bundle extras = getIntent().getExtras();
        double avgFlow = extras.getDouble("avgFlow");
        double avgTime = extras.getDouble("avgTime");

        double defaultCost = .00183;

        calcTimeValue = findViewById(R.id.CalcTimeValue);
        calcFlowValue = findViewById(R.id.CalcFlowValue);
        calcCostValue = findViewById(R.id.CalcCostValue);
        monthlyWaterCost = findViewById(R.id.MonthlyWaterCostValue);
        yearlyWaterCost = findViewById(R.id.YearlyWaterCostValue);
        monthlyWaterUsage = findViewById(R.id.MonthlyWaterUsageValue);
        yearlyWaterUsage = findViewById(R.id.YearlyWaterUsageValue);
        calcButton = findViewById(R.id.CalcButton);

        calcFlowValue.setText(Double.toString(avgFlow));
        calcTimeValue.setText(Double.toString(avgTime));
        calcCostValue.setText(Double.toString(defaultCost));

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double flowRate = Double.parseDouble(calcFlowValue.getText().toString());
                double time = Double.parseDouble(calcTimeValue.getText().toString());
                double cost = Double.parseDouble(calcCostValue.getText().toString());
                DecimalFormat dollarFormat = new DecimalFormat("#.00");
                double monthlyUsage = flowRate*time*30.44;
                double yearlyUsage = monthlyUsage*12;
                double monthlyCost = monthlyUsage*cost;
                double yearlyCost = monthlyCost*12;
                monthlyWaterUsage.setText(Math.round(10.0*monthlyUsage)/10.0 + " Gallons");
                yearlyWaterUsage.setText(Math.round(10.0*yearlyUsage)/10.0 + " Gallons");
                monthlyWaterCost.setText("$ " + dollarFormat.format(monthlyCost));
                yearlyWaterCost.setText("$ " + dollarFormat.format(yearlyCost));
            }
        });
    }
}
