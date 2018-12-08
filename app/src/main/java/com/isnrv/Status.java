package com.isnrv;

import com.firebase.client.Config;
import com.firebase.client.DataSnapshot;
import com.firebase.client.EventTarget;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
/**
 * This class updates the parking status based on data available in Firebase database
 * @author Yasir
 *
 */
public class Status{
	private TextView parkingTextView;
	private TextView connectionTextView;
	private View parkingRectangle;
	private Firebase parkingRef;
	private Firebase connectedRef;
	private String status;
	private boolean isConnected;
	private boolean isVisible;
	private Context context;
	
	public Status(Context context){
		this.context = context;
		status = context.getResources().getString(R.string.connecting);

		parkingRef = new Firebase("https://isnrvhub.firebaseio.com/parking");
		connectedRef = new Firebase("https://isnrvhub.firebaseio.com/.info/connected");
		isConnected = false;
		setCallbacks();
	}
	
	public void isAppVisible(Boolean isVisible){
		this.isVisible = isVisible;
	}
	
	private void setCallbacks(){
		//callback to check connection status
		connectedRef.addValueEventListener(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 isConnected = snapshot.getValue(Boolean.class);
		    	 updateStatus();
		     }
		     @Override
		     public void onCancelled(FirebaseError error) {
		     }

		});
		//callback to update parking status
		parkingRef.addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snap) {
		    	//System.out.println(snap.getName() + " -> " + snap.getValue());
		        status = snap.getValue().toString();
		        notifyUser();
		        updateStatus();
		    }
			@Override
			public void onCancelled(FirebaseError error) {
				// TODO Auto-generated method stub			
			}
		});
	}
	
	//create a view for the status page and update status
	public void setGUI(View view){
		if(view!=null){
			parkingTextView = (TextView) view.findViewById(R.id.parkingTextView);
			connectionTextView = (TextView) view.findViewById(R.id.connectionTextView);
			parkingRectangle = view.findViewById(R.id.parkingRectangle);
			updateStatus();
		}
	}
	
	public void notifyUser(){
		Resources res = context.getResources();
		//make sure activity is visible
		if(isVisible && parkingTextView!=null){
			//if status is not set to the deafult, notify user of status change
			if(parkingTextView.getText().toString() != res.getString(R.string.connecting)){
				//check if vibration is enabled
				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vibrateConfig", true)){
					Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
				   	v.vibrate(800);
				}
				//check if sound is enabled
				if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("soundConfig", true)){
					try {
					    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					    Ringtone r = RingtoneManager.getRingtone(context, notification);
					    r.play();
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}
			}
		}
	}
	public void updateStatus(){
		Resources res = context.getResources();
		
		if(parkingTextView!=null){
			//update statue text and change color of statue rectangle background
			parkingTextView.setText(status);
			GradientDrawable myGrad = (GradientDrawable)parkingRectangle.getBackground();
			//Available
			if(status.equals(res.getString(R.string.available)))
			{
				myGrad.setColor(res.getColor(R.color.green));
				myGrad.setStroke(2, res.getColor(R.color.greenStroke));
			}
			//Carpooling
			else if(status.equals(res.getString(R.string.carpooling)))
			{
				myGrad.setColor(res.getColor(R.color.yellow));
				myGrad.setStroke(2, res.getColor(R.color.yellowStorke));
			} 
			//Full
			else if(status.equals(res.getString(R.string.full)))
			{
				myGrad.setColor(res.getColor(R.color.red));
				myGrad.setStroke(2, res.getColor(R.color.redStroke));
			} 
			//Default
			else
			{
				myGrad.setColor(res.getColor(R.color.white));
				myGrad.setStroke(2, res.getColor(R.color.blueStroke));
			}
		}
		//update connection status field
		if(connectionTextView!=null){
			if(isConnected){
				connectionTextView.setText(res.getString(R.string.connected));
			}
			else{
				connectionTextView.setText(res.getString(R.string.disconnected));
			}
		}
	}
	
}
