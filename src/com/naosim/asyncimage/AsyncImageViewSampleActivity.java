package com.naosim.asyncimage;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.naosim.asyncimage.AsyncImageView.ImageLoader;
import com.naosim.asyncimage.AsyncImageViewSampleActivity.SampleAdapter.ViewHolder;
import com.naosim.asyncimage.InputStreamHelper.InputStreamLoader;

public class AsyncImageViewSampleActivity extends Activity implements
		ImageLoader, OnScrollListener {
	/** Called when the activity is first created. */

	ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(new SampleAdapter());
//		listView.setOnScrollListener(this);

		// Bitmap b = load(null,
		// "http://img5.blogs.yahoo.co.jp/ybi/1/21/a4/honke_aj7/folder/248590/img_248590_4563489_0?1333697918");

	}

	public class SampleAdapter extends BaseAdapter {
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 20;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		
		public class ViewHolder {
			AsyncImageView asyncImageView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.row, null);
				ViewHolder vh = new ViewHolder();
				vh.asyncImageView = (AsyncImageView) convertView
						.findViewById(R.id.asyncImageView);
				convertView.setTag(vh);
			}
			
			
			ViewHolder vh = (ViewHolder)convertView.getTag();
			vh.asyncImageView
					.setAsyncImage(
							new Integer(position),
							"http://img5.blogs.yahoo.co.jp/ybi/1/21/a4/honke_aj7/folder/248590/img_248590_4563489_0?1333697918",
							AsyncImageViewSampleActivity.this);
			return convertView;
		}

	}

	public InputStream getInputStream(String url) {
		HttpGet method = new HttpGet(url);

		DefaultHttpClient client = new DefaultHttpClient();

		InputStream in = null;
		try {
			HttpResponse response = client.execute(method);
			in = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}

	Bitmap bmp;
	
	@Override
	public Bitmap load(Object id, Object userData) {
		String url = (String) userData;
		InputStream in = getInputStream(url);
		
		if(bmp != null) {
			Log.e("load", "bmp exist");
			return bmp;
		}
		
		try {
			bmp = (Bitmap) InputStreamHelper.startStream(in,
					new InputStreamLoader() {

						@Override
						public Object stream(InputStream in) {
							return BitmapFactory.decodeStream(in);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bmp;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	/**
	 * スクロールイベント スクロール操作は画像の描画されないようにする。 スクロール捜査終了時に画像の描画を開始する。
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int count = listView.getChildCount();
		// スクロールは終了しているか？
		boolean isStopScroll = (scrollState == OnScrollListener.SCROLL_STATE_IDLE);
		for (int i = 0; i < count; i++) {
			View v = listView.getChildAt(i);
			ViewHolder vh = (ViewHolder)v.getTag();
			vh.asyncImageView.setIsDrawable(isStopScroll);
		}

	}
}