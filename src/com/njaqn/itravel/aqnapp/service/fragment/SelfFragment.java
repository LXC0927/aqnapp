package com.njaqn.itravel.aqnapp.service.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.njaqn.itravel.aqnapp.R;
import com.njaqn.itravel.aqnapp.am.AM001HomePageActivity;
import com.njaqn.itravel.aqnapp.bm.BM005LoginActivity;
import com.njaqn.itravel.aqnapp.bm.BM007SettingAcitivity;
import com.njaqn.itravel.aqnapp.util.ImageDownLoader;
import com.njaqn.itravel.aqnapp.util.ImageDownLoader.onImageLoaderListener;
import com.njaqn.itravel.aqnapp.util.SharedPreferencesUtil;
import com.tencent.connect.UserInfo;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.login.Loginable;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.ui.activities.FindActivity;
import com.umeng.comm.ui.activities.UserInfoActivity;
import com.umeng.common.ui.widgets.NetworkImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
	private NetworkImageView mImageView;

	private int loginWay = 10;
	private boolean isLogined = false;

	protected CommUser mUser;

	// private Oauth2AccessToken weiboAccessToken;
	// private UsersAPI weiboUsersAPI;

	// private UserInfo qqInfo = null;
	// private Tencent mTencent;
	// private static String mAppid = "1105324183";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.am001_fragment_selfview, null);
		initView();
		mUser = new CommUser();
		initUser();
		return root;
	}

	public void initUser() {
		isLogined = CommonUtils.isLogin(getActivity());
		if (isLogined) {
			if (!mUser.equals(CommConfig.getConfig().loginedUser)) {
				mUser = CommConfig.getConfig().loginedUser;
				mlogin.setText(mUser.name);
				String iconUrl = mUser.iconUrl;
				mImageView.setImageUrl(iconUrl);
			}
		} else {
			mImageView.setImageResource(R.drawable.icon_unlogin);
			mlogin.setText("点击登陆");
		}
	}

	@Override
	public void onStart() {
		initUser();
		super.onStart();
	}

	private void initView() {
		mImageView = (NetworkImageView) root.findViewById(R.id.iv_login);
		mlogin = (TextView) root.findViewById(R.id.tv_login);
		ReltFeedback = (RelativeLayout) root.findViewById(R.id.rl_feedback);
		ReltOrder = (RelativeLayout) root.findViewById(R.id.rl_order);
		ReltSetting = (RelativeLayout) root.findViewById(R.id.rl_setting);
		ReltSpot = (RelativeLayout) root.findViewById(R.id.rl_spot);

		ReltSetting.setOnClickListener(this);
		mlogin.setOnClickListener(this);

	}

	public TextView getMlogin() {
		return mlogin;
	}

	public NetworkImageView getmImageView() {
		return mImageView;
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
			if (isLogined) {
				intent = new Intent(getActivity(), FindActivity.class);
				intent.putExtra(Constants.TAG_USER, mUser);
				startActivity(intent);
			} else {
				CommunitySDK communitySDK = ((AM001HomePageActivity) getActivity())
						.getmCommSDK();
				communitySDK.login(getActivity().getApplicationContext(),
						new LoginListener() {

							@Override
							public void onStart() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onComplete(int arg0, CommUser commUser) {
								mUser = commUser;
								mlogin.setText(commUser.name);
								String iconUrl = commUser.iconUrl;
								mImageView.setImageUrl(iconUrl);
								isLogined = true;
							}
						});
			}

			break;

		default:
			break;
		}
	}
}
