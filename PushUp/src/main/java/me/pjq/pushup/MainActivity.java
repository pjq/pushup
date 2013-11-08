package me.pjq.pushup;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity implements View.OnClickListener {
    ImageView startImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startImageView = (ImageView) findViewById(R.id.start_button);
        startImageView.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.start_button:
                Intent intent = new Intent();
                intent.setClass(this, ProximityActivity.class);
                startActivity(intent);

                break;
        }

    }
}
