/**
 *    ���ߣ�ë����
 */
package com.njaqn.itravel.aqnapp.am;

import com.njaqn.itravel.aqnapp.bm.BM005LoginActivity;

import com.njaqn.itravel.aqnapp.service.fragment.CommunityFragment;
import com.njaqn.itravel.aqnapp.service.fragment.FacilityFragment;
import com.njaqn.itravel.aqnapp.service.fragment.MapFragment;
import com.njaqn.itravel.aqnapp.service.fragment.SelfFragment;
import com.njaqn.itravel.aqnapp.util.MapUtil;
import com.njaqn.itravel.aqnapp.util.PlayAuditData;
import com.njaqn.itravel.aqnapp.util.VoiceUtil;
import com.njaqn.itravel.aqnapp.AppInfo;
import com.njaqn.itravel.aqnapp.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class AM001HomePageActivity extends Activity
{
    private MapUtil map = null;
    private VoiceUtil vutil;

    private ImageView imgPlayView;
    private ImageView imgCommunityView;
    private ImageView imgFacilityView;
    private ImageView imgSelfView;
    private ImageView imgMapView;

    private Button btnLocation;
    private AppInfo app;

    private MapFragment mapFrg;
    private CommunityFragment commFrg;
    private FacilityFragment facFrg;
    private SelfFragment selfFrg;

    private Animation animation;

    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	PlayAuditData data = new PlayAuditData();
	app = (AppInfo) getApplication(); // ��ȡӦ�ó���
	// ��ʼ������������
	vutil = new VoiceUtil(this.getApplicationContext(), data);

	map = new MapUtil(this.getApplicationContext(), data, app,vutil);
	setContentView(R.layout.am001_home_page);

	// ��������
	animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
	animation.setFillAfter(true);

	// ��ʼ�������ؼ�
	initView();
	setEvent(0);

    }

    public void setPlayViewAnimation()
    {
	imgPlayView.setAnimation(animation);
	imgPlayView.startAnimation(animation);
    }
    // ������������
    public void switchPlayMode(View v)
    {
	if (vutil != null)
	{
	    // playMode��ʾ����ģʽ��0��ʾδ���ţ�1��ʾ���ڲ��ţ�2��ʾ��ͣ
	    if (vutil.getPlayMode() == 0)
	    {
		vutil.start();
		imgPlayView.setBackgroundResource(R.drawable.am001_menu_c0);
		imgPlayView.setAnimation(animation);
		imgPlayView.startAnimation(animation);
		vutil.setPlayMode(1);
	    }
	    else if (vutil.getPlayMode() == 1)
	    {
		vutil.playPause();
		imgPlayView.clearAnimation();
		imgPlayView.setBackgroundResource(R.drawable.am001_menu_c1);
		vutil.setPlayMode(2);
	    }
	    else if (vutil.getPlayMode() == 2)
	    {
		vutil.playResume();
		imgPlayView.setBackgroundResource(R.drawable.am001_menu_c0);
		imgPlayView.setAnimation(animation);
		imgPlayView.startAnimation(animation);
		vutil.setPlayMode(1);
	    }
	}
    }

    // �л�����ͼ
    public void switchMainContent(View v)
    {
	setDefaultImage();
	switch (v.getId())
	{
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

    private void setEvent(int i)
    {
	FragmentManager fragManager = getFragmentManager();
	FragmentTransaction fragTransaction = fragManager.beginTransaction();
	hideFragment(fragTransaction);
	switch (i)
	{
	case 0:
	    if (mapFrg == null)
	    {
		mapFrg = new MapFragment(map, btnLocation);
		fragTransaction.add(R.id.homeView, mapFrg);
	    }
	    else
	    {
		fragTransaction.show(mapFrg);
	    }
	    break;

	case 1:
	    if (commFrg == null)
	    {
		commFrg = new CommunityFragment();
		fragTransaction.add(R.id.homeView, commFrg);
	    }
	    else
	    {
		fragTransaction.show(commFrg);
	    }
	    break;
	case 2:
	    if (facFrg == null)
	    {
		facFrg = new FacilityFragment();
		fragTransaction.add(R.id.homeView, facFrg);
	    }
	    else
	    {
		fragTransaction.show(facFrg);
	    }
	    break;
	case 3:
	    if (selfFrg == null)
	    {
		selfFrg = new SelfFragment();

		fragTransaction.add(R.id.homeView, selfFrg);
	    }
	    else
	    {
		fragTransaction.show(selfFrg);
	    }
	    break;
	}
	fragTransaction.commit();

    }

    private void hideFragment(FragmentTransaction ft)
    {
	if (mapFrg != null)
	{
	    ft.hide(mapFrg);
	}
	if (commFrg != null)
	{
	    ft.hide(commFrg);
	}
	if (facFrg != null)
	{
	    ft.hide(facFrg);
	}
	if (selfFrg != null)
	{
	    ft.hide(selfFrg);
	}
    }

    public void downloadOnClick(View v)
    {
	Intent intent = new Intent(this, AM004DownloadActivity.class);
	startActivity(intent);
    }

    public void btnLocationOnClick(View v)
    {
	Intent city = new Intent(this, AM003CityChangeActivity.class);
	startActivityForResult(city, 0);
    }

    public void btnMyOnClick(View v)
    {
	Intent login = new Intent(this, BM005LoginActivity.class);
	startActivity(login);
    }

    // ȡ�ó����л�activity����������
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
	super.onActivityResult(requestCode, resultCode, data);
	this.btnLocation.setText(app.getCity());
    }

    public void imgSearchOnClick(View v)
    {
	Intent it = new Intent(this, AM002SearchActivity.class);
	startActivity(it);
    }

    private void setDefaultImage()
    {
	imgMapView.setBackgroundResource(R.drawable.am001_menu_a0);
	imgCommunityView.setBackgroundResource(R.drawable.am001_menu_b0);
	imgFacilityView.setBackgroundResource(R.drawable.am001_menu_d0);
	imgSelfView.setBackgroundResource(R.drawable.am001_menu_e0);
    }

    // ��ʼ��view�е�ʹ�õ��Ŀؼ�
    private void initView()
    {
	imgPlayView = (ImageView) findViewById(R.id.imgPlayView);
	imgCommunityView = (ImageView) findViewById(R.id.imgCommunityView);
	imgFacilityView = (ImageView) findViewById(R.id.imgFacilityView);
	imgSelfView = (ImageView) findViewById(R.id.imgSelfView);
	imgMapView = (ImageView) findViewById(R.id.imgMapView);

	btnLocation = (Button) this.findViewById(R.id.btnLocation);
    }

}