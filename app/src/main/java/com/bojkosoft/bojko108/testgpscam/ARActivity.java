package com.bojkosoft.bojko108.testgpscam;

import android.os.Bundle;
import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bojkosoft.bojko108.testgpscam.augmentedreality.DirectionView;

public class ARActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private float rX;
    private float rY;
    private float rZ;

    private DirectionView customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        this.customView = (DirectionView) findViewById(R.id.customView);

        this.rX = this.rY = this.rZ = 0f;

        ((SeekBar) findViewById(R.id.seekBarX)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarY)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarZ)).setOnSeekBarChangeListener(this);

        ((SeekBar) findViewById(R.id.seekBarX)).setProgress(Math.round(this.rX));
        ((SeekBar) findViewById(R.id.seekBarY)).setProgress(Math.round(this.rY));
        ((SeekBar) findViewById(R.id.seekBarZ)).setProgress(Math.round(this.rZ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateRotation();
    }

    private void updateRotation() {
        ((TextView) findViewById(R.id.textViewRotX)).setText(String.valueOf(this.rX));
        ((TextView) findViewById(R.id.textViewRotY)).setText(String.valueOf(this.rY));
        ((TextView) findViewById(R.id.textViewRotZ)).setText(String.valueOf(this.rZ));

        this.customView.setNewRotation(this.rX, this.rY, this.rZ);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.seekBarX) {
            this.rX = (float) progress;
        }
        if (seekBar.getId() == R.id.seekBarY) {
            this.rY = (float) progress;
        }
        if (seekBar.getId() == R.id.seekBarZ) {
            this.rZ = (float) progress;
        }

        updateRotation();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //updateRotation();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat("rX", this.rX);
        outState.putFloat("rY", this.rY);
        outState.putFloat("rZ", this.rZ);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.rX = savedInstanceState.getFloat("rX");
        this.rY = savedInstanceState.getFloat("rY");
        this.rZ = savedInstanceState.getFloat("rZ");
    }
}