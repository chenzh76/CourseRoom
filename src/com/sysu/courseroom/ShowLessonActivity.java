package com.sysu.courseroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sysu.courseroom.ShowClassroomActivity.SortByIntervalTime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShowLessonActivity extends ActionBarActivity {
	List<Map<String, String>> mDataList = new ArrayList<Map<String, String>>();
	ArrayList<Course> lessons;
	SimpleAdapter mSimpleAdapter;
	String lessonNameQuery;
	String collegeQuery;
	String typeQuery;
	String teacherNameQuery;
	
	Button back;
	ListView mListView;
	CourseRoomDAO courseRoomDAO = CourseRoomDAO.getInstance(this);
	RemoteDB remoteDB = new RemoteDB();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_lesson);
		
		String lessonName;
		String college;
		String type;
		String teacherName;
		
		mListView = (ListView)findViewById(R.id.lessonList);
		
		Bundle bundle = this.getIntent().getExtras();
		lessonName = bundle.getString("lessonName");
		college = bundle.getString("college");
		type = bundle.getString("type");
		teacherName = bundle.getString("teacherName");
		
		lessonNameQuery = lessonName;
		collegeQuery = college;
		typeQuery = type;
		teacherNameQuery = teacherName;
		
		setData(lessonName, college, type, teacherName);
		Collections.sort(mDataList, new SortByGood());
		
		mSimpleAdapter = new SimpleAdapter(ShowLessonActivity.this, mDataList,R.layout.lesson_item,new String[]{"lessonName","credit","type","teacherName","good","bad"},
	    		new int[]{R.id.itemLessonName,R.id.itemCredit, R.id.itemType, R.id.itemTeacherName, R.id.itemLike, R.id.itemDislike});
	    mListView.setAdapter(mSimpleAdapter);
	    
	    mListView.setOnItemClickListener(
        		new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						HashMap<String, String> tmp = (HashMap<String, String>) mDataList.get(position);
						
						Bundle bundle = new Bundle();
						bundle.putString("lessonName", tmp.get("lessonName"));
						bundle.putString("credit", tmp.get("credit"));
						bundle.putString("college", tmp.get("college"));
						bundle.putString("id", tmp.get("id"));
						bundle.putString("teacherName", tmp.get("teacherName"));
						bundle.putString("time", tmp.get("time"));
						bundle.putString("type", tmp.get("type"));
						bundle.putString("good", tmp.get("good"));
						bundle.putString("bad", tmp.get("bad"));
						bundle.putString("quality", tmp.get("quality"));
						
						bundle.putString("lessonNameQuery", lessonNameQuery);
						bundle.putString("collegeQuery", collegeQuery);
						bundle.putString("typeQuery", typeQuery);
						bundle.putString("teacherNameQuery", teacherNameQuery);
						
						Intent intent = new Intent();
						intent.putExtras(bundle);
						intent.setClass(ShowLessonActivity.this, ShowLessonDetailActivity.class);
						startActivity(intent);
					}
        		}
		);
		
		back = (Button)findViewById(R.id.showLessonBack);
		back.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(ShowLessonActivity.this, SearchLessonActivity.class);
						startActivity(intent);
					}
				}
		);
	}
	
	void setData(String lessonNameStr, String collegeStr, String typeStr, String teacherNameStr) {
		if(lessonNameStr.equals("")) {
			lessonNameStr = null;
		}
		if(collegeStr.equals("")) {
			collegeStr = null;
		}
		if(typeStr.equals("")) {
			typeStr = null;
		}
		if(teacherNameStr.equals("")) {
			teacherNameStr = null;
		}
		lessons = courseRoomDAO.queryCourse(teacherNameStr, collegeStr, typeStr, lessonNameStr);
		
		for(Course course: lessons){
			Map<String, String> mMap = new HashMap<String, String>();
			mMap.put("lessonName", course.name);
			mMap.put("credit", String.valueOf(course.credit));
			mMap.put("college", course.college);
			mMap.put("id", course.id);
			mMap.put("teacherName", course.teacher.replace("-", ","));
			mMap.put("time", course.time.replace("&", ","));
			mMap.put("type", course.type);
			mMap.put("good", String.valueOf(course.good));
			mMap.put("bad", String.valueOf(course.bad));
			mMap.put("quality", String.valueOf(course.quality));
			mDataList.add(mMap);
		}
	}
	
	class SortByGood implements Comparator {
		  public int compare(Object o1, Object o2) {
			  HashMap<String, String> s1 = (HashMap<String, String>) o1;
			  HashMap<String, String> s2 = (HashMap<String, String>) o2;
			  int time1 = Integer.parseInt((String)s1.get("good"));
			  int time2 = Integer.parseInt((String)s2.get("good"));
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
