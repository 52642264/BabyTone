package com.ou0618.babytone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipException;
import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener,
		OnGestureListener {

	private static final int MIN_SD_SIZE = 30;

	private ImageView iv;
	private String filePath;
	private MediaPlayer mMediaPlayer;
	private ArrayList<String> toneList;
	private int state = 0;
	private GestureDetector detector;
	private LinearLayout rl;
	private String ZIP_FILE_NAME = "BabyTone.zip";
	private SharedPreferences sp;
	private CustomProgressDialog progressDialog;
	private String verName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String verName = getVersionName();
		Log.v("debug00", "verName" + verName);
		AdManager.getInstance(this).init("4ea66934b6f1bd33",
				"cc66df9e94f8f0b1", false);
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.SIZE_320x50);
		// 获取要广告条的布局
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		// 将广告条加入到布局中
		adLayout.addView(adView);
		rl = (LinearLayout) this.findViewById(R.id.main);
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)
				|| getAvailaleSize() < MIN_SD_SIZE) {
			Toast.makeText(getApplicationContext(),
					getApplicationContext().getString(R.string.sd_statu_err),
					Toast.LENGTH_SHORT).show();
		} else {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + "BabyTone/";
			File file = new File(filePath);
			sp = getPreferences(Activity.MODE_PRIVATE);
			// 解压图片和音频
			if (sp.getInt("init", 0) == 0
					|| sp.getString("verName", "1.0").equals(verName)||(!file.exists())) {
				UnZipAsyncTask unZip = new UnZipAsyncTask();
				unZip.execute();
			} else {
				updateView();
			}
		}
		// 有米升级
		UpdateHelper update = new UpdateHelper(this);
		update.execute();
	}

	private void updateView() {
		// TODO Auto-generated method stub
		toneList = new ArrayList<String>();
		File[] dir = new File(filePath).listFiles();
		for (int i = 0; i < dir.length; i++) {
			String n[] = dir[i].getName().split("\\.");
			if (n.length > 1 && n[1].equalsIgnoreCase("mp3")) {
				toneList.add(n[0]);
			}
		}
		iv = (ImageView) this.findViewById(R.id.imageView1);
		detector = new GestureDetector(this);
		rl.setOnTouchListener(this);
		rl.setLongClickable(true);
		iv.setImageBitmap(this.getImage((String) toneList.get(state)));
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.v("debug00", "+==============");
		if (e1.getX() - e2.getX() > 120) {
			updataState(1);
			iv.setImageBitmap(this.getImage((String) toneList.get(state)));
			Log.v("debug00", "+");
			return true;
		} else if (e1.getX() - e2.getX() < -120) {
			updataState(2);
			iv.setImageBitmap(this.getImage((String) toneList.get(state)));
			Log.v("debug00", "-");
			return true;
		}

		return false;
	}

	private void updataState(int type) {
		if (type == 1) {
			state++;
			if (state >= toneList.size()) {
				state = 0;
			}
		} else if (type == 2) {
			state--;
			if (state < 0) {
				state = toneList.size() - 1;
			}
		}
	}

	private void playSound(String fileName) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(filePath + fileName + ".mp3");
			mMediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mMediaPlayer.start();

	}

	public long getAvailaleSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize / 1024 / 1024;
	}

	private String getVersionName() {
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			return "1.0";
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public Bitmap getImage(String fileName) {
		File mfile = new File(filePath + fileName + ".jpg");
		if (mfile.exists()) {// 若该文件存在
			Bitmap bm = BitmapFactory.decodeFile(filePath + fileName + ".jpg");
			return bm;
		}
		return null;
	}

	public boolean onTouch(View v, MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		playSound((String) toneList.get(state));
		return false;
	}

	private void copyDataBase() throws IOException {

		// 判断目录是否存在。如不存在则创建一个目录
		File file = new File(filePath);

		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(filePath + ZIP_FILE_NAME);
		if (!file.exists()) {
			file.createNewFile();
		}
		// Open your local db as the input stream
		InputStream myInput = this.getAssets().open(ZIP_FILE_NAME);
		// Open the empty db as the output stream128
		OutputStream myOutput = new FileOutputStream(filePath + ZIP_FILE_NAME);
		// transfer bytes from the inputfile to the outputfile130
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams136
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	// 设置三种类型参数分别为String,Integer,String 
	class UnZipAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			startProgressDialog();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				copyDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File zipFile = new File(filePath + ZIP_FILE_NAME);
			try {
				new Util().upZipFile(zipFile, filePath);
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("init", 1);
			editor.putString("verName", verName);
			editor.commit();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			stopProgressDialog();
			updateView();
		}

	}

	/**
	 * 
	 * ローディングページの先頭
	 * 
	 */

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			Toast.makeText(this, "数据解压中，请稍后", Toast.LENGTH_LONG).show();
		}
		progressDialog.show();
	}

	/**
	 * 
	 * ローディングページの終わり
	 * 
	 */

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

}
