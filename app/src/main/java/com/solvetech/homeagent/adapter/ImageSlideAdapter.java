package com.solvetech.homeagent.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.solvetech.homeagent.HomeActivity;
import com.solvetech.homeagent.R;
import com.solvetech.homeagent.data.DataAccessClient;

import java.util.ArrayList;

/**
 * Created by wpy on 10/3/15.
 */
public class ImageSlideAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    DataAccessClient client;
    int[] resources = { R.drawable.sample_layout, R.drawable.cat, R.drawable.elec };
    String[] urls2 = { "http://solvetech.cn:8080/homeagent/project/0/0-1.jpg",
                        "http://solvetech.cn:8080/homeagent/project/0/0-2.jpg",
                        "http://solvetech.cn:8080/homeagent/project/0/0-3.jpg" };
    ArrayList<String> urls;

    public ImageSlideAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.urls = images;
        this.layoutInflater = LayoutInflater.from(context);
        String accessToken = context.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);
    }

    @Override
    public int getCount() {
        //return resources.length;
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.image_swipe, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_slider);

        // Update the image
        setRemoteBitmapToImageview(imageView, urls.get(position));
        //setLocalResourceToImageview(imageView, resources[position]);
        ViewGroup parent = (ViewGroup) imageView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d("ImageSliderAdapter", "destroyItem is called");
        container.removeView((ImageView) object);
    }

    private void setRemoteBitmapToImageview(ImageView imageView, String url) {
        new ImageDownloadTask(imageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private void setLocalResourceToImageview(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView = null;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... args) {

            Log.d("ImageSlideAdapter", "Now get image: " + args[0]);
            Bitmap image = client.getWebImage((String) args[0]);
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                imageView.setImageBitmap(image);
            }
        }
    }
}
