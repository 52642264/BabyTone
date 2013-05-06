package com.ou0618.babytone;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SoundActivity extends ListActivity {
	Button btn1, btn2;
	int currertindex = 0;
	private MediaRecorder mediarecorder; // 创建录音机
	private MediaPlayer playerVedio; // 创建播放器
	File recordFile = null;
	File sdcardFile = null;// 录音文件目录
	String sdcardPath = null;
	String Tempfile = "Record_temp_";
	List<String> fileList = new ArrayList<String>();
	List<String> fileNameList = new ArrayList<String>();
	private Button btn3;
	private Button btn4;
	private Button btn5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound);
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			sdcardFile = Environment.getExternalStorageDirectory();
			sdcardPath = Environment.getExternalStorageDirectory().getPath();
			// setTitle(sdcardPath.getName());
		} else {
			Toast.makeText(SoundActivity.this, "没有SD卡", 1000).show();
		}
		getRecordList();
		btn1 = (Button) findViewById(R.id.btnStart);
		btn2 = (Button) findViewById(R.id.btnStop);
		btn3 = (Button) findViewById(R.id.btnPlay);
		btn4 = (Button) findViewById(R.id.btnPause);
		btn5 = (Button) findViewById(R.id.btnPStop);
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(SoundActivity.this)
						.setTitle("请输入录音名称")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(new EditText(SoundActivity.this))
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
//										SoundActivity.this.Tempfile ="";
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).show();
				// TODO Auto-generated method stub
				try {
					recordFile = File.createTempFile(Tempfile, ".mp3",
							sdcardFile);
					mediarecorder = new MediaRecorder();
					mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					// 设置麦克风
					mediarecorder
							.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); //
					// 设置输出文件格式
					mediarecorder
							.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //
					// 设置编码格式
					mediarecorder.setOutputFile(recordFile.getAbsolutePath()); //
					// 使用绝对路径进行保存文件
					mediarecorder.prepare();
					mediarecorder.start();
					Toast.makeText(SoundActivity.this, "开始", Toast.LENGTH_LONG)
							.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (recordFile != null) {
						mediarecorder.stop();
						fileList.add(recordFile.getName());
						fileNameList.add(recordFile.getName().substring(0,
								recordFile.getName().lastIndexOf(".")));
						ArrayAdapter<String> list = new ArrayAdapter<String>(
								SoundActivity.this, R.layout.home_list,
								R.id.textView, fileNameList);
						setListAdapter(list);
						mediarecorder = null;
						Toast.makeText(SoundActivity.this, "停止",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
				}
			}
		});

		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playerVedio = new MediaPlayer();
				// AssetFileDescriptor afd = null;
				// afd=new AssetFileDescriptor(fd, startOffset, length)
				playerVedio.reset();
				try {
					playerVedio.setDataSource(recordFile.getAbsolutePath()); // 获取绝对路径来播放音频
					System.out.println(recordFile.getAbsolutePath()
							+ "--------------------------");
					playerVedio.prepare();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				playerVedio.start();
			}
		});
		btn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playerVedio.pause();
			}
		});
		btn5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (playerVedio != null) {
					playerVedio.stop();
				}
			}
		});
	}

	void getRecordList() {
		// 首先检测是否存在SDCard
		try {
			File home = sdcardFile;
			fileList.clear();
			home.list();
			if (home.list(new Fileter()).length > 0) {
				for (File file : home.listFiles(new Fileter())) {
					fileList.add(file.getName());
					fileNameList.add(file.getName().substring(0,
							file.getName().lastIndexOf(".")));
				}

				ArrayAdapter<String> list = new ArrayAdapter<String>(this,
						R.layout.home_list, R.id.textView, fileNameList);
				setListAdapter(list);
			}
		} catch (Exception e) {
			Toast.makeText(SoundActivity.this, e.getMessage(), 1000).show();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.v("debug00", "abc" + sdcardPath + "/" + fileList.get(position));
		recordFile = new File(sdcardPath + "/" + fileList.get(position));
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	class Fileter implements FilenameFilter {
		public boolean accept(File dir, String filename) {
			// Log.v("debug00", filename);
			// TODO Auto-generated method stub
			// filename.equals(".xml")
			return filename.contains(".mp3");
		}
	}
}
