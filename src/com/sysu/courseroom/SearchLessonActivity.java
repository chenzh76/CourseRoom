package com.sysu.courseroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SearchLessonActivity extends ActionBarActivity {
	Button cancel;
	Button confirm;
	EditText lessonName;
	EditText college;
	EditText type;
	EditText teacherName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_lesson);
		
		lessonName = (EditText)findViewById(R.id.lessonName);
		college = (EditText)findViewById(R.id.college);
		type = (EditText)findViewById(R.id.type);
		teacherName = (EditText)findViewById(R.id.teacherName);
		
		cancel = (Button)findViewById(R.id.lessonCancel);
		cancel.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(SearchLessonActivity.this, MainActivity.class);
						startActivity(intent);
					}
				}
		);
		
		confirm = (Button)findViewById(R.id.lessonConfirm);
		confirm.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						
						intent.putExtra("lessonName", lessonName.getText().toString());
						intent.putExtra("college", college.getText().toString());
						intent.putExtra("type", type.getText().toString());
						intent.putExtra("teacherName", teacherName.getText().toString());
						intent.setClass(SearchLessonActivity.this, ShowLessonActivity.class);
						
						startActivity(intent);
					}
				}
		);
	}
}
