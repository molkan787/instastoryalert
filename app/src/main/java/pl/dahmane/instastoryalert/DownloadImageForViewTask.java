package pl.dahmane.instastoryalert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.HashMap;

public class DownloadImageForViewTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    int delay;

    final static HashMap<String, Bitmap> cache = new HashMap<>();

    static void downloadAndSet(ImageView imageView, String imageURL){
        downloadAndSet(imageView, imageURL, 0);
    }

    static void downloadAndSet(ImageView imageView, String imageURL, int delay){
        Bitmap cachedImage = cache.get(imageURL);
        if(cachedImage != null){
            imageView.setImageBitmap(cachedImage);
        }else{
            new DownloadImageForViewTask(imageView, delay).execute(imageURL);
        }
    }

    public DownloadImageForViewTask(ImageView bmImage, int delay) {
        this.bmImage = bmImage;
        this.delay = delay;
    }

    protected Bitmap doInBackground(String... urls) {
        if(this.delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String url = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            cache.put(url, mIcon11);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}