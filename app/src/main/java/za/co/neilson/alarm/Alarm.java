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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import za.co.neilson.alarm.alert.AlarmAlertBroadcastReciever;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
public class Alarm implements Serializable {

	public enum Howto{
		IMAGE,
		MATH;

		@Override
		public String toString() {
			switch(this.ordinal()){
				case 0:
					return "이미지인식";
				case 1:
					return "사칙연산";
			}
			return super.toString();
		}
	}

	public enum Day{
		SUNDAY,
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY;

		@Override
		public String toString() {
			switch(this.ordinal()){
				case 0:
					return "일요일";
				case 1:
					return "월요일";
				case 2:
					return "화요일";
				case 3:
					return "수요일";
				case 4:
					return "목요일";
				case 5:
					return "금요일";
				case 6:
					return "토요일";
			}
			return super.toString();
		}

	}
	private static final long serialVersionUID = 8699489847426803789L;
	private int id;
	private Boolean alarmActive = true;
	private Calendar alarmTime = Calendar.getInstance();
	private Day[] days = {Day.MONDAY,Day.TUESDAY,Day.WEDNESDAY,Day.THURSDAY,Day.FRIDAY,Day.SATURDAY,Day.SUNDAY};
	private String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
	private Boolean vibrate = true;
	private String alarmName = "알람이름을 입력하세요";


	private Howto howto = Howto.IMAGE;

	public Alarm() {

	}

	//체크박스 체크 확인 값
	public Boolean getAlarmActive() {
		return alarmActive;
	}
	public void setAlarmActive(Boolean alarmActive) {
		this.alarmActive = alarmActive;
	}


	//알람 시간 등록 (요일, 날짜)
	public Calendar getAlarmTime() {
		//je
		//알람 시간이 현재 시간 이전이라면 다음날로 알람 날짜를 바꿈
		if (alarmTime.before(Calendar.getInstance()))
			alarmTime.add(Calendar.DAY_OF_MONTH, 1);
		//DAY_OF_WEEK 현재 요일  DAY_OF_MONTH 현재날짜

		//je
		//알람을 지정해둔 요일이 아니라면 계속해서 알람 날짜를 다음날로 바꿈
		while(!Arrays.asList(getDays()).contains(Day.values()[alarmTime.get(Calendar.DAY_OF_WEEK)-1])){
			alarmTime.add(Calendar.DAY_OF_MONTH, 1);
		}
		return alarmTime;
	}


	//알람시간 스트링 HH:MM
	public String getAlarmTimeString() {

		String time = "";
		if (alarmTime.get(Calendar.HOUR_OF_DAY) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.HOUR_OF_DAY));
		time += ":";

		if (alarmTime.get(Calendar.MINUTE) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.MINUTE));

		return time;
	}


	public void setAlarmTime(Calendar alarmTime) {
		this.alarmTime = alarmTime;
	}


	public void setAlarmTime(String alarmTime) {

		String[] timePieces = alarmTime.split(":");

		Calendar newAlarmTime = Calendar.getInstance();
		newAlarmTime.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timePieces[0]));
		newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
		newAlarmTime.set(Calendar.SECOND, 0);
		setAlarmTime(newAlarmTime);
	}

	/**
	 * @return the repeatDays
	 */
	public Day[] getDays() {
		return days;
	}

	/**
	 * @param set
	 *            the repeatDays to set
	 */
	public void setDays(Day[] days) {
		this.days = days;
	}

	public void addDay(Day day){
		boolean contains = false;
		for(Day d : getDays())
			if(d.equals(day))
				contains = true;
		if(!contains){
			List<Day> result = new LinkedList<Day>();
			for(Day d : getDays())
				result.add(d);
			result.add(day);
			setDays(result.toArray(new Day[result.size()]));
		}
	}

	public void removeDay(Day day) {

		List<Day> result = new LinkedList<Day>();
		for(Day d : getDays())
			if(!d.equals(day))
				result.add(d);
		setDays(result.toArray(new Day[result.size()]));
	}

	/**
	 * @return the alarmTonePath
	 */
	public String getAlarmTonePath() {
		return alarmTonePath;
	}

	/**
	 * @param alarmTonePath the alarmTonePath to set
	 */
	public void setAlarmTonePath(String alarmTonePath) {
		this.alarmTonePath = alarmTonePath;
	}

	/**
	 * @return the vibrate
	 */
	public Boolean getVibrate() {
		return vibrate;
	}

	/**
	 * @param vibrate
	 *            the vibrate to set
	 */
	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
	}

	/**
	 * @return the alarmName
	 */
	public String getAlarmName() {
		return alarmName;
	}

	/**
	 * @param alarmName
	 *            the alarmName to set
	 */
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public Howto getHowto() {
		return howto;
	}

	public void setHowto(Howto howto) {
		this.howto = howto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	//요일명 가져오기
	public String getRepeatDaysString() {
		StringBuilder daysStringBuilder = new StringBuilder();
		if(getDays().length == Day.values().length){
			daysStringBuilder.append("모든요일");
		}else{
			Arrays.sort(getDays(), new Comparator<Day>() {
				@Override
				public int compare(Day lhs, Day rhs) {

					return lhs.ordinal() - rhs.ordinal();
				}
			});
			for(Day d : getDays()){
				switch(d){
					//case TUESDAY:
					//case THURSDAY:
//					daysStringBuilder.append(d.toString().substring(0, 4));
//					break;
					default:
						daysStringBuilder.append(d.toString().substring(0, 3));
						break;
				}
				daysStringBuilder.append(',');
			}
			daysStringBuilder.setLength(daysStringBuilder.length()-1);
		}

		return daysStringBuilder.toString();
	}

	//알람등록
	public void schedule(Context context) {
		setAlarmActive(true);

		Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
		myIntent.putExtra("alarm", this);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		//sj
		//알람시간 정확하게 울리게하는 함수 (API23이상에서 사용가능)
		alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getAlarmTime().getTimeInMillis(), pendingIntent);
	}

	///시간 계산 스트링 변환함수 (몇 분 후에 알람이 울리는지 계산)
	public String getTimeUntilNextAlarmMessage(){
		long timeDifference = getAlarmTime().getTimeInMillis() - System.currentTimeMillis();
		long days = timeDifference / (1000 * 60 * 60 * 24);
		long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
		long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
		long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
		String alert = " ";
		if (days > 0) {
			alert += String.format(
					"%d 일, %d 시간, %d 분, %d 초 후에 알람이 울립니다", days, hours, minutes, seconds);
		} else {
			if (hours > 0) {
				alert += String.format("%d 시간, %d 분, %d 초 후에 알람이 울립니다", hours, minutes, seconds);
			} else {
				if (minutes > 0) {
					alert += String.format("%d 분, %d 초 후에 알람이 울립니다", minutes, seconds);
				} else {
					alert += String.format("%d 초 후에 알람이 울립니다", seconds);
				}
			}
		}
		return alert;
	}
}