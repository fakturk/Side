package com.unist.netlab.fakturk.side;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.hardware.SensorManager.GRAVITY_EARTH;

public class MainActivity extends AppCompatActivity
{
    ArrowView arrowView ;
    ArrowView sideYView;
    ArrowView sideXView;
    ArrowView gyrView;
    LinearLayout linearLayoutHor1, linearLayoutHor2;

    TextView tv_gravity;
    Intent i;
    Button buttonStart, buttonGra, buttonAcc, buttonLinear, buttonGyr ;

    float[] acc, gyr, oldAcc, oldGyr, gravity,sideY,sideX, oldGravity;
    float[][] rotation, resultOfDynamic;
    boolean start;

    Gravity g;
    Orientation orientation;
    DynamicAcceleration dynamic;
    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrowView = (ArrowView) findViewById(R.id.arrowView);
        sideYView = (ArrowView) findViewById(R.id.accView);
        sideXView = (ArrowView) findViewById(R.id.accGraDiffView);
        gyrView = (ArrowView) findViewById(R.id.gyrView);

        arrowView.setType("Gravity");
        sideYView.setType("SideY");
        sideXView.setType("SideX");
        gyrView.setType("Gyr");

        linearLayoutHor1 = (LinearLayout) findViewById(R.id.linearLayHor1);
        linearLayoutHor2 = (LinearLayout) findViewById(R.id.linearLayHor2);


        tv_gravity = (TextView) findViewById(R.id.tv_gravity);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonGra = (Button) findViewById(R.id.buttonGra);
        buttonAcc = (Button) findViewById(R.id.buttonAcc);
        buttonLinear = (Button) findViewById(R.id.buttonAccGraDiff);
        buttonGyr = (Button) findViewById(R.id.buttonGyr);
        i = new Intent(this, SensorService.class);

        acc = new float[3];
        gyr = new float[3];
        oldAcc = null;
        oldGyr = null;
        gravity = new float[3];
        sideX = new float[3];
        sideY = new float[3];
        rotation = null;
        resultOfDynamic = new float[5][3];

        g = new Gravity();
        orientation = new Orientation();
        dynamic = new DynamicAcceleration();
        counter=0;

        arrowView.setLine(0,100,0);
        sideYView.setLine(0,100,0);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                acc = (intent.getFloatArrayExtra("ACC_DATA"));
                gyr = intent.getFloatArrayExtra("GYR_DATA");

                if (acc!=null && oldAcc==null)
                {
                    oldAcc = new float[3];
                    System.arraycopy(acc, 0, oldAcc, 0,acc.length);

                }
                if (gyr!=null && oldGyr==null)
                {
                    oldGyr = new float[3];
                    System.arraycopy(gyr,0,oldGyr,0,gyr.length);

                }
                if (acc==null && oldAcc!=null)
                {
                    acc = new float[3];
                    System.arraycopy(oldAcc, 0, acc, 0,oldAcc.length);

                }
                if (gyr==null && oldGyr!=null)
                {
                    gyr = new float[]{0,0,0};

                }


