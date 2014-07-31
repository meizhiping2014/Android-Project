package com.mzp.chat.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mzp.chat.global.IpMessageConst.FileType;
import com.mzp.chat.widget.FaceDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.Toast;

public class ToolUtils {
	private static Toast myToast;
	/**
	 * 判断WIFI是否连接
	 */
	public static boolean isWifiActive(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(null != mConnectivity){
			NetworkInfo[] infos = mConnectivity.getAllNetworkInfo();
			if(null != infos){
				for(NetworkInfo netInfo: infos){
					if("WIFI".equals(netInfo.getTypeName()) && netInfo.isConnected()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	  /**
	   *Toast提示
	   * @param currentContext
	   * 				当前上下文
	   * @param message
	   * 				提示信息
	   */
	  public static void commonToast(Context currentContext ,String stringValue){
			if(myToast == null){
				myToast = Toast.makeText(currentContext, stringValue, Toast.LENGTH_SHORT);
			}else{
				//myToast.cancel();
				myToast.setText(stringValue);
			}
			myToast.show();
	  }

	  /**
	   * 获取文件类型
	   * @param f
	   * @return
	   */
	  public static String getRecordFileType(File f){
		    String type="";
		    String fName=f.getName();
		    /* 取得扩展名 */
		    String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase(); 
	           if(end.equals(FileType.avi.name())){
	              type = FileType.avi.name();
	            }else if(end.equals(FileType.mp3.name())){
	            	type = FileType.mp3.name();	
	            }else if(end.equals(FileType.pdf.name())){
	            	type = FileType.pdf.name();	
	            }else if(end.equals(FileType.ppt.name())){
	            	type = FileType.ppt.name();	
	            }else if(end.equals(FileType.word.name())){
	            	type = FileType.word.name();	
	            }else if(end.equals(FileType.xls.name())){
	            	type = FileType.xls.name();	
	            }else if(end.equals(FileType.zip.name())){
	            	type = FileType.zip.name();	
	            }else if(end.equals(FileType.jpg.name())){
	            	type = FileType.jpg.name();	
	            }else if(end.equals(FileType.png.name())){
	            	type = FileType.png.name();	
	            }else{
	              /* 如果无法直接打开，就跳出软件列表给用户选择 */
	              type="*/*";
	            }
		    return type; 
		  }
	  /**
	   * 缩放图片的方法
	   * @param bitMap
	   * @param x
	   * @param y
	   * @param newWidth
	   * @param newHeight
	   * @param matrix
	   * @param isScale
	   * @return
	   */
	  public static Bitmap fitSizePic(File f){ 
	    Bitmap resizeBmp = null;
	    BitmapFactory.Options opts = new BitmapFactory.Options(); 
	    //数字越大读出的图片占用的heap越小 不然总是溢出
	    if(f.length()<20480){         //0-20k
	      opts.inSampleSize = 1;
	    }else if(f.length()<51200){   //20-50k
	      opts.inSampleSize = 2;
	    }else if(f.length()<307200){  //50-300k
	      opts.inSampleSize = 4;
	    }else if(f.length()<819200){  //300-800k
	      opts.inSampleSize = 6;
	    }else if(f.length()<1048576){ //800-1024k
	      opts.inSampleSize = 8;
	    }else{
	      opts.inSampleSize = 10;
	    }
	    resizeBmp = BitmapFactory.decodeFile(f.getPath(),opts);
	    return resizeBmp; 
	  }
	  
	  /**
	     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	     * @param context
	     * @param str
	     * @return
	     */
	    public static SpannableString getExpressionString(Context context,String str,String zhengze){
	    	SpannableString spannableString = new SpannableString(str);
	        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		//通过传入的正则表达式来生成一个pattern
	        try {
	            dealExpression(context,spannableString, sinaPatten, 0);
	        } catch (Exception e) {
	            Log.e("dealExpression", e.getMessage());
	        }
	        return spannableString;
	    }
	    
	    /**
	     * 对spanableString进行正则判断
	     * @param context
	     * @param spannableString
	     * @param patten
	     * @param start
	     * @throws SecurityException
	     * @throws NoSuchFieldException
	     * @throws NumberFormatException
	     * @throws IllegalArgumentException
	     * @throws IllegalAccessException
	     */
	        public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
	        	Matcher matcher = patten.matcher(spannableString);
	            while (matcher.find()) {
	                String key = matcher.group();
	                if (matcher.start() < start) {
	                    continue;
	                }
//	                Field field = R.drawable.class.getDeclaredField(key);
//	    			int resId = Integer.parseInt(field.get(null).toString());		//通过上面匹配得到的字符串来生成图片资源id
	                int a=Integer.valueOf(key.replace("f", ""));
	                int resId=FaceDialog.imageIds[a%107];
	                if (resId != 0) {
	                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
	                    Drawable bd =    new BitmapDrawable(context.getResources(), bitmap);
	                    bd.setBounds(0, 0, 56, 56);
	                     ImageSpan imageSpan = new ImageSpan(bd,ImageSpan.ALIGN_BOTTOM);//通过图片资源id来得到bitmap，用一个ImageSpan来包装
	                   // Log.e("文本中图片的大小", "宽为："+bitmap.getWidth()+" 高为："+ bitmap.getHeight());
	                    int end = matcher.start() + key.length();					//计算该图片名字的长度，也就是要替换的字符串的长度
	                    spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);	//将该图片替换字符串中规定的位置中
	                    if (end < spannableString.length()) {						//如果整个字符串还未验证完，则继续。。
	                        dealExpression(context,spannableString,  patten, end);
	                    }
	                    break;
	                }
	            }
	        }
}
