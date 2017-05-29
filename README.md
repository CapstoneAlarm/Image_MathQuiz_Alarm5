디버깅 해야할것!
1. 어플 다운받고 "처음" 알람 실행시 미션dialog 충돌 문제
   (아무래도 권한 받아오는 메세지창이랑 미션창이랑 충돌하는듯?)
   
   ImageProblem.java #178 alertDialog2.show();
   => BadTokenException 오류
   




2. AlarmPreference(알람 설정 창)에서 있을 때 알람이 울리면 강제종료됨ㅠㅠ

   java.lang.RuntimeException: Parcelable encountered IOException writing serializable object (name = za.co.neilson.alarm.preferences.AlarmPreferenceListAdapter)
   
   AlarmPreferenceListener에서 오류
   




3. 이미지 인식 시 인식된 이름만 같아도 이미지 인식 성공함
   기준을 내가 지워버렸나...?ㅜㅜ
   
  
  
  
  
4. 홈키 누르면 화면은 꺼지지만 알람소리는 계속 들림
   그런데 그 상태에서 다시 알람 어플을 누르면 빈화면만 나옴ㅜㅜ
   
   
