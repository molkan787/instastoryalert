package pl.dahmane.instastoryalert;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramCandidate;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import lombok.Getter;
import pl.dahmane.instastoryalert.instaApi.StoryItem;

public class Magic {

    @Getter
    private DataManager dataManager;
    @Getter
    private InstaTool instaTool;
    private long[] monitoredUsers;
    @Getter
    private ArrayList<User> monitoredUsersData;
    private boolean _isReady = false;

    public boolean isReady(){
        return _isReady;
    }

    public Magic(Context context){
        instaTool = new InstaTool();
        dataManager = new DataManager(context);
        monitoredUsersData = dataManager.getMonitoredUsers();
        monitoredUsers = getUsersIds(monitoredUsersData);
    }

    public boolean getReady(){
        if(_isReady) return true;
        SessionData sessionData = dataManager.getSessionData();
        if(sessionData.uuid == null || sessionData.uuid.isEmpty()) return false;
        instaTool.loadSession(sessionData);
        _isReady = true;
        return true;
    }

    public boolean logic(String username, String password){
        boolean isSuccess = instaTool.login(username, password);
        if(isSuccess){
            dataManager.setSessionData(instaTool.exportSession(), false);
            _isReady = true;
        }
        return isSuccess;
    }

    public Magic.AddUserResult addUser(String username){
        if(isMonitored(username)) return AddUserResult.AlreadyAdded;
        User user = instaTool.getBasicUserData(username);
        if(user != null){
            _addUser(user);
            return  AddUserResult.Success;
        }
        return AddUserResult.NotFound;
    }

    public ArrayList<InstaView> getNewViews(){
        ArrayList<InstaView> views = new ArrayList<>();
        if(monitoredUsers.length < 1) return views;

        mReel reel = instaTool.getMyStoriesWithViewers();

        HashMap<Long, ArrayList<String>> map = new HashMap<>();

        if(reel == null) {
            setWatchedItems(map);
            return views;
        }
        ArrayList<StoryItem> stories = reel.getItems();
        for(StoryItem story: stories){
            long storyPk = story.getData().getPk();
            List<InstagramUser> users = story.getViewers();
            for(InstagramUser user: users){
                final long userPk = user.getPk();
                if(!isMonitored(userPk)) continue;

                ArrayList<String> colletion = map.get(userPk);
                if(colletion == null){
                    colletion = new ArrayList<>();
                    map.put(userPk, colletion);
                }
                List<InstagramCandidate> images = story.getData().getImage_versions2().getCandidates();
                colletion.add(images.get(images.size() - 1).getUrl());

                if(dataManager.isNotified(userPk, storyPk)) continue;
                dataManager.setNotifiedFlag(userPk, storyPk);
                InstaView instaView = new InstaView(User.fromInstagramUser(user), story.getData());
                views.add(instaView);
            }
        }

        setWatchedItems(map);

        return views;
    }

    private void setWatchedItems(HashMap<Long, ArrayList<String>> data){
        for(long userPk: monitoredUsers){
            ArrayList<String> items = data.get(userPk);
            if(items != null){
                dataManager.setWatchedItems(userPk, items);
            }else{
                dataManager.setWatchedItems(userPk, new ArrayList<String>());
            }
        }
    }

    private void _addUser(User user){
        dataManager.addMonitoredUser(user);
        monitoredUsersData.add(user);
        monitoredUsers = getUsersIds(monitoredUsersData);
    }

    public boolean isMonitored(long userId){
        for(int i = 0; i < monitoredUsers.length; i++){
            if(monitoredUsers[i] == userId){
                return  true;
            }
        }
        return false;
    }

    public boolean isMonitored(String username){
        for(int i = 0; i < monitoredUsersData.size(); i++){
            if(monitoredUsersData.get(i).getUsername().equals(username)){
                return  true;
            }
        }
        return false;
    }

    private long[] getUsersIds(ArrayList<User> users){
        final int len = users.size();
        long[] ids = new long[len];
        for(int i = 0; i < len; i++){
            ids[i] = users.get(i).getUser_id();
        }
        return ids;
    }

    public enum AddUserResult{
        Success,
        NotFound,
        AlreadyAdded
    }
}
