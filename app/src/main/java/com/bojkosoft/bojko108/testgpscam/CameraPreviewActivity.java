package com.bojkosoft.bojko108.testgpscam;

import android.app.Activity;
import android.os.Bundle;

public class CameraPreviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, CameraFragment.newInstance())
                .commit();
    }

    protected void onResume(){
        super.onResume();

        getFragmentManager().findFragmentById(R.id.container);
    }
}