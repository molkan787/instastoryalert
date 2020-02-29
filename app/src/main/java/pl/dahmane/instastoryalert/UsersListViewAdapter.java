package pl.dahmane.instastoryalert;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class UsersListViewAdapter extends ArrayAdapter<User> {

    private int resourceLayout;
    private Context mContext;
    private List<User> items;
    private DataManager dataManager;

    public UsersListViewAdapter(Context context, int resource, List<User> items, DataManager dataManager) {
        super(context, resource, items);
        this.items = items;
        this.resourceLayout = resource;
        this.mContext = context;
        this.dataManager = dataManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        String[] images = dataManager.getWatchedItems(user.getUser_id());
        if(images.length > 0) {
            return getExtendedView(user, images);
        }else {
            return getSimpleView(user);
        }
    }

    public View getSimpleView(User user) {

        LayoutInflater vi;
        vi = LayoutInflater.from(mContext);
        View v = vi.inflate(resourceLayout, null);

        if (user != null) {
            ImageView pictureView = v.findViewById(R.id.picture);
            TextView usernameView = v.findViewById(R.id.username);
            TextView fullnameView = v.findViewById(R.id.fullname);

            usernameView.setText(user.getUsername());
            fullnameView.setText(user.getFullname());
            DownloadImageForViewTask.downloadAndSet(pictureView, user.getPicture_url());
        }

        return v;
    }

    public View getExtendedView(User user, String[] images) {

        LayoutInflater vi;
        vi = LayoutInflater.from(mContext);
        View v = vi.inflate(R.layout.user_views, null);

        View userView = v.findViewById(R.id.user);
        ImageView pictureView = userView.findViewById(R.id.picture);
        TextView usernameView = userView.findViewById(R.id.username);
        TextView fullnameView = userView.findViewById(R.id.fullname);
        LinearLayout imagesView = v.findViewById(R.id.images);

        DownloadImageForViewTask.downloadAndSet(pictureView, user.getPicture_url());
        usernameView.setText(user.getUsername());
        fullnameView.setText(user.getFullname());

        int idx = 0;
        for(String imageUrl: images){
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(42), LinearLayout.LayoutParams.MATCH_PARENT));
            imagesView.addView(imageView);
            DownloadImageForViewTask.downloadAndSet(imageView, imageUrl, idx++ * 500);
        }

        return v;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}