package roth.ben.ShowerMonitoringApp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class BluetoothMainActivity extends Activity
{
    TextView myLabel;
    TextView myTime;
    TextView myGallons;
    TextView myTemp;
    double tempAvg;
    long time;
    double gallons;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        Button openButton = findViewById(R.id.open);
        Button closeButton = findViewById(R.id.close);
        myLabel = findViewById(R.id.label);
        myTime = findViewById(R.id.timeValue);
        myGallons = findViewById(R.id.gallonsValue);
        myTemp = findViewById(R.id.tempValue);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    findBT();
                    openBT();
                }
                catch (IOException ex) {
                    Log.e("Error", "IOException: " + ex);
                }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    closeBT();
                }
                catch (IOException ex) {
                    Log.e("Error", "IOException: " + ex);
                }
            }
        });
    }

    void saveData(double time, double gallons, double temp) {
        final MyDBHandler db = new MyDBHandler(this, null, null, 1);
        db.addRow(time, gallons, temp);
    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            String noAdapterText = "No bluetooth adapter available";
            myLabel.setText(noAdapterText);
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("RNBT-CFA2"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        String deviceFoundText = "Bluetooth Device Found";
        myLabel.setText(deviceFoundText);
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        beginListenForData();
        String bluetoothOpenedText = "Bluetooth Opened";
        myLabel.setText(bluetoothOpenedText);
    }

    void beginListenForData()
    {
        String bluetoothConnectedText = "Device connected";
        myLabel.setText(bluetoothConnectedText);
        final Handler handler = new Handler();
        final byte delimiter = 120; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                final long initialTime = System.currentTimeMillis();
                int delimiterFlag = 0;
                String[] stringParts;
                StringBuilder builder = new StringBuilder();
                StringBuilder text = new StringBuilder();
                String sAdcValue;
                String sFlowValue;
                final double cycleTime = 6.25 * Math.pow(10,-8);
                long cycleSum = 0;
                long runCount = 0;
                double avgTime;
                double avgFreq;
                double avgFlowRate;
                double liters;
                double voltage;
                double resistance;
                double tempKalvin;
                double tempSum = 0;
                DecimalFormat df = new DecimalFormat("#.#");

                while (!Thread.currentThread().isInterrupted() && !stopWorker) try {

                    byte[] buffer = new byte[1024];
                    int bytes = mmInputStream.read(buffer);
                    text.setLength(0);

                    for (int j = 0; j < bytes; j++) {
                        if(buffer[j] != 0) {
                            text.append((char) buffer[j]);
                            if(buffer[j] == delimiter) {
                                delimiterFlag = 1;
                            }
                        }
                    }
                    builder.append(text.toString());

                    if(delimiterFlag == 1) {
                        runCount++;
                        stringParts = builder.toString().split("f");
                        sAdcValue = stringParts[0].replaceAll("[^\\d.]", "");
                        sFlowValue = stringParts[1].replaceAll("[^\\d.]", "");
                        final int iAdcValue = Integer.parseInt(sAdcValue);
                        final int iFlowValue = Integer.parseInt(sFlowValue);
                        builder.setLength(0);
                        delimiterFlag = 0;

                        //Time
                        time = (System.currentTimeMillis() - initialTime) / 1000;
                        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
                        final String formattedTime = formatter.format(time*1000);

                        //Gallons
                        cycleSum += iFlowValue;
                        avgTime = (cycleSum/runCount)*cycleTime;
                        avgFreq = 1/avgTime;
                        avgFlowRate = avgFreq/7.5;
                        liters = (avgFlowRate*time)/60;
                        gallons = Double.parseDouble(df.format(liters*0.264172));

                        //Temperature
                        voltage = (double) iAdcValue*5/1024; //Voltage across thermistor
                        resistance = 1000/((5/voltage)-1); //Resistance of thermistor
                        tempKalvin = (3960*298.15)/(3960-298.15*Math.log(9983/resistance));
                        final double tempFahrenheit = Double.parseDouble(df.format(1.8*(tempKalvin-273.15)+32));
                        tempSum += tempFahrenheit;
                        tempAvg = tempSum/runCount;

                        handler.post(new Runnable() {
                            public void run() {
                                //Time
                                myTime.setText(formattedTime);

                                //Gallons
                                String gallonText = gallons + " Gallons";
                                myGallons.setText(gallonText);

                                //Temperature
                                String tempText = tempFahrenheit + " Degrees (F)";
                                myTemp.setText(tempText);
                            }
                        });
                    }

                } catch (IOException ex) {
                    stopWorker = true;
                }
                saveData(time, gallons, tempAvg);
            }
        });
        workerThread.start();
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        String bluetoothClosedText = "Bluetooth Closed";
        myLabel.setText(bluetoothClosedText);
    }
}