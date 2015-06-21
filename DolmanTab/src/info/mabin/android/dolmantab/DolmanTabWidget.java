package info.mabin.android.dolmantab;

import info.mabin.android.dolmantab.DolmanTabInterface.OnTabListener;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DolmanTabWidget extends RelativeLayout{
	private final static int COLOR_ICS_CYAN = 0xff33b5e5;

	private Context context;
	
	private LinearLayout widgetBackground;
	private RelativeLayout indicatorRail;
	private LinearLayout tabIndicator, tabIndicatorExtra;
	private LinearLayout tabViewLabel;
	private LinearLayout tabViewBackground;

	private boolean isLockedTouch = false;
	
	private int tabIndicatorWidth = 0;
	private int currentTabIndex = 0;
	private int widgetWidth;
	
	private OnTabListener onTabListener;
	
	private ArrayList<Tab> listTab;
	private DolmanTabLayout dolmanTabLayout;
	
	

	public DolmanTabWidget(Context context) {
		super(context);
		init(context);
	}

	public DolmanTabWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public DolmanTabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init (Context context){
		this.context = context;
		this.listTab = new ArrayList<Tab>();

		widgetBackground = new LinearLayout(context);
		widgetBackground.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		widgetBackground.setOrientation(LinearLayout.HORIZONTAL);
		widgetBackground.setBackgroundColor(Color.TRANSPARENT);
		
		if(this.getBackground() == null){
			widgetBackground.addView(new DefaultWidgetBackground(context));
		}
		this.addView(widgetBackground);

		tabViewBackground = new LinearLayout(context);
		tabViewBackground.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		tabViewBackground.setOrientation(LinearLayout.HORIZONTAL);
		tabViewBackground.setBackgroundColor(Color.TRANSPARENT);
		this.addView(tabViewBackground);
		
		indicatorRail = new RelativeLayout(context);
		indicatorRail.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		indicatorRail.setBackgroundColor(Color.TRANSPARENT);
		this.addView(indicatorRail);
		
		tabIndicator = new LinearLayout(context);
		tabIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		tabIndicator.setOrientation(LinearLayout.HORIZONTAL);
		tabIndicator.setBackgroundColor(Color.TRANSPARENT);
		tabIndicator.addView(new DefaultTabIndicator(context));
		indicatorRail.addView(tabIndicator);
		
		tabIndicatorExtra = new LinearLayout(context);
		tabIndicatorExtra.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		tabIndicatorExtra.setOrientation(LinearLayout.HORIZONTAL);
		tabIndicatorExtra.setBackgroundColor(Color.TRANSPARENT);
		tabIndicatorExtra.addView(new DefaultTabIndicator(context));
		indicatorRail.addView(tabIndicatorExtra);
		
		tabViewLabel = new LinearLayout(context);
		tabViewLabel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		tabViewLabel.setOrientation(LinearLayout.HORIZONTAL);
		tabViewLabel.setBackgroundColor(Color.TRANSPARENT);
		this.addView(tabViewLabel);
		
		
		this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				widgetWidth = getWidth();
				tabIndicatorWidth = widgetWidth;
				
				if(listTab.size() != 0)
					tabIndicatorWidth = widgetWidth / listTab.size();
				else
					tabIndicatorWidth = widgetWidth;		// for LayoutEditor

				Log.d("width", widgetWidth + "");
				Log.d("tabWidth", tabIndicatorWidth + "");

				DolmanTabWidget.this.getLayoutParams().width = widgetWidth + tabIndicatorWidth;
				indicatorRail.setLayoutParams(new LayoutParams(widgetWidth + tabIndicatorWidth, LayoutParams.MATCH_PARENT));
				tabViewLabel.setLayoutParams(new LayoutParams(widgetWidth, LayoutParams.MATCH_PARENT));
				widgetBackground.setLayoutParams(new LayoutParams(widgetWidth, LayoutParams.MATCH_PARENT));
				
				tabIndicator.setLayoutParams(new LayoutParams(tabIndicatorWidth, LayoutParams.MATCH_PARENT));
				tabIndicatorExtra.setLayoutParams(new LayoutParams(tabIndicatorWidth, LayoutParams.MATCH_PARENT));
				
				getViewTreeObserver().removeOnPreDrawListener(this);
				
				return true;
			}
		});
		
		//TODO more
	}
	
	public void addTab(Tab tab){
		//TODO more
		tab.setIndex(listTab.size());
		
		listTab.add(tab);
		tabViewBackground.addView(tab.getLayoutBackground());
		tabViewLabel.addView(tab.getLayoutLabel());
		Log.d("called", "true");
		tabIndicatorWidth = widgetWidth / listTab.size();
//		indicatorRail.setLayoutParams(new LayoutParams(width + tabIndicatorWidth, LayoutParams.MATCH_PARENT));
		tabIndicator.setLayoutParams(new LayoutParams(tabIndicatorWidth, LayoutParams.MATCH_PARENT));
		tabIndicatorExtra.setLayoutParams(new LayoutParams(tabIndicatorWidth, LayoutParams.MATCH_PARENT));
		
		if(listTab.size() == 0){
			this.setCurrentTab(0);
		}
	}
	
	public void setOnTabListener(OnTabListener onTabListener) {
		this.onTabListener = onTabListener;
	}
	
	public Tab newTab(){
		//TODO
		return new Tab();
	}
	
	private void onClickEvent(int clickedTabIndex){
		if(isLockedTouch)
			return;
		
		isLockedTouch = true;
		
		if(currentTabIndex == clickedTabIndex){
			onTabListener.onTabReselected(clickedTabIndex, dolmanTabLayout);
			isLockedTouch = false;
		} else {
			onTabListener.onTabUnselected(currentTabIndex, dolmanTabLayout);
			onTabListener.onTabSelected(clickedTabIndex, dolmanTabLayout);
		}
	}
	
	public void setLayout(DolmanTabLayout layout) {
		this.dolmanTabLayout = layout;
	}
	
	public int getCount(){
		return listTab.size();
	}

	public void setCurrentTab(int position) {
		// TODO Auto-generated method stub
		currentTabIndex = position;
		
		LayoutParams lp = (LayoutParams) tabIndicator.getLayoutParams();
		lp.setMargins(tabIndicatorWidth * position, 0, 0, 0);
		tabIndicator.setLayoutParams(lp);
		
		LayoutParams lp2 = (LayoutParams) tabIndicatorExtra.getLayoutParams();
		lp2.setMargins(tabIndicatorWidth * listTab.size(), 0, 0, 0);
		tabIndicatorExtra.setLayoutParams(lp2);
	}
	
	public void setTextColor(int color){
		for(Tab tab: listTab){
			tab.setTextColor(color);
		}
	}
	
	public void setTextSize(float size){
		for(Tab tab: listTab){
			tab.setTextSize(size);
		}
	}
	
