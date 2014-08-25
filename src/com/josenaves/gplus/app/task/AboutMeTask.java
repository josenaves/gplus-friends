package com.josenaves.gplus.app.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.josenaves.gplus.app.AboutMeActivity;
import com.josenaves.gplus.app.R;
import com.josenaves.gplus.app.task.AboutMeTask.UserInfo;

public final class AboutMeTask extends AsyncTask<Void, Void, UserInfo> {
	
	private final String LOG_TAG = AboutMeTask.class.getSimpleName();

	private GoogleApiClient api;
	private ProgressDialog progress;
	private AboutMeActivity view;
	private UserInfo me;
	
	public AboutMeTask(AboutMeActivity view, GoogleApiClient api) {
		this.api = api;
		this.view = view;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress = ProgressDialog.show(view, "Please wait", "Getting your data ...", true);
	}

	@Override
	protected UserInfo doInBackground(Void... params) {
		
		Log.d(LOG_TAG, "Getting profile data");
		// get data about me
		Person myself = Plus.PeopleApi.getCurrentPerson(api);
		if (myself != null) {
			me = new UserInfo(myself.getDisplayName(), myself.getImage().getUrl());
			
			if (me.getImageUrl() != null) {
				me.setImageBitmap(downloadImage(me.getImageUrl()));
			}
		}
		return me;
	}

	@Override
	protected void onPostExecute(UserInfo result) {
		super.onPostExecute(result);
		if (progress.isShowing()) {
			progress.dismiss();
		}
		
		if (result != null) {
			((TextView)view.findViewById(R.id.text_my_name)).setText(result.getName());
			((ImageView)view.findViewById(R.id.image_my_photo)).setImageBitmap(result.getImageBitmap());
		}
	}
	
    private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        } 
        catch (IOException e) {
        	Log.e(LOG_TAG, "Error downloading profile image");
            e.printStackTrace();
        }
        return bitmap;
    }
	
    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(String imageUrl) throws IOException {
        InputStream stream = null;
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }
    
    
	public class UserInfo {
	    private String name;
	    private String imageUrl;
	    private Bitmap imageBitmap;
	    
	    public UserInfo(String name, String photo) {
	    	this.name = name;
	    	this.imageUrl = photo;
	    }

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public Bitmap getImageBitmap() {
			return imageBitmap;
		}

		public void setImageBitmap(Bitmap imageBitmap) {
			this.imageBitmap = imageBitmap;
		}
	}
}
