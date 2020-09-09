package org.pytorch.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensorAcc;
    private Sensor sensorGyro;
    private TriggerEventListener triggerEventListener;
    float accXValue;
    float accYValue;
    float accZValue;
    float gyroXValue;
    float gyroYValue;
    float gyroZValue;
   double maxAX=1.997222249219917;
   double minAX=-0.4888889218044315;
   double maxAY=1.634722245672321;
    double minAY=-1.252777853006882;
    double maxAZ=1.463888933022928;
   double minAZ=-0.7916666716198171;
   double maxGX=5.320330619812012;
    double minGX=-3.276986598968506;
   double maxGY=3.133127927780151;
    double minGY=-4.782769203186035;
    double maxGZ=1.985922932624817;
    double minGZ=-2.441933870315552;



    Module module = null;
    float[] ax = new float[128];
    float[] ay = new float[128];
    float[] az = new float[128];
    float[] gx = new float[128];
    float[] gy = new float[128];
    float[] gz = new float[128];


    int axC = 0;
    int ayC = 0;
    int azC = 0;
    int gxC = 0;
    int gyC = 0;
    int gzC = 0;
    int i =0;







    public void onAccuracyChanged(Sensor sensor,int i){



    }

    public void onSensorChanged(SensorEvent sensorEvent){
        Sensor sensor = sensorEvent.sensor;






     if(sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION) {



         accXValue=sensorEvent.values[0];
         accYValue=sensorEvent.values[1];
         accZValue=sensorEvent.values[2];



         Log.d("ACC", "AX+ " + sensorEvent.values[0] + " AY+ " + sensorEvent.values[1] + " AZ+ " + sensorEvent.values[2]);
         //Log.d("ACC2", "AX+ " + accXValue + " AY+ " + accYValue + " AZ+ " + accZValue);
     }
       if(sensor.getType()==sensor.TYPE_GYROSCOPE) {
           gyroXValue=sensorEvent.values[0];
           gyroYValue=sensorEvent.values[1];
           gyroZValue=sensorEvent.values[2];


         Log.d("GYRO", "GX+ " + sensorEvent.values[0] + " GY+ " + sensorEvent.values[1] + " GZ+ " + sensorEvent.values[2]);
     }



            if(i<128) {
//               double normAX= (accXValue - minAX) / (maxAX - minAX);
//               double normAY= (accYValue - minAY) / (maxAY - minAY);
//               double normAZ= (accZValue - minAZ) / (maxAZ - minAZ);
//                double normGX= (gyroXValue - minGX) / (maxGX - minGX);
//                double normGY= (gyroYValue - minGY) / (maxGY - minGY);
//               double normGZ= (gyroZValue - minGZ) / (maxGZ - minGZ);
//                Log.d("norm","AX "+normAX + "AY "+normAY+"AZ "+normAZ+"GX "+normGX+"GY "+normGY+"GZ "+normGZ     ) ;
//                Log.d("values","AX "+accXValue + " AY "+accYValue+" AZ "+accZValue+" GX "+gyroXValue+" GY "+gyroYValue+" GZ "+gyroZValue     ) ;
                ax[i]=accXValue;
                axC++;
                ay[i]=accYValue;
                ayC++;
                az[i]=accZValue;
                azC++;
                gx[i]=gyroXValue;
                gxC++;
                gy[i]=gyroYValue;
                gyC++;
                gz[i]=gyroZValue;
                gzC++;
                i++;


                // fill up segment
            }
          else if(i>=128) {


//                // send to model and restart segment
                //activityOutput(ax,ay,az,gx,gy,gz);
                i=0;
                axC=0;
                ayC=0;
                azC=0;
                gxC=0;
                gyC=0;
                gzC=0;

//                }


            }

        }






  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
