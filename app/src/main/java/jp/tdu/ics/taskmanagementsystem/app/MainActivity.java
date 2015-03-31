package jp.tdu.ics.taskmanagementsystem.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;


public class MainActivity extends Activity {
    private String taskText;
    private String placeText;
    private ListView taskListView;
    private ArrayAdapter<String> taskListAdapter;
    private EditText taskEditText;
    private EditText placeEditText;
    SQLiteDatabase sqLiteDatabase;
    MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonAction();
            }
        });

        taskEditText = (EditText)findViewById(R.id.taskEditText);
        placeEditText = (EditText)findViewById(R.id.placeEditText);

        taskListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        taskListView = (ListView)findViewById(R.id.taskListView);


        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("taskTable", new String[]{"task", "placeID"}, null, null, null, null, null);
        boolean next = cursor.moveToFirst();
        while(next){
            taskListAdapter.add("タスク名: " + cursor.getString(0) + "\n" + "場所名: " + cursor.getString(1));
            next = cursor.moveToNext();
        }
        taskListView.setAdapter(taskListAdapter);


        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView list = (ListView) parent;
                String selectedItem = (String) list.getItemAtPosition(position);
                deleteTask(selectedItem);
                return false;
            }
        });
    }

    private void deleteTask(String selectedItem){
        taskListAdapter.remove(selectedItem);
        sqLiteDatabase.delete("taskTable", ("タスク名: " || "task" || "\n" || "場所名: " || "placeID") + " = ?", new String[]{selectedItem});
    }

    private void addButtonAction(){
        taskText = taskEditText.getText().toString();
        placeText = placeEditText.getText().toString();
        taskListAdapter.add("タスク名: " + taskText + "\n" + "場所名: " + placeText);

        if(!(nullJudge(taskText) && nullJudge(placeText))){
            taskListView.setAdapter(taskListAdapter);
        }
        taskEditText.getText().clear();
        placeEditText.getText().clear();

        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", taskText);
        contentValues.put("placeID", placeText);
        sqLiteDatabase.insert("taskTable", null, contentValues);
    }



    private boolean nullJudge(String str){
        if(str == null || str.length() == 0){
            return true;
        }else{
            return  false;
        }
    }

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        static final String DB = "taskDB";
        static final int DB_VERSION = 1;
        static final String CREATE_TABLE = "create table taskTable(task text, placeID text);";
        static final String DROP_TABLE = "drop table taskDB;";
        public MySQLiteOpenHelper(Context c) {
            super(c, DB, null, DB_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }
}
