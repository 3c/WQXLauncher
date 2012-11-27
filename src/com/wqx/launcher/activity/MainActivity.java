package com.wqx.launcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wqx.launcher.adapter.ApplicationListAdapter;
import com.wqx.launcher.bean.AppListInfo;
import com.wqx.launcher.utils.DataCache;
import com.wqx.launcher.views.ScrollLayout;
import com.wqx.wqxlauncher.R;

public class MainActivity extends Activity {

	private ScrollLayout mScrollLayout;
	private int location;
	private LinearLayout NavigateLayout;

	private final int APP_PAGE_SIZE = 24; // 每页显示的模块数
	private final int COUNT_PER_LINE = 6; // 每行有几个Apk
	private int pageCounts;
	private int currentPage;

	private ArrayList<AppListInfo> appList = new ArrayList<AppListInfo>();

	private Handler h = new Handler() {
		public void handleMessage(android.os.Message msg) {
			currentPage = msg.what;
			refreshNavigateLayout();
			ImageView temp = (ImageView) NavigateLayout.getChildAt(msg.what);
			temp.setImageResource(R.drawable.dot_white);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appList = getAppList();
		loadNine();
	}

	/**
	 * load views
	 */
	private void loadNine() {

		mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayoutTest);
		pageCounts = (int) Math.ceil(appList.size() / (float) APP_PAGE_SIZE);
		DataCache.handler_home = h;
		initNavigateLayout();
		initViews();

	}

	public void initNavigateLayout() {
		NavigateLayout = (LinearLayout) findViewById(R.id.navigateLayout);
		for (int i = 0; i < pageCounts; i++) {
			ImageView dot_grey = new ImageView(this);
			dot_grey.setImageResource(R.drawable.dot_grey);
			dot_grey.setPadding(5, 0, 0, 0);
			NavigateLayout.addView(dot_grey);
		}
	}

	/**
	 * 获取配置文件里的模块数量，并根据APP_PAGE_SIZE生成相应的GridView页面
	 */
	public void initViews() {
		System.out.println("app size " + appList.size());
		h.sendEmptyMessage(0);
		ArrayList<AppListInfo> appListCache = new ArrayList<AppListInfo>();
		for (int i = 0; i < pageCounts; i++) {
			GridView appPage = new GridView(this);
			appListCache.clear();
			if (i == (pageCounts - 1)) {// 最后一页
				appListCache.addAll(appList.subList(i * APP_PAGE_SIZE, appList.size()));
			} else {
				appListCache.addAll(appList.subList(i * APP_PAGE_SIZE, (i + 1) * APP_PAGE_SIZE));
			}
			System.out.println("cache size " + appListCache.size());
			appPage.setAdapter(new ApplicationListAdapter(MainActivity.this, appListCache));
			appPage.setNumColumns(COUNT_PER_LINE);
			appPage.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					location = position + (int) APP_PAGE_SIZE * currentPage;

				}
			});
			mScrollLayout.addView(appPage);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean refreshNavigateLayout() {
		int childCount = NavigateLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			ImageView child = (ImageView) NavigateLayout.getChildAt(i);
			child.setImageResource(R.drawable.dot_grey);
		}
		return true;
	}

	public ArrayList<AppListInfo> getAppList() {
		ArrayList<AppListInfo> listApp = new ArrayList<AppListInfo>();
		List<PackageInfo> list;
		list = getPackageManager().getInstalledPackages(0);
		for (PackageInfo info : list) {
			if (info == null) {
				continue;
			}
			AppListInfo app = new AppListInfo();
			app.className = info.packageName;
			app.title = info.applicationInfo.loadLabel(getPackageManager()).toString();
			app.icon = info.applicationInfo.loadIcon(getPackageManager());
			app.checked = false;

			// 判断是否为系统应用，如果不是则打印出Application的名字。并且添加到listApp中
			// if ((info.applicationInfo.flags &
			// info.applicationInfo.FLAG_SYSTEM) == 0) {
			// System.out.println("appName=" + app.title);
			// listApp.add(app);
			// }
			listApp.add(app);
		}
		return listApp;
	}

}
