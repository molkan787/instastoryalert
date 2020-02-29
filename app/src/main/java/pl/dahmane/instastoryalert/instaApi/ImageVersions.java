package pl.dahmane.instastoryalert.instaApi;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ImageVersions {

    public List<ImageMeta> candidates;
}