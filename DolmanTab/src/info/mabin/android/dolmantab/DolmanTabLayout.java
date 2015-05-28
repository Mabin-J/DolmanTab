package info.mabin.android.dolmantab;

import java.util.concurrent.atomic.AtomicInteger;

import info.mabin.android.dolmantab.DolmanTabInterface.PageAnimator;
import info.mabin.android.dolmantab.DolmanTabWidget.Tab;
import info.mabin.android.dolmantab.FragmentAnimator.Direction;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


public class DolmanTabLayout extends RelativeLayout{
	private LayoutParams fragmentLayoutParams = null;
	private int layoutWidth, layoutHeight;

	private Context context;

	private DolmanTabWidget tabWidget;
	private PageAnimator pageAnimator;
	private DolmanTabAdapter tabAdapter;

	private Fragment initFragment;
	private Fragment currentFragment;

	private FrameLayout arrViewFragment[] = new FrameLayout[2];

	private int tabIndexCurrent = 0;
	private int tabIndexNext = 0;

	private FragmentManager fragmentManager;


	public static class LayoutParams extends RelativeLayout.LayoutParams{

		public LayoutParams(Context arg0, AttributeSet arg1) {
			super(arg0, arg1);
		}

		public LayoutParams(int w, int h){
			super(w, h);
		}

		public LayoutParams(ViewGroup.LayoutParams source){
			super(source);
		}

		public LayoutParams(ViewGroup.MarginLayoutParams source){
			super(source);
		}
	}

	public DolmanTabLayout(Context context) {
		super(context);
		this.context = context;
		triggerInit();
	}

	public DolmanTabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		triggerInit();
	}

	public DolmanTabLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		triggerInit();
	}

	private void triggerInit(){
		DolmanTabWidget tmpWidget = new DolmanTabWidget(context);
		tmpWidget.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Constant.DEFAULT_WIDGET_HEIGHT));
		this.addView(tmpWidget);
		
		setTabWidget(tmpWidget);

		
		this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				layoutWidth = getWidth();
				layoutHeight = getHeight();

				if (fragmentLayoutParams == null){
					fragmentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, layoutHeight - Constant.DEFAULT_WIDGET_HEIGHT);
					fragmentLayoutParams.setMargins(0, Constant.DEFAULT_WIDGET_HEIGHT, 0, 0);
				}
				/*
				for(FrameLayout layout: arrViewFragment){
					layout.setLayoutParams(layoutParams);
				}
				 */
				getViewTreeObserver().removeOnPreDrawListener(this);

				init();

				return true;
			}
		});

	}

	private void init (){

		ViewConfiguration vc = ViewConfiguration.get(getContext());
		scaledTouchSlop = vc.getScaledTouchSlop();
		scaledMinFlingVelocity = vc.getScaledMinimumFlingVelocity() / 10;
		//		scaledMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();

		arrViewFragment[0] = new FrameLayout(context);
		arrViewFragment[0].setBackgroundColor(Color.TRANSPARENT);
		arrViewFragment[0].setVisibility(VISIBLE);
		arrViewFragment[0].setId(DolmanTabLayout.customGenerateViewId());
		arrViewFragment[0].setLayoutParams(fragmentLayoutParams);

		arrViewFragment[1] = new FrameLayout(context);
		arrViewFragment[1].setBackgroundColor(Color.TRANSPARENT);
		arrViewFragment[1].setVisibility(VISIBLE);
		arrViewFragment[1].setId(DolmanTabLayout.customGenerateViewId());
		arrViewFragment[1].setLayoutParams(fragmentLayoutParams);
		
		this.addView(arrViewFragment[1]);
		this.addView(arrViewFragment[0]);

		fragmentAnimator = new FragmentAnimator();
		fragmentAnimator.addListener(tabAnimatorListener);
		
		if(context.getClass().getSuperclass() == FragmentActivity.class){		// for LayoutEditor
			fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();

			initFragment = new Fragment();
			currentFragment = initFragment;

			fragmentManager.beginTransaction()
			.add(arrViewFragment[0].getId(), initFragment)
			.commit();
			
			if(tabAdapter.getCount() != 0){
				tabIndexNext = 0;
			
				tabAnimatorListener.onAnimationStart(null);
				tabAnimatorListener.onAnimationEnd(null);
			}
		}
	}

	public void setTabWidget(DolmanTabWidget tabWidget){
		if(this.tabWidget != null){
			this.removeView(this.tabWidget);

			fragmentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			fragmentLayoutParams.setMargins(0, 0, 0, 0);

			if(arrViewFragment[0] != null)
				arrViewFragment[0].setLayoutParams(fragmentLayoutParams);
			
			if(arrViewFragment[1] != null)
				arrViewFragment[1].setLayoutParams(fragmentLayoutParams);
			
		}

		tabWidget.setLayout(this);
		tabWidget.setOnTabListener(tabAdapter);

		this.tabWidget = tabWidget;
	}

	public void setTabAdapter(DolmanTabAdapter tabAdapter){
		this.tabAdapter = tabAdapter;
		tabWidget.setOnTabListener(tabAdapter);
	}

	public void addTab(Tab tab) {
		tabWidget.addTab(tab);
	}

	public DolmanTabWidget.Tab newTab(){
		return tabWidget.newTab();
	}

	public void setCurrentTab(int targetTabIndex){
		tabIndexNext = targetTabIndex;

		if(pageAnimator == null){
			tabAnimatorListener.onAnimationStart(null);
			tabAnimatorListener.onAnimationEnd(null);
		} else {
			int tabSize = tabAdapter.getCount();
			if(tabIndexCurrent == 0 && targetTabIndex == tabSize - 1){
				fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Backward);
			} else if(tabIndexCurrent == tabSize - 1 && targetTabIndex == 0){
				fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Forward);
			} else if(tabIndexCurrent < targetTabIndex){
				fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Forward);
			} else {
				fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Backward);
			}

			fragmentAnimator.setTarget(arrViewFragment[0], arrViewFragment[1]);

			fragmentAnimator.start();
		}
	}

	public void setPageAnimator(PageAnimator pageAnimator){
		this.pageAnimator = pageAnimator;
	}

	public void setWidgetTextColor(int color){
		tabWidget.setTextColor(color);
	}

	public void setWidgetTextSize(float size){
		tabWidget.setTextSize(size);
	}

	public void setWidgetBackground(View view){
		tabWidget.setWidgetBackground(view);
	}

	public void setWidgetHeight(int size){
		tabWidget.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, size));
	}

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link #setId(int)}.
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 *
	 * @return a generated ID value
	 */
	public static int customGenerateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}








	// ===== ANIMATION PART =============================================
	private int scaledTouchSlop;
	private int scaledMinFlingVelocity;
	//	private int scaledMaxFlingVelocity;

	private int currentX = 0;
	private int currentY = 0;
	private int beforeX = 0;

	private int touchStartX;
	private int touchStartY;

	//	private float moveRange = 0;
	private float movePercent = 0;

	private boolean verticalScroll = false;
	private boolean horizenScroll = false;

	private Direction touchDirectionCurrent = Direction.Forward;
	private Direction touchDirectionBefore = FragmentAnimator.Direction.Forward;

	private FragmentAnimator fragmentAnimator;

	private boolean isStartedAnimation = false;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		touchStartX = (int) ev.getRawX();
		touchStartY = (int) ev.getRawY();

		verticalScroll = false;
		horizenScroll = false;

		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(pageAnimator == null)
			return false;

		if(isStartedAnimation)
			return false;

		int action = ev.getAction();

		if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
			if(verticalScroll){
				return false;
			}

			beforeX = currentX;

			currentX = (int) ev.getRawX();
			currentY = (int) ev.getRawY();

			if(horizenScroll){
				if(touchStartX - currentX > 0){
					Log.d("forward", "true");
					touchDirectionCurrent = FragmentAnimator.Direction.Forward;
					movePercent = ((touchStartX - currentX) / (float) layoutWidth);

					if(touchDirectionBefore != touchDirectionCurrent){
						Log.d("reverse b->f", "true");
						int tmpTabIndexNext = (tabIndexCurrent + 1) % tabAdapter.getCount();

						fragmentManager.beginTransaction()
						.remove(tabAdapter.getItem(tabIndexNext))
						.commit();

						fragmentManager.beginTransaction()
						.replace(arrViewFragment[1].getId(), tabAdapter.getItem(tmpTabIndexNext))
						.commit();



						tabIndexNext = tmpTabIndexNext;

						//						fragmentAnimator.setTarget(arrViewFragment[0], arrViewFragment[1]);
						fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Forward);
					}

				} else {
					Log.d("backward", "true");
					touchDirectionCurrent = FragmentAnimator.Direction.Backward;
					movePercent = ((currentX - touchStartX) / (float) layoutWidth);

					if(touchDirectionBefore != touchDirectionCurrent){
						Log.d("reverse f->b", "true");
						int tmpTabIndexNext = 0;

						if(tabIndexCurrent == 0){
							tmpTabIndexNext = tabAdapter.getCount() - 1;
						} else {
							tmpTabIndexNext = tabIndexCurrent - 1;
						}

						fragmentManager.beginTransaction()
						.remove(tabAdapter.getItem(tabIndexNext))
						.commit();

						fragmentManager.beginTransaction()
						.replace(arrViewFragment[1].getId(), tabAdapter.getItem(tmpTabIndexNext))
						.commit();

						tabIndexNext = tmpTabIndexNext;

						//						fragmentAnimator.setTarget(arrViewFragment[0], arrViewFragment[1]);
						fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Backward);
					}
				}

				if(movePercent > 0){
					fragmentAnimator.setCurrentPlayTime((long) (fragmentAnimator.getDuration() * movePercent));
				}

				touchDirectionBefore = touchDirectionCurrent;
				return true;
			} else if(Math.abs(currentX - touchStartX) > scaledTouchSlop){
				horizenScroll = true;
				Log.d("scrollType", "Horizen");


				if(touchStartX - currentX > 0){
					tabIndexNext = (tabIndexCurrent + 1) % tabAdapter.getCount();

					fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Forward);
					touchDirectionCurrent = touchDirectionBefore = FragmentAnimator.Direction.Forward;
				} else {
					if(tabIndexCurrent == 0)
						tabIndexNext = tabAdapter.getCount() - 1;
					else
						tabIndexNext = tabIndexCurrent - 1;

					fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Backward);
					touchDirectionCurrent = touchDirectionBefore = FragmentAnimator.Direction.Backward;
				}

				fragmentManager.beginTransaction()
				.replace(arrViewFragment[1].getId(), tabAdapter.getItem(tabIndexNext))
				.commit();

				fragmentAnimator.setTarget(arrViewFragment[0], arrViewFragment[1]);

				return true;
			} else if(Math.abs(currentY - touchStartY) > scaledTouchSlop){
				verticalScroll = true;
				Log.d("scrollType", "Vertical");
				return false;
			}
		} else if(action == MotionEvent.ACTION_UP){
			if(horizenScroll == false){
				return false;
			}
			isStartedAnimation = true;

			if(movePercent > 0.5 || Math.abs(beforeX - currentX) > scaledMinFlingVelocity){
				fragmentAnimator.resume();
			} else {
				fragmentAnimator.setDuration(100);
				//				fragmentAnimator.cancel();
				fragmentAnimator.reverse();
				isStartedAnimation = false;
				tabWidget.unlockTouch();
			}
		}
		return true;
	}

	private FragmentAnimatorListener tabAnimatorListener = new FragmentAnimatorListener() {
		private FrameLayout tmpLayout;
		@Override
		public void onAnimationStart(FragmentAnimator animation) {
			if(animation != null && animation.getDirection() == Direction.Forward){
				arrViewFragment[1].setVisibility(INVISIBLE);
				DolmanTabLayout.this.removeView(arrViewFragment[0]);
				DolmanTabLayout.this.addView(arrViewFragment[0]);
				arrViewFragment[1].setVisibility(VISIBLE);
			} else {
				arrViewFragment[0].setVisibility(INVISIBLE);
				DolmanTabLayout.this.removeView(arrViewFragment[1]);
				DolmanTabLayout.this.addView(arrViewFragment[1]);
				arrViewFragment[0].setVisibility(VISIBLE);
			}
			fragmentManager.beginTransaction()
			.replace(arrViewFragment[1].getId(), tabAdapter.getItem(tabIndexNext))
			.commit();

			tabWidget.lockTouch();

			Log.d("AnimatorEvent", "Start");
		}

		@Override
		public void onAnimationEnd(FragmentAnimator animation) {
			fragmentManager.beginTransaction()
			.remove(currentFragment)
			.commit();

			tabAdapter.onPageSelected(tabIndexNext, tabWidget);
			tabAdapter.onPageUnselected(tabIndexCurrent, tabWidget);

			tmpLayout = arrViewFragment[0];
			arrViewFragment[0] = arrViewFragment[1];
			arrViewFragment[1] = tmpLayout;

			tabIndexCurrent = tabIndexNext;
			currentFragment = tabAdapter.getItem(tabIndexNext);

			tabWidget.unlockTouch();
			isStartedAnimation = false;
			Log.d("AnimatorEvent", "End");
		}

		@Override
		public void onAnimationRepeat(FragmentAnimator animation) {}

		@Override
		public void onAnimationCancel(FragmentAnimator animation) {}

		@Override
		public void onAnimationPlaying(FragmentAnimator animation, long currentTime) {
			float ratio = currentTime / (float)animation.getDuration();

			if(ratio > 1)
				ratio = 1;

			int sizeTab = tabAdapter.getCount();
			if(tabIndexNext == sizeTab - 1 && tabIndexCurrent == 0){
				ratio *= -1;
			} else if(tabIndexCurrent == sizeTab - 1 && tabIndexNext == 0){
			} else if(tabIndexNext < tabIndexCurrent){
				ratio *= -1;
			}

			tabAdapter.onPageScrolled(tabIndexCurrent, ratio, tabWidget);
		}
	};
}


