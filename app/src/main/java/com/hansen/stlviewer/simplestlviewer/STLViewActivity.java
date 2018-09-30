package com.hansen.stlviewer.simplestlviewer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static java.lang.Math.abs;

public class STLViewActivity extends Activity {

    private stlPaser isStl;
    private String StlPath;
    private MyGLSurfaceView mGLView;
    private long processSpeed = 0;
    private float complete = 0;
    private float xyz[];
    private float xSize = 0, ySize = 0, zSize = 0;

    int faceCnt =0;
    static RelativeLayout mainLayout;
    private TextView objectInfo_left;
    private TextView objectInfo_right;
    private Intent intent_data;

    private int inputState = 0;   //0:앱내부접근 1:앱외부접근

    private SharedPreferences appdatas = null;
    private SharedPreferences.Editor editor = null;
    private String dataName = "appDatas";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stlview);

        Log.i("stlviewlog","create");

        mainLayout = (RelativeLayout)findViewById(R.id.glview);
        objectInfo_left = (TextView)findViewById(R.id.objectInfo_left);
        objectInfo_right = (TextView)findViewById(R.id.objectInfo_right);

        //Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.welcome);
        //Uri url = Uri.parse("android.resource://" +getResources().getResourcePackageName(R.raw.welcome));

                //stl path cash reset
        //intent_data = getIntent();
        appdatas = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = appdatas.edit();
        //editor.putString("stlfilepath",url.toString());
        //editor.commit();

        permissionCheck();   //권한검사


    }

    public void loadButton(View v){
        //stlLoad = new Intent(getApplicationContext(),FileFinderActivity.class);
        //startActivity(stlLoad);
        finish();
    }

    public void recover(View v){
        mGLView.setObject();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mGLView.onPause();

        Log.i("stlviewlog","pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mGLView.onResume();
        //mGLView.requestRender();

        Log.i("stlviewlog","resume");

        //loadStl();

    }

    private void STLViewSet(File file){
        processSpeed = System.currentTimeMillis();    //parsing speed check

        isStl = new stlPaser(file);  //pasering start
        xyz = isStl.getXYZ();          // object max,min coordinate
        faceCnt = isStl.getFaceCnt();  //triangle count

        xSize = abs(xyz[0]-xyz[1]);
        ySize = abs(xyz[2]-xyz[3]);
        zSize = abs(xyz[4]-xyz[5]);

        mGLView = new MyGLSurfaceView(this,isStl);

        objectInfo_left.setText(xSize+" x "+ySize+" x "+zSize+" ");
        objectInfo_right.setText(faceCnt+" faces");

        mainLayout.addView(mGLView,0);  //gl view add

        complete = ( System.currentTimeMillis()-processSpeed)/1000.0f;

        if(faceCnt==0){
            Toast.makeText(STLViewActivity.this,"nothing faces", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(STLViewActivity.this, "parse complete! \n" + complete + " seconds", Toast.LENGTH_LONG).show();
        }

    }

    private void checknLoadStl(){
        String path="";

        intent_data = getIntent();

        if(intent_data.getDataString() != null) {    //external searched
            path = intent_data.getDataString().substring(7);
            //editor.putString("stlfilepath", path);
            //editor.commit();
            //Log.i("stl- view-","save----------------------------------"+path);
        }else {                                     //app in searched
            path = intent_data.getExtras().getString("stlPath");
            //Log.i("stl- view-","load-------------------------------");
        }

        if(StlPath!=""){

            File stlFile = new File(path);
            if(stlFile.exists()){   //파일 존재
                mainLayout.removeAllViews();
                STLViewSet(stlFile);
            }else{                  //파일 비존재
                Toast.makeText(STLViewActivity.this,"not exists file!!", Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }

    private void permissionCheck(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionResult = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                // no permission
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //  once permission checking
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

                } else {
                    // not once permission checking
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                }
            } else {
                // Permissin ok
                checknLoadStl();
            }
        } else {
            // low version
            checknLoadStl();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //PERMISSION Agree
                    Toast.makeText(STLViewActivity.this, "The application is available.", Toast.LENGTH_LONG).show();
                    checknLoadStl();


                } else {
                    //PERMISSION Refuse
                    Toast.makeText(STLViewActivity.this, "If you refuse, you will not be able to use the app.", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
        }
    }


}