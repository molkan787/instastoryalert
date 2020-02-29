package pl.dahmane.instastoryalert;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import lombok.Getter;
import lombok.Setter;

public class User {

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String fullname;

    @Getter @Setter
    private String picture_url;

    @Getter @Setter
    private long user_id;

    public static User fromInstagramUser(InstagramUser iuser){
        User user = new User();
        user.username = iuser.getUsername();
        user.fullname = iuser.getFull_name();
        user.picture_url = iuser.getProfile_pic_url();
        user.user_id = iuser.getPk();
        return user;
    }

}
