package com.hansen.stlviewer.simplestlviewer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hansen.stlviewer.simplestlviewer.customSpinner.storeSpinnerAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class FileFinderActivity extends AppCompatActivity {

    private ListView mFileList;
    private Spinner storeSpinner;
    private ArrayList<ListItem> lTemp = null;
    private String mRoot ="";
    private String defaultRoot="";
    private TextView mPath;
    private ListViewAdapter mAdapter = null;
    private String mDirPath;
    private Intent stlLoad;

    private String DeivcePath="";
    private String DownloadPath="";

    private SharedPreferences appdatas = null;
    private SharedPreferences.Editor editor = null;
    private String dataName = "appDatas";

    private ArrayList<String>  storeLists;
    private ArrayList<Integer>  storeIconLists;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //storeSpinner = (Spinner)findViewById(R.id.storeSpinner);
        mPath = (TextView)findViewById(R.id.tvPath);
        mFileList = (ListView)findViewById(R.id.filelist);
        stlLoad = new Intent(getApplicationContext(),STLViewActivity.class);

        //DeivcePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //DownloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();


        mAdapter = new ListViewAdapter(this);
        mFileList.setAdapter(mAdapter);

        mFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = (FileInfo)mAdapter.getItem(position);
                File file = new File(fileInfo.getFilePath());
                if(file.isDirectory()){
                    if(file.canRead()){
                        getDir(fileInfo.getFilePath());
                    }else{
                        Toast.makeText(FileFinderActivity.this, "can not read", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    //editor.putString("currentRoot",mDirPath);
                    //editor.commit();

                    stlLoad.putExtra("stlPath",fileInfo.getFilePath());
                    startActivity(stlLoad);

                    //finish();

                }

            }
        });
    }


    private void getDir(String dirPath){
        mAdapter = new ListViewAdapter(this);
        mPath.setText("Location: " + dirPath);
        mDirPath = dirPath;

        lTemp = new ArrayList<ListItem>();

        //root save
        editor.putString("currentRoot",mDirPath);
        editor.commit();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        ListItem listItem = null;
        for(int i=0; i<files.length; i++){
            listItem = new ListItem();
            File file = files[i];
            if (file.isDirectory()){
                listItem.setName("./" + file.getName());
            }else{
                if(!FileUtil.isStlFile(file.getName())){
                    continue;
                }
                listItem.setName(file.getName());
            }
            listItem.setPath(file.getAbsolutePath());
            lTemp.add(listItem);
        }

        Collections.sort(lTemp, new Comparator<ListItem>() {

            @Override
            public int compare(ListItem lhs, ListItem rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        FileInfo fileInfo = null;
        if(!dirPath.equals(mRoot)){
            fileInfo = new FileInfo();
            fileInfo.setFileName("../");
            fileInfo.setFilePath(f.getParent());
            mAdapter.addItem(fileInfo);
        }

        for(int i=0; i<lTemp.size(); i++){
            File file = new File(lTemp.get(i).getPath());
            if(file != null){
                fileInfo = new FileInfo();
                if(file.isDirectory()){
                    fileInfo.setFileName(file.getName() + "/");
                    fileInfo.setFileSize("");
                    fileInfo.setFileIcon(R.drawable.folder_icon);
                }else{
                    fileInfo.setFileName(file.getName());
                    fileInfo.setFileSize(file.length()+"");
                    fileInfo.setFileIcon(R.drawable.stl_icon);
                }
                fileInfo.setFilePath(file.getAbsolutePath());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA);
                Date date = new Date(file.lastModified());
                String fileDate = formatter.format(date);
                fileInfo.setFileDate(fileDate);
                mAdapter.addItem(fileInfo);
            }

        }
        mFileList.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        if(mRoot.equals(mDirPath)){            //if rootDirectory   app finish!!!!
            Toast.makeText(FileFinderActivity.this, "no more directory", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            FileInfo fileInfo = (FileInfo)mAdapter.getItem(0);
            File file = new File(fileInfo.getFilePath());
            if(file.isDirectory()){
                if(file.canRead()){
                    getDir(fileInfo.getFilePath());
                }
            }
        }
    }

    public void download_store(View v){
        //mRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        getDir(DownloadPath);
        //mFileList.setAdapter(mAdapter);
    }

    public void device_store(View v){
        //mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        getDir(Environment.getExternalStorageDirectory().getAbsolutePath());
        //FileList.setAdapter(mAdapter);
    }

    private void permissionCheck(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionResult = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                // no permission
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // once permission checking

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

                } else {
                    // not once permission checking
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                }
            } else {
                //permission ok
                //setRoot();

                //if(!permissionFlag) {
                    //mRoot = appdatas.getString("currentRoot", DeivcePath);

                    defaultRoot = appdatas.getString("currentRoot", Environment.getExternalStorageDirectory().getAbsolutePath());
                    //permissionFlag = true;

                    spinner_N_list_Set();
                //}

                //getDir(mDirPath);
            }
        } else {
            // low version
            //permissionFlag = true;
            setRoot();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //PERMISSION Agree
                    Toast.makeText(FileFinderActivity.this, "The application is available.", Toast.LENGTH_LONG).show();
                    setRoot();

                } else {
                    //PERMISSION Refuse
                    Toast.makeText(FileFinderActivity.this, "If you refuse, you will not be able to use the app.", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
        }
    }

    private void spinner_N_list_Set(){

        storeSpinner = (Spinner)findViewById(R.id.storeSpinner);
        storeLists =  new ArrayList<String>();
        storeIconLists = new ArrayList<Integer>();

        storeLists.add(defaultRoot);                                                     //past or default path
        storeIconLists.add(R.drawable.folder_icon);

        storeLists.add(Environment.getExternalStorageDirectory().getAbsolutePath());     //device paths
        storeIconLists.add(R.drawable.device_storage);

        for(String exPaths : sdCardCheck.getExternalMounts()){                          //sd care path
            storeLists.add(exPaths);
            storeIconLists.add(R.drawable.sd_card);
        }

        storeLists.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());   //download path
        storeIconLists.add(R.drawable.download_storage);





//////////////////////////////////////////////////custom spinner///////////////////////////////////////////////////////////////////////
        // 어댑터와 스피너를 연결합니다.

        storeSpinnerAdapter StoreSpinnerAdapter = new storeSpinnerAdapter(FileFinderActivity.this, storeLists, storeIconLists);
        storeSpinner.setAdapter(StoreSpinnerAdapter);




        // 스피너에서 아이템 선택시 호출하도록 합니다.

        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //selected_fruit_idx = spinner_fruits.getSelectedItemPosition();
                //Toast.makeText(MainActivity.this, spinnerNames[selected_fruit_idx], Toast.LENGTH_LONG).show();

                Toast.makeText(getApplicationContext(), storeLists.get(i), Toast.LENGTH_SHORT).show();
                getDir(storeLists.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

    }


    protected void onResume() {
        super.onResume();

        appdatas = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = appdatas.edit();

        permissionCheck();
        //setRoot();

    }

    private void setRoot(){
        //mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        defaultRoot = DeivcePath;
        spinner_N_list_Set();
        //getDir(DeivcePath);
    }
}
