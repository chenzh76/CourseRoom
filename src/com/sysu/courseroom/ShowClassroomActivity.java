package com.sysu.courseroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShowClassroomActivity extends ActionBarActivity{
	Button back;
	ArrayList<Room> emptyRooms;
	CourseRoomDAO courseRoomDAO = CourseRoomDAO.getInstance(this);
	List<Map<String, String>> mDataList = new ArrayList<Map<String, String>>();
	SimpleAdapter mSimpleAdapter;
	ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_classroom);
		
		mListView = (ListView)findViewById(R.id.emptyClassroomList);
		
		Bundle bundle = this.getIntent().getExtras();
		String timeString;
		timeString = bundle.getString("time");
		setData(timeString);
		Collections.sort(mDataList, new SortByIntervalTime());
		
	    mSimpleAdapter = new SimpleAdapter(ShowClassroomActivity.this, mDataList,R.layout.classroom_item,new String[]{"id","startTime","endTime"},
	    		new int[]{R.id.roomId,R.id.roomStartTime, R.id.roomEndTime});
	    mListView.setAdapter(mSimpleAdapter);
		
		back = (Button)findViewById(R.id.showClassroomBack);
		back.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}
		);
	}
	
	void setData(String timeStr){
		String[] dataAndTime = timeStr.split(" ");
		String[] yearMonthDay = dataAndTime[0].split("-");
		String[] hourMinute = dataAndTime[1].split(":");
		
		emptyRooms = courseRoomDAO.queryEmptyRoom(Integer.parseInt(yearMonthDay[0]),Integer.parseInt(yearMonthDay[1]),
				Integer.parseInt(yearMonthDay[2]),Integer.parseInt(hourMinute[0]),Integer.parseInt(hourMinute[1]));
	    
		for(Room room: emptyRooms){
			Map<String, String> mMap = new HashMap<String, String>();
			mMap.put("startTime", room.startTime);
			mMap.put("endTime", room.endTime);
			mMap.put("intervalTime", String.valueOf(room.intervalTime));
			mMap.put("id", room.id);
			mDataList.add(mMap);
		}
	}
	
	class SortByIntervalTime implements Comparator {
	  public int compare(Object o1, Object o2) {
		  HashMap<String, String> s1 = (HashMap<String, String>) o1;
		  HashMap<String, String> s2 = (HashMap<String, String>) o2;
		  int time1 = Integer.parseInt((String)s1.get("intervalTime"));
		  int time2 = Integer.parseInt((String)s2.get("intervalTime"));
		  if (time1 > time2) {
			  return -1;
		  }
		  else if (time1 == time2) {
			  return 0;
		  }
		  else {
			  return 1;
		  }
	  }
	}
}
