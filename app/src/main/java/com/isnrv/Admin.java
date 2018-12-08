package com.isnrv;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.firebase.simplelogin.User;
import com.firebase.simplelogin.enums.Error;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/**
 * This class provides access to Firebase database to the admin. It allows admin to change
 * parking status
 * @author Yasir
 *
 */
public class Admin extends Activity{

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
        actionBar.setCustomView(R.layout.custom_actionbar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
		
		isnrvRef = new Firebase("https://isnrvhub.firebaseio.com");
		parkingRef = new Firebase("https://isnrvhub.firebaseio.com/parking");
		connectedRef = new Firebase("https://isnrvhub.firebaseio.com/.info/connected");
		//restore fields of email and password if they are defined
		emailEditText = (EditText)findViewById(R.id.emailEditText);
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		emailEditText.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", ""));
		passwordEditText.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("password", ""));
		
		isConnected = false;
		setCallbacks();
		
		//check if already logged in
		if( PreferenceManager.getDefaultSharedPreferences(this).getBoolean("loggedIn", false)){
			login();
		}
		
	}
	
	private void setCallbacks(){
		final TextView connectionTextView = (TextView) findViewById(R.id.connectionTextView);
		Button loginButton = (Button) findViewById(R.id.loginButton);
		//callback to check connection status
		connectedRef.addValueEventListener(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 isConnected = snapshot.getValue(Boolean.class);
		    	 RadioGroup radioGroupStatus = (RadioGroup)findViewById(R.id.radioGroupStatus);
		    	 
		    	 if(isConnected){
					connectionTextView.setText(getResources().getString(R.string.connected));
					for(int i = 0; i < radioGroupStatus.getChildCount(); i++){
						radioGroupStatus.getChildAt(i).setEnabled(true);
				    }
				}
				else{
					connectionTextView.setText(getResources().getString(R.string.disconnected));
					for(int i = 0; i < radioGroupStatus.getChildCount(); i++){
						((RadioButton)radioGroupStatus.getChildAt(i)).setEnabled(false);
				    }
				}
		     }
		     @Override
		     public void onCancelled(FirebaseError error) {
		     }

		});
		//callback to update parking status once
		parkingRef.addListenerForSingleValueEvent(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snap) {
		    	//set radio button to current state
		    	RadioGroup radioGroupStatus = (RadioGroup)findViewById(R.id.radioGroupStatus);
		    	for(int i = 0; i < radioGroupStatus.getChildCount(); i++){
		    		RadioButton rb = (RadioButton)radioGroupStatus.getChildAt(i); 
		    		if((snap.getValue()).equals(rb.getText().toString())){
		    			rb.setChecked(true);
		    		}
			    }
		    }
			@Override
			public void onCancelled(FirebaseError error) {
				// TODO Auto-generated method stub			
			}
		});
		
		//login button
		loginButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				final String email = emailEditText.getText().toString();
				final String password = passwordEditText.getText().toString();
				authClient = new SimpleLogin(isnrvRef);
				authClient.loginWithEmail(email, password, new SimpleLoginAuthenticatedHandler() {
					  public void authenticated(Error error, User user) {
					    if(error != null) {
					      // There was an error logging into this account
					    	Log.d("TEST", "couldn't log in");
					    	((TextView)findViewById(R.id.incorrectTextView)).setVisibility(View.VISIBLE);
					    }
					    else {
					    	//We are now logged in
					    	Log.d("TEST", "logged in");
					    	//remember username and password for later login
					    	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
					        editor.putString("email", email);
					        editor.putString("password", password);
					        editor.putBoolean("loggedIn", true);
					        editor.commit();
					        login();
					    }
					  }
					});
			}});
	}
	
	//login successful
	public void login(){
		findViewById(R.id.loginLinearLayout).setVisibility(View.GONE);
        findViewById(R.id.adminLinearLayout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.connectionTextView)).setVisibility(View.VISIBLE);
        //hide keyboard when admin first appears
        InputMethodManager imm = (InputMethodManager)getSystemService(
        	      getApplicationContext().INPUT_METHOD_SERVICE);
        	imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    String status = getResources().getString(R.string.available);
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radioAvailable:
	            if (checked){
	            	status = getResources().getString(R.string.available);
	            }
	            break;
	        case R.id.radioCarpooling:
	            if (checked){
	            	status = getResources().getString(R.string.carpooling);
	            }
	            break;
	        case R.id.radioFull:
	            if (checked){
	            	status = getResources().getString(R.string.full);
	            }
	            break;
	    }
	    
	    if(isConnected)
			parkingRef.setValue(status,new Firebase.CompletionListener() {
			    @Override
			    public void onComplete(FirebaseError error, Firebase arg1) {
			        if (error == null) {
			        	Toast.makeText(getApplicationContext(), "Data is saved", Toast.LENGTH_SHORT).show();
			        }
			    }
			});
	}
	
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
