package com.mzp.chat.adapter;



import java.io.File;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.mzp.chat.activity.R;
import com.mzp.chat.bean.FileInfo;
import com.mzp.chat.global.IpMessageConst.FileType;
import com.mzp.chat.util.ToolUtils;

/**
 * ץ���ļ��б��������
 */
/* �Զ����Adapter���̳�android.widget.BaseAdapter */
public class FileListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Bitmap mIcon_directory;
	private Bitmap mIcon_avi;
	private Bitmap mIcon_ppt;
	private Bitmap mIcon_mp3;
	private Bitmap mIcon_pdf;
	private Bitmap mIcon_xls;
	private Bitmap mIcon_zip;
	private Bitmap mIcon_word;
	private Bitmap mIcon_default;
	private List<FileInfo> fileInfos;
	/* MyAdapter�Ĺ����� */
	public FileListAdapter(Context context, List<FileInfo> files) {

		mInflater = LayoutInflater.from(context);
		fileInfos = files;
		mIcon_directory = BitmapFactory.decodeResource(context.getResources(),R.drawable.file_directory); // �ļ���
		mIcon_avi = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_avi); // AVI
		mIcon_ppt = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_ppt); // PPT
		mIcon_mp3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_mp3); // MP3
		mIcon_pdf = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_pdf); // PDF
		mIcon_xls = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_xls); // XLS
		mIcon_zip = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_zip); // ZIP
		mIcon_word = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_word); // WORD
		mIcon_default = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_default); // Ĭ��
	}
	

	
	/* ��̳�BaseAdapter������д���·��� */
	@Override
	public int getCount() {
		//Log.e("RecordFileListAdapter", "��ǰ�б����~~~~~~~~~~~~~~~~~"+fileInfos.size());
		return fileInfos.size();
	} 

	@Override
	public Object getItem(int position) {
		return fileInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup par) {
		ViewHolder holder = null;
		Bitmap bitMap = null;
		if (convertView == null) {
			/* ʹ���Զ����list_items��ΪLayout */
			convertView = mInflater.inflate(R.layout.filelist_items, null);
			/* ��ʼ��holder��text��icon */
			holder = new ViewHolder();
			holder.f_title = ((TextView) convertView.findViewById(R.id.f_title));
			holder.f_icon = ((ImageView) convertView.findViewById(R.id.f_icon));
			holder.f_cb = (CheckBox) convertView.findViewById(R.id.isNotFolder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FileInfo fileInfo = fileInfos.get(position);
		File f = new File(fileInfo.getFilePath());
		/* �����ļ����ļ��е�������icon */
		holder.f_title.setText(f.getName());
		String f_type = ToolUtils.getRecordFileType(f);
		
		
		if (f.isDirectory()) {
			holder.f_cb.setVisibility(View.GONE);
			holder.f_icon.setImageBitmap(mIcon_directory);
		} else {
			holder.f_cb.setVisibility(View.VISIBLE);
		if(FileType.jpg.name().equals(f_type) || FileType.png.name().equals(f_type)){
				bitMap = ToolUtils.fitSizePic(f);
				if (bitMap != null) {
					holder.f_icon.setImageBitmap(bitMap);
				} else {
					holder.f_icon.setImageBitmap(mIcon_default);
				}
				bitMap = null;
			}else if(FileType.avi.name().equals(f_type)){
				holder.f_icon.setImageBitmap(mIcon_avi);
            }else if(FileType.mp3.name().equals(f_type)){
				holder.f_icon.setImageBitmap(mIcon_mp3);
            }else if(FileType.pdf.name().equals(f_type)){
				holder.f_icon.setImageBitmap(mIcon_pdf);
            }else if(FileType.ppt.name().equals(f_type)){
				holder.f_icon.setImageBitmap(mIcon_ppt);
            }else if(FileType.word.name().equals(f_type)){
				holder.f_icon.setImageBitmap(mIcon_word);
            }else if(FileType.xls.name().equals(f_type)){
				holder.f_icon.setImageBitmap(mIcon_xls);
            }else if(FileType.zip.name().equals(f_type)){
				holder.f_icon.setImageBitmap(mIcon_zip);
            }else{
				holder.f_icon.setImageBitmap(mIcon_default);
			}
		}
		holder.f_cb.setChecked(fileInfo.isCheck());
		return convertView;
	}

	
	/**
	 * ������дget set�������Ч�� class ViewHolder
	 * */
	private class ViewHolder {
		TextView f_title;
		//TextView f_text;
		ImageView f_icon;
		CheckBox f_cb;
	}
}