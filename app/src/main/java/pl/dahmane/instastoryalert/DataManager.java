package pl.dahmane.instastoryalert;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    final static String KEY_MONITORED_USERS = "monitored_users";
    final static String KEY_SESSION_DATA = "session_data";

    SharedPreferences preferences;
    Gson gson;

    public DataManager(Context context){
        preferences = context.getSharedPreferences("instadata", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void setSessionData(SessionData session, boolean immidiateWrite){
        String json = gson.toJson(session);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_SESSION_DATA, json);
        if(immidiateWrite){
            editor.commit();
        }else{
            editor.apply();
        }
    }

    public SessionData getSessionData(){
        String json = preferences.getString(KEY_SESSION_DATA, "{}");
        return gson.fromJson(json, SessionData.class);
    }

    public void addMonitoredUser(User user){
        ArrayList<User> users = getMonitoredUsers();
        users.add(0, user);
        setMonitoredUsers(users);
    }

    public void removeMonitoredUser(long userId){
        setWatchedItems(userId, new ArrayList<String>());
        ArrayList<User> users = getMonitoredUsers();
        for(User user: users){
            if(user.getUser_id() == userId){
                users.remove(user);
                break;
            }
        }
        setMonitoredUsers(users);
    }

    public ArrayList<User> getMonitoredUsers(){
        String json = preferences.getString(KEY_MONITORED_USERS, "{}");
        ArrayList<User> users = gson.fromJson(json, UsersList.class).data;
        if(users == null) return new ArrayList<User>();
        return users;
    }

    public void setNotifiedFlag(long userId, long storyItemId){
        String key = "notified_" + userId + "_" + storyItemId;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, true);
        editor.apply();
    }

    public boolean isNotified(long userId, long storyItemId){
        String key = "notified_" + userId + "_" + storyItemId;
        return preferences.getBoolean(key, false);
    }

    public void setWatchedItems(long userId, ArrayList<String> images){
        String data = new Gson().toJson(images);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(userId + "_watched", data);
        editor.apply();
    }

    public String[] getWatchedItems(long userId){
        String raw = preferences.getString(userId + "_watched", "[]");
        return new Gson().fromJson(raw, new TypeToken<String[]>(){}.getType());
    }

    public List<UserWatchedItems> getAllWatches(){
        ArrayList<UserWatchedItems> items = new ArrayList<>();
        List<User> users = getMonitoredUsers();
        for(User user: users){
            String[] watchedItems = getWatchedItems(user.getUser_id());
            if(watchedItems.length > 0){
                items.add(new UserWatchedItems(user, watchedItems));
            }
        }
        return items;
    }

    private void setMonitoredUsers(ArrayList<User> users){
        UsersList usersList = new UsersList(users);
        String json = gson.toJson(usersList);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_MONITORED_USERS, json);
        editor.apply();
    }

    private class UsersList{
        public ArrayList<User> data;
        public UsersList(ArrayList<User> data){
            this.data = data;
        }
    }

}