                if (acc!=null && gyr!=null && start!=true)
                {
                    start = true;
                    float accNorm = (float) Math.sqrt(Math.pow(acc[0],2)+Math.pow(acc[1],2)+Math.pow(acc[2],2));
                    for (int j = 0; j < 3; j++)
                    {
                        gravity[j] = acc[j]*(GRAVITY_EARTH/accNorm);
                    }
                    rotation = orientation.rotationFromGravity(gravity);
//                    orientation.setRotationMatrix(rotation);

                }
                if (start)
                {

                    float thresholdAcc = 0; //0.15
                    float thresholdGyr = 0.15f; //0.2
                    float accNorm = (float) Math.sqrt(Math.pow(acc[0],2)+Math.pow(acc[1],2)+Math.pow(acc[2],2));
                    //if phone stable gravity = acc
                    if ((Math.abs(gyr[0])+Math.abs(gyr[1])+Math.abs(gyr[2]))<thresholdGyr )
                    {

                        System.out.print("stable, ");
                        if (counter>10) //if phone is stable over a second
                        {
                            for (int j = 0; j < 3; j++)
                            {
                                gravity[j] = acc[j]*(GRAVITY_EARTH/accNorm);

                            }
//                            rotation = orientation.rotationFromGravity(gravity);
//                            orientation.setRotationMatrix(rotation);
                            counter=0;
                        }
                        else
                        {
                            counter++;
                        }


                    }
//                    // if not rotating
//                    else if ((Math.abs(gyr[0])+Math.abs(gyr[1])+Math.abs(gyr[2]))<thresholdGyr && Math.abs(accNorm - GRAVITY_EARTH)>=thresholdAcc)
//                    {
//                        System.out.print("linear, ");
//                        counter=0;
//                    }
//                    // if rotating but not moving
//                    else if ((Math.abs(gyr[0])+Math.abs(gyr[1])+Math.abs(gyr[2]))>=thresholdGyr && Math.abs(accNorm - GRAVITY_EARTH)<thresholdAcc)
//                    {
//                        System.out.print("rotate, ");
//                        counter=0;
//                        for (int j = 0; j < 3; j++)
//                        {
//                            gravity[j] = acc[j]*(GRAVITY_EARTH/accNorm);
//
//                        }
//                        rotation = orientation.rotationFromGravity(gravity);
////                        orientation.setRotationMatrix(rotation);
//
//
//                    }

                    else // not stable
                    {
                        System.out.print("not stable, ");
                        counter=0;
//                        resultOfDynamic = dynamic.calculate(acc,oldAcc,gyr,gravity,rotation);
//                        rotation = orientation.updateRotationMatrix(rotation,gyr,dynamic.getDeltaT());
//                        for (int j = 0; j < 3; j++)
//                        {
//                            gravity[j]=resultOfDynamic[1][j];
//                        }
//                        System.out.println("before");
//                        g.printGravity(gravity);
//                        orientation.printRotation(rotation);
                        rotation = orientation.rotationFromGravity(gravity);
                        rotation = orientation.updateRotationMatrix(rotation, orientation.rotatedGyr(gyr,rotation),dynamic.getDeltaT());
                        gravity = g.gravityAfterRotation(rotation);
                        sideX = g.sideXAfterRotation(rotation);
                        sideY = g.sideYAfterRotation(rotation);
//                        orientation.setRotationMatrix(rotation);
//                        System.out.println("after");
//                        g.printGravity(gravity);
//                        orientation.printRotation(rotation);
                    }


                    //store acc values
                    System.arraycopy(acc, 0, oldAcc, 0,acc.length);

                    //set views
                    int lS = 20; // size coefficient of the line
                    arrowView.setLine((-1)*gravity[0]*lS,gravity[1]*lS,gravity[2]*lS);
                    sideYView.setLine((-1)*sideY[0]*lS,sideY[1]*lS, sideY[2]*lS);
                    sideXView.setLine((-1)*sideX[0]*lS,sideX[1]*lS, sideX[2]*lS);
                    gyrView.setLine(gyr[1]*lS,gyr[0]*lS, -1*gyr[2]*lS);

                    System.out.println(acc[0]+", "+acc[1]+", "+acc[2]+", "+gyr[0]+", "+gyr[1]+", "+gyr[2]+", "+gravity[0]+", "+gravity[1]+", "+gravity[2]+", ");





                }
            }
        }, new IntentFilter(SensorService.ACTION_SENSOR_BROADCAST));

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStart.getText().equals("Start")) {
                    buttonStart.setText("Stop");
                    startService(new Intent(MainActivity.this, SensorService.class));

                } else {
                    buttonStart.setText("Start");
                    stopService(new Intent(MainActivity.this, SensorService.class));


                }
            }
        });
        buttonGra.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonGra.getText().equals("GRA")) {
//                    buttonGra.setText("gra");
//                    System.out.println("GRA");
                    if (buttonAcc.getText().equals("acc"))
                    {
                        makeSmaller("ACC");
                    }
                    else if (buttonLinear.getText().equals("linear"))
                    {
                        makeSmaller("LINEAR");
                    }
                    else if (buttonGyr.getText().equals("gyr"))
                    {
                        makeSmaller("GYR");
                    }
                    else
                    {
                        makeBigger("GRA");
                    }




                } else {
//                    buttonGra.setText("GRA");
//                    System.out.println("gra");
                    makeSmaller("GRA");




                }
            }
        });

        buttonAcc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonAcc.getText().equals("ACC")) {

//                    System.out.println("ACC");
                    if (buttonGra.getText().equals("gra"))
                    {
                        makeSmaller("GRA");
                    }
                    else if (buttonLinear.getText().equals("linear"))
                    {
                        makeSmaller("LINEAR");
                    }
                    else if (buttonGyr.getText().equals("gyr"))
                    {
                        makeSmaller("GYR");
                    }
                    else
                    {
                        makeBigger("ACC");
                    }



                } else {

//                    System.out.println("acc");
                    makeSmaller("ACC");




                }
            }
        });

        buttonLinear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonLinear.getText().equals("LINEAR")) {
//                    buttonLinear.setText("linear");
//                    System.out.println("LINEAR");
                    if (buttonAcc.getText().equals("acc"))
                    {
                        makeSmaller("ACC");
                    }
                    else if (buttonGra.getText().equals("gra"))
                    {
                        makeSmaller("GRA");
                    }
                    else if (buttonGyr.getText().equals("gyr"))
                    {
                        makeSmaller("GYR");
                    }
                    else
                    {
                        makeBigger("LINEAR");
                    }




                } else {
//                    buttonLinear.setText("LINEAR");
//                    System.out.println("linear");
                    makeSmaller("LINEAR");




                }
            }
        });

        buttonGyr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonGyr.getText().equals("GYR")) {
//                    buttonGyr.setText("gyr");
//                    System.out.println("GYR");
                    if (buttonAcc.getText().equals("acc"))
                    {
                        makeSmaller("ACC");
                    }
                    else if (buttonLinear.getText().equals("linear"))
                    {
                        makeSmaller("LINEAR");
                    }
                    else  if (buttonGra.getText().equals("gra"))
                    {
                        makeSmaller("GRA");
                    }
                    else
                    {
                        makeBigger("GYR");
                    }



                } else {
//                    buttonGyr.setText("GYR");
//                    System.out.println("gyr");
                    makeSmaller("GYR");



                }
            }
        });
    }

    void makeBigger(String button)
    {

//        System.out.println("make bigger "+button);
        if (button=="GRA")
        {
            buttonGra.setText("gra");
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*3/2));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*1/2));

            linearLayoutHor1.removeView(sideYView);
            linearLayoutHor2.addView(sideYView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*3/2,arrowView.getHeight()*3/2));
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*1/2, sideYView.getHeight()*1/2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*1/2, sideXView.getHeight()*1/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*1/2,gyrView.getHeight()*1/2));

            arrowView.setCoefficient(arrowView.getCoefficient()*3/2);
            sideYView.setCoefficient(sideYView.getCoefficient()*1/2);
            sideXView.setCoefficient(sideXView.getCoefficient()*1/2);
            gyrView.setCoefficient(gyrView.getCoefficient()*1/2);
        }
        if (button=="ACC")
        {
            buttonAcc.setText("acc");
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*3/2));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*1/2));

            linearLayoutHor1.removeView(arrowView);
            linearLayoutHor2.addView(arrowView);
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*3/2, sideYView.getHeight()*3/2));
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*1/2,arrowView.getHeight()*1/2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*1/2, sideXView.getHeight()*1/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*1/2,gyrView.getHeight()*1/2));

            arrowView.setCoefficient(arrowView.getCoefficient()*1/2);
            sideYView.setCoefficient(sideYView.getCoefficient()*3/2);
            sideXView.setCoefficient(sideXView.getCoefficient()*1/2);
            gyrView.setCoefficient(gyrView.getCoefficient()*1/2);

        }
        if (button=="LINEAR")
        {
            buttonLinear.setText("linear");
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*3/2));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*1/2));

            linearLayoutHor2.removeView(gyrView);
            linearLayoutHor1.addView(gyrView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*1/2,arrowView.getHeight()*1/2));
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*1/2, sideYView.getHeight()*1/2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*3/2, sideXView.getHeight()*3/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*1/2,gyrView.getHeight()*1/2));

            arrowView.setCoefficient(arrowView.getCoefficient()*1/2);
            sideYView.setCoefficient(sideYView.getCoefficient()*1/2);
            sideXView.setCoefficient(sideXView.getCoefficient()*3/2);
            gyrView.setCoefficient(gyrView.getCoefficient()*1/2);
        }
        if (button=="GYR")
        {
            buttonGyr.setText("gyr");
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*3/2));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*1/2));

            linearLayoutHor2.removeView(sideXView);
            linearLayoutHor1.addView(sideXView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*1/2,arrowView.getHeight()*1/2));
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*1/2, sideYView.getHeight()*1/2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*1/2, sideXView.getHeight()*1/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*3/2,gyrView.getHeight()*3/2));

            arrowView.setCoefficient(arrowView.getCoefficient()*1/2);
            sideYView.setCoefficient(sideYView.getCoefficient()*1/2);
            sideXView.setCoefficient(sideXView.getCoefficient()*1/2);
            gyrView.setCoefficient(gyrView.getCoefficient()*3/2);

        }

    }

    void makeSmaller(String button)
    {
//        System.out.println("make smaller "+button);
        if (button=="GRA")
        {
            buttonGra.setText("GRA");
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2/3));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2));

            linearLayoutHor2.removeView(sideYView);
            linearLayoutHor1.addView(sideYView);

            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2/3,arrowView.getHeight()*2/3));
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*2, sideYView.getHeight()*2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*2, sideXView.getHeight()*2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2,gyrView.getHeight()*2));

            arrowView.setCoefficient(arrowView.getCoefficient()*2/3);
            sideYView.setCoefficient(sideYView.getCoefficient()*2);
            sideXView.setCoefficient(sideXView.getCoefficient()*2);
            gyrView.setCoefficient(gyrView.getCoefficient()*2);

        }
        if (button=="ACC")
        {
            buttonAcc.setText("ACC");
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2/3));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2));

            linearLayoutHor2.removeView(arrowView);
            linearLayoutHor1.addView(arrowView);
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*2/3, sideYView.getHeight()*2/3));
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2,arrowView.getHeight()*2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*2, sideXView.getHeight()*2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2,gyrView.getHeight()*2));

            arrowView.setCoefficient(arrowView.getCoefficient()*2);
            sideYView.setCoefficient(sideYView.getCoefficient()*2/3);
            sideXView.setCoefficient(sideXView.getCoefficient()*2);
            gyrView.setCoefficient(gyrView.getCoefficient()*2);

        }
        if (button=="LINEAR")
        {
            buttonLinear.setText("LINEAR");
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2/3));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2));

            linearLayoutHor1.removeView(gyrView);
            linearLayoutHor2.addView(gyrView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2,arrowView.getHeight()*2));
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*2, sideYView.getHeight()*2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*2/3, sideXView.getHeight()*2/3));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2,gyrView.getHeight()*2));

            arrowView.setCoefficient(arrowView.getCoefficient()*2);
            sideYView.setCoefficient(sideYView.getCoefficient()*2);
            sideXView.setCoefficient(sideXView.getCoefficient()*2/3);
            gyrView.setCoefficient(gyrView.getCoefficient()*2);

        }
        if (button=="GYR")
        {
            buttonGyr.setText("GYR");
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2/3));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2));

            linearLayoutHor1.removeView(sideXView);
            linearLayoutHor2.addView(sideXView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2,arrowView.getHeight()*2));
            sideYView.setLayoutParams(new LinearLayout.LayoutParams(sideYView.getWidth()*2, sideYView.getHeight()*2));
            sideXView.setLayoutParams(new LinearLayout.LayoutParams(sideXView.getWidth()*2, sideXView.getHeight()*2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2/3,gyrView.getHeight()*2/3));

            arrowView.setCoefficient(arrowView.getCoefficient()*2);
            sideYView.setCoefficient(sideYView.getCoefficient()*2);
            sideXView.setCoefficient(sideXView.getCoefficient()*2);
            gyrView.setCoefficient(gyrView.getCoefficient()*2/3);


        }

    }



    @Override
    protected void onPause() {

        super.onPause();


    }


    @Override
    protected void onResume() {

        super.onResume();
//        startService(new Intent(this, SensorService.class));
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();
    }


    @Override
    public void onStart() {

        super.onStart();


    }

    @Override
    public void onStop() {

        super.onStop();


    }
}
