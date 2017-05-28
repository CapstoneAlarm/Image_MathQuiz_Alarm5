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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import za.co.neilson.alarm.R;

public class MathProblem extends AppCompatActivity implements View.OnClickListener {

	enum Operator {
		ADD, SUBTRACT, MULTIPLY, DIVIDE;

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			String string = null;
			switch (ordinal()) {
				case 0:
					string = "+";
					break;
				case 1:
					string = "-";
					break;
				case 2:
					string = "*";
					break;
				case 3:
					string = "/";
					break;
			}
			return string;
		}
	}

	private ArrayList<Integer> parts;
	private ArrayList<Operator> operators;
	private int answer = 0;
	private int min = 0;
	private int max = 12;
	private int numParts = 3;

	private StringBuilder answerBuilder = new StringBuilder();
	private TextView problemView;
	private TextView answerView;
	private String answerString;

	public boolean alarmActive;

	AlarmAlertActivity alarm_alert_activity = (AlarmAlertActivity)AlarmAlertActivity.alarm_alert_activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_alert);


		Intent it = getIntent();
		alarmActive = it.getExtras().getBoolean("it_alarmActive");


		Random random = new Random(System.currentTimeMillis());

		//숫자 arraylist
		parts = new ArrayList<Integer>(numParts);
		for (int i = 0; i < numParts; i++)
			parts.add(i, (Integer) random.nextInt(max - min + 1) + min);

		//연산자 arraylist
		operators = new ArrayList<MathProblem.Operator>(numParts - 1);
		for (int i = 0; i < numParts - 1; i++)
			operators.add(i, Operator.values()[random.nextInt(2) + 1]);

		ArrayList<Object> combinedParts = new ArrayList<Object>();
		for (int i = 0; i < numParts; i++) {
			combinedParts.add(parts.get(i));
			if (i < numParts - 1)
				combinedParts.add(operators.get(i));
		}


		//sj
		//받아온 연산자에 따른 계산 수행

		while(combinedParts.contains(Operator.DIVIDE) ||combinedParts.contains(Operator.MULTIPLY)
				||combinedParts.contains(Operator.ADD) ||combinedParts.contains(Operator.SUBTRACT)){

			int i = 0;
			//Operator를 object형태로 combinedParts.get(i)에 넘겨주어 연산자를 식별할 수 있게함
			while(!(combinedParts.get(i) instanceof Operator)){
				i++;
			}

			if(combinedParts.get(i) == Operator.DIVIDE){
				answer = (Integer)combinedParts.get(i-1) / (Integer)combinedParts.get(i+1);
			}else if(combinedParts.get(i) == Operator.MULTIPLY){
				answer = (Integer)combinedParts.get(i-1) * (Integer)combinedParts.get(i+1);
			}else if(combinedParts.get(i) == Operator.ADD){
				answer = (Integer)combinedParts.get(i-1) + (Integer)combinedParts.get(i+1);
			}else {	//combinedParts.get(i) == Operator.SUBTRACT
				answer = (Integer)combinedParts.get(i-1) - (Integer)combinedParts.get(i+1);
			}

			for (int r = 0; r < 2; r++)
				combinedParts.remove(i-1);
			combinedParts.set(i-1, answer);
		}

		//사칙연산의 문제와 답, 버튼 출력
		try {
			answerString = String.valueOf(this.getAnswer());

			problemView = (TextView) findViewById(R.id.textView1);
			problemView.setText(this.toString());

			answerView = (TextView) findViewById(R.id.textView2);
			answerView.setText("?");

			((Button) findViewById(R.id.Button0)).setOnClickListener(this);
			((Button) findViewById(R.id.Button1)).setOnClickListener(this);
			((Button) findViewById(R.id.Button2)).setOnClickListener(this);
			((Button) findViewById(R.id.Button3)).setOnClickListener(this);
			((Button) findViewById(R.id.Button4)).setOnClickListener(this);
			((Button) findViewById(R.id.Button5)).setOnClickListener(this);
			((Button) findViewById(R.id.Button6)).setOnClickListener(this);
			((Button) findViewById(R.id.Button7)).setOnClickListener(this);
			((Button) findViewById(R.id.Button8)).setOnClickListener(this);
			((Button) findViewById(R.id.Button9)).setOnClickListener(this);
			((Button) findViewById(R.id.Button_clear)).setOnClickListener(this);
			//((Button) findViewById(R.id.Button_decimal)).setOnClickListener(this);
			((Button) findViewById(R.id.Button_minus)).setOnClickListener(this);

		} catch(Exception e){

		}

	}

	//sj
	//문제 출력 ( a * b ) - c 이런 식으로
	@Override
	public String toString() {
		StringBuilder problemBuilder = new StringBuilder();

		problemBuilder.append("( ");
		problemBuilder.append(parts.get(0));
		problemBuilder.append(" ");
		problemBuilder.append(operators.get(0).toString());
		problemBuilder.append(" ");
		problemBuilder.append(parts.get(1));
		problemBuilder.append(" ) ");
		problemBuilder.append(operators.get(1).toString());
		problemBuilder.append(" ");
		problemBuilder.append(parts.get(2));
		problemBuilder.append(" ");

		return problemBuilder.toString();

	}

	public float getAnswer() {
		return answer;
	}


	//버튼 터치시 이벤트
	@Override
	public void onClick(View v) {


		try {
			if (!alarmActive)
				return;
			String button = (String) v.getTag();
			v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			if (button.equalsIgnoreCase("clear")) {
				if (answerBuilder.length() > 0) {
					answerBuilder.setLength(answerBuilder.length() - 1);
					answerView.setText(answerBuilder.toString());
				}
			} else if (button.equalsIgnoreCase("-")) {
				if (answerBuilder.length() == 0) {
					answerBuilder.append(button);
					answerView.setText(answerBuilder.toString());
				}
			} else {
				answerBuilder.append(button);
				answerView.setText(answerBuilder.toString());

				// 사칙연산 정답으로 액티비티 종료
				if (isAnswerCorrect()) {
					alarm_alert_activity.finish();
					this.finish();
				}
			}
			//입력한 답이 틀린 경우 빨간색, 정답인 경우 검은색으로 표시
			if (answerView.getText().length() >= answerString.length()
					&& !isAnswerCorrect()) {
				answerView.setTextColor(Color.RED);
			} else {
				answerView.setTextColor(Color.BLACK);
			}
		} catch(Exception e){

		}
	}


	//사칙연산 정답일 경우
	public boolean isAnswerCorrect() {
		boolean correct = false;
		try {
			correct = this.getAnswer() == Integer.parseInt(answerBuilder.toString());
		} catch (NumberFormatException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return correct;
	}


	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}
}