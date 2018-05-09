package budikpet.cvut.cz.semestralwork.screens.preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import java.io.Console;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.config.Config;

public class ActivityPreferences extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if(Config.isCheckWifiEnabled()) {
			CheckBox box = findViewById(R.id.isWifiCheckRequired);
			box.setChecked(Config.isCheckWifiEnabled());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case (android.R.id.home):
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onWifiCheckClick(View view) {
		CheckBox box = findViewById(R.id.isWifiCheckRequired);
		Config.setCheckWifiEnabled(!box.isChecked());
		box.setChecked(Config.isCheckWifiEnabled());

		Config.serializeConfig(this);

		Log.i("TEST", Config.isCheckWifiEnabled() + "");
	}
}
