package com.hansen.stlviewer.simplestlviewer.stlprocess;

import com.hansen.stlviewer.simplestlviewer.MyGLSurfaceView;

public interface stlprocessListener {
    void getGLView(MyGLSurfaceView GLView, float xSize , float ySize  ,float zSize ,int faceCnt);
}
