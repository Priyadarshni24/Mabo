package com.advengers.mabo.ServerCall;

import com.advengers.mabo.Activity.App;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleTone {

    private static VolleySingleTone sInstance = null;
    private RequestQueue mrequestqueue;
    private ImageLoader mImageLoader;
    private VolleySingleTone(){
        mrequestqueue = Volley.newRequestQueue(App.getappContext());
    }
    public static VolleySingleTone getsInstance() {
        if (sInstance == null)
        {
            sInstance = new VolleySingleTone();
        }
        return sInstance;
    }
    public RequestQueue getMrequestqueue(){

       /* File dir = new File(MainActivity.APP_FOLDER);
        dir.mkdirs();
        // Instantiate the cache
        Cache cache = new DiskBasedCache(dir, 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mrequestqueue = new RequestQueue(cache, network);

        // Start the queue
        mrequestqueue.start();*/

        return mrequestqueue;
    }
    /*public ImageLoader getImageLoader() {
        getMrequestqueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mrequestqueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }*/
}
