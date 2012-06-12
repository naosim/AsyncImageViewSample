package com.naosim.asyncimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 画像を非同期で取得するImageView
 * 
 * @author naosim
 * 
 */
public class AsyncImageView extends ImageView {

	/** 現在表示している画像のID */
	public Object currentId;
	/** 表示中のBitmap */
	public Bitmap currentBitmap;
	public ImageLoadTask currentTask;

	/**
	 * 描画可能状態かどうか デフォルトは、true
	 * */
	public boolean isDrawable = true;
	/** メインスレッドのハンドラ */
	protected final Handler handler = new Handler();
	/** 画像取得で使うハンドラ */
	public static Handler imageLoadHandler;

	// protected Thread imageLoadThread;

	public AsyncImageView(Context context) {
		super(context);
	}

	/**
	 * 描画可能状態かどうかのセッタ これがfalseの場合は、画像取得完了のタイミングで 描画がされない。デフォルトはtrue。
	 * trueをセットすると即座に画像表示が反映される。
	 * 
	 * @param isDrawable
	 */
	public void setIsDrawable(boolean isDrawable) {
		this.isDrawable = isDrawable;
		setImageBitmap(currentBitmap);
	}

	/**
	 * Viewに画像を表示する ただし、描画可能状態の場合のみ有効
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		currentBitmap = bm;

		// 描画可能状態の場合のみ描画する
		if (bm == null || isDrawable) {
			super.setImageBitmap(bm);
		}
	}

	public boolean getIsDrawable() {
		return this.isDrawable;
	}

	public AsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 非同期画像表示のセッター
	 * 
	 * @param id
	 *            画像にひもづくユニークなID。nullにすると何も反映されない。
	 * @param userData
	 *            ロードに必要な任意のデータ。(URLとか)
	 * @param loader
	 *            実際にロードを実行するリスナー
	 */
	public void setAsyncImage(Object id, Object userData, ImageLoader loader) {
		currentId = id;

		// 表示中の画像を削除
		setImageBitmap(null);
		
		// 現行のタスクを削除
		Handler h = getImageLoadHandler();
		h.removeCallbacks(currentTask);

		// スレッドで画像取得開始
		currentTask = new ImageLoadTask(id, userData, loader);
		 h.post(currentTask);
//		h.postAtFrontOfQueue(currentTask);
	}

	public Handler getImageLoadHandler() {
		if (imageLoadHandler == null) {
			//imageLoadHandlerのセットアップ
			HandlerThread handlerThread = new HandlerThread("other");
			// スレッドの優先度最低
//			handlerThread.setPriority(Thread.MIN_PRIORITY);
			handlerThread.start();
			imageLoadHandler = new Handler(handlerThread.getLooper());
		}
		return imageLoadHandler;
	}

	/**
	 * 画像のロードをするインターフェース スレッドから呼ばれる。
	 * 
	 * @author naosim
	 * 
	 */
	public static interface ImageLoader {
		public Bitmap load(Object id, Object userData);
	}

	/**
	 * スレッドで画像をロードする
	 * 
	 * @author naosim
	 * 
	 */
	private class ImageLoadTask implements Runnable {
		final public Object id;
		final public Object userData;
		public ImageLoader loader;
		private Bitmap bmp;

		/**
		 * コンストラクタ
		 * 
		 * @param id
		 * @param userData
		 * @param loader
		 */
		public ImageLoadTask(Object id, Object userData, ImageLoader loader) {
			this.id = id;
			this.userData = userData;
			this.loader = loader;
		}

		@Override
		public void run() {
			// 画像をロード
			bmp = loader.load(id, userData);

			// メインスレッドで結果を反映
			handler.postAtFrontOfQueue(new Runnable() {

				@Override
				public void run() {
					// 現在のIDと取得した画像のIDが同じ場合、画像をセットする
					if (id != null && id.equals(currentId)) {
						setImageBitmap(bmp);
					} else {
						clearBitmap();
					}
				}
			});
//			handler.post(new Runnable() {
//
//				@Override
//				public void run() {
//					// 現在のIDと取得した画像のIDが同じ場合、画像をセットする
//					if (id != null && id.equals(currentId)) {
//						setImageBitmap(bmp);
//					} else {
//						clearBitmap();
//					}
//				}
//			});
		}

		/**
		 * bitmapの解放
		 */
		public void clearBitmap() {
//			bmp.recycle();
			bmp = null;
		}
	}

}
