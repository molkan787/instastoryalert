package pl.dahmane.instastoryalert;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.dahmane.instastoryalert.instaApi.StoryItem;

@AllArgsConstructor
public class mReel {

    @Getter
    private ArrayList<StoryItem> items;
    @Getter
    private String uid;



}
