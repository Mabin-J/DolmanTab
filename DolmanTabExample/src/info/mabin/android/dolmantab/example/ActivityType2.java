package info.mabin.android.dolmantab.example;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import info.mabin.android.dolmantab.DolmanTabInterface;
import info.mabin.android.dolmantab.DolmanTabWidget;
import info.mabin.android.dolmantab.DolmanTabLayout;
import info.mabin.android.dolmantab.DolmanTabAdapter;

public class ActivityType2 extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type2);

		DolmanTabLayout dolmanTabLayout = (DolmanTabLayout) findViewById(R.id.tabLayout);

		TabsAdapter tabsAdapter = new TabsAdapter(this, dolmanTabLayout);
		dolmanTabLayout.setPageAnimator(DolmanTabInterface.PageAnimator.CARD_FLIP);

		tabsAdapter.addTab(dolmanTabLayout.newTab().setText("Front"), Fragment1.class, null);
		tabsAdapter.addTab(dolmanTabLayout.newTab().setText("Back"), Fragment2.class, null);
		tabsAdapter.addTab(dolmanTabLayout.newTab().setText("Text"), Fragment3.class, null);
	}

	private class TabsAdapter extends DolmanTabAdapter{
		private List<Fragment> listFragment = new ArrayList<Fragment>();

		public TabsAdapter(Context context, DolmanTabLayout layout) {
			super(context, layout);
		}

		@Override
		public void addTab(DolmanTabWidget.Tab tab, Class<?> clss, Bundle args){
			Fragment tabFragment = Fragment.instantiate(ActivityType2.this, clss.getName());
			listFragment.add(tabFragment);

			super.addTab(tab, clss, args);
		}

		@Override
		public Fragment getItem(int position) {
			return listFragment.get(position);
		}

		@Override
		public int getCount() {
			return listFragment.size();
		}

		@Override
		public void onTabSelected(int position, DolmanTabLayout layout) {
			Log.d("onTabSelected", "Position: " + position);
			layout.setCurrentTab(position);
		}

		@Override
		public void onTabUnselected(int position, DolmanTabLayout layout) {
			Log.d("onTabUnSelected", "Position: " + position);
		}

		@Override
		public void onTabReselected(int position, DolmanTabLayout layout) {
			Log.d("onTabReSelected", "Position: " + position);
		}

		@Override
		public void onPageSelected(int position, DolmanTabWidget widget) {
			widget.setCurrentTab(position);
		}

		@Override
		public void onPageUnselected(int position, DolmanTabWidget widget) {}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				DolmanTabWidget widget) {
			widget.moveTabLocation(position, positionOffset);
		}
	}
}