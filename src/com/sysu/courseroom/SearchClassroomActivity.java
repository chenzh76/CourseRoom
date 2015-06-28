package com.sysu.courseroom;

import java.util.Calendar;






import android.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.widget.Toast;

public class SearchClassroomActivity extends ActionBarActivity implements OnTouchListener {
	EditText time;
	Button confirm;
	Button cancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchclassroom);
		time = (EditText)findViewById(R.id.Time);
		time.setOnTouchListener(this);
		
		cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						finish();
					}
				}
		);
		
		confirm = (Button)findViewById(R.id.confirm);
		confirm.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						String timeString = time.getText().toString();
						Intent intent = new Intent();
						if(timeString.equals("")) {
							Toast.makeText(SearchClassroomActivity.this, "You haven't selected a time!", Toast.LENGTH_LONG).show();
						}
						else if(isInvalid(timeString)) {
							Toast.makeText(SearchClassroomActivity.this, "You have selected an invalid time!", Toast.LENGTH_LONG).show();
						}
						else {
							intent.putExtra("time", timeString);
							intent.setClass(SearchClassroomActivity.this, ShowClassroomActivity.class);
							startActivity(intent);
						}
					}
				}
		);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) { 
			   
	        AlertDialog.Builder builder = new AlertDialog.Builder(this); 
	        View view = View.inflate(this, R.layout.time_dialog, null);
	        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
	        final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.timePicker); 
            builder.setView(view);
            
	        Calendar cal = Calendar.getInstance(); 
	        cal.setTimeInMillis(System.currentTimeMillis());
	        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
	        datePicker.setCalendarViewShown(false);
	        
	        timePicker.setIs24HourView(true);
	        timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
	        timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
	   
	        if (v.getId() == R.id.Time) { 
                final int inType = time.getInputType(); 
                time.setInputType(InputType.TYPE_NULL); 
                time.onTouchEvent(event); 
                time.setInputType(inType); 
                time.setSelection(time.getText().length()); 
	                   
	            builder.setTitle("Select a Time"); 
	            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() { 
	   
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                    StringBuffer sb = new StringBuffer();
	                    sb.append(String.format("%d-%02d-%02d %02d:%02d",  
                                datePicker.getYear(),  
                                datePicker.getMonth() + 1, 
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute()));
	                        
	                    time.setText(sb);
	                    time.requestFocus();
	                    
	                    dialog.cancel();
	                } 
	            }); 
	                   
	        }
	            
	        builder.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {  
	           	public void onClick(DialogInterface dialog, int whichButton) {   
	           	}  
	        });  
	               
	        Dialog dialog = builder.create(); 
	        dialog.show(); 
	    } 
	    return true; 
	}
	
	boolean isInvalid(String timeStr) {
		String timeStr1 = timeStr.split(" ")[1];
		int hour = Integer.parseInt(timeStr1.split(":")[0]);
		int min = Integer.parseInt(timeStr1.split(":")[1]);
		if(hour < 8)
			return true;
		else if(hour >= 22)
			return true;
		else if(hour == 21 && min >= 35)
			return true;
		else
			return false;
	}
}
