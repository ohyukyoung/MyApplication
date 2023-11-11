package com.ai.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.DatePicker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
public class MainActivity extends AppCompatActivity {
    public TextView textView;   //날짜 정보 출력할 TextView
    EditText mainContent;
    DBHelper dbHelper;

    final static String dbName="mainTxt.db";
    final static int dbVersion=2;

    //출력 형식 지정
    SimpleDateFormat simpleDate=new SimpleDateFormat("yyyy-MM-dd");

    //날짜 저장 변수
    String dateData = new String();
    Button button;  //저장 버튼
    DatePicker datePicker;  //날짜 선택 위젯
    EditText editText;  //글 기록 부분
    String filename;    //파일 입출력을 위해 저장할 파일 이름
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("감정 일기장");

        button = findViewById(R.id.button);
        datePicker=findViewById(R.id.datepicker);
        //sqlite 부분
        mainContent=(EditText) findViewById(R.id.mainContent);
        dbHelper=new DBHelper(this,dbName,null,dbVersion);

        //datePicker를 현재 날짜로 초기화하기 위해 오늘 년, 월, 일 받아옴
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);

        //datepicker 오늘의 날짜로 초기값 정함
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                filename=Integer.toString(i)+"_"+Integer.toString(i1)+"_"+Integer.toString(i2);
                String str=readDiary(filename);
                editText.setText(str);
                button.setEnabled(true);
            }
        });
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    FileOutputStream outFs = openFileOutput(filename, Context.MODE_PRIVATE);
                    String str = editText.getText().toString();
                    outFs.write(str.getBytes());
                    outFs.close();
                    Toast.makeText(MainActivity.this, filename + "이 저장", Toast.LENGTH_SHORT).show();
                    button.setText("수정하기");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    String readDiary(String filename){

        String diaryStr = null;
        FileInputStream inFs;
        try {  //파일이 있는경우
            inFs = openFileInput(filename);

            byte[] txt = new byte[500];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt)).trim();
            button.setText("수정하기");


        }catch (IOException e){  // 파일이 없는 경우
            editText.setHint("일기 없음");
            button.setText("새로 저장");

        }

        return diaryStr;
    }
    //클릭 시 db에 저장, 삭제
    public void mOnClick(View v){
        SQLiteDatabase db;
        String sql;
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String getTime=sdf.format(date);


        //추가 버튼 누르면 문장 별로 sqlite에 저장
        if (v.getId() == R.id.btnInsert) {
            String inputText = mainContent.getText().toString(); // 사용자로부터 입력받은 여러 문장
            String[] sentences = inputText.split("\n"); // 개행 문자('\n')를 기준으로 문장 분리

            db = dbHelper.getWritableDatabase();

            for (String sentence : sentences) {
                ContentValues values = new ContentValues();
                values.put("text", sentence);

                db.insert("sentences", null, values);
            }

            db.close();
        }

        //삭제 버튼 누르면 해당 날짜 db 삭제됨
        else if (v.getId() == R.id.btnDelete) {
            db = dbHelper.getWritableDatabase();
            String deleteSql = "DELETE FROM mainTxt";
            db.execSQL(deleteSql);
            db.close();
        }

        dbHelper.close();
    }
}