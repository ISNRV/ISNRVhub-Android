package com.isnrv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.firebase.simplelogin.User;
import com.firebase.simplelogin.enums.Error;

/**
 * This class provides access to Firebase database to the admin. It allows admin to change
 * parking status
 *
 * @author Yasir
 */
public class Admin extends Activity {

	private static final String URL = "https://isnrvhub.firebaseio.com";
	private EditText emailEditText;
	private EditText passwordEditText;
	private Firebase parkingRef;
	private Firebase connectedRef;
	private Firebase isnrvRef;
	private SimpleLogin authClient;
	private boolean isConnected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin);

		//setup actionbar
		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setCustomView(R.layout.custom_actionbar);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayUseLogoEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
		}

		isnrvRef = new Firebase(URL);
		parkingRef = new Firebase(URL + "/parking");
		connectedRef = new Firebase(URL + "/.info/connected");

		emailEditText = findViewById(R.id.emailEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		emailEditText.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", ""));

		isConnected = false;
		setCallbacks();

		//check if already logged in
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("loggedIn", false)) {
			login();
		}

	}

	private void setCallbacks() {
		final TextView connectionTextView = findViewById(R.id.connectionTextView);
		final Button loginButton = findViewById(R.id.loginButton);
		//callback to check connection status
		connectedRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				isConnected = snapshot.getValue(Boolean.class);
				RadioGroup radioGroupStatus = findViewById(R.id.radioGroupStatus);

				final String text = isConnected ? getResources().getString(R.string.connected) : getResources().getString(R.string.disconnected);
				connectionTextView.setText(text);
				for (int i = 0; i < radioGroupStatus.getChildCount(); i++) {
					radioGroupStatus.getChildAt(i).setEnabled(isConnected);
				}
			}

			@Override
			public void onCancelled(FirebaseError error) {
				// Do nothing
			}
		});
		//callback to update parking status once
		parkingRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snap) {
				//set radio button to current state
				RadioGroup radioGroupStatus = findViewById(R.id.radioGroupStatus);
				for (int i = 0; i < radioGroupStatus.getChildCount(); i++) {
					RadioButton rb = (RadioButton) radioGroupStatus.getChildAt(i);
					if ((snap.getValue()).equals(rb.getText().toString())) {
						rb.setChecked(true);
					}
				}
			}

			@Override
			public void onCancelled(FirebaseError error) {
				// Do nothing
			}
		});

		//login button
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String email = emailEditText.getText().toString();
				final String password = passwordEditText.getText().toString();
				authClient = new SimpleLogin(isnrvRef);
				authClient.loginWithEmail(email, password, new SimpleLoginAuthenticatedHandler() {
					public void authenticated(Error error, User user) {
						if (error != null) {
							// There was an error logging into this account
							Log.d("TEST", "couldn't log in");
							findViewById(R.id.incorrectTextView).setVisibility(View.VISIBLE);
						} else {
							//We are now logged in
							Log.d("TEST", "logged in");
							//remember username and password for later login
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
							editor.putString("email", email);
							editor.putString("password", password);
							editor.putBoolean("loggedIn", true);
							editor.apply();
							login();
						}
					}
				});
			}
		});
	}

	//login successful
	private void login() {
		findViewById(R.id.loginLinearLayout).setVisibility(View.GONE);
		findViewById(R.id.adminLinearLayout).setVisibility(View.VISIBLE);
		findViewById(R.id.connectionTextView).setVisibility(View.VISIBLE);
		//hide keyboard when admin first appears
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();
		String status = getResources().getString(R.string.available);

		// Check which radio button was clicked
		switch (view.getId()) {
			case R.id.radioAvailable:
				if (checked) {
					status = getResources().getString(R.string.available);
				}
				break;
			case R.id.radioCarpooling:
				if (checked) {
					status = getResources().getString(R.string.carpooling);
				}
				break;
			case R.id.radioFull:
				if (checked) {
					status = getResources().getString(R.string.full);
				}
				break;
			default:
		}

		if (isConnected) {
			parkingRef.setValue(status, new Firebase.CompletionListener() {
				@Override
				public void onComplete(FirebaseError error, Firebase arg1) {
					if (error == null) {
						Toast.makeText(getApplicationContext(), "Data is saved", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
}