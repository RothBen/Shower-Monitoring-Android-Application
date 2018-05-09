package roth.ben.ShowerMonitoringApp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

public class TemperatureChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperature_chart);

        LineChart chart;
        chart = findViewById(R.id.linechart);

        TextView averageText = findViewById(R.id.tempGoalUnits);

        Bundle extras = getIntent().getExtras();
        double avgTemp = extras.getDouble("avgTemp");

        String avgTempString = avgTemp + " Degrees (F)";
        averageText.setText(avgTempString);

        final MyDBHandler db = new MyDBHandler(this, null, null, 1);

        double[] tempArray = db.getTempData();

        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        float dataPoint;

        for(int count = 13; count >= 0; count--) {
            xValues.add(Integer.toString(13-count));
            if(tempArray.length > count) {
                dataPoint = (float) tempArray[tempArray.length-1-count];
            }
            else {
                dataPoint = 0;
            }
            yValues.add(new Entry(13-count, dataPoint));
        }

        String[] xValueArray = new String[xValues.size()];
        for(int i = 0; i < xValueArray.length; i++) {
            xValueArray[i] = xValues.get(i);
        }
        LineDataSet data = new LineDataSet(yValues, "Average Temperature (degrees Fahrenheit)");
        data.setDrawCircles(false);
        data.setColor(Color.rgb(232, 125, 92));
        data.setLineWidth(5);
        data.setValueTextSize(14);

        IValueFormatter format = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int intValue = (int) value;
                return String.valueOf(intValue);
            }
        };
        data.setValueFormatter(format);
        LimitLine goal = new LimitLine((float) avgTemp, "Average");
        chart.setData(new LineData(data));
        chart.setVisibleXRangeMaximum(14f);
        Description desc = new Description();
        desc.setText("Temperature Chart");
        chart.setDescription(desc);
        chart.getAxisLeft().addLimitLine(goal);
    }
}
