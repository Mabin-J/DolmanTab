package info.mabin.android.dolmantab.example;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnType1 = (Button) findViewById(R.id.btnType1);
		Button btnType2 = (Button) findViewById(R.id.btnType2);
		Button btnType3 = (Button) findViewById(R.id.btnType3);
		
		btnType1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActivityMain.this, ActivityType1.class));
			}
		});
		
		btnType2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActivityMain.this, ActivityType2.class));
			}
		});
		
		btnType3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActivityMain.this, ActivityType3.class));
			}
		});
	}
}