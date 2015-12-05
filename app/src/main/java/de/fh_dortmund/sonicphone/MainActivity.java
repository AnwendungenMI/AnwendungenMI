package de.fh_dortmund.sonicphone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        int res = (int) (Math.random() * 10);
//        RandomClass.DivideByZero();

        Button nextActivity = (Button)findViewById(R.id.frameViewer);
        nextActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Intent myIntent = new Intent(MainActivity.this, neueActivity.class);
                Intent myIntent = new Intent(view.getContext(), FrameViewerActivity.class);
                MainActivity.this.startActivity(myIntent);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
