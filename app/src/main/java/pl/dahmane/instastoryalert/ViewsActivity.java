package pl.dahmane.instastoryalert;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);

        DataManager dataManager = new DataManager(this);
        List<UserWatchedItems> items = dataManager.getAllWatches();

        ListView itemsView = findViewById(R.id.items);
        itemsView.setAdapter(new ListAdapter(this, R.layout.user_views, items));
    }

    private class ListAdapter extends ArrayAdapter<UserWatchedItems>{

        private int resourceLayout;
        private Context mContext;

        public ListAdapter(@NonNull Context context, int resource, @NonNull List<UserWatchedItems> items) {
            super(context, resource, items);
            this.resourceLayout = resource;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            View v = vi.inflate(resourceLayout, null);

            UserWatchedItems data = getItem(position);
            if(data == null) return  v;
            User user = data.getUser();

            View userView = v.findViewById(R.id.user);
            ImageView pictureView = userView.findViewById(R.id.picture);
            TextView usernameView = userView.findViewById(R.id.username);
            TextView fullnameView = userView.findViewById(R.id.fullname);
            LinearLayout imagesView = v.findViewById(R.id.images);

            DownloadImageForViewTask.downloadAndSet(pictureView, user.getPicture_url());
            usernameView.setText(user.getUsername());
            fullnameView.setText(user.getFullname());

            for(String imageUrl: data.getImages()){
                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(108), LinearLayout.LayoutParams.MATCH_PARENT));
                imagesView.addView(imageView);
                DownloadImageForViewTask.downloadAndSet(imageView, imageUrl);
            }

            return v;
        }

    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
