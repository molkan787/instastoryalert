package pl.dahmane.instastoryalert;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import lombok.Getter;

public class InstaView {

    @Getter
    private User user;
    @Getter
    private InstagramFeedItem story;

    public InstaView(User user, InstagramFeedItem story){
        this.user = user;
        this.story = story;
    }

}
