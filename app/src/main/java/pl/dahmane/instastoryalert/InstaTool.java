package pl.dahmane.instastoryalert;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramSearchUsernameRequest;
import dev.niekirk.com.instagram4android.requests.InstagramSearchUsersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramUserStoryFeedRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetUserFollowersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramReel;
import dev.niekirk.com.instagram4android.requests.payload.InstagramSearchUsernameResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramSearchUsersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramSearchUsersResultUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserStoryFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;
import pl.dahmane.instastoryalert.instaApi.InstagramGetStoryViewersRequest;
import pl.dahmane.instastoryalert.instaApi.InstagramGetStoryViewersResult;
import pl.dahmane.instastoryalert.instaApi.StoryItem;

public class InstaTool {

    private Instagram4Android client;

    public boolean login(String username, String password){
        client = Instagram4Android.builder().username(username).password(password).build();
        client.setup();
        try {
            InstagramLoginResult result = client.login();
            return result.getStatus().equals("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void loadSession(SessionData session){
        client = Instagram4Android.builder().username(session.username).password(session.password).build();
        client.setCookieStore(session.cookies);
        client.setUuid(session.uuid);
        client.setUserId(session.userId);
        client.setLoggedIn(true);
        client.setup();
    }

    public SessionData exportSession(){
        SessionData session = new SessionData();
        session.username = this.client.getUsername();
        session.password = this.client.getPassword();
        session.cookies = this.client.getCookieStore();
        session.uuid = this.client.getUuid();
        session.userId = this.client.getUserId();
        return session;
    }

    public User getBasicUserData(String username){
        try {
            InstagramSearchUsernameResult result = client.sendRequest(new InstagramSearchUsernameRequest(username));
            InstagramUser user = result.getUser();
            if(user == null) return null;
            return User.fromInstagramUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<InstagramSearchUsersResultUser> searchUsers(String query){
        try {
            InstagramSearchUsersResult result = client.sendRequest(new InstagramSearchUsersRequest(query));
            return  result.getUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public List<InstagramUserSummary> getMyFollowers(){
        try {
            InstagramGetUserFollowersResult result = client.sendRequest(new InstagramGetUserFollowersRequest(client.getUserId()));
            return  result.getUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public mReel getMyStoriesWithViewers(){

        try {
            ArrayList<StoryItem> storyItems = new ArrayList<>();
            InstagramUserStoryFeedResult story = client.sendRequest(new InstagramUserStoryFeedRequest(client.getUserId() + ""));
            InstagramReel reel = story.getReel();
            if(reel == null) return null;
            String uid = reel.getId() + "_" + reel.getLatest_reel_media();
//            log("MYDATA", "InstagramUserStoryFeedResult: " + new Gson().toJson(story));
            for(InstagramFeedItem item : reel.getItems()){
                StoryItem storyItem = new StoryItem(item, this.getStoryItemViewers(item.getPk() + ""));
                storyItems.add(storyItem);
            }
            return  new mReel(storyItems, uid);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<InstagramUser> getStoryItemViewers(String pk){
        try {
            InstagramGetStoryViewersResult result = client.sendRequest(new InstagramGetStoryViewersRequest(pk));
            return result.getUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void log(String TAG, String message) {
        int maxLogSize = 2000;
        for(int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            Log.d(TAG, message.substring(start, end));
        }
    }

}
