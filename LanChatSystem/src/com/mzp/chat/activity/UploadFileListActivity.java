package com.mzp.chat.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mzp.chat.adapter.FileListAdapter;
import com.mzp.chat.bean.FileInfo;
import com.mzp.chat.util.ToolUtils;

public class UploadFileListActivity extends BaseActivity {
	private static String TAG = "UploadFileListActivity";
	private static final String SDCardRoot = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	private List<FileInfo> fileInfos = null;
	private TextView path_view;
	private ListView fileList;
	private FileListAdapter fileAdapter;
	private RelativeLayout back_layout;
	private Button sendFileBtn;
	private int checkNum; // ��¼ѡ�е���Ŀ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist_view);
		path_view = (TextView) findViewById(R.id.path_view);
		fileList = (ListView) findViewById(R.id.fileList);
		back_layout = (RelativeLayout) findViewById(R.id.back_layout);
		sendFileBtn = (Button) findViewById(R.id.sendFileBtn);
		sendFileBtn.setText("���� 0/5");
		fileList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FileInfo info = fileInfos.get(position);
				File file = new File(info.getFilePath());
				back_layout.setVisibility(View.VISIBLE);
				if (file.isDirectory()) {
					getFileDir(file.getPath());
					checkNum = 0;
				} else {
					// ��CheckBox��ѡ��״����¼����

					if (info.isCheck()) {
						checkNum--;
						info.setCheck(false);
					} else {
						if (5 > checkNum) {
							checkNum++;
							info.setCheck(true);
						} else {
							ToolUtils.commonToast(UploadFileListActivity.this,
									"�ļ��������ܳ���5����");
						}
					}
					fileAdapter.notifyDataSetChanged();

				}
				sendFileBtn.setText("���� " + checkNum + "/5");
			}
		});

		sendFileBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkNum == 0) {
					ToolUtils.commonToast(UploadFileListActivity.this,
							"�����ļ�����Ϊ�գ�");
					return;
				}
				String file_path = null;
				for (FileInfo info : fileInfos) {
					if (info.isCheck()) {
						if (null == file_path) {
							file_path = info.getFilePath();
						} else {
							file_path += "\0" + info.getFilePath();
						}
					}
				}
				Intent intent = new Intent();
				intent.putExtra("filePaths", file_path);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		getFileDir(SDCardRoot);
		back_layout.setOnClickListener(new TextViewListener());
	}

	/**
	 * ȡ���ļ��ṹ�ķ���
	 * 
	 * @param filePath
	 */
	private void getFileDir(String filePath) {
		/* ����Ŀǰ����·�� */
		path_view.setText(filePath);
		fileInfos = new ArrayList<FileInfo>();
		File f = new File(filePath);
		File[] files = f.listFiles();
		// boolean temp = false;
		if (files != null) {
			if (files.length > 0) {
				/* �������ļ����ArrayList�� */
				for (int i = 0; i < files.length; i++) {
					FileInfo f_info = new FileInfo();
					f_info.setFileName(files[i].getName());
					f_info.setFilePath(files[i].getPath());
					f_info.setCheck(false);
					fileInfos.add(f_info);
				}
			}

		}
		/* ʹ���Զ����MyAdapter�������ݴ���ListActivity */
		fileAdapter = new FileListAdapter(this, fileInfos);
		fileList.setAdapter(fileAdapter);
	}

	class TextViewListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			path_view = (TextView) findViewById(R.id.path_view);
			File file = new File(path_view.getText().toString());
			System.out.println(file.getParent());
			if (SDCardRoot.equals(file.getParent())) {
				back_layout.setVisibility(View.INVISIBLE);
			}
			if (!SDCardRoot.equals(path_view.getText().toString())) {
				getFileDir(file.getParent());
			}
			checkNum = 0;
			sendFileBtn.setText("���� " + checkNum + "/5");
		}
	}

	@Override
	public void onResume() {
		Log.e(TAG, "========onResume============");
		// getFileDir(path_view.getText().toString());
		super.onResume();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "========onDestroy============");
		super.onDestroy();
	}

	@Override
	public void processMessage(Message msg) {
	}

}
