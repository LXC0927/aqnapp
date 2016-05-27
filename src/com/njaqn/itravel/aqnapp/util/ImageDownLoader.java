package com.njaqn.itravel.aqnapp.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

public class ImageDownLoader {

	/** 
     * ����Image���࣬���洢Image�Ĵ�С����LruCache�趨��ֵ��ϵͳ�Զ��ͷ��ڴ� 
     */  
    private LruCache<String, Bitmap> mMemoryCache;  
    /** 
     * �����ļ�������������� 
     */  
    private ImageFileUtil fileUtils;  
    /** 
     * ����Image���̳߳� 
     */  
    private ExecutorService mImageThreadPool = null;  
      
      
    public ImageDownLoader(Context context){  
        //��ȡϵͳ�����ÿ��Ӧ�ó��������ڴ棬ÿ��Ӧ��ϵͳ����32M  
        int maxMemory = (int) Runtime.getRuntime().maxMemory();    
        int mCacheSize = maxMemory / 4;  
        Log.i("CacheSize", mCacheSize+"");
        //��LruCache����1/8 4M  
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){  
  
            //������д�˷�����������Bitmap�Ĵ�С  
            @Override  
            protected int sizeOf(String key, Bitmap value) {  
                return value.getRowBytes() * value.getHeight();  
            }  
              
        };  
          
        fileUtils = new ImageFileUtil(context);  
    }  
      
      
    /** 
     * ��ȡ�̳߳صķ�������Ϊ�漰�����������⣬���Ǽ���ͬ���� 
     * @return 
     */  
    public ExecutorService getThreadPool(){  
        if(mImageThreadPool == null){  
            synchronized(ExecutorService.class){  
                if(mImageThreadPool == null){  
                    //Ϊ������ͼƬ��ӵ���������������3���߳�������ͼƬ  
                    mImageThreadPool = Executors.newFixedThreadPool(3);  
                }  
            }  
        }  
          
        return mImageThreadPool;  
          
    }  
      
    /** 
     * ���Bitmap���ڴ滺�� 
     * @param key 
     * @param bitmap 
     */  
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {    
        if (getBitmapFromMemCache(key) == null && bitmap != null) {    
            mMemoryCache.put(key, bitmap);    
        }    
    }    
       
    /** 
     * ���ڴ滺���л�ȡһ��Bitmap 
     * @param key 
     * @return 
     */  
    public Bitmap getBitmapFromMemCache(String key) {    
        return mMemoryCache.get(key);    
    }   
      
    /** 
     * �ȴ��ڴ滺���л�ȡBitmap,���û�оʹ�SD�������ֻ���л�ȡ��SD�������ֻ�� 
     * û�о�ȥ���� 
     * @param url 
     * @param listener 
     * @return 
     */  
    public Bitmap downloadImage(final String url, final onImageLoaderListener listener){   
        final String subUrl = url.replaceAll("[^\\w]", "");  
        Bitmap bitmap = showCacheBitmap(subUrl);  
        if(bitmap != null){  
            return bitmap;  
        }else{  
              
            final Handler handler = new Handler(){  
                @Override  
                public void handleMessage(Message msg) {  
                    super.handleMessage(msg);  
                    listener.onImageLoader((Bitmap)msg.obj, url);  
                }  
            };  
              
            getThreadPool().execute(new Runnable() {  
                  
                @Override  
                public void run() {  
                    Bitmap bitmap = getBitmapFormUrl(url);  
                    Message msg = handler.obtainMessage();  
                    msg.obj = bitmap;  
                    handler.sendMessage(msg);  
                      
                    try {  
                        //������SD�������ֻ�Ŀ¼  
                        fileUtils.savaBitmap(subUrl, bitmap);  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                      
                    //��Bitmap �����ڴ滺��  
                    addBitmapToMemoryCache(subUrl, bitmap);  
                }  
            });  
        }  
          
        return null;  
    }  
      
    /** 
     * ��ȡBitmap, �ڴ���û�о�ȥ�ֻ����sd���л�ȡ����һ����getView�л���ã��ȽϹؼ��һ�� 
     * @param url 
     * @return 
     */  
    public Bitmap showCacheBitmap(String url){  
        if(getBitmapFromMemCache(url) != null){  
            return getBitmapFromMemCache(url);  
        }else if(fileUtils.isFileExists(url) && fileUtils.getFileSize(url) != 0){  
            //��SD����ȡ�ֻ������ȡBitmap  
            Bitmap bitmap = fileUtils.getBitmap(url);  
              
            //��Bitmap �����ڴ滺��  
            addBitmapToMemoryCache(url, bitmap);  
            return bitmap;  
        }  
          
        return null;  
    }  
      
      
    /** 
     * ��Url�л�ȡBitmap 
     * @param url 
     * @return 
     */  
    private Bitmap getBitmapFormUrl(String url) {  
        Bitmap bitmap = null;  
        HttpURLConnection con = null;  
        try {  
            URL mImageUrl = new URL(url);  
            con = (HttpURLConnection) mImageUrl.openConnection();  
            con.setConnectTimeout(10 * 1000);  
            con.setReadTimeout(10 * 1000);  
            con.setDoInput(true);  
            con.setDoOutput(true);  
            bitmap = BitmapFactory.decodeStream(con.getInputStream());  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (con != null) {  
                con.disconnect();  
            }  
        }  
        return bitmap;  
    }  
      
    /** 
     * ȡ���������ص����� 
     */  
    public synchronized void cancelTask() {  
        if(mImageThreadPool != null){  
            mImageThreadPool.shutdownNow();  
            mImageThreadPool = null;  
        }  
    }  
      
      
    /** 
     * �첽����ͼƬ�Ļص��ӿ� 
     * @author len 
     * 
     */  
    public interface onImageLoaderListener{  
        void onImageLoader(Bitmap bitmap, String url);  
    }  
}
