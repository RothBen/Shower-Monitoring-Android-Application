package roth.ben.ShowerMonitoringApp;

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

public class WaterChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_chart);

        TextView avgWaterText = findViewById(R.id.waterUnits);

        LineChart chart;

        Bundle extras = getIntent().getExtras();
        double avgGallons = extras.getDouble("avgGallons");

        String avgWaterString = avgGallons + " Gallons/day";
        avgWaterText.setText(avgWaterString);

        chart = findViewById(R.id.linechart);

        final MyDBHandler db = new MyDBHandler(this, null, null, 1);

        double[] galArray = db.getGallonData();

        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        float dataPoint;

        for(int count = 13; count >= 0; count--) {
            xValues.add(Integer.toString(13-count));
            if(galArray.length > count) {
                dataPoint = (float) galArray[galArray.length-1-count];
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

        LineDataSet data = new LineDataSet(yValues, "Water Usage (Gallons)");
        data.setDrawCircles(false);
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

        LimitLine goal = new LimitLine((float) avgGallons, "Average");

        chart.setData(new LineData(data));
        chart.setVisibleXRangeMaximum(14f);
        Description desc = new Description();
        desc.setText("Water Flow Chart");
        chart.setDescription(desc);
        chart.getAxisLeft().addLimitLine(goal);
    }
}
