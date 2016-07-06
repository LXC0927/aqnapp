package com.njaqn.itravel.aqnapp;

import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.iflytek.cloud.SpeechUtility;
import com.njaqn.itravel.aqnapp.service.BaseService;
import com.njaqn.itravel.aqnapp.service.BaseServiceImpl;
import com.njaqn.itravel.aqnapp.service.bean.BCityBean;
import com.njaqn.itravel.aqnapp.service.bean.BCountryBean;
import com.njaqn.itravel.aqnapp.service.bean.BProvinceBean;
import com.umeng.comm.core.constants.Constants;
import com.umeng.message.PushAgent;
import com.umeng.message.UHandler;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Vibrator;

public class AppInfo extends Application {
	private String city;
	private String localCity;
	private int localCityId;

	public int getLocalCityId() {
		return localCityId;
	}

	public void setLocalCityId(int localCityId) {
		this.localCityId = localCityId;
	}

	public String getLocalCity() {
		return localCity;
	}

	public void setLocalCity(String localCity) {
		this.localCity = localCity;
		BaseService bs = new BaseServiceImpl();
		BCityBean cb = bs.getCityByName(localCity);
		setLocalCityId(cb.getId());
	}

	private int cityId = 1;
	private String country;
	private int spotId;
	private int jingDianId;
	private int countryId;
	private int provinceId;

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	private SharedPreferences sp;
	private Editor editor;
	private double longitude;
	private double latitude;

	public BDLocation getLocation() {
		return location;
	}

	public void setLocation(BDLocation location) {
		this.location = location;
	}

	private BDLocation location;

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getJingDianId() {
		return jingDianId;
	}

	public void setJingDianId(int jingDianId) {
		this.jingDianId = jingDianId;
	}

	public int getSpotId() {
		return spotId;
	}

	public void setSpotId(int spotId) {
		this.spotId = spotId;
	}

	public String getCityImageUrl() {
		return cityImageUrl;
	}

	public void setCityImageUrl(String cityImageUrl) {
		this.cityImageUrl = cityImageUrl;
	}

	private boolean isLogin;
	private String cityImageUrl;

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		sp = getSharedPreferences("appInfo", Activity.MODE_PRIVATE);
		editor = sp.edit();
		BaseService bs = new BaseServiceImpl();
		BCityBean cb;
		if (city != null) {
			this.city = city;
			editor.putString("city", city);
			editor.commit();
			cb = bs.getCityByName(city);
		} else {
			this.city = sp.getString("city", "南京");
			cb = bs.getCityByName(this.city);
		}

		if (cb != null) {
			this.cityId = cb.getId();
			editor.putInt("cityId", cb.getId());

			BProvinceBean pb = bs.getProvinceByCityId(cityId);
			BCountryBean countryBean = bs.getCountryByProvinceId(pb.getId());
			this.countryId = countryBean.getId();
			editor.putInt("countryId", countryBean.getId());
			this.country = countryBean.getName();
			editor.putString("countryName", countryBean.getName());
			editor.commit();
		} else {
			this.cityId = sp.getInt("cityId", 1);
			BProvinceBean pb = bs.getProvinceByCityId(cityId);
			if (pb != null) {
				BCountryBean countryBean = bs
						.getCountryByProvinceId(pb.getId());
				this.countryId = countryBean.getId();
				this.country = countryBean.getName();
			} else {
				this.countryId = 1;
				this.country = "中国";
			}

		}
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public LocationService locationService;

	@Override
	public void onCreate() {
		super.onCreate();
		SpeechUtility.createUtility(this, "appid=55379711");
		locationService = new LocationService(getApplicationContext());
		SDKInitializer.initialize(getApplicationContext());

		PlatformConfig.setWeixin("wx96110a1e3af63a39",
				"c60e3d3ff109a5d17013df272df99199");
		// 豆瓣RENREN平台目前只能在服务器端配置
		// 新浪微博
		PlatformConfig.setSinaWeibo("1149737488",
				"9b8b7f6f50d8d83be633d5b18ab70921");
		PlatformConfig.setQQZone("1105324183", "j67wUYKODKjfPkaD");
		PushAgent.getInstance(this).setDebugMode(true);
		PushAgent.getInstance(this).setMessageHandler(
				new UmengMessageHandler() {
					@Override
					public void dealWithNotificationMessage(Context arg0,
							UMessage msg) {
						// 调用父类方法,这里会在通知栏弹出提示信息
						super.dealWithNotificationMessage(arg0, msg);
						// Log.e("", "### 自行处理推送消息");
					}
				});
		PushAgent.getInstance(this).setNotificationClickHandler(new UHandler() {
			@Override
			public void handleMessage(Context context, UMessage uMessage) {
				com.umeng.comm.core.utils.Log.d("notifi", "getting message");
				try {
					JSONObject jsonObject = uMessage.getRaw();
					String feedid = "";
					if (jsonObject != null) {
						com.umeng.comm.core.utils.Log.d("json",
								jsonObject.toString());
						JSONObject extra = uMessage.getRaw().optJSONObject(
								"extra");
						feedid = extra.optString(Constants.FEED_ID);
					}
					Class myclass = Class.forName(uMessage.activity);
					Intent intent = new Intent(context, myclass);
					Bundle bundle = new Bundle();
					bundle.putString(Constants.FEED_ID, feedid);
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (Exception e) {
					com.umeng.comm.core.utils.Log.d("class", e.getMessage());
				}
			}
		});
	}

	// 如果发现Method Over 65K的错误的话就反注释这段代码
	// @Override
	// protected void attachBaseContext(Context base) {
	// super.attachBaseContext(base);
	// MultiDex.install(this);
	// }

}
