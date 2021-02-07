package com.hansen.stlviewer.simplestlviewer.stlprocess;

import android.content.Context;

import com.hansen.stlviewer.simplestlviewer.MyGLSurfaceView;
import com.hansen.stlviewer.simplestlviewer.stlPaser;

import java.io.File;

import static java.lang.Math.abs;

public class stl_pocessing_thread extends Thread {

    //private static final String TAG = "ExampleThread";
    private Context context;
    private stlprocessListener Listener;
    private File file;

    private stlPaser isStl;
    private MyGLSurfaceView mGLView;
    private float xyz[];
    private int faceCnt =0;
    private float xSize = 0, ySize = 0, zSize = 0;

    public stl_pocessing_thread(Context context, stlprocessListener Listener , File file)  {
        this.context = context;
        this.file = file;
        this.Listener = Listener;
    }
    public void run() {
        isStl = new stlPaser(file);  //pasering start
        xyz = isStl.getXYZ();          // object max,min coordinate
        faceCnt = isStl.getFaceCnt();  //triangle count

        xSize = abs(xyz[0]-xyz[1]);
        ySize = abs(xyz[2]-xyz[3]);
        zSize = abs(xyz[4]-xyz[5]);

        mGLView = new MyGLSurfaceView(context,isStl);

        Listener.getGLView(mGLView, xSize , ySize  , zSize , faceCnt);
    }
}