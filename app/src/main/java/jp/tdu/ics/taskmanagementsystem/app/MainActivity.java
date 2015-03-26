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
    private ListView taskList;
    private ArrayAdapter<String> taskListAdapter;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = (Button)findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonAction();
            }
        });

        editText = (EditText)findViewById(R.id.editText);

        taskListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        taskList = (ListView)findViewById(R.id.listView);
    }

    protected void addButtonAction(){
        taskText = editText.getText().toString();
        taskListAdapter.add(taskText);
        taskList.setAdapter(taskListAdapter);
        editText.getText().clear();
    }
}
