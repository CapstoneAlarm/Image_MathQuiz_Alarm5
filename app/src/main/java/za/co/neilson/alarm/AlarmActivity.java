/* Copyright 2014 Sheldon Neilson www.neilson.co.za
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * I modified part of the contents in Korean.
 * In addition, the menu was added in the scope without erasing the contents.
 * The source of this source is "https://github.com/SheldonNeilson/Android-Alarm-Clock.git"
 */
package za.co.neilson.alarm;

import java.util.List;

import za.co.neilson.alarm.database.Database;
import za.co.neilson.alarm.preferences.AlarmPreferencesActivity;
import za.co.neilson.alarm.R;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class AlarmActivity extends BaseActivity {

	ListView mathAlarmListView;	//리스트뷰
	AlarmListAdapter alarmListAdapter;	//알람 리스트 어댑터

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alarm_activity);

		//리스트뷰생성
		mathAlarmListView = (ListView) findViewById(android.R.id.list);

		//길게 클릭하는거 인식 -> 삭제
		mathAlarmListView.setLongClickable(true);
		mathAlarmListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
				final Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Builder dialog = new AlertDialog.Builder(AlarmActivity.this);
				dialog.setTitle("삭제 확인");
				dialog.setMessage("알람을 삭제하시겠습니까?");
				dialog.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Database.init(AlarmActivity.this);
						Database.deleteEntry(alarm);
						AlarmActivity.this.callMathAlarmScheduleService();

						updateAlarmList();
					}
				});
				dialog.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				dialog.show();

				return true;
			}
		});

		callMathAlarmScheduleService();

		alarmListAdapter = new AlarmListAdapter(this);
		this.mathAlarmListView.setAdapter(alarmListAdapter);

		//리스트뷰 클릭시
		mathAlarmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

				//알람 아이템 값 가져오기
				Alarm alarm = (Alarm) alarmListAdapter.getItem(position);

				//인텐트로 아이템올리기
				Intent intent = new Intent(AlarmActivity.this, AlarmPreferencesActivity.class);
				intent.putExtra("alarm", alarm);
				startActivity(intent);
			}

		});
	}

	//액션바 메뉴생성 - 저장, 삭제 버튼 안보이게
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_item_save).setVisible(false);
		menu.findItem(R.id.menu_item_delete).setVisible(false);
		return result;
	}

	@Override
	protected void onPause() {
		// setListAdapter(null);
		Database.deactivate();
		super.onPause();
	}

	//화면 재생성
	@Override
	protected void onResume() {
		super.onResume();
		updateAlarmList();
	}

	//리스트뷰 재생성
	public void updateAlarmList(){
		Database.init(AlarmActivity.this);
		final List<Alarm> alarms = Database.getAll();	//알람리스트에 DB내용 가져오기
		alarmListAdapter.setMathAlarms(alarms);

		runOnUiThread(new Runnable() {
			public void run() {
				// reload content
				AlarmActivity.this.alarmListAdapter.notifyDataSetChanged();

				//알람이 있을 시 "알람을 추가해주세요" 안 보이게 함
				if(alarms.size() > 0){
					findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
				}else{
					findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
				}
			}
		});
	}

	//체크박스 클릭
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.checkBox_alarm_active) {
			CheckBox checkBox = (CheckBox) v;

			//알람 가져오기
			Alarm alarm = (Alarm) alarmListAdapter.getItem((Integer) checkBox.getTag());

			//체크박스값 설정
			alarm.setAlarmActive(checkBox.isChecked());

			//DB갱신
			Database.update(alarm);
			AlarmActivity.this.callMathAlarmScheduleService();
			if (checkBox.isChecked()) {
				Toast.makeText(AlarmActivity.this, alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}

}