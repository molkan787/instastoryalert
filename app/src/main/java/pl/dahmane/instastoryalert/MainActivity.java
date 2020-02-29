package pl.dahmane.instastoryalert;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MYDATA";

    Toolbar toolbar;
    ListView usersListView;
    TextInputEditText searchBox;
    LinearLayout searchBar;
    Magic magic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchBar = findViewById(R.id.search_bar);
        searchBox = findViewById(R.id.search_box);
        usersListView = findViewById(R.id.usersList);
        usersListView.setEmptyView(LayoutInflater.from(this).inflate(R.layout.empty_users, null));
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                confirmRemoveUser((User) usersListView.getAdapter().getItem(i));
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddUserActivity.class));
            }
        });

        magic = new Magic(this);
        boolean magicIsReady = magic.getReady();
        Log.i(TAG, "onCreate: magicIsReady:" + magicIsReady);

        if(magicIsReady){
            setup();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        scheduleJob();
        setAppBarState(false);

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAppBarState(true);
            }
        });
        findViewById(R.id.close_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAppBarState(false);
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                searchUsers(editable.toString());
            }
        });
    }

    private void setAppBarState(boolean iss){
        toolbar.setVisibility(iss ? View.INVISIBLE : View.VISIBLE);
        toolbar.getLayoutParams().height = iss ? 0 : AppBarLayout.LayoutParams.WRAP_CONTENT;
        searchBar.setVisibility(iss ? View.VISIBLE : View.INVISIBLE);
        searchBar.getLayoutParams().height = iss ? AppBarLayout.LayoutParams.MATCH_PARENT : 0;
        toolbar.requestLayout();
        searchBar.requestLayout();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        if(iss){
            searchBox.requestLayout();
            searchBox.requestFocus();
            imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);
        }else{
            searchBox.setText("");
            imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            searchUsers("");
        }
    }

    private void searchUsers(String query){
        ArrayList<User> users = magic.getMonitoredUsersData();
        if(query.isEmpty()){
            setUsersListViewItems(users);
            return;
        }
        ArrayList<User> filtered = new ArrayList<>();
        for(User user: users){
            if(user.getUsername().contains(query)){
                filtered.add(user);
            }
        }
        setUsersListViewItems(filtered);
    }

    private void confirmRemoveUser(final User user){
        showMessage("Remove user", "Do you want to remove @" + user.getUsername() + " from your alert list?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeUser(user);
                    }
                });
    }

    private void removeUser(User user){
        magic.getDataManager().removeMonitoredUser(user.getUser_id());
        ((UsersListViewAdapter)usersListView.getAdapter()).remove(user);
    }

    private void setup(){
        ArrayList<User> users = magic.getMonitoredUsersData();
        setUsersListViewItems(users);
    }

    private void setUsersListViewItems(ArrayList<User> items){
        usersListView.setAdapter(new UsersListViewAdapter(this, R.layout.user_row, items, magic.getDataManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        SharedPreferences preferences = getSharedPreferences("instadata", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        restartApp();
    }

    private void restartApp(){
        Intent mStartActivity = new Intent(this, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "View Alerts";
            String description = "Get alerts about new views on your stories";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("alerts", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(int id, String title, String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "alerts")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(id, builder.build());
    }


    private void scheduleJob() {
        // Initially run the job
        new CheckerService.CheckTask().execute(this);

        // Schedule the job
        CheckerService.scheduleJob(this);
    }

    private void showMessage(String title, String text, @Nullable DialogInterface.OnClickListener clickListener){
        DialogInterface.OnClickListener _clickListener = clickListener;
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(text);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("Yes", _clickListener);
        dlgAlert.setNegativeButton("No", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}
