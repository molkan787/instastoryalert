package pl.dahmane.instastoryalert.instaApi;

import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import lombok.Getter;
import lombok.Setter;

public class StoryItem {

    @Getter @Setter
    private InstagramFeedItem data;

    @Getter @Setter
    private List<InstagramUser> viewers;

    public StoryItem(InstagramFeedItem data, List<InstagramUser> viewers){
        this.data = data;
        this.viewers = viewers;
    }

}
