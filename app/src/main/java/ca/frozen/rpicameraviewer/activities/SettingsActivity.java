
package ca.frozen.rpicameraviewer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

import ca.frozen.library.classes.Log;
import ca.frozen.rpicameraviewer.App;
import ca.frozen.rpicameraviewer.classes.Settings;
import ca.frozen.rpicameraviewer.classes.Utils;
import ca.frozen.rpicameraviewer.R;

public class SettingsActivity extends AppCompatActivity
{
	// instance variables
	private EditText cameraName;
	private EditText scanTimeout;
	private EditText port;
	private Switch showAllNetworks;
	private Settings settings;

	//******************************************************************************
	// onCreate
	//******************************************************************************
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// configure the activity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// initialize the logger
		Utils.initLogFile(getClass().getSimpleName());

		// get the settings
		settings = (savedInstanceState == null)
						? new Settings(Utils.getSettings())
						: (Settings) savedInstanceState.getParcelable("settings");

		// set the views
		cameraName = findViewById(R.id.settings_camera_name);
		cameraName.setText(settings.cameraName);

		scanTimeout = findViewById(R.id.settings_scan_timeout);
		scanTimeout.setText(Integer.toString(settings.scanTimeout));

		port = findViewById(R.id.settings_port);
		port.setText(Integer.toString(settings.port));

		showAllNetworks = findViewById(R.id.settings_show_all_networks);
		showAllNetworks.setChecked(settings.showAllCameras);
	}

	//******************************************************************************
	// onSaveInstanceState
	//******************************************************************************
	@Override
	protected void onSaveInstanceState(Bundle state)
	{
		settings.cameraName = cameraName.getText().toString().trim();
		String scanTimeoutString = scanTimeout.getText().toString();
		settings.scanTimeout = scanTimeoutString.isEmpty() ? Settings.DEFAULT_TIMEOUT : Integer.parseInt(scanTimeoutString);
		String portString = port.getText().toString();
		settings.port = portString.isEmpty() ? Settings.DEFAULT_PORT : Integer.parseInt(portString);
		settings.showAllCameras = showAllNetworks.isChecked();
		state.putParcelable("settings", settings);
		super.onSaveInstanceState(state);
	}

	//******************************************************************************
	// onCreateOptionsMenu
	//******************************************************************************
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_save, menu);
		return true;
	}

	//******************************************************************************
	// onOptionsItemSelected
	//******************************************************************************
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		// save the camera
		if (id == R.id.action_save)
		{
			if (getAndCheckSettings())
			{
				Log.info("menu: save " + settings.toString());
				Utils.setSettings(settings);
				Utils.saveData();
				finish();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	//******************************************************************************
	// getAndCheckSettings
	//******************************************************************************
	private boolean getAndCheckSettings()
	{
		// get and check the camera name
		settings.cameraName = cameraName.getText().toString().trim();
		if (settings.cameraName.isEmpty())
		{
			App.error(this, R.string.error_no_camera_name);
			return false;
		}

		// get and check the scan timeout
		settings.scanTimeout = Utils.getNumber(scanTimeout);
		if (settings.scanTimeout < Settings.MIN_TIMEOUT || settings.scanTimeout > Settings.MAX_TIMEOUT)
		{
			App.error(this, String.format(getString(R.string.error_bad_timeout), Settings.MIN_TIMEOUT, Settings.MAX_TIMEOUT));
			return false;
		}

		// get and check the port
		settings.port = Utils.getNumber(port);
		if (settings.port < Settings.MIN_PORT || settings.port > Settings.MAX_PORT)
		{
			App.error(this, String.format(getString(R.string.error_bad_port), Settings.MIN_PORT, Settings.MAX_PORT));
			return false;
		}

		// get the show all cameras flag
		settings.showAllCameras = showAllNetworks.isChecked();

		// indicate success
		return true;
	}
}