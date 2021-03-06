package com.hansen.stlviewer.simplestlviewer;

import com.hansen.stlviewer.simplestlviewer.stlpaser.paser;
import com.hansen.stlviewer.simplestlviewer.stlASCiiParser;
import com.hansen.stlviewer.simplestlviewer.stlBinaryParser;

import java.io.File;

/**
 * Created by Administrator on 2017-02-17.
 */

public class stlPaser {

    static {
        System.loadLibrary("STLpaser_C");
    }
    paser STLpaser;
    File file;
    String format=null;

    //public stlPaser(String fileRoot){
    public stlPaser(File file){
        this.file = file;

        if(fileSize(file.getPath()) == file.length()){
            STLpaser =new stlBinaryParser(file);
            format = "Binary";
        }else{
            STLpaser =new stlASCiiParser(file);
            format = "ASCii";
        }

    }

    public float[] getVectors(){ return STLpaser.getVectors(); }

    public float[] getNormals(){
        return STLpaser.getNormals();
    }

    public float[] getXYZ(){
        return STLpaser.getXYZ();
    }

    public float[] getCenterPoint() {return STLpaser.getObjectCenterPoint(); }

    public int getFaceCnt(){
        return STLpaser.getObjectFaceCnt();
    }

    public String getFileFormat(){
        return format;
    }

    private native int fileSize(String f);

}
