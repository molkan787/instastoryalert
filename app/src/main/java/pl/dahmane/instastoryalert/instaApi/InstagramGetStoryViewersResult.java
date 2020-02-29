package pl.dahmane.instastoryalert.instaApi;

import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.StatusResult;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class InstagramGetStoryViewersResult extends StatusResult {

    private List<InstagramUser> users;
    private String next_max_id;
    private int user_count;
    private int total_viewer_count;
    private int total_screenshot_count;
    private InstagramItem updated_media;

}