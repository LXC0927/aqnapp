package com.njaqn.itravel.aqnapp.service.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.platform.comapi.map.r;
import com.njaqn.itravel.aqnapp.AccessTokenKeeper;
import com.njaqn.itravel.aqnapp.WeiboConstants;
import com.njaqn.itravel.aqnapp.R;
import com.njaqn.itravel.aqnapp.am.AM001HomePageActivity;
import com.njaqn.itravel.aqnapp.bm.BM005LoginActivity;
import com.njaqn.itravel.aqnapp.bm.BM007SettingAcitivity;
import com.njaqn.itravel.aqnapp.util.ImageDownLoader;
import com.njaqn.itravel.aqnapp.util.SharedPreferencesUtil;
import com.njaqn.itravel.aqnapp.util.ImageDownLoader.onImageLoaderListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.sdk.BaseUIListener;
import com.tencent.sdk.qqUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelfFragment extends Fragment implements OnClickListener {
	private View root;
	private RelativeLayout ReltOrder;
	private RelativeLayout ReltSpot;
	private RelativeLayout ReltSetting;
	private RelativeLayout ReltFeedback;
	private TextView mlogin;
	private ImageView mImageView;

	private int loginWay = 10;

	private Oauth2AccessToken weiboAccessToken;
	private UsersAPI weiboUsersAPI;

	private UserInfo qqInfo = null;
	private Tencent mTencent;
	private static String mAppid = "1105324183";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.am001_fragment_selfview, null);
		initView();
		return root;
	}

	@Override
	public void onStart() {
		mImageView.setImageResource(R.drawable.icon_unlogin);
		mlogin.setText("点击登陆/注册");
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
				getActivity(), "loginWay", Activity.MODE_PRIVATE);
		String way = sharedPreferencesUtil.getData("loginWay");
		if (way.length() > 0) {
			loginWay = Integer.parseInt(way);
		}
		switch (loginWay) {
		case 0:
			showWeiboInfo();
			break;
		case 1:
			showQQInfo();
			break;
		default:
			break;
		}

		super.onStart();
	}

	private void showQQInfo() {
		mTencent = Tencent.createInstance(mAppid, getActivity());
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(
				getActivity(), "QQOpenidAndYOKen", Activity.MODE_PRIVATE);

		String token = sharedPreferencesUtil.getData("token");
		String expires = sharedPreferencesUtil.getData("expires");
		String openId = sharedPreferencesUtil.getData("openId");
		if (token.length() > 0 && expires.length() > 0 && openId.length() > 0) {
			mTencent.setAccessToken(token, expires);
			mTencent.setOpenId(openId);
//			 qqInfo = new UserInfo(getActivity(), mTencent.getQQToken());
//			 qqInfo.getUserInfo(new BaseUIListener(getActivity(),
//			 "get_simple_userinfo"));
//			 qqUtil.showProgressDialog(getActivity(), null, null);
			if (mTencent.isSessionValid()) {
				IUiListener listener = new IUiListener() {

					@Override
					public void onError(UiError arg0) {

					}

					@Override
					public void onComplete(final Object response) {
						Message msg = new Message();
						msg.obj = response;
						msg.what = 0;
						qqHandler.sendMessage(msg);
						new Thread() {

							@Override
							public void run() {
								JSONObject json = (JSONObject) response;
								if (json.has("figureurl")) {
									Bitmap bitmap = null;
									try {
										bitmap = qqUtil.getbitmap(json
												.getString("figureurl_qq_2"));
									} catch (JSONException e) {

									}
									Message msg = new Message();
									msg.obj = bitmap;
									msg.what = 1;
									qqHandler.sendMessage(msg);
								}
							}

						}.start();
					}

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub

					}
				};
				qqInfo = new UserInfo(getActivity(), mTencent.getQQToken());
				qqInfo.getUserInfo(listener);
			}
		}

	}

	Handler qqHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						mlogin.setText(response.getString("nickname"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				mImageView.setImageBitmap(bitmap);
			}
		}

	};

	public void showWeiboInfo() {
		weiboAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
		if (weiboAccessToken != null && weiboAccessToken.isSessionValid()) {
			weiboUsersAPI = new UsersAPI(getActivity(), WeiboConstants.APP_KEY,
					weiboAccessToken);
			long uid = Long.parseLong(weiboAccessToken.getUid());
			weiboUsersAPI.show(uid, new RequestListener() {

				@Override
				public void onWeiboException(WeiboException e) {
					LogUtil.e("weibo", e.getMessage());
					ErrorInfo info = ErrorInfo.parse(e.getMessage());
					Toast.makeText(getActivity(), info.toString(),
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(String response) {
					if (!TextUtils.isEmpty(response)) {
						LogUtil.i("weibo", response);
						// 调用 User#parse 将JSON串解析成User对象
						User user = User.parse(response);
						if (user != null) {
							mlogin.setText(user.screen_name);
							String string = user.avatar_large;
							Bitmap bitmap = new ImageDownLoader(getActivity())
									.downloadImage(string,
											new onImageLoaderListener() {

												@Override
												public void onImageLoader(
														Bitmap bitmap,
														String url) {
													if (mImageView != null
															&& bitmap != null) {
														mImageView
																.setImageBitmap(bitmap);
													}

												}
											});
							if (bitmap != null) {
								mImageView.setImageBitmap(bitmap);
							}

						} else {
							Toast.makeText(getActivity(), response,
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}
	}

	private void initView() {
		mImageView = (ImageView) root.findViewById(R.id.iv_login);
		mlogin = (TextView) root.findViewById(R.id.tv_login);
		ReltFeedback = (RelativeLayout) root.findViewById(R.id.rl_feedback);
		ReltOrder = (RelativeLayout) root.findViewById(R.id.rl_order);
		ReltSetting = (RelativeLayout) root.findViewById(R.id.rl_setting);
		ReltSpot = (RelativeLayout) root.findViewById(R.id.rl_spot);

		ReltSetting.setOnClickListener(this);
		mlogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.rl_setting:
			intent = new Intent(this.getActivity(), BM007SettingAcitivity.class);
			startActivity(intent);
			break;
		case R.id.tv_login:
			intent = new Intent(this.getActivity(), BM005LoginActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
