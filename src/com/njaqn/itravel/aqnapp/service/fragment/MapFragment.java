package com.njaqn.itravel.aqnapp.service.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.service.LocationService;
import com.baidu.location.service.Utils;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.njaqn.itravel.aqnapp.AppInfo;
import com.njaqn.itravel.aqnapp.R;
import com.njaqn.itravel.aqnapp.am.AM001HomePageActivity;
import com.njaqn.itravel.aqnapp.am.AM006SpotActivity;
import com.njaqn.itravel.aqnapp.listener.MyOrientationListener;
import com.njaqn.itravel.aqnapp.listener.MyOrientationListener.OnOrientationListener;
import com.njaqn.itravel.aqnapp.service.AmService;
import com.njaqn.itravel.aqnapp.service.AmServiceImpl;
import com.njaqn.itravel.aqnapp.service.MapService;
import com.njaqn.itravel.aqnapp.service.MapServiceImpl;
import com.njaqn.itravel.aqnapp.service.adapter.MySwipListAdapter;
import com.njaqn.itravel.aqnapp.service.bean.JSpotBean;
import com.njaqn.itravel.aqnapp.service.bean.JingDianBean;
import com.njaqn.itravel.aqnapp.service.bean.MapResultBean;
import com.njaqn.itravel.aqnapp.util.MapUtil;
import com.njaqn.itravel.aqnapp.util.MapUtil.JingDianListener;
import com.njaqn.itravel.aqnapp.util.VoiceUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapFragment extends Fragment implements
		OnGetDistricSearchResultListener, OnGetPoiSearchResultListener {

	// map基础类
	// private MapUtil map;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	// map功能类
	private DistrictSearch mDistrictSearch;
	private PoiSearch mPoiSearch;
	private Marker facilityMarker;
	private final int cityFillColor = 0xAA24b69f;
	private final int citySideColor = 0xAA878887;
	private final int textColor = 0xFF000000;
	private final int colors[] = { 0xAA51c6f4, 0xAA0ad15a, 0xAAc0d108,
			0xAAef7ada };
	private int i = 0;
	// 用于功能显示的view
	private LinearLayout layout;
	private RelativeLayout titleLayout;
	private TextView resultTextView;
	private ImageView closeImageView;
	private String lastText;
	private SwipeMenuListView resultListView;
	private MySwipListAdapter resultAdapter;
	private SwipeMenuCreator creator;
	private List<MapResultBean> resultList;
	private List<MapResultBean> spotList;
	private int resultCount;
	private List<PoiInfo> poiInfos;
	private boolean isOpen = false;
	private boolean isSwipeOpen = false;
	private boolean isFromIntro = true;
//	private boolean isRangeSpot = false;
	private int facilityId;
	private static final int TOILET = 1;
	private static final int PARKING = 2;
	private static final int CITY = 3;
	private static final int SPOT = 4;
	private int spotId = 0;
	private String spotName;
	private AmService as;
	private BDLocation myLocation;
	private MapService mapService;
	private ImageView resultBackImageView;
	private List<JingDianBean> jingDians;

	// map定位类
	// private LocationClient locClient;
	private LocationService locService;
	private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
	boolean isFirstLoc = true; // 是否首次定位
	private BitmapDescriptor mCurrentMarker;
	private MyLocationConfiguration.LocationMode mCurrentMode;
	private ImageView requestLocButton;
	private boolean isInspot = false;
	private MapUtil mapUtil;
	private String lastJingDianName = "";
	private int mXDirection = 0;
	private MyOrientationListener myOrientationListener;

	// 应用相关类
	private AppInfo app;
	private AM001HomePageActivity activity;
	private View rootView;
	private Handler handler;
	private VoiceUtil voiceUtil;
	private float density = 1f;

	public MapFragment() {
		
	}

	public void initDensity() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		displayMetrics = getActivity().getResources().getDisplayMetrics();
		
		density = displayMetrics.density;
	}
	
    public int dp2px(int dp) {
        if (dp == 0) {
            return 0;
        }
        return (int) (dp * density + 0.5f);

    }

	/**
	 * 生命周期
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.am001_fragment_mapview, null);
		mMapView = (MapView) rootView.findViewById(R.id.bmapView);
		as = new AmServiceImpl();
		mapService = new MapServiceImpl();
		app = (AppInfo) getActivity().getApplication();
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		mBaiduMap.setMyLocationEnabled(true);
		initDensity();
		initHandler();
		initLocation();
		initMapUtil();
		setCurLocation();

		return rootView;
	}

	private void initSwipeMenuCreator() {
		creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem moreItem = new SwipeMenuItem(getActivity());
				moreItem.setBackground(new ColorDrawable(Color.rgb(0xc9, 0xc9,
						0xce)));
				moreItem.setWidth(dp2px(100));
				moreItem.setTitle("详情");
				moreItem.setTitleSize(18);
				moreItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(moreItem);

				SwipeMenuItem checkItem = new SwipeMenuItem(getActivity());
				checkItem.setBackground(new ColorDrawable(Color.rgb(0x5f, 0xae,
						0xe1)));
				checkItem.setWidth(dp2px(100));
				checkItem.setTitle("查看景点");
				checkItem.setTitleSize(18);
				checkItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(checkItem);

			}
		};

	}

	public void initMapUtil() {
		mapUtil = new MapUtil(handler);
		mapUtil.setJingDianListener(new JingDianListener() {

			@Override
			public void onApproachJingDian(int id, String jingDianName,
					double distance) {
				if (voiceUtil != null) {
					if (!lastJingDianName.equals(jingDianName)) {
						DecimalFormat decimalFormat = new DecimalFormat(".00");
						String disString = decimalFormat.format(distance);
						voiceUtil.playAudio("您已接近" + jingDianName + ",距离"
								+ disString + "米");
						lastJingDianName = jingDianName;
					}

				}

			}
		});
	}

	private void initLocation() {
		requestLocButton = (ImageView) rootView.findViewById(R.id.location_btn);
		mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setImageResource(R.drawable.location2);
					mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));

					break;
				case FOLLOWING:
					requestLocButton.setImageResource(R.drawable.location3);
					mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setImageResource(R.drawable.location1);
					mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

					MapStatus ms = new MapStatus.Builder(
							mBaiduMap.getMapStatus()).rotate(0f).overlook(0f)
							.build();
					MapStatusUpdate update = MapStatusUpdateFactory
							.newMapStatus(ms);
					mBaiduMap.animateMapStatus(update);
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				default:
					break;
				}
				if (myLocation != null) {
					setSpotRange(myLocation);
				}

			}
		};
		requestLocButton.setOnClickListener(btnClickListener);
	}

	public void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					if (resultList != null) {
						// resultAdapter = new MySwipListAdapter(resultList,
						// getActivity());
						// resultListView.setAdapter(resultAdapter);
						// resultListView.setMenuCreator(creator);
						// resultListView
						// .setOnItemClickListener(new
						// ResultItemClickListener());
						// resultListView
						// .setOnMenuItemClickListener(new
						// ResultMeauClickListener());
						resultAdapter.notifyDataSetChanged();
					}

					break;
				case 1:
					try {
						BDLocation location = msg.getData()
								.getParcelable("loc");
						int iscal = msg.getData().getInt("iscalculate");
						if (location != null) {
							myLocation = location;
							app.setLocation(location);
							app.setLongitude(location.getLongitude());
							app.setLatitude(location.getLatitude());
							if (isFirstLoc) {
								String city = location.getCity();
								app.setCity(city.substring(0, city.length() - 1));
								app.setLocalCity(city.substring(0,
										city.length() - 1));
								((AM001HomePageActivity) getActivity())
										.getBtnLocation()
										.setText(app.getCity());
								setSpotRange(location);
								isFirstLoc = false;
							}
							// if (iscal == 1) {
							MyLocationData myLocationData = new MyLocationData.Builder()
									.accuracy(location.getRadius())
									.direction(mXDirection)
									.latitude(location.getLatitude())
									.longitude(location.getLongitude()).build();
							mBaiduMap.setMyLocationData(myLocationData);
							LatLng latLng = new LatLng(location.getLatitude(),
									location.getLongitude());
							mapUtil.setLatLng(latLng);
							// }
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 2:
					if (myLocation != null) {
						JingDianBean jingDianBean = mapService
								.getNearestJingDian(new LatLng(myLocation
										.getLatitude(), myLocation
										.getLongitude()));
						mapUtil.setNearestOne(jingDianBean);
						System.out.println("获得数据一次 "+jingDianBean.getName());
					}
					break;
				default:
					break;
				}

			}
		};
	}

	@Override
	public void onStart() {
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onStop() {
		myOrientationListener.stop();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		mapUtil.destory();
		if (mPoiSearch != null) {
			mPoiSearch.destroy();
		}
		if (mDistrictSearch != null) {
			mDistrictSearch.destroy();
		}
		locService.unregisterListener(listener);
		locService.stop();
		super.onDestroy();
	}

	/**
	 * map定位
	 */
	private void setCurLocation() {
		mBaiduMap.clear();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
		locService = app.locationService;
		LocationClientOption mOption = locService
				.getDefaultLocationClientOption();
		mOption.setLocationMode(LocationMode.Battery_Saving);
		mOption.setCoorType("bd0911");
		locService.setLocationOption(mOption);
		locService.registerListener(listener);
		locService.start();
		myOrientationListener = new MyOrientationListener(getActivity());
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {

					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;

					}
				});
	}

	BDLocationListener listener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub

			if (location != null
					&& (location.getLocType() == 161 || location.getLocType() == 66)) {
				Message locMsg = handler.obtainMessage();
				locMsg.what = 1;
				Bundle locData;
				locData = Algorithm(location);
				if (locData != null) {
					locData.putParcelable("loc", location);
					locMsg.setData(locData);
					handler.sendMessage(locMsg);
				}
			}
			// if (isFirstLoc) {
			// isFirstLoc = false;
			//
			// }
		}
	};

	public void setSpotRange(BDLocation location) {
		List<JSONObject> locationSpot = as.judgeLocation(
				location.getLongitude(), location.getLatitude());
		JSONObject currentSpot = locationSpot.get(0);
		try {

			voiceUtil = ((AM001HomePageActivity) getActivity()).getVutil();
			if (currentSpot.getInt("distance") < 1000) {
				spotName = currentSpot.getString("name");
				voiceUtil
						.playAudio("您当前所在的景区是" + spotName);
				isInspot = true;
				spotId = currentSpot.getInt("ID");
				setJingDianPointer(currentSpot.getInt("ID"));
				addView(4);
				setJingDianResultListView(spotId, spotName);
				closeResult();
				closeImageView.setVisibility(View.INVISIBLE);
			} else {
				spotName = "当前不在任何景区";
				voiceUtil.playAudio("您当前不在任何景区");
				for (JSONObject j : locationSpot) {
					setSpotArea(j.getInt("ID"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSpotArea(int id) {
		List<JSONObject> spotAroundPoints = as.getSpotAroundPointsBysoptId(id);
		List<LatLng> pts = null;
		// 绘制景区范围
		if (spotAroundPoints.size() >= 3) {
			pts = new ArrayList<LatLng>();
			for (JSONObject j : spotAroundPoints) {
				LatLng pt = null;
				try {
					pt = new LatLng(j.getDouble("latitude"),
							j.getDouble("longitude"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pts.add(pt);

			}
			i++;
			i = i % 4;
			//
			OverlayOptions polygonOption = new PolygonOptions().points(pts)
					.stroke(new Stroke(1, colors[i])).fillColor(colors[i]);
			mBaiduMap.addOverlay(polygonOption);
		}
	}

	public void setJingDianPointer(int id) {
		mBaiduMap.clear();
		jingDians = as.getAllJingDianBySpotId(id);
		if (jingDians != null) {
			for (JingDianBean i : jingDians) {
				LatLng latlng = new LatLng(i.getLatitude(), i.getLongitude());
				Bundle bundle = new Bundle();
				bundle.putString("type", "jingDian");
				bundle.putString("intro", i.getIntro());
				bundle.putString("name", i.getName());
				bundle.putInt("id", i.getId());
				BitmapDescriptor bitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.am001_map_spot);
				OverlayOptions option = new MarkerOptions().position(latlng)
						.icon(bitmap).zIndex(10).period(25).extraInfo(bundle);
				mBaiduMap.addOverlay(option);
			}
		}
		JSpotBean spotBean = as.getSpotById(id);
		if (spotBean != null) {
			LatLng latLng = new LatLng(Double.parseDouble(spotBean
					.getLatitude()),
					Double.parseDouble(spotBean.getLongitude()));
			int zoom = 16;// spotBean.getZoom;
			setMapCenter(latLng, zoom, spotBean.getName());
		}
		setSpotArea(id);
	}

	private void setMapCenter(LatLng latLng, int zoom, String name) {
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(
				latLng, zoom);
		mBaiduMap.animateMapStatus(mapStatusUpdate);
		OverlayOptions ooText = new TextOptions().fontSize((int) (15*density))
				.fontColor(textColor).text(name).rotate(0).position(latLng);
		mBaiduMap.addOverlay(ooText);
	}

	/***
	 * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
	 * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
	 * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
	 * 
	 * @param BDLocation
	 * @return Bundle
	 */
	private Bundle Algorithm(BDLocation location) {
		Bundle locData = new Bundle();
		double curSpeed = 0;
		if (locationList.isEmpty() || locationList.size() < 2) {
			LocationEntity temp = new LocationEntity();
			temp.location = location;
			temp.time = System.currentTimeMillis();
			locData.putInt("iscalculate", 0);
			locationList.add(temp);
		} else {
			if (locationList.size() > 5)
				locationList.removeFirst();
			double score = 0;
			for (int i = 0; i < locationList.size(); ++i) {
				LatLng lastPoint = new LatLng(
						locationList.get(i).location.getLatitude(),
						locationList.get(i).location.getLongitude());
				LatLng curPoint = new LatLng(location.getLatitude(),
						location.getLongitude());
				double distance = DistanceUtil.getDistance(lastPoint, curPoint);
				curSpeed = distance
						/ (System.currentTimeMillis() - locationList.get(i).time)
						/ 1000;
				score += curSpeed * Utils.EARTH_WEIGHT[i];
			}
			if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
				location.setLongitude((locationList.get(locationList.size() - 1).location
						.getLongitude() + location.getLongitude()) / 2);
				location.setLatitude((locationList.get(locationList.size() - 1).location
						.getLatitude() + location.getLatitude()) / 2);
				locData.putInt("iscalculate", 1);
			} else {
				locData.putInt("iscalculate", 0);
			}
			LocationEntity newLocation = new LocationEntity();
			newLocation.location = location;
			newLocation.time = System.currentTimeMillis();
			locationList.add(newLocation);

		}
		return locData;
	}

	private class LocationEntity {
		BDLocation location;
		long time;
	}

	/**
	 * map功能
	 */
	public void addView(int switchId) {
		facilityId = switchId;
		if (facilityId == 4) {
			isFromIntro = true;
		}
		if (isOpen) {
			closeResult();
		}
		mBaiduMap.setPadding(0, 0, 0, dp2px(50));
		String result = setResultTitle(switchId);
		closeImageView.setVisibility(View.VISIBLE);
		if (layout == null) {
			layout = new LinearLayout(getActivity());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(titleLayout);

		}
		if (layout.getParent() == null) {
			MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder();
			builder.layoutMode(MapViewLayoutParams.ELayoutMode.absoluteMode);
			builder.width(mMapView.getWidth());
			builder.height(dp2px(50));
			builder.point(new Point(0, mMapView.getHeight()));
			builder.align(MapViewLayoutParams.ALIGN_LEFT,
					MapViewLayoutParams.ALIGN_BOTTOM);
			mMapView.addView(layout, builder.build());
		}
		resultTextView.setText(result);

	}

	public String setResultTitle(int switchId) {
		if (resultBackImageView != null){
			resultBackImageView.setVisibility(View.INVISIBLE);
		}
		if (resultTextView == null) {
			resultTextView = new TextView(getActivity());
			resultTextView.setTextSize(18.0f);
			resultTextView.setHeight(dp2px(50));
			resultTextView.setGravity(Gravity.CENTER);
			resultTextView.setTextColor(Color.WHITE);
			resultTextView.setBackgroundColor(Color.parseColor("#AA24b69f"));
			resultTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (resultCount > 0) {

						if (isOpen) {
							closeResult();
						} else {
							openResult();
						}
					}
				}
			});
		}
		String result = "";
		switch (switchId) {
		case CITY:
			activity = (AM001HomePageActivity) getActivity();
			int cityId = activity.getApp().getCityId();
			resultCount = mapService.getSpotCountInCity(cityId);
			result = "该城市中有" + resultCount + "个景区";
			break;
		case TOILET:
			resultCount = 0;
			result = "搜索中...";
			break;
		case PARKING:
			resultCount = 0;
			result = "搜索中...";
			break;
		default:
			break;
		}

		if (titleLayout == null) {
			titleLayout = new RelativeLayout(getActivity());
			RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(
					mMapView.getWidth(), dp2px(50));
			titleLayout.addView(resultTextView, textLayoutParams);
			closeImageView = new ImageView(getActivity());
//			closeImageView.setVisibility(View.VISIBLE);
			closeImageView.setBackgroundResource(R.drawable.map_result_close);
			closeImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {					
					mBaiduMap.clear();
					if (spotId != 0) {
						setJingDianPointer(spotId);
						closeImageView.setVisibility(View.INVISIBLE);
						setJingDianResultListView(spotId, spotName);
						closeResult();						
					} else {
						mBaiduMap.setPadding(0, 0, 0, 0);
						mMapView.removeView(layout);
						List<JSONObject> locationSpot = as.judgeLocation(
								myLocation.getLongitude(),
								myLocation.getLatitude());
					}

				}
			});
			RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(
					dp2px(35), dp2px(35));
			imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL,
					RelativeLayout.TRUE);
			titleLayout.addView(closeImageView, imageLayoutParams);

		}
		return result;
	}

	public void setCityRange(int provinceId, String cityName) {
		mMapView.removeView(layout);
		mDistrictSearch = DistrictSearch.newInstance();
		mDistrictSearch.setOnDistrictSearchListener(this);
		if (cityName != null) {
			switch (provinceId) {
			case 5:
				mDistrictSearch.searchDistrict(new DistrictSearchOption()
						.cityName("北京").districtName(cityName));
				break;
			case 6:
				mDistrictSearch.searchDistrict(new DistrictSearchOption()
						.cityName("天津").districtName(cityName));
				break;
			case 13:
				mDistrictSearch.searchDistrict(new DistrictSearchOption()
						.cityName("上海").districtName(cityName));
				break;
			case 26:
				mDistrictSearch.searchDistrict(new DistrictSearchOption()
						.cityName("重庆").districtName(cityName));
				break;
			default:
				mDistrictSearch.searchDistrict(new DistrictSearchOption()
						.cityName(cityName));
				break;
			}

		}
	}

	@Override
	public void onGetDistrictResult(DistrictResult districtResult) {
		mBaiduMap.clear();
		if (districtResult == null) {
			return;
		}
		if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
			List<List<LatLng>> polyLines = districtResult.getPolylines();
			if (polyLines == null) {
				return;
			}
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			for (List<LatLng> polyline : polyLines) {
				OverlayOptions ooPolylinell = new PolylineOptions().width(5)
						.points(polyline).dottedLine(true).color(citySideColor);
				mBaiduMap.addOverlay(ooPolylinell);

				OverlayOptions ooPolygon = new PolygonOptions()
						.points(polyline).stroke(new Stroke(1, citySideColor))
						.fillColor(cityFillColor);
				mBaiduMap.addOverlay(ooPolygon);
				for (LatLng latLng : polyline) {
					builder.include(latLng);
				}
			}
			mBaiduMap.setMapStatus(MapStatusUpdateFactory
					.newLatLngBounds(builder.build()));
		}

	}

	public void setToiletSign(LatLng latLng) {
		// mBaiduMap.clear();
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		facilityId = TOILET;
		mPoiSearch.searchNearby((new PoiNearbySearchOption()).keyword("厕所")
				.location(latLng).pageNum(0).radius(5000)
				.sortType(PoiSortType.distance_from_near_to_far));
		addView(facilityId);

	}

	public void setParkingSign(LatLng latLng) {
		// mBaiduMap.clear();
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		facilityId = PARKING;
		mPoiSearch.searchNearby((new PoiNearbySearchOption()).keyword("停车场")
				.location(latLng).pageNum(0).radius(5000)
				.sortType(PoiSortType.distance_from_near_to_far));
		addView(facilityId);
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getActivity(),
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result, facilityId);
			overlay.addToMap();
			overlay.zoomToSpan();
			poiInfos = result.getAllPoi();
			resultCount = poiInfos.size();
			String resultString = "找到最近的" + resultCount + "个结果";
			resultTextView.setText(resultString);
			if (resultList == null) {
				resultList = new ArrayList<>();// 可能没有必要
			} else {
				resultList.clear();
			}
			for (int i = 0; i < resultCount; i++) {
				String string = poiInfos.get(i).name + ":"
						+ poiInfos.get(i).address;
				resultList.add(new MapResultBean(0, string));
			}
			// resultAdapter = new MySwipListAdapter(resultList, getActivity());
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();
		}

	}

	public void closeResult() {
		isOpen = false;
		mBaiduMap.setPadding(0, 0, 0, dp2px(50));
		mMapView.removeView(layout);
		layout.removeView(resultListView);
		MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder();
		builder.layoutMode(MapViewLayoutParams.ELayoutMode.absoluteMode);
		builder.width(mMapView.getWidth());
		builder.height(dp2px(50));
		builder.point(new Point(0, mMapView.getHeight()));
		builder.align(MapViewLayoutParams.ALIGN_LEFT,
				MapViewLayoutParams.ALIGN_BOTTOM);
		mMapView.addView(layout, builder.build());
	}

	public void openResult() {
		isOpen = true;
		mBaiduMap.setPadding(0, 0, 0, dp2px(275));
		mMapView.removeView(layout);
		if (resultListView == null) {
			resultListView = new SwipeMenuListView(getActivity());
			resultListView.setBackgroundColor(Color.WHITE);
		}
		if (creator == null) {
			initSwipeMenuCreator();
		}
		if (resultList == null) {
			resultList = new ArrayList<>();
			spotList = new ArrayList<>();
		}
		if (resultAdapter == null) {
			resultAdapter = new MySwipListAdapter(resultList, getActivity());
		}
		if (resultListView.getParent() == null) {
			layout.addView(resultListView, mMapView.getWidth(), dp2px(225));
			resultListView.setAdapter(resultAdapter);
			resultListView.setMenuCreator(creator);
			resultListView
					.setOnItemClickListener(new ResultItemClickListener());
			resultListView
					.setOnMenuItemClickListener(new ResultMeauClickListener());
		}
		if (facilityId == 3) {
			new MyResultThread().run();
		}
		MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder();
		builder.layoutMode(MapViewLayoutParams.ELayoutMode.absoluteMode);
		builder.width(mMapView.getWidth());
		builder.height(dp2px(275));
		builder.point(new Point(0, mMapView.getHeight()));
		builder.align(MapViewLayoutParams.ALIGN_LEFT,
				MapViewLayoutParams.ALIGN_BOTTOM);
		mMapView.addView(layout, builder.build());
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	private class MyResultThread extends Thread {
		@Override
		public void run() {
			List<String> list = new MapServiceImpl().getSpotInCity(activity
					.getApp().getCityId(), 1);
			if (resultList == null) {
				resultList = new ArrayList<>();// 可能没有必要
			} else {
				resultList.clear();
			}
			for (int i = 0; i < list.size(); i++) {
				resultList.add(new MapResultBean(1, list.get(i)));
				spotList.add(new MapResultBean(1, list.get(i)));
			}
			handler.sendEmptyMessage(0);
			super.run();
		}
	}

	private class ResultItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (facilityId == 3) {
				mBaiduMap.clear();
				String name = ((MapResultBean) resultListView
						.getItemAtPosition(position)).getName();
				int spotId = mapService.getSpotIdByName(name);
				setJingDianPointer(spotId);

			} else if (facilityId == 4) {
				if (facilityMarker != null) {
					facilityMarker.remove();
				}
				JingDianBean jingDianBean = jingDians.get(position);
				LatLng latLng = new LatLng(jingDianBean.getLatitude(),
						jingDianBean.getLongitude());
				MapStatusUpdate mapStatusUpdate = null;
				mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(mapStatusUpdate);
				MarkerOptions options = new MarkerOptions()
						.position(latLng)
						.zIndex(10)
						.icon(new BitmapDescriptorFactory()
								.fromResource(R.drawable.am001_map_spot_selected));
				facilityMarker = (Marker) mBaiduMap.addOverlay(options);
			} else {
				if (facilityMarker != null) {
					facilityMarker.remove();
				}
				LatLng latLng = poiInfos.get(position).location;
				MapStatusUpdate mapStatusUpdate = null;
				mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(mapStatusUpdate);
				MarkerOptions options = new MarkerOptions().position(latLng)
						.icon(new BitmapDescriptorFactory()
								.fromResource(R.drawable.am001_map_spot));
				facilityMarker = (Marker) mBaiduMap.addOverlay(options);
			}

		}

	}

	private class ResultMeauClickListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
			String name = resultList.get(position).getName();
			int id = mapService.getSpotIdByName(name);
			switch (index) {
			case 0:
				if (id != 0) {
					Intent i = new Intent(getActivity(),
							AM006SpotActivity.class);
					i.putExtra("id", id + "");
					i.putExtra("name", name);
					startActivity(i);
				}
				break;
			case 1:
				if (id != 0) {
					isFromIntro = false;
					mBaiduMap.clear();
					setJingDianPointer(id);
					setJingDianResultListView(id, name);
				}
				break;
			default:
				break;
			}
			return false;
		}

	}

	public void setJingDianResultListView(int id, String name) {
		// TODO Auto-generated method stub
		closeResult();
		openResult();
		if (resultBackImageView == null) {
			resultBackImageView = new ImageView(getActivity());
			resultBackImageView
					.setBackgroundResource(R.drawable.map_result_back);
			resultBackImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					resultBackImageView.setVisibility(View.INVISIBLE);
					resultTextView.setText(lastText);
					facilityId = 3;
					resultList.clear();
					for (int i = 0; i < spotList.size(); i++) {
						resultList.add(spotList.get(i));
					}
					resultAdapter.notifyDataSetChanged();

				}
			});
			RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(
					dp2px(35), dp2px(35));
			imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL,
					RelativeLayout.TRUE);
			titleLayout.addView(resultBackImageView, imageLayoutParams);
		} else {
			resultBackImageView.setVisibility(View.VISIBLE);
		}
		if (isFromIntro) {
			resultBackImageView.setVisibility(View.INVISIBLE);
		}
		lastText = resultTextView.getText().toString();
		resultTextView.setText(name);
		resultList.clear();
		if (jingDians != null) {
			resultCount = jingDians.size();
			for (JingDianBean i : jingDians) {
				resultList.add(new MapResultBean(0, i.getName()));
			}
			
			facilityId = 4;
		}
		resultAdapter.notifyDataSetChanged();

	}

}