/*
	public void setIndicator(View view){
		tabIndicator.removeAllViews();
		tabIndicator.addView(view);
		
		tabIndicatorExtra.removeAllViews();
		tabIndicatorExtra.addView(view);
	}
*/	
	public void setIndicator(int resIdLayout){
		View tmpView = View.inflate(context, resIdLayout, null);
		tabIndicator.removeAllViews();
		tabIndicator.addView(tmpView);
		
		tmpView = View.inflate(context, resIdLayout, null);
		tabIndicatorExtra.removeAllViews();
		tabIndicatorExtra.addView(tmpView);
	}
	
	public void setWidgetBackground(View view){
		this.setBackgroundResource(0);
		widgetBackground.removeAllViews();
		widgetBackground.addView(view);
	}
	
	public void setTabBackground(View view){
		for(Tab tab: listTab){
			tab.setTabBackground(view);
		}
	}
	
	public void setTabBackground(int resId){
		for(Tab tab: listTab){
			tab.setTabBackground(resId);
		}
	}
	
	public void setTabBackground(View view, int tabIndex){
		listTab.get(tabIndex).setTabBackground(view);
	}
	
	public void moveTabLocation(int position, float rate) {
		
		LayoutParams lp = (LayoutParams) tabIndicator.getLayoutParams();
		lp.setMargins((int) (tabIndicatorWidth * position + (tabIndicatorWidth * rate)), 0, 0, 0);
		tabIndicator.setLayoutParams(lp);
		
		if(currentTabIndex == listTab.size() - 1){
			LayoutParams lpExtra = (LayoutParams) tabIndicatorExtra.getLayoutParams();
			lpExtra.setMargins((int) (tabIndicatorWidth * -1 + (tabIndicatorWidth * rate)), 0, 0, 0);
			tabIndicatorExtra.setLayoutParams(lpExtra);
		} else if(currentTabIndex == 0){
			LayoutParams lpExtra = (LayoutParams) tabIndicatorExtra.getLayoutParams();
			lpExtra.setMargins((int) (tabIndicatorWidth * listTab.size() + (tabIndicatorWidth * rate)), 0, 0, 0);
			tabIndicatorExtra.setLayoutParams(lpExtra);
		}
	}
	
	public void lockTouch(){
		isLockedTouch = true;
	}
	
	public void unlockTouch(){
		isLockedTouch = false;
	}

	
	public final static class DefaultWidgetBackground extends LinearLayout{
		public DefaultWidgetBackground(Context context) {
			super(context);
			
			this.setBackgroundColor(Color.BLACK);
			this.setOrientation(VERTICAL);
			this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			LinearLayout bottomLine = new LinearLayout(context);
			bottomLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 8, 1));
			bottomLine.setBackgroundColor(COLOR_ICS_CYAN);
			LinearLayout blankArea = new LinearLayout(context);
			blankArea.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			
			this.addView(blankArea);
			this.addView(bottomLine);
		}
	}
	
	public final static class DefaultTabIndicator extends LinearLayout{
		public DefaultTabIndicator(Context context) {
			super(context);
			
			this.setBackgroundColor(Color.BLACK);
			this.setOrientation(VERTICAL);
			this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			LinearLayout bottomLine = new LinearLayout(context);
			bottomLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24, 1));
			bottomLine.setBackgroundColor(COLOR_ICS_CYAN);
			LinearLayout blankArea = new LinearLayout(context);
			blankArea.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			
			this.addView(blankArea);
			this.addView(bottomLine);
		}
	}
	
	public class Tab{
		private final int DEFAULT_TEXT_SIZE = 14;
		private final int DEFAULT_TEXT_COLOR = Color.WHITE;
		
		private int tabIndex;
		
		private LinearLayout tabLayoutBackground;
		private LinearLayout tabLayoutLabel;
		
		private TextView tabTextView;
		private ImageView tabImageView;
		private View tabCustomView;
		
		private boolean statusSelected = false;
		
		public Tab() {
			tabLayoutBackground = new LinearLayout(context);
			tabLayoutLabel = new LinearLayout(context);
			init();
		}

		public Tab(AttributeSet attrs) {
			tabLayoutBackground = new LinearLayout(context, attrs);
			tabLayoutLabel = new LinearLayout(context, attrs);
			init();
		}

		private void init(){
			tabLayoutBackground.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			tabLayoutBackground.setBackgroundColor(Color.TRANSPARENT);

			tabLayoutLabel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			tabLayoutLabel.setGravity(Gravity.CENTER);
			tabLayoutLabel.setBackgroundColor(Color.TRANSPARENT);
			
			tabImageView = new ImageView(context);
			tabImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tabImageView.setTag("tabImage");
			tabLayoutLabel.addView(tabImageView);
			
			tabTextView = new TextView(context);
			tabTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tabTextView.setTextSize(DEFAULT_TEXT_SIZE);
			tabTextView.setTextColor(DEFAULT_TEXT_COLOR);
			tabTextView.setTag("tabText");
			tabLayoutLabel.addView(tabTextView);
			
			tabLayoutLabel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					onClickEvent(tabIndex);
				}
			});
		}

		public void setIndex(int tabIndex){
			if(tabIndex == 0){
				this.statusSelected = true;
			}
			this.tabIndex = tabIndex;
		}
		
		public Tab setText(CharSequence charSequence){
			if(tabCustomView == null){
				tabTextView.setText(charSequence);
				return this;
			} else
				return null;
		}
		
		public Tab setText(int textResourceId){
			if(tabCustomView == null){
				tabTextView.setText(textResourceId);
				return this;
			} else
				return null;
		}
		
		public TextView getTextView(){
			return tabTextView;
		}
		
		public Tab setIcon(Drawable drawable){
			if(tabCustomView == null){
				tabImageView.setImageDrawable(drawable);
				return this;
			} else
				return null;
		}
		
		public Tab setIcon(int imageResourceId){
			if(tabCustomView == null){
				tabImageView.setImageResource(imageResourceId);
				return this;
			} else
				return null;
		}
		
		public Tab setIcon(Bitmap bitmap){
			if(tabCustomView == null){
				tabImageView.setImageBitmap(bitmap);
				return this;
			} else
				return null;
		}

		public ImageView getIconView(){
			return tabImageView;
		}
		
		public Tab setCustomView(View customView){
			tabLayoutLabel.removeAllViews();
			
			tabCustomView = customView;
			tabLayoutLabel.addView(tabCustomView);
			
			return this;
		}
		
		public View getCustomView(){
			return tabCustomView;
		}
		
		public Tab setTextColor(int color){
			if(tabCustomView == null){
				tabTextView.setTextColor(color);
				
				return this;
			}else {
				return null;
			}
		}
		
		public Tab setTextSize(float size){
			if(tabCustomView == null){
				tabTextView.setTextSize(size);
				
				return this;
			} else {
				return null;
			}
		}
		
		public Tab setTabBackground(View view){
			tabLayoutBackground.removeAllViews();
			tabLayoutBackground.addView(view);
			
			return this;
		}
		
		public Tab setTabBackground(int resourceId){
			tabLayoutBackground.setBackgroundResource(resourceId);
			
			return this;
		}
		
		public LinearLayout getLayoutLabel(){
			return tabLayoutLabel;
		}
		
		public LinearLayout getLayoutBackground(){
			return tabLayoutBackground;
		}
		
		public boolean isSelected(){
			return statusSelected;
		}
	}
}


