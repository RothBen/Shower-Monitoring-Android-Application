package roth.ben.ShowerMonitoringApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ElectricityCalculator extends AppCompatActivity {

    private EditText coldWaterValue;
    private EditText timeValue;
    private EditText flowRateValue;
    private EditText efficiencyValue;
    private EditText powerCostValue;
    private EditText tempValue;
    private TextView monthlyPowerValue;
    private TextView monthlyCostValue;
    private TextView powerValue;
    private TextView totalCostValue;
    private Button calcButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.electricity_calculator);

        Bundle extras = getIntent().getExtras();
        double avgFlow = extras.getDouble("avgFlow");
        double avgTime = extras.getDouble("avgTime");
        double avgTemp = extras.getDouble("avgTemp");

        coldWaterValue = findViewById(R.id.ColdWaterValue);
        timeValue = findViewById(R.id.TimeValue);
        flowRateValue = findViewById(R.id.FlowRateValue);
        efficiencyValue = findViewById(R.id.EfficiencyValue);
        powerCostValue = findViewById(R.id.PowerCostValue);
        tempValue = findViewById(R.id.TempValue);
        powerValue = findViewById(R.id.PowerValue);
        totalCostValue = findViewById(R.id.TotalCostValue);
        monthlyPowerValue = findViewById(R.id.MonthlyPowerValue);
        monthlyCostValue = findViewById(R.id.MonthlyCostValue);
        calcButton = findViewById(R.id.CalcButton);

        double defaultColdWaterTemp = 55;
        double defaultEfficiency = .92;
        double defaultPowerCost = .12;

        coldWaterValue.setText(Double.toString(defaultColdWaterTemp));
        timeValue.setText(Double.toString(avgTime));
        flowRateValue.setText(Double.toString(avgFlow));
        efficiencyValue.setText(Double.toString(defaultEfficiency));
        powerCostValue.setText(Double.toString(defaultPowerCost));
        tempValue.setText(Double.toString(avgTemp));

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat dollarFormat = new DecimalFormat("#.00");
                double flowRate = Double.parseDouble(flowRateValue.getText().toString());
                double time = Double.parseDouble(timeValue.getText().toString());
                double temp = Double.parseDouble(tempValue.getText().toString());
                double coldTemp = Double.parseDouble(coldWaterValue.getText().toString());
                double efficiency = Double.parseDouble(efficiencyValue.getText().toString());
                double powerCost = Double.parseDouble(powerCostValue.getText().toString());
                double C = 4.186; //Specific heat of water (Joules/g*degreeC)
                double V = flowRate*time*3.785411784; //Volume (Liters)
                double dT = (temp-coldTemp)/1.8; //Difference of Temperature (Celsius)
                double E = C*V*dT/efficiency; //Energy (kiloJoules)
                double monthlyPower = Math.round(100.0*E*30.44/3600)/100.0; //Yearly Power (kiloWatt Hours)
                double monthlyCost = Double.parseDouble(dollarFormat.format(monthlyPower*powerCost)); //Cost (dollars)
                double yearlyPower = Math.round(100.0*E*365.25/3600)/100.0; //Yearly Power (kiloWatt Hours)
                double cost = Double.parseDouble(dollarFormat.format(yearlyPower*powerCost)); //Cost (dollars)
                monthlyPowerValue.setText(Double.toString(monthlyPower) + " kWh");
                monthlyCostValue.setText("$ " + Double.toString(monthlyCost));
                powerValue.setText(Double.toString(yearlyPower) + " kWh");
                totalCostValue.setText("$ " + Double.toString(cost));
            }
        });
    }
}
