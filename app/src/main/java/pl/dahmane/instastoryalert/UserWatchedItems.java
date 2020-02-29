package pl.dahmane.instastoryalert;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class UserWatchedItems {

    @Getter
    private User user;
    @Getter
    private String[] images;

}
