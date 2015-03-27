package jp.tdu.ics.taskmanagementsystem.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {
    private String taskText;
    private String placeText;
    private ListView taskList;
    private ArrayAdapter<String> taskListAdapter;
    private EditText taskEditText;
    private EditText placeEditText;
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
        taskList = (ListView)findViewById(R.id.taskListView);
    }

    protected void addButtonAction(){
        taskText = taskEditText.getText().toString();
        placeText = placeEditText.getText().toString();
        taskListAdapter.add("タスク名: " + taskText + "\n" + "場所名: " + placeText);

        if(!(nullJudge(taskText) && nullJudge(placeText))){
            taskList.setAdapter(taskListAdapter);
        }

        taskEditText.getText().clear();
        placeEditText.getText().clear();
    }

    protected boolean nullJudge(String str){
        if(str == null || str.length() == 0){
            return true;
        }else{
            return  false;
        }
    }
}