Log.d("hi","hi");
    // declaratin of sensors
      sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
      if(sensorAcc==null)
          Log.d("error2","not supported");

            sensorManager.registerListener(MainActivity.this,sensorAcc,SensorManager.SENSOR_DELAY_NORMAL);
            sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
      if(sensorGyro==null)
          Log.d("error","not supported");
      sensorManager.registerListener(MainActivity.this,sensorGyro,SensorManager.SENSOR_DELAY_NORMAL);



    try {
        float [] ax = new float[128];
        float [] ay = new float[128];
        float [] az = new float[128];
        float [] gx = new float[128];
        float [] gy = new float[128];
        float [] gz = new float[128];
        module = Module.load(assetFilePath(this, "final.pt"));



        BufferedReader reader1 = new BufferedReader(
               new InputStreamReader(getAssets().open("accStanding.txt")));
//
   int accCount =0;
        String mLine;
        while ((mLine = reader1.readLine()) != null) {

            String[] splited = mLine.split(" ");
            //Log.d("size",splited.length+"");
            ax[accCount]=(float)Double.parseDouble(splited[0]);
            ay[accCount]=(float)Double.parseDouble(splited[1]);
            az[accCount]=(float)Double.parseDouble(splited[2]);
            //Log.d("accStanding",accStanding[accCount]+" ");
            accCount++;


//
//
        }

        BufferedReader reader2 = new BufferedReader(
                new InputStreamReader(getAssets().open("gyroStanding.txt")));
//
        int gyroCount =0;
        String mLine2;
        while ((mLine2 = reader2.readLine()) != null) {

            String[] splited = mLine2.split(" ");
            //Log.d("size",splited.length+"");
            gx[gyroCount]=(float)Double.parseDouble(splited[0]);
            gy[gyroCount]=(float)Double.parseDouble(splited[1]);
            gz[gyroCount]=(float)Double.parseDouble(splited[2]);
            Log.d("accStanding",gx[gyroCount]+" "+" "+gy[gyroCount]+" "+gz[gyroCount]);
            gyroCount++;


//
//
        }

        activityOutput(ax,ay,az,gx,gy,gz);

    }






    catch (Exception e) {
      Log.d("error","hiiiiii2");
      Log.d("eror",e+"");


      finish();
    }





  }

  /**
   * Copies specified asset to the file in /files app directory and returns this file absolute path.
   *
   * @return absolute file path
   */
  public static String assetFilePath(Context context, String assetName) throws IOException {
    File file = new File(context.getFilesDir(), assetName);
    if (file.exists() && file.length() > 0) {
      return file.getAbsolutePath();
    }

    try (InputStream is = context.getAssets().open(assetName)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
    }
  }





  public void activityOutput(float[] ax,float[] ay,float[] az,float[] gx,float[] gy,float[] gz) {

//long s []={128};
long s [] = {768};
FloatBuffer b = Tensor.allocateFloatBuffer(768);
b.put(ax);
b.put(ay);
b.put(az);
b.put(gx);
b.put(gy);
b.put(gz);
Tensor input = Tensor.fromBlob(b,s);






//Tensor input = Tensor.fromBlob(arr,s);
//Log.d("shape",input.numel()+"");
      //Log.d("heree", "hereeeeeee");
     Tensor output = module.forward(IValue.from(input)).toTensor();
//      // Log.d("here", "YARAAAAB");

      float[] result = output.getDataAsFloatArray();
      Log.d("len",result.length+"");
      for (int k = 0; k < result.length; k++) {
          Log.d("result", result[k]+" "+k);
      }
      Log.d("lennnnn",result.length+"");

      float max = result[0];
      int index = 0;
      for(int l=1;l<result.length;l++){
          if(result[l]>=max){

              max = result[l];
              index = l;
          }

      }
      Log.d("max", max + "");
      Log.d("index", index + "");
      switch (index) {
          case 0:
              Log.d("activity", "Walking");
              break;
          case 1:
              Log.d("activity", "Walking upstairs");
              break;
          case 2:
              Log.d("activity", "Walking downstairs");
              break;
          case 3:
              Log.d("activity", "sitting");
              break;
          case 4:
              Log.d("activity", "standing");
              break;
          case 5:
              Log.d("activity", "laying");
              break;
          default:
              Log.d("activity", "undefined");
              break;
      }



  }


}
