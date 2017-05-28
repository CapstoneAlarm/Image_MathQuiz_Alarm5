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
package za.co.neilson.alarm.alert;

import za.co.neilson.alarm.Alarm;
import za.co.neilson.alarm.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.HapticFeedbackConstants;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmAlertActivity extends Activity {

	private Alarm alarm;
	private MediaPlayer mediaPlayer;

	private Vibrator vibrator;

	public boolean alarmActive;

	public static Activity alarm_alert_activity;


	private boolean authenticated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(vibrateReceiver, filter);


		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); //Lock 화면 위로 실행 , Keyguard 해지
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //Screen을 켜진 상태로 유지, Screen On

		Bundle bundle = this.getIntent().getExtras();
		alarm = (Alarm) bundle.getSerializable("alarm");

		this.setTitle(alarm.getAlarmName());


		boolean check_flag = false;

		alarm_alert_activity = AlarmAlertActivity.this;

		/*
		try {
			Intent intent2 = getIntent();
			check_flag = intent2.getExtras().getBoolean("flag");
			Toast.makeText(this,String.valueOf(check_flag),Toast.LENGTH_SHORT).show();


		}catch (Exception e){

		}
		*/

		//폰 상태 모니터링
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		//통화 중일 경우 벨소리 멈춤, 통화 종료후에 다시 울림
		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
					case TelephonyManager.CALL_STATE_RINGING:
						Log.d(getClass().getSimpleName(), "Incoming call: "
								+ incomingNumber);
						try {
							mediaPlayer.pause();
						} catch (IllegalStateException e) {

						}
						break;
					case TelephonyManager.CALL_STATE_IDLE:
						Log.d(getClass().getSimpleName(), "Call State Idle");
						try {
							mediaPlayer.start();
						} catch (IllegalStateException e) {

						}
						break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		//모니터링할 이벤트(통화상태)를 리스너에 등록
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();

		startAlarm();


		//해제방법에 따라 이미지인식 or 사칙연산 실행
		switch (alarm.getHowto()) {
			case IMAGE:
				//imageProblem = new ImageProblem();

				Intent intent1 = new Intent(this, ImageProblem.class);
				intent1.putExtra("it_alarmActive", alarmActive);
				startActivity(intent1);

				/*
				try {
					Intent intent2 = getIntent();
					check_flag = intent2.getExtras().getBoolean("flag");
					Toast.makeText(this,String.valueOf(check_flag),Toast.LENGTH_SHORT).show();


				}catch (Exception e){

				}
				*/



				/*
				if(check_flag=true) {
					//알람 끄기
					alarmActive = false;
					if (vibrator != null)
						vibrator.cancel();
					try {
						mediaPlayer.stop();
					} catch (IllegalStateException ise) {

					}
					try {
						mediaPlayer.release();
					} catch (Exception e) {

					}
					this.finish();
				}
				*/


				//boolean check_flag = isAnswerCorrect2();
				/*
				if(check_flag==true){
					this.finish();
				}
				*/
				break;


			case MATH:
				Intent intent2 = new Intent(this, MathProblem.class);
				intent2.putExtra("it_alarmActive", true);
				startActivity(intent2);
				break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void startAlarm() {

		if (alarm.getAlarmTonePath() != "") {
			mediaPlayer = new MediaPlayer();
			if (alarm.getVibrate()) {
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				long[] pattern = {1000, 200, 200, 200};
				vibrator.vibrate(pattern, 0);
			}
			try {
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.setDataSource(this,
						Uri.parse(alarm.getAlarmTonePath()));
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.setLooping(true);
				mediaPlayer.prepare();
				mediaPlayer.start();
				alarmActive = true;

			} catch (Exception e) {
				mediaPlayer.release(); //mediaPlayer 객체 완전히 제거(재사용 불가능)
				alarmActive = false;
			}
		}

	}

	public BroadcastReceiver vibrateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			long[] pattern = {100, 300, 100, 700, 300, 2000};


			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				if (authenticated == false)
					vibrator.vibrate(pattern, 0); //0:무한반복
				else
					vibrator.cancel();


			}
		}
	};


	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		StaticWakeLock.lockOff(this);
	}

	@Override
	protected void onDestroy() {
		try {
			alarmActive = false;
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.release();
		} catch (Exception e) {

		}
		super.onDestroy();
		unregisterReceiver(vibrateReceiver);
	}
}