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

import java.lang.reflect.Field;

import za.co.neilson.alarm.preferences.AlarmPreferencesActivity;
import za.co.neilson.alarm.service.AlarmServiceBroadcastReciever;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

public abstract class BaseActivity  extends AppCompatActivity implements android.view.View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	//액션바 메뉴버튼 클릭시
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String url = null;
		Intent intent = null;
		switch (item.getItemId()) {
			case R.id.menu_item_new:
				Intent newAlarmIntent = new Intent(this, AlarmPreferencesActivity.class);
				startActivity(newAlarmIntent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	//알람 서비스 인텐트 등록
	protected void callMathAlarmScheduleService() {
		Intent mathAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
		sendBroadcast(mathAlarmServiceIntent, null);
	}
}
