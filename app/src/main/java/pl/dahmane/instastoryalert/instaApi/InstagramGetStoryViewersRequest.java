package pl.dahmane.instastoryalert.instaApi;

import dev.niekirk.com.instagram4android.requests.InstagramGetRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class InstagramGetStoryViewersRequest extends InstagramGetRequest<InstagramGetStoryViewersResult> {

    private String storyPk;

    @Override
    public String getUrl(){
        return "media/" + storyPk + "/list_reel_media_viewer/";
    }

    @Override
    @SneakyThrows
    public InstagramGetStoryViewersResult parseResult(int statusCode, String content){

        return parseJson(statusCode, content, InstagramGetStoryViewersResult.class);

    }

}