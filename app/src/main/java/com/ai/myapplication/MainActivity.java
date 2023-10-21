package com.ai.myapplication;

import androidx.appcompat.app.AppCompatActivity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //listView -> 운동 추천 시스템 ui용
        ListView listView=findViewById(R.id.listView);
        /*
        sqlite에서 listView 값 가져올 수 있음
        여러가지 행 중 랜덤으로 4개만 가져오기 가능
         */

        //sqlite 부분
        mainContent=(EditText) findViewById(R.id.mainContent);
        dbHelper=new DBHelper(this,dbName,null,dbVersion);

        //날짜 출력용
        textView=findViewById(R.id.textView);

        //date 생성
        Date date=new Date();
        dateData=simpleDate.format(date);

        //화면 출력
        textView.setText(String.valueOf(dateData));
    }
    //클릭 시 db에 저장, 삭제
    public void mOnClick(View v){
        SQLiteDatabase db;
        String sql;
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String getTime=sdf.format(date);


        if (v.getId() == R.id.btnInsert) {
            String name = mainContent.getText().toString();
            db = dbHelper.getWritableDatabase();
            sql = "INSERT INTO mainTxt (column_name) VALUES (?)";
            db.execSQL(sql, new String[]{name});
            db.close();
        } else if (v.getId() == R.id.btnDelete) {
            db = dbHelper.getWritableDatabase();
            String deleteSql = "DELETE FROM mainTxt";
            db.execSQL(deleteSql);
            db.close();
        }

        dbHelper.close();
    }
}