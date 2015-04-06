package jp.tdu.ics.taskmanagementsystem.app;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MainActivity extends Activity {
    private String taskText;
    private String placeID;
    private ListView taskListView;
    private ArrayAdapter<String> taskListAdapter;
    private EditText taskEditText;
    Toast toast;
    SQLiteDatabase sqLiteDatabase;
    MySQLiteOpenHelper mySQLiteOpenHelper;
    ArrayList<String> result;
    Socket socket = null;



    IntentFilter intentFilter;
    WifiManager wifiManager;
    WiFiReceiver wifiReceiver;
    final private String icsGlobalIpAddrss = "133.20.243.197";
    final private String icsPrivateIpAddress = "192.168.11.9";
    final private int PORT = 6666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_LONG);

        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonAction();
            }
        });


        taskEditText = (EditText)findViewById(R.id.taskEditText);

        taskListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        taskListView = (ListView)findViewById(R.id.taskListView);

        ArrayAdapter<String> placeListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        placeListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("taskTable", new String[]{"task", "placeID"}, null, null, null, null, null);
        boolean next = cursor.moveToFirst();
        while(next){
            taskListAdapter.add("タスク名: " + cursor.getString(0) + "\n" + "場所名: " + cursor.getString(1));
            next = cursor.moveToNext();
        }
        cursor.close();
        taskListView.setAdapter(taskListAdapter);
        placeListAdapter.add("事務部");
        placeListAdapter.add("生協");
        placeListAdapter.add("ATM");
        placeListAdapter.add("研究棟412");
        placeListAdapter.add("206教室");
        placeListAdapter.add("207教室");
        placeListAdapter.add("208教室");
        placeListAdapter.add("301教室");
        placeListAdapter.add("302教室");
        placeListAdapter.add("303教室");
        placeListAdapter.add("304教室");
        placeListAdapter.add("305教室");
        placeListAdapter.add("306教室");
        placeListAdapter.add("307教室");
        placeListAdapter.add("308教室");
        placeListAdapter.add("401教室");
        placeListAdapter.add("402教室");
        placeListAdapter.add("403教室");
        placeListAdapter.add("404教室");
        placeListAdapter.add("405教室");
        placeListAdapter.add("406教室");
        placeListAdapter.add("407教室");
        placeListAdapter.add("408教室");
        placeListAdapter.add("409教室");
        placeListAdapter.add("410教室");
        placeListAdapter.add("411教室");


        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView list = (ListView) parent;
                String selectedItem = (String) list.getItemAtPosition(position);
                deleteTask(selectedItem);
                return false;
            }
        });

        Spinner spinner = (Spinner)findViewById(R.id.placeSpinner);
        spinner.setAdapter(placeListAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                placeID = (String) spinner.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
    }

    private void deleteTask(String selectedItem){
        taskListAdapter.remove(selectedItem);
        sqLiteDatabase.delete("taskTable", null , null);
    }

    private void addButtonAction(){
        taskText = taskEditText.getText().toString();
        taskListAdapter.add("タスク名: " + taskText + "\n" + "場所名: " + placeID);

        if(!(nullJudge(taskText) && nullJudge(placeID))){
            taskListView.setAdapter(taskListAdapter);
        }
        taskEditText.getText().clear();

        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", taskText);
        contentValues.put("placeID", placeID);
        sqLiteDatabase.insert("taskTable", null, contentValues);
    }



    private boolean nullJudge(String str){
        if(str == null || str.length() == 0){
            return true;
        }else{
            return  false;
        }
    }


    public void  postWiFiData(final Map<String, List<Integer>> wifiData){
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                String message = "";
                try {
                    if(wifiManager.getConnectionInfo().getSSID().equals("\"TDN SERA\"")) {
                        socket = new Socket(icsPrivateIpAddress, PORT);
                    }else{
                        socket = new Socket(icsGlobalIpAddrss, PORT);
                    }
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(wifiData);

                    /*
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receivedMessage;
                    while ((receivedMessage = bufferedReader.readLine()) != null) {
                        message += receivedMessage;
                    }
                    */

                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    result = (ArrayList<String>) objectInputStream.readObject();
                    if(socket != null) {
                        socket.close();
                    }
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<String> result){
                mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
                sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.query("taskTable", new String[]{"task", "placeID"}, null, null, null, null, null);
                boolean next = cursor.moveToFirst();
                StringBuilder toastMessage = new StringBuilder();
                if(result != null) {
                    while (next) {
                        if (result.get(0).equals(cursor.getString(1))) {
                            toastMessage.append(result.get(0) + "付近にいます。" + "\n" + "タスク名: " + "{" + cursor.getString(0) + "}" + "が解決できます。" + "\n");
                        }
                        next = cursor.moveToNext();
                    }
                    toastMessage.append(result);
                }else{
                    toastMessage.append("推定できませんでした");
                }

                /*
                if(toast != null) {
                    toast.cancel();
                }
                */
                /*
                if (toastMessage.length() != 0) {
                    toast.setText(toastMessage);
                    toast.setText(result.toString());
                } else if(result.get(0).equals("推定できませんでした")){
                    toast.setText(result.get(0));
                    toast.setText(result.toString());
                }else if((result.get(0)).toString().length() != 0){
                    toast.setText(result.get(0) + "付近にいます。");
                    toast.setText(result.toString());
                }
                */

                toast.setText(toastMessage);
                toast.show();
                cursor.close();
                wifiManager.startScan();
            }
        }.execute();
    }

    class WiFiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            Map<String,List<Integer>> wifidata = new TreeMap<>();
            for (ScanResult scanResult : scanResultList) {
                String bssid = scanResult.BSSID;
                int rssi = scanResult.level;
                if(!wifidata.containsKey(bssid)){
                    List<Integer> rssiList = new ArrayList<>();
                    rssiList.add(rssi);
                    wifidata.put(bssid, rssiList);
                }else{
                    wifidata.get(bssid).add(rssi);
                }
            }
            postWiFiData(wifidata);
            toast.cancel();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiReceiver = new WiFiReceiver();
        registerReceiver(wifiReceiver, intentFilter);
    }
    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }
}
