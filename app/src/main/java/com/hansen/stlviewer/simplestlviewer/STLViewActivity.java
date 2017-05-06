package com.hansen.stlviewer.simplestlviewer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stlview);

        mainLayout = (RelativeLayout)findViewById(R.id.layout_view);
        objectInfo_left = (TextView)findViewById(R.id.objectInfo_left);
        objectInfo_right = (TextView)findViewById(R.id.objectInfo_right);

        intent_data = getIntent();

        if(intent_data.getDataString() != null){
            StlPath = intent_data.getDataString().substring(7);   // "file://" --> delete
            //permissionCheck();  //permission checking
            STLViewSet(); // view Start

        }else if(intent_data.getExtras() != null){

            StlPath = intent_data.getExtras().getString("stl_path");
            STLViewSet(); // view Start

        }else{
            Toast.makeText(STLViewActivity.this,"path error!!", Toast.LENGTH_LONG).show();
            finish();
        }

        //Log.i("stl- view-","StlPath-"+intent_data.getDataString());



        //Log.i("stl- view-","open-"+isStl.getFileFormat());
    }

    public void backButton(View v){
        finish();
    }

    public void recover(View v){
        mGLView.setObject();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        //mGLView.requestRender();
    }

    private void STLViewSet(){
        processSpeed = System.currentTimeMillis();    //parsing speed check

        isStl = new stlPaser(StlPath);  //pasering start
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
                StlPath = intent_data.getDataString().substring(7);   // "file://" --> delete
                STLViewSet();
            }
        } else {
            // low version
            StlPath = intent_data.getDataString().substring(7);   // "file://" --> delete
            STLViewSet();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //PERMISSION Agree
                    Toast.makeText(STLViewActivity.this, "The application is available.", Toast.LENGTH_LONG).show();
                    StlPath = intent_data.getDataString().substring(7);   // "file://" --> delete
                    STLViewSet();


                } else {
                    //PERMISSION Refuse
                    Toast.makeText(STLViewActivity.this, "If you refuse, you will not be able to use the app.", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
        }
    }


}