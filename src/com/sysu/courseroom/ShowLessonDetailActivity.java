package com.sysu.courseroom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sysu.courseroom.ShowClassroomActivity.SortByIntervalTime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class ShowLessonDetailActivity extends ActionBarActivity {
	TextView showLessonName;
	TextView showType;
	TextView showCredit;
	TextView showCollege;
	TextView showTeacherName;
	TextView showTime;
	TextView showLike;
	TextView showDislike;
	EditText showCommentContent;
	Button back;
	Button addComment;
	Button like;
	Button dislike;
	TabHost tabHost;
	ListView mListView;
	
	List<Map<String, String>> mDataList = new ArrayList<Map<String, String>>();
	ArrayList<Comment> comments = new ArrayList<Comment>();
	ArrayList<Comment> remoteComments = new ArrayList<Comment>();
	SimpleAdapter mSimpleAdapter;
	CourseRoomDAO courseRoomDAO = CourseRoomDAO.getInstance(this);
	RemoteDB remoteDB = new RemoteDB();
	Handler handler = new Handler();
	Runnable r;
	boolean reFresh = false;
	
	String id;
	int good;
	int bad;
	int quality;
	
	String lessonNameQuery;
	String collegeQuery;
	String typeQuery;
	String teacherNameQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_lesson_detail);
		Bundle bundle = this.getIntent().getExtras();

		id = bundle.getString("id");
		
		new Thread( new Runnable() {
				@Override
				public void run() {
					remoteDB.connect();
					CourseRoomDAO courseRoomDAO_ = CourseRoomDAO.getInstance(ShowLessonDetailActivity.this);
					
					if(remoteDB.isConnected()) {
						good = remoteDB.getGood(id);
						bad = remoteDB.getBad(id);
						
						String timeLatest = courseRoomDAO_.getLatestCommentTimeOfCourse(id);
						if (timeLatest == null) {
							Log.i("AA", "null");
							remoteComments = remoteDB.getComments(id, "1970-06-15 14:00");
						} else {
							remoteComments = remoteDB.getComments(id, timeLatest);
						}
						courseRoomDAO.setGood(id, good);
						courseRoomDAO.setBad(id, bad);						
						setData(id, remoteComments);
					}
					reFresh = true;
				}          
		}).start();
		
		r = new Runnable() {
			@Override
			public void run() {
				if(reFresh) {
					mSimpleAdapter.notifyDataSetChanged();
					showLike.setText(String.valueOf(good));
					showDislike.setText(String.valueOf(bad));
					handler.removeCallbacks(r);
				}
				else {
					handler.postDelayed(r, 100);
				}
			}
		};
		
		handler.post(r);
		
		like = (Button)findViewById(R.id.like);
		dislike = (Button)findViewById(R.id.dislike);
		back = (Button)findViewById(R.id.showDetailBack);
		addComment = (Button)findViewById(R.id.addComment);
		showCommentContent = (EditText)findViewById(R.id.comment);
		mListView = (ListView)findViewById(R.id.commentList);
		
		showLessonName = (TextView)findViewById(R.id.showLessonName);
		showCredit = (TextView)findViewById(R.id.showCredit);
		showType = (TextView)findViewById(R.id.showType);
		showCollege = (TextView)findViewById(R.id.showCollege);
		showTeacherName = (TextView)findViewById(R.id.showTeacherName);
		showTime = (TextView)findViewById(R.id.showTimePlace);
		showLike = (TextView)findViewById(R.id.showLike);
		showDislike = (TextView)findViewById(R.id.showDislike);
		
		String lessonName = bundle.getString("lessonName");
		String credit = bundle.getString("credit");
		String college = bundle.getString("college");
		String teacherName = bundle.getString("teacherName");
		String time = bundle.getString("time");
		String type = bundle.getString("type");
		good = Integer.parseInt(bundle.getString("good"));
		bad = Integer.parseInt(bundle.getString("bad"));
		quality = Integer.parseInt(bundle.getString("quality"));
		
		lessonNameQuery = bundle.getString("lessonNameQuery");
		collegeQuery = bundle.getString("collegeQuery");
		typeQuery = bundle.getString("typeQuery");
		teacherNameQuery = bundle.getString("teacherNameQuery");
		
		showLessonName.setText(lessonName);
		showCredit.setText(credit);
		showType.setText(type);
		showCollege.setText(college);
		showTeacherName.setText(teacherName);
		showTime.setText(time);
		showLike.setText(bundle.getString("good"));
		showDislike.setText(bundle.getString("bad"));
		
        setData(id, remoteComments);
		
		mSimpleAdapter = new SimpleAdapter(ShowLessonDetailActivity.this, mDataList,R.layout.comment_item,new String[]{"content","time"},
	    		new int[]{R.id.itemCommentContent,R.id.itemCommentTime});
	    mListView.setAdapter(mSimpleAdapter);
		
	    like.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(quality == 0) {
							Toast.makeText(ShowLessonDetailActivity.this, "You cannot like it or dislike it twice!", Toast.LENGTH_LONG).show();
						}
						else {
							quality = 0;
							courseRoomDAO.setQuality(id, quality);
							good = good + 1;
							courseRoomDAO.setGood(id, good);
							showLike.setText(String.valueOf(good));
							
							if(remoteDB.isConnected()) {
								boolean flag = false;
								while(!flag) {
									flag = remoteDB.increaseGood(id);
								}
							}
						}
					}
	    		}
	    );
	    
	    dislike.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(quality == 0) {
							Toast.makeText(ShowLessonDetailActivity.this, "You cannot like it or dislike it twice!", Toast.LENGTH_LONG).show();
						}
						else {
							quality = 0;
							courseRoomDAO.setQuality(id, quality);
							bad = bad + 1;
							courseRoomDAO.setBad(id, bad);
							showDislike.setText(String.valueOf(bad));
							
							if(remoteDB.isConnected()) {
								boolean flag = false;
								while(!flag) {
									flag = remoteDB.increaseBad(id);
								}
							}
						}
					}
	    		}
	    );
	    
		back.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("lessonName", lessonNameQuery);
						intent.putExtra("college", collegeQuery);
						intent.putExtra("type", typeQuery);
						intent.putExtra("teacherName", teacherNameQuery);
						intent.setClass(ShowLessonDetailActivity.this, ShowLessonActivity.class);
						startActivity(intent);
					}
				}
		);
		
		addComment.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String content = showCommentContent.getText().toString();
						if(content.equals("")) {
							Toast.makeText(ShowLessonDetailActivity.this, "Please don't make an empty comment!", Toast.LENGTH_LONG).show();
						}
						else {
							if(content.length() > 120) {
								Toast.makeText(ShowLessonDetailActivity.this, "Your comment is too long!", Toast.LENGTH_LONG).show();
							}
							else {
								Calendar cal = Calendar.getInstance(); 
						        cal.setTimeInMillis(System.currentTimeMillis());
								String time = String.format("%d-%02d-%02d %02d:%02d",  cal.get(Calendar.YEAR),  cal.get(Calendar.MONTH), 
										cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
								
								Comment comment = new Comment();
								comment.id = id;
								comment.time = time;
								comment.content = content;
								courseRoomDAO.addComment(comment);
								
								if(remoteDB.isConnected()) {
									boolean flag = false;
									while(!flag) {
										flag = remoteDB.addComment(comment);
									}
									
								}
								
								setData(id, remoteComments);
								showCommentContent.setText("");
								mSimpleAdapter.notifyDataSetChanged();
							}
						}
					}
				}
		);
		
		tabHost = (TabHost)findViewById(R.id.tabhost);
		tabHost = (TabHost)findViewById(R.id.tabhost);
		tabHost.setup();
		tabHost.setOnTabChangedListener(
			new OnTabChangeListener() {

			        @Override
                    public void onTabChanged(String tabId) {
				    tabHost.clearFocus();
			}
		});
		tabHost.addTab(tabHost.newTabSpec("OneTab")   
	                .setIndicator("Information")   
	                .setContent(R.id.tab1));
	        
		tabHost.addTab(tabHost.newTabSpec("TwoTab")   
	                .setIndicator("Comments")   
	                .setContent(R.id.tab2));
		tabHost.setCurrentTab(0);
	}
	
	void setData(String idTmp, ArrayList<Comment> reComments) {
		comments = courseRoomDAO.queryComments(idTmp);
		mDataList.clear();
		
		if (reComments != null) {
			for(Comment comment: reComments) {
				if(!comments.contains(comment)) {
					comments.add(comment);
					courseRoomDAO.addComment(comment);
				}
			}
		}
		
		for(Comment comment: comments){
			Map<String, String> mMap = new HashMap<String, String>();
			mMap.put("content", comment.content);
			mMap.put("time", comment.time);
			mDataList.add(mMap);
		}
		
		Collections.sort(mDataList, new SortByTime());
	}
	
	class SortByTime implements Comparator {
		  public int compare(Object o1, Object o2) {
			  HashMap<String, String> s1 = (HashMap<String, String>) o1;
			  HashMap<String, String> s2 = (HashMap<String, String>) o2;
			  long time1 = timeToLong((String)s1.get("time"));
			  long time2 = timeToLong((String)s2.get("time"));
			  if (time1 < time2) {
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
	
	public static long timeToLong(String date) {
		  try {
		    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		    return sf.parse(date).getTime();
		  } catch (ParseException e) {
		   e.printStackTrace();
		  }
		  return -1;
	}

}
