package com.example.bringo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;


import com.example.bringo.database.CheckedItemsDB;
import com.example.bringo.database.CustomizedSceDB;
import com.example.bringo.database.ScenarioAlarmDB;


import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
    private static final String SELECTED_ITEM = "arg_selected_item";
    private final static int DEFAULT_SCENARIOS_COUNT = 6;
    List<String> namesGet;
    private GridView gridView;
    private Toolbar myToolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = new Intent(HomeActivity.this, SetAlarmActivity.class);
            startActivity(intent);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set up toolbar
        myToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        myToolbar.setTitle("Home");
        setSupportActionBar(myToolbar);
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        // set up bottom bar

        mBottomNav = (BottomNavigationView) findViewById(R.id.nav_home);
        //listener for nav item
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //function to change activity
                navItemSelected(item, 0);
                return true;
            }
        });
        //set default NavItem
        setDefaultNavItem(savedInstanceState );


        final HomeActivity ha = this;
        // initialize the default scenarios db
        initialDefaultScenariosDB();
        GetScenarios gs = new GetScenarios();
        gs.getScenarioNames(ha);
    }



    /*
     * initialDefaultScenarioDB() adds default scenario IDs into the DefaultScenario Database
     */
    private void initialDefaultScenariosDB(){
        DefaultScenarios.deleteAll(DefaultScenarios.class);
        List<ScenarioAlarmDB> list = ScenarioAlarmDB.listAll(ScenarioAlarmDB.class);
        if (list == null || list.size() == 0) {
            for(int i=1;i<=DEFAULT_SCENARIOS_COUNT;i++){
                DefaultScenarios defaultSce = new DefaultScenarios(i);
                defaultSce.save();
                ScenarioAlarmDB defaultAlarm = new ScenarioAlarmDB(i);
                defaultAlarm.save();
            }
        } else {
            for(int i=1;i<=DEFAULT_SCENARIOS_COUNT;i++){
                DefaultScenarios defaultSce = new DefaultScenarios(i);
                defaultSce.save();
            }
        }
    }

    /*
     * namesReady() adds scenario names to the grid view of the layouts
     */
    public void namesReady(List<String> names){
        // set the layout's gridview by GridButtonAdapter
        namesGet = names;
        gridView = (GridView)findViewById(R.id.scenarioView);
        gridView.setAdapter(new GridButtonAdapter(this, names));
    }

    /*
     * GridButtonAdapter set the content of the gridview with dynamic scenario names and numbers
     */
    public class GridButtonAdapter extends BaseAdapter{
        // names is a Arraylist containing Strings of scenario names
        private List<String> names;
        private Context context;

        public GridButtonAdapter(Context context, List<String> names){
            this.context = context;
            this.names = names;
        }


        @Override
        public int getCount() {
            return names.size()+1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // add an add sign "+" to position 0
            if(position==0){
                Button addButton;
                if (convertView == null) {
                    addButton = new Button(context);
                    addButton.setLayoutParams(new GridView.LayoutParams(550, 280));
                    addButton.setPadding(8, 8, 8, 8);
                } else {
                    addButton = (Button) convertView;
                }
                addButton.setText("+");
                addButton.setTextSize(40);
                addButton.setBackgroundColor(Color.LTGRAY);
                addButton.setId(position);
                // set OnClickLinstener for each item on the grid view
                addButton.setOnClickListener(new AddScenarioOnClickListener(position));
                return addButton;

             /*
                ImageView addImage;
                if(convertView == null) {
                    addImage = new ImageView(context);
                    addImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    addImage.setLayoutParams(new LinearLayoutCompat.LayoutParams(450,280));
                    addImage.setPadding(8, 8, 8, 8);
                }else{
                    addImage = (ImageView) convertView;
                }
                addImage.setImageResource(R.drawable.add);
                addImage.setBackgroundColor(Color.LTGRAY);
                // set OnClickListener for the add button
                addImage.setOnClickListener(new AddScenarioOnClickListener(position));
                return addImage;*/
            }
            // add scenario names to other positions
            else {
                Button sceButton;
                if (convertView == null) {
                    sceButton = new Button(context);
                    sceButton.setLayoutParams(new GridView.LayoutParams(550, 280));
                    sceButton.setPadding(8, 8, 8, 8);
                } else {
                    sceButton = (Button) convertView;
                }
                sceButton.setText(names.get(position-1));
                sceButton.setTextSize(20);
                sceButton.setBackgroundColor(Color.LTGRAY);
                sceButton.setId(position);
                // set OnClickLinstener for each item on the grid view
                sceButton.setOnClickListener(new ScenarioOnClickListener(position));
                // set long press click listener for customized scenarios
                if(position>=7){
                    sceButton.setOnLongClickListener(new ScenarioLongClickListener(position));
                }
                return sceButton;
            }
        }
    }

    private class ScenarioLongClickListener implements View.OnLongClickListener{
        int sID;
        public ScenarioLongClickListener(int position){
            String sceName = namesGet.get(position-1);
            System.out.println("Customized sce name: "+sceName);
            // find its sceID in CustomizedSceDB
            List<CustomizedSceDB> sceDBs = CustomizedSceDB.find(CustomizedSceDB.class,"name = ?",sceName);
            CustomizedSceDB sceDB = sceDBs.get(0);
            this.sID = sceDB.getScenarioID();
        }

        @Override
        public boolean onLongClick(View v) {
            /*
            System.out.println("long click scenario");
            Intent intent = new Intent(HomeActivity.this, CreateSceActivity.class);
            startActivity(intent);*/
            System.out.println("long click scenario");
            View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.activity_delete_sce,null);


            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("Are you sure?").setView(view)
                    .setNegativeButton("Cancel",null)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("choose to delete scenario");
                            // delete the scenario in CustomizedSceDB
                            List<CustomizedSceDB> sceDBs = CustomizedSceDB.find(CustomizedSceDB.class,"scenario_id = ?",String.valueOf(sID));
                            CustomizedSceDB sceDB = sceDBs.get(0);
                            sceDB.delete();
                            // delete items in CheckedItemsDB with scenarioID = sID
                            CheckedItemsDB.deleteAll(CheckedItemsDB.class,"scenario_id = ?",String.valueOf(sID));
                            // delete the scenario in Home page's grid view
                            Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
    }


    /*
     * ScenarioOnclickListener directs user to the item list page of the scenario they select
     */
    private class ScenarioOnClickListener implements View.OnClickListener{
        int sID;
        public ScenarioOnClickListener(int position){
            if(position<7) {
                this.sID = position;
            }else{
                String sceName = namesGet.get(position-1);
                System.out.println("Customized sce name: "+sceName);
                // find its sceID in CustomizedSceDB
                List<CustomizedSceDB> sceDBs = CustomizedSceDB.find(CustomizedSceDB.class,"name = ?",sceName);
                CustomizedSceDB sceDB = sceDBs.get(0);
                this.sID = sceDB.getScenarioID();
                System.out.println("it's sceID is "+sID);
            }
        }
        @Override
        public void onClick(View v) {
            // the following code is just for test!!!
            System.out.println(sID);

            // jump to the selected default scenario
            if(sID<7){
                // jump to DefaultListActivity
                if (sID == 6) {
                    Intent intent = new Intent(HomeActivity.this,TravelActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this,DefaultListActivity.class);
                    intent.putExtra("sID",String.valueOf(sID));
                    startActivity(intent);
                }
            }else{
                // jump to CustomizedListActivity
                Intent intent = new Intent(HomeActivity.this,CustomizedListActivity.class);
                intent.putExtra("sID",String.valueOf(sID));
                startActivity(intent);
            }

        }
    }

    /*
     * AddScenarioOnclickListener directs user to the process of creating their customised scenario
     */
    private class AddScenarioOnClickListener implements View.OnClickListener{
        int sID;
        public AddScenarioOnClickListener(int sID){
            this.sID = sID;
        }
        @Override
        public void onClick(View v) {
            // the following code is just for test
            System.out.println(sID);

            // the following code is just for notification set test
//             NotificationReceiver.updateNotification("Title","Notification Content");
//             setNotificationAlarm(17, 59, 50, false);

            // jump to the page of create new list step 1
            Intent intent = new Intent(HomeActivity.this,CreateSceActivity.class);
            startActivity(intent);
        }
    }

    public void setNotificationAlarm(int hour,int minute,int second, boolean repeat){
        // the following code is just for notification test!!!
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,second);
        System.out.println("************************* i am working.TAT");

        // NotificationReceiver is a BroadcastReceiver class
        Intent intent = new Intent(getApplicationContext(),NotificationReceiver.class);
        // Alarm Service requires a PendingIntent as param, set the intent to the pendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),101,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // set an alarm that works even if app is cosed, depends on calendar time,
        // repeats everyday, with pendingIntent
        // So when alarm goes off NotificationReceiver will be triggered
        if(repeat == true){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,pendingIntent);
        }else{
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }

        // cancel the alarm
        //alarmManager.cancel(pendingIntent);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // to make the Toolbar has the functionality of Menu，do not delete
        getMenuInflater().inflate(R.menu.top_bar_alarm, menu);
        return true;
    }

    private void navItemSelected (MenuItem item, int current) {

        // update selected item
        mSelectedItem = item.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
            System.out.println("uncheck others: "+item.getItemId());
        }

        int a = mBottomNav.getMenu().getItem(0).getItemId();
        int b = mBottomNav.getMenu().getItem(1).getItemId();
        int c = mBottomNav.getMenu().getItem(2).getItemId();
        int d = mBottomNav.getMenu().getItem(3).getItemId();
        //change activity here
        if(mSelectedItem == a && current != 0){
            System.out.println("jump to HOME");
            Intent intent0 = new Intent(this, HomeActivity.class);
            startActivity(intent0);
        }
        else if (mSelectedItem == b && current != 1){
            System.out.println("jump to TODAY'S LIST");
            //TODO: *********change to today's list**************
            Intent intent1 = new Intent(this, TodayListActivity.class);
            startActivity(intent1);
        }
        else if (mSelectedItem == c && current != 2){
            System.out.println("jump to Tracking");
            Intent intent2 = new Intent(this, TrackActivity.class);
            startActivity(intent2);
        }
        else if (mSelectedItem == d && current != 3){
            System.out.println("jump to SETTINGS");
            Intent intent3 = new Intent(this, SettingsActivity.class);
            startActivity(intent3);
        }
    }

    private void setDefaultNavItem( Bundle savedInstanceState ){
        System.out.println("set default nav item");
        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
            System.out.println("maybe for currently selected item");
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
            System.out.println("Current is null, so force to be 0");
        }
        // update selected item
        mSelectedItem = selectedItem.getItemId();

        // uncheck the other items.
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == selectedItem.getItemId());
        }
    }



}