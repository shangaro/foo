package uwaterloo.com.lab01_204_04;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import ca.uwaterloo.sensortoy.LineGraphView;

public class LAB01_204_04 extends AppCompatActivity {


    public int graphWidth;
    public int graphHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab01_204_04);

        // TextView setup for real time sensor readings

        TextView tvLight= (TextView) findViewById(R.id.tvLight);
        TextView tvMagnetic= (TextView) findViewById(R.id.tvMagnetic);
        TextView tvAccelerometer= (TextView) findViewById(R.id.tvAccelerometer);
        TextView tvRotation= (TextView) findViewById(R.id.tvRotation);
        LinearLayout layout=(LinearLayout)findViewById(R.id.layout);
        layout.setOrientation(LinearLayout.VERTICAL);


        //  Singleton SensorManager
        SensorManager sensorManager= ((SensorManager) getSystemService(SENSOR_SERVICE));


        //Singleton GraphView
        LineGraphView graph= new LineGraphView(getApplicationContext(),100, Arrays.asList("x","y","z"));

        graphWidth=Resources.getSystem().getDisplayMetrics().widthPixels;
        graphHeight=Resources.getSystem().getDisplayMetrics().heightPixels/2;
        graph.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        graph.setSize(graphWidth,graphHeight);
//        graph.setColors();


        // Singleton csvUtility
        final CSVUtility csvUtility=new CSVUtility(getApplicationContext(),new File(getExternalFilesDir(CSVUtility.folderName),CSVUtility.fileName));


        // Add graph to the layout
        layout.addView(graph,0);
        layout.setVisibility(View.VISIBLE);

        // Next access the default sensor

        Sensor accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor rotationalSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor lightSensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        final AccelerometerSensorHandler accelerometerSensorHandler=new AccelerometerSensorHandler(tvAccelerometer,graph);
        final MagneticSensorHandler magneticSensorHandler=new MagneticSensorHandler(tvMagnetic,graph);
        final RotationalSensorHandler rotationalSensorHandler=new RotationalSensorHandler(tvRotation);
        final LightSensorHandler lightSensorHandler=new LightSensorHandler(tvLight);



        RegisterListener(sensorManager,accelerometerSensor,accelerometerSensorHandler);
        RegisterListener(sensorManager,magneticSensor,magneticSensorHandler);
        RegisterListener(sensorManager,rotationalSensor,rotationalSensorHandler);
        RegisterListener(sensorManager,lightSensor,lightSensorHandler);

        //Buttons

        final Button csv_button=(Button)findViewById(R.id.button_csv);
        final Button clean_button=(Button)findViewById(R.id.button_maxRecord);


        // Button events
        csv_button.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                csvUtility.GenerateCSV();

                Toast.makeText(LAB01_204_04.this, "CSV generated at " + getExternalFilesDir(CSVUtility.folderName).getPath(), Toast.LENGTH_LONG)

                        .show();

            }

        });



        clean_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                lightSensorHandler.cleanMaxRecord();
                accelerometerSensorHandler.cleanMaxRecord();
                magneticSensorHandler.cleanMaxRecord();
            }
        });




    }

    public void RegisterListener(SensorManager sM,Sensor s, SensorEventListener se){

        sM.registerListener(se, s, sM.SENSOR_DELAY_UI);



    }
}
