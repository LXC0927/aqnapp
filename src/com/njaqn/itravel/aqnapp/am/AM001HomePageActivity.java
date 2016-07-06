package com.njaqn.itravel.aqnapp.am;

import com.njaqn.itravel.aqnapp.service.fragment.FacilityFragment;
import com.njaqn.itravel.aqnapp.service.fragment.MapFragment;
import com.njaqn.itravel.aqnapp.service.fragment.SelfFragment;
import com.njaqn.itravel.aqnapp.util.VoiceUtil;
import com.njaqn.itravel.aqnapp.AppInfo;
import com.njaqn.itravel.aqnapp.R;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.ui.fragments.CommunityMainFragment;
import com.umeng.community.location.DefaultLocationImpl;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AM001HomePageActivity extends FragmentActivity {
	// private MapUtil map = null;
	private VoiceUtil vutil;

	public VoiceUtil getVutil() {
		return vutil;
	}
	
	private TextView topTitle;
	private ImageView imgSearch;
	private ImageView imgPlayView;
	private ImageView imgCommunityView;
	private ImageView imgFacilityView;
	private ImageView imgSelfView;
	private ImageView imgMapView;
	private View layoutMapView;

	public View getLayoutMapView() {
		return layoutMapView;
	}

	private Button btnLocation;

	public Button getBtnLocation() {
		return btnLocation;
	}

	private AppInfo app;

	public AppInfo getApp() {
		return app;
	}

	private MapFragment mapFrg;

	public MapFragment getMapFrg() {
		return mapFrg;
	}

	private CommunityMainFragment commFrg;
	private FacilityFragment facFrg;
	private SelfFragment selfFrg;

	private Animation animation;

	private CommunitySDK mCommSDK = null;
	public CommunitySDK getmCommSDK() {
		return mCommSDK;
	}


	private String topicId = "";

	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 1、初始化友盟微社区
		mCommSDK = CommunityFactory.getCommSDK(this);
		super.onCreate(savedInstanceState);
		// 设置地理位置SDK
		LocationSDKManager.getInstance().addAndUse(new DefaultLocationImpl());
		app = (AppInfo) getApplication(); // 获取Application
		// 初始化语音播放配置
		vutil = new VoiceUtil(this);

		setContentView(R.layout.am001_home_page);

		//
		animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		animation.setFillAfter(true);

		// 初始化控件
		initView();
		vutil.changImageMode(imgPlayView, animation);
		setEvent(0);

	}

	public void setPlayViewAnimation() {
		imgPlayView.setAnimation(animation);
		imgPlayView.startAnimation(animation);
	}

	// 切换播放模式
	public void switchPlayMode(View v) {
		if (vutil != null) {
			if (vutil.getPlayMode() == 1) {
				vutil.playPause();
			} else if (vutil.getPlayMode() == 2) {
				vutil.playResume();
			}
		}
	}

	public void switchMainContent(View v) {
		setDefaultImage();
		switch (v.getId()) {
		case R.id.layoutMapView:
			imgMapView.setBackgroundResource(R.drawable.am001_menu_a1);
			setEvent(0);
			break;

		case R.id.layoutCommunityView:
			imgCommunityView.setBackgroundResource(R.drawable.am001_menu_b1);
			setEvent(1);
			break;
		case R.id.layoutFacilityView:
			setEvent(2);
			imgFacilityView.setBackgroundResource(R.drawable.am001_menu_d1);
			break;
		case R.id.layoutSelfView:
			setEvent(3);
			imgSelfView.setBackgroundResource(R.drawable.am001_menu_e1);
			break;
		}
	}

	private void setEvent(int i) {
		FragmentManager fragManager = getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragManager.beginTransaction();
		hideFragment(fragTransaction);
		resetTopMeau();
		switch (i) {
		case 0:
			topTitle.setText("");
			imgSearch.setVisibility(View.VISIBLE);
			btnLocation.setVisibility(View.VISIBLE);		
			if (mapFrg == null) {
				mapFrg = new MapFragment();
				fragTransaction.add(R.id.homeView, mapFrg);
			} else {
				fragTransaction.show(mapFrg);
			}
			break;

		case 1:
			topTitle.setText("社区");
			if (commFrg == null) {
				commFrg = new CommunityMainFragment();
				commFrg.setBackButtonVisibility(View.GONE);
				fragTransaction.add(R.id.homeView, commFrg);
			} else {
				fragTransaction.show(commFrg);
			}
			break;
		case 2:
			topTitle.setText("设施");
			if (facFrg == null) {
				facFrg = new FacilityFragment();
				fragTransaction.add(R.id.homeView, facFrg);
			} else {
				fragTransaction.show(facFrg);
			}
			break;
		case 3:
			topTitle.setText("我的");
			if (selfFrg == null) {
				selfFrg = new SelfFragment();

				fragTransaction.add(R.id.homeView, selfFrg);
			} else {
				fragTransaction.show(selfFrg);
				selfFrg.initUser();
			}
			break;
		}
		fragTransaction.commit();

	}

	private void resetTopMeau() {
		topTitle.setText("");
		imgSearch.setVisibility(View.INVISIBLE);
		btnLocation.setVisibility(View.INVISIBLE);		
	}

	private void hideFragment(FragmentTransaction ft) {
		if (mapFrg != null) {
			ft.hide(mapFrg);
		}
		if (commFrg != null) {
			ft.hide(commFrg);
		}
		if (facFrg != null) {
			ft.hide(facFrg);
		}
		if (selfFrg != null) {
			ft.hide(selfFrg);
		}
	}

	public void btnLocationOnClick(View v) {
		Intent city = new Intent(this, AM003CityChangeActivity.class);
		startActivityForResult(city, 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			this.btnLocation.setText(app.getCity());
			switchMainContent(layoutMapView);
			mapFrg.setCityRange(app.getProvinceId(), app.getCity());
			mapFrg.addView(3);
		} else {
			ShareSDKManager.getInstance().getCurrentSDK()
					.onActivityResult(this, requestCode, resultCode, data);
		}

	}

	public void imgSearchOnClick(View v) {
		Intent it = new Intent(this, AM002SearchActivity.class);
		startActivity(it);
	}

	private void setDefaultImage() {
		imgMapView.setBackgroundResource(R.drawable.am001_menu_a0);
		imgCommunityView.setBackgroundResource(R.drawable.am001_menu_b0);
		imgFacilityView.setBackgroundResource(R.drawable.am001_menu_d0);
		imgSelfView.setBackgroundResource(R.drawable.am001_menu_e0);
	}

	private void initView() {
		topTitle = (TextView) findViewById(R.id.topTitle);
		imgSearch = (ImageView) findViewById(R.id.imgSearch);
		imgPlayView = (ImageView) findViewById(R.id.imgPlayView);
		imgCommunityView = (ImageView) findViewById(R.id.imgCommunityView);
		imgFacilityView = (ImageView) findViewById(R.id.imgFacilityView);
		imgSelfView = (ImageView) findViewById(R.id.imgSelfView);
		imgMapView = (ImageView) findViewById(R.id.imgMapView);
		layoutMapView = findViewById(R.id.layoutMapView);
		btnLocation = (Button) this.findViewById(R.id.btnLocation);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		if (type != null) {
			switch (type) {
			case "spot":
				intent.putExtra("type", "null");
				int spotId = Integer.parseInt(intent.getStringExtra("id"));
				mapFrg.setJingDianPointer(spotId);
				mapFrg.addView(4);
				mapFrg.setJingDianResultListView(spotId,
						intent.getStringExtra("spotName"));
				break;

			default:
				break;
			}
		}

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
//		super.onSaveInstanceState(outState);
	}

}
