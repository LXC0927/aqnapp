package com.njaqn.itravel.aqnapp.bm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.njaqn.itravel.aqnapp.R;
import com.njaqn.itravel.aqnapp.util.SharedPreferencesUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class BM005LoginActivity extends Activity {
	private ListView loginListView;
	private SimpleAdapter mAdapter;

//	private AuthInfo weiboAuthInfo;
//	private Oauth2AccessToken weiboAccessToken;
//	private SsoHandler weiboSsoHandler;
//
//	private Tencent mTencent;
//	private UserInfo qqInfo;
//	private static boolean isServerSideLogin = false;
	
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bm005_login);
		loginListView = (ListView) findViewById(R.id.login_list);
		mAdapter = new SimpleAdapter(this, getData(), R.layout.login_item,
				new String[] { "logo", "name" }, new int[] { R.id.login_logo,
						R.id.login_name });
		loginListView.setAdapter(mAdapter);
		loginListView.setOnItemClickListener(new LoginItemClickListener());
		findViewById(R.id.login_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				returnOnClick();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (weiboSsoHandler != null) {
//			weiboSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//		}
//
//		if (requestCode == Constants.REQUEST_LOGIN
//				|| requestCode == Constants.REQUEST_APPBAR) {
//			Tencent.onActivityResultData(requestCode, resultCode, data,
//					qqLoginListener);
//		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private class LoginItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			switch (position) {
			case 0:
//				weiboAuthInfo = new AuthInfo(BM005LoginActivity.this,
//						WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL,
//						WeiboConstants.SCOPE);
//				weiboSsoHandler = new SsoHandler(BM005LoginActivity.this,
//						weiboAuthInfo);
//				weiboSsoHandler.authorize(new AuthListener());
				break;

			case 1:
//				mTencent = Tencent.createInstance("1105324183",
//						getApplicationContext());
				onQQClickLogin();
			default:
				break;
			}
		}

	}

	public void onQQClickLogin() {
//		if (!mTencent.isSessionValid()) {
//			mTencent.login(this, "all", qqLoginListener);
//			isServerSideLogin = false;
//			Log.d("SDKQQAgentPref",
//					"FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
//		} else {
//			if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
//				mTencent.logout(this);
//				mTencent.login(this, "all", qqLoginListener);
//				isServerSideLogin = false;
//				Log.d("SDKQQAgentPref",
//						"FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
//				return;
//			}
//			mTencent.logout(this);
//		}

	}
//
//	IUiListener qqLoginListener = new BaseUiListener() {
//		protected void doComplete(JSONObject values) {
//			Log.d("SDKQQAgentPref",
//					"AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
//			initOpenidAndToken(values);
//		};
//	};

	public void initOpenidAndToken(JSONObject jsonObject) {
//		try {
//			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
//			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
//			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
//			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
//					&& !TextUtils.isEmpty(openId)) {
//				mTencent.setAccessToken(token, expires);
//				mTencent.setOpenId(openId);
//			}
//			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
//					BM005LoginActivity.this, "QQOpenidAndYOKen",
//					Activity.MODE_PRIVATE);
//			sharedPreferencesUtil.saveDate("token", token);
//			sharedPreferencesUtil.saveDate("expires", expires);
//			sharedPreferencesUtil.saveDate("openId", openId);
//			returnOnClick();
//		} catch (Exception e) {
//		}
	}

//	private class BaseUiListener implements IUiListener {
//
//		@Override
//		public void onComplete(Object response) {
//			if (null == response) {
//				qqUtil.showResultDialog(BM005LoginActivity.this, "返回为空", "登录失败");
//				return;
//			}
//			JSONObject jsonResponse = (JSONObject) response;
//			if (null != jsonResponse && jsonResponse.length() == 0) {
//				qqUtil.showResultDialog(BM005LoginActivity.this, "返回为空", "登录失败");
//				return;
//			}
////			qqUtil.showResultDialog(BM005LoginActivity.this,
////					response.toString(), "登录成功");
//			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
//					BM005LoginActivity.this, "loginWay",
//					Activity.MODE_PRIVATE);
//			sharedPreferencesUtil.saveDate("loginWay", "1");
//			Toast.makeText(BM005LoginActivity.this, "QQ登录成功", Toast.LENGTH_SHORT).show();
//			doComplete((JSONObject) response);
//		}
//
//		protected void doComplete(JSONObject values) {
//
//		}
//
//		@Override
//		public void onError(UiError e) {
//			qqUtil.toastMessage(BM005LoginActivity.this, "onError: "
//					+ e.errorDetail);
//			qqUtil.dismissDialog();
//		}
//
//		@Override
//		public void onCancel() {
//			qqUtil.toastMessage(BM005LoginActivity.this, "onCancel: ");
//			qqUtil.dismissDialog();
//			if (isServerSideLogin) {
//				isServerSideLogin = false;
//			}
//		}
//	}

	private List<HashMap<String, Object>> getData() {
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> map = new HashMap<>();
		map.put("logo", R.drawable.weibo_logo);
		map.put("name", "微博登录");
		list.add(map);

		HashMap<String, Object> map2 = new HashMap<>();
		map2.put("logo", R.drawable.qq_logo);
		map2.put("name", "QQ登录");
		list.add(map2);
		return list;
	}

	public void returnOnClick() {
		this.finish();
	}

	/**
	 * 登入按钮的监听器，接收授权结果。
	 */
//	private class AuthListener implements WeiboAuthListener {
//		@Override
//		public void onComplete(Bundle values) {
//			weiboAccessToken = Oauth2AccessToken.parseAccessToken(values);
//			if (weiboAccessToken != null && weiboAccessToken.isSessionValid()) {
//				AccessTokenKeeper.writeAccessToken(getApplicationContext(),
//						weiboAccessToken);
//				SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
//						BM005LoginActivity.this, "loginWay",
//						Activity.MODE_PRIVATE);
//				sharedPreferencesUtil.saveDate("loginWay", "0");
//				Toast.makeText(BM005LoginActivity.this, "微博登录成功",
//						Toast.LENGTH_SHORT).show();
//				returnOnClick();
//			} else {
//				// 以下几种情况，您会收到 Code：
//				// 1. 当您未在平台上注册的应用程序的包名与签名时；
//				// 2. 当您注册的应用程序包名与签名不正确时；
//				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//				String code = values.getString("code");
//				String message = "failed";
//				if (!TextUtils.isEmpty(code)) {
//					message = message + "\nObtained the code: " + code;
//				}
//				Toast.makeText(BM005LoginActivity.this, message,
//						Toast.LENGTH_SHORT).show();
//			}
//		}
//
//		@Override
//		public void onWeiboException(WeiboException e) {
//			Toast.makeText(BM005LoginActivity.this, e.getMessage(),
//					Toast.LENGTH_SHORT).show();
//		}
//
//		@Override
//		public void onCancel() {
//			Toast.makeText(BM005LoginActivity.this, "cancle",
//					Toast.LENGTH_SHORT).show();
//		}
//	}

}
