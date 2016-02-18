package com.hackathon.byteme.card_reader;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements Reader.AccountCallback{

	Reader r;
	public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

	public void btnStart_clicked(View v){
		EditText ip = (EditText) findViewById(R.id.txtAddress);
		EditText port = (EditText) findViewById(R.id.txtPort);

		r = new Reader(this);
		r.setServerInfo(ip.getText().toString(), Integer.parseInt(port.getText().toString()));
		CheckBox chk = (CheckBox) findViewById(R.id.chkRunning);
		chk.setVisibility(View.VISIBLE);
		enableReaderMode();
	}
	public void onAccountReceived(final String account) {
		// This callback is run on a background thread, but updates to UI elements must be performed
		// on the UI thread.
		disableReaderMode();

	}
	public void enableReaderMode() {
		NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
		if (nfc != null) {
			nfc.enableReaderMode(this, r, READER_FLAGS, null);
		}
	}

	public  void disableReaderMode() {
		NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
		if (nfc != null) {
			nfc.disableReaderMode(this);
		}
	}
}
