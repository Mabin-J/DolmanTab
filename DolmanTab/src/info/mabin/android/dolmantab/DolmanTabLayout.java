package info.mabin.android.dolmantab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.nineoldandroids.animation.AnimatorSet;

import info.mabin.android.dolmantab.DolmanTabInterface.PageAnimator;
import info.mabin.android.dolmantab.DolmanTabWidget.Tab;
import info.mabin.android.dolmantab.FragmentAnimator.Direction;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

	private int widgetHeight = Constant.DEFAULT_WIDGET_HEIGHT; 
	
	private Context context;

	private DolmanTabWidget tabWidget;
	private PageAnimator pageAnimator;
	private DolmanTabAdapter tabAdapter;

	private Fragment initFragment;
	private Fragment currentFragment;

	private FrameLayout arrViewFragment[] = new FrameLayout[2];
	private RelativeLayout arrViewAnimation[] = new RelativeLayout[2];
	
	private RelativeLayout layoutRealView;
	private RelativeLayout layoutAnimation;
	private FrameLayout layoutTouch;

	private boolean isInit = false;
	
	private int tabIndexCurrent = 0;
	private int tabIndexNext = 0;

	private FragmentManager fragmentManager;
	
	private List<PageAnimatorQueue> listQueue = new ArrayList<PageAnimatorQueue>();

	private Bitmap arrBitmapAnimation[] = new Bitmap[2];
	private boolean arrIsCapture[] = new boolean[2];
	
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
					fragmentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, layoutHeight - widgetHeight);
					fragmentLayoutParams.setMargins(0, widgetHeight, 0, 0);
				}
				
				getViewTreeObserver().removeOnPreDrawListener(this);

				init();

				isInit = true;


				return true;
			}
		});

	}

	private void init (){

		ViewConfiguration vc = ViewConfiguration.get(getContext());
		scaledTouchSlop = vc.getScaledTouchSlop();
		scaledMinFlingVelocity = vc.getScaledMinimumFlingVelocity() / 10;
		//		scaledMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		
		layoutAnimation = new RelativeLayout(context);
		layoutAnimation.setBackgroundColor(Color.TRANSPARENT);
		layoutAnimation.setLayoutParams(fragmentLayoutParams);
		this.addView(layoutAnimation);
		
		arrViewAnimation[0] = new RelativeLayout(context);
		arrViewAnimation[0].setBackgroundColor(Color.TRANSPARENT);
		arrViewAnimation[0].setVisibility(VISIBLE);
		arrViewAnimation[0].setId(DolmanTabLayout.customGenerateViewId());
		arrViewAnimation[0].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		arrViewAnimation[1] = new RelativeLayout(context);
		arrViewAnimation[1].setBackgroundColor(Color.TRANSPARENT);
		arrViewAnimation[1].setVisibility(VISIBLE);
		arrViewAnimation[1].setId(DolmanTabLayout.customGenerateViewId());
		arrViewAnimation[1].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		layoutAnimation.addView(arrViewAnimation[1]);
		layoutAnimation.addView(arrViewAnimation[0]);
		
		layoutRealView = new RelativeLayout(context);
		layoutRealView.setBackgroundColor(Color.TRANSPARENT);
		layoutRealView.setLayoutParams(fragmentLayoutParams);

		this.addView(layoutRealView);
		
		
		
		arrViewFragment[0] = new FrameLayout(context);
		arrViewFragment[0].setBackgroundColor(Color.TRANSPARENT);
		arrViewFragment[0].setVisibility(VISIBLE);
		arrViewFragment[0].setId(DolmanTabLayout.customGenerateViewId());
		arrViewFragment[0].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		arrViewFragment[1] = new FrameLayout(context);
		arrViewFragment[1].setBackgroundColor(Color.TRANSPARENT);
		arrViewFragment[1].setVisibility(VISIBLE);
		arrViewFragment[1].setId(DolmanTabLayout.customGenerateViewId());
		arrViewFragment[1].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		layoutRealView.addView(arrViewFragment[1]);
		layoutRealView.addView(arrViewFragment[0]);

		layoutTouch = new TouchLayout(context);
		layoutTouch.setBackgroundColor(Color.TRANSPARENT);
		layoutTouch.setLayoutParams(fragmentLayoutParams);
		this.addView(layoutTouch);
		
		fragmentAnimator = new FragmentAnimator();
		fragmentAnimator.addListener(tabAnimatorListener);
		
		onChangeLayoutHeight();
		
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

	private void onChangeLayoutHeight(){

	}
	
	public void setTabWidget(DolmanTabWidget tabWidget){
		if(this.tabWidget != null){
			this.removeView(this.tabWidget);

			fragmentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			fragmentLayoutParams.setMargins(0, 0, 0, 0);
			
			if(arrViewFragment[0] != null){
				arrViewFragment[0].setLayoutParams(fragmentLayoutParams);
			}
			
			if(arrViewFragment[1] != null)
				arrViewFragment[1].setLayoutParams(fragmentLayoutParams);
			widgetHeight = tabWidget.getHeight();
		}

		if(arrViewFragment[0] != null){
			onChangeLayoutHeight();
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

	public Tab newTab(){
		return tabWidget.newTab();
	}

	public void setCurrentTab(int targetTabIndex){
		if(pageAnimator == null){
			tabIndexNext = targetTabIndex;
			tabAnimatorListener.onAnimationStart(null);
			tabAnimatorListener.onAnimationEnd(null);
		} else {
			int tabSize = tabAdapter.getCount();
			
			MoveInfo tmpInfo = calcPageMove(tabIndexCurrent, targetTabIndex, tabSize);
			Log.d("MoveInfo Direction", tmpInfo.direction + "");
			Log.d("MoveInfo MoveCount", tmpInfo.moveCnt + "");
			
			for(int i = 1; i <= tmpInfo.moveCnt; i++){
				PageAnimatorQueue tmpQueue = new PageAnimatorQueue();
				
				tmpQueue.animator = pageAnimator;
				tmpQueue.direction = tmpInfo.direction;
				if(tmpInfo.direction == Direction.Forward){
					tmpQueue.targetIdx = (tabIndexCurrent + i) % tabSize;
				} else {
					tmpQueue.targetIdx = (tabSize + tabIndexCurrent - i) % tabSize;
				}
				
				listQueue.add(tmpQueue);
			}
			
			//StartQueue
			PageAnimatorQueue firstQueue = listQueue.get(0);
			tabIndexNext = firstQueue.targetIdx;
			fragmentAnimator.setPageAnimator(firstQueue.animator, firstQueue.direction);
			fragmentAnimator.setTarget(arrViewAnimation[0], arrViewAnimation[1]);

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
	@SuppressLint("NewApi")
	private static int customGenerateViewId() {
		try {
			RelativeLayout.class.getMethod("generateViewId");
			return RelativeLayout.generateViewId();
		} catch (NoSuchMethodException e) {
		}
		
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
	private FragmentAnimator fragmentAnimator;
	private boolean isStartedAnimation = false;

	
	
	private int scaledTouchSlop;
	private int scaledMinFlingVelocity;


	private FragmentAnimatorListener tabAnimatorListener = new FragmentAnimatorListener() {
		private FrameLayout tmpLayout;
		@Override
		public void onAnimationStart(FragmentAnimator animation) {
			if(animation != null){
				if(animation != null && animation.getDirection() == Direction.Forward){
					arrViewAnimation[1].setVisibility(INVISIBLE);
					layoutAnimation.removeView(arrViewAnimation[0]);
					layoutAnimation.addView(arrViewAnimation[0]);
					arrViewAnimation[1].setVisibility(VISIBLE);
				} else {
					arrViewAnimation[0].setVisibility(INVISIBLE);
					layoutAnimation.removeView(arrViewAnimation[1]);
					layoutAnimation.addView(arrViewAnimation[1]);
					arrViewAnimation[0].setVisibility(VISIBLE);
				}
			}
			
			final Fragment nextFragment = tabAdapter.getItem(tabIndexNext);
			
			fragmentManager.beginTransaction()
			.replace(arrViewFragment[1].getId(), nextFragment)
			.commit();

			tabWidget.lockTouch();

			Log.d("AnimatorEvent", "Start");
			if(animation != null){
				
				if(isInit){
					if(arrBitmapAnimation[0] == null){
						arrBitmapAnimation[0] = Bitmap.createBitmap(arrViewFragment[0].getWidth(),
								arrViewFragment[0].getHeight(),
								Bitmap.Config.ARGB_8888);
		
						arrBitmapAnimation[1] = Bitmap.createBitmap(arrViewFragment[1].getWidth(),
								arrViewFragment[1].getHeight(),
								Bitmap.Config.ARGB_8888);
					} else {
						arrBitmapAnimation[0].eraseColor(Color.TRANSPARENT);
						arrBitmapAnimation[1].eraseColor(Color.TRANSPARENT);
					}
					
					
					arrViewFragment[0].draw(new Canvas(arrBitmapAnimation[0]));
	
					BitmapDrawable tmpDrawable = new BitmapDrawable(arrBitmapAnimation[0]);
					arrViewAnimation[0].setBackgroundDrawable(tmpDrawable);
					
					arrViewFragment[1].getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
						
						@Override
						public boolean onPreDraw() {
							if(nextFragment.getView() != null){
								getViewTreeObserver().removeOnPreDrawListener(this);
	
								arrBitmapAnimation[1].eraseColor(Color.TRANSPARENT);
	
								arrViewFragment[1].draw(new Canvas(arrBitmapAnimation[1]));
		
								BitmapDrawable tmpDrawable = new BitmapDrawable(arrBitmapAnimation[1]);
								arrViewAnimation[1].setBackgroundDrawable(tmpDrawable);
							}
							return true;
						}
					});
				}
				
				layoutAnimation.setVisibility(VISIBLE);
				layoutRealView.setVisibility(INVISIBLE);
				arrViewFragment[1].setVisibility(INVISIBLE);
				arrViewFragment[0].setVisibility(INVISIBLE);
			}
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

			RelativeLayout tmpLayout2;
			tmpLayout2 = arrViewAnimation[0];
			arrViewAnimation[0] = arrViewAnimation[1];
			arrViewAnimation[1] = tmpLayout2;
			
			tabIndexCurrent = tabIndexNext;
			currentFragment = tabAdapter.getItem(tabIndexNext);

			tabWidget.unlockTouch();
			isStartedAnimation = false;

			if(animation != null){
				arrViewFragment[0].setVisibility(VISIBLE);
				layoutRealView.setVisibility(VISIBLE);
				layoutAnimation.setVisibility(INVISIBLE);
			}

			// For Queue =========================
			if(listQueue.size() > 0){
				listQueue.remove(0);
				if(listQueue.size() > 0){
					PageAnimatorQueue nextQueue = listQueue.get(0);
					tabIndexNext = nextQueue.targetIdx;
					fragmentAnimator.setPageAnimator(nextQueue.animator, nextQueue.direction);
					fragmentAnimator.setTarget(arrViewAnimation[0], arrViewAnimation[1]);

					fragmentAnimator.start();
				}
			}
			
			Log.d("AnimatorEvent", "End");
		}

		@Override
		public void onAnimationRepeat(FragmentAnimator animation) {}

		@Override
		public void onAnimationCancel(FragmentAnimator animation) {}

		@Override
		public void onAnimationPlaying(FragmentAnimator animation, long currentTime) {
			float ratio = currentTime / (float)animation.getDuration();

			if(animation.getDirection() == Direction.Backward)
				ratio *= -1;
			
			tabAdapter.onPageScrolled(tabIndexCurrent, ratio, tabWidget);
		}

		@Override
		public void onAnimationEndReverse(FragmentAnimator animation) {
/*			fragmentManager.beginTransaction()
			.remove(tabAdapter.getItem(tabIndexNext))
			.commit();
*/
			arrViewFragment[0].setVisibility(VISIBLE);
			layoutRealView.setVisibility(VISIBLE);
			layoutAnimation.setVisibility(INVISIBLE);
			
			isStartedAnimation = false;
			tabWidget.unlockTouch();
			Log.d("AnimatorEvent", "EndReverse");
		}
	};
	
	public static MoveInfo calcPageMove(int currentIdx, int nextIdx, int size){
		MoveInfo tmpInfo = new MoveInfo();
		
		if(size == 2){
			if(currentIdx > nextIdx){
				tmpInfo.direction = Direction.Backward;
				tmpInfo.moveCnt = 1;
			} else {
				tmpInfo.direction = Direction.Forward;
				tmpInfo.moveCnt = 1;
			}
			
			return tmpInfo;
		}
		
		if(currentIdx > nextIdx){
			if((size + nextIdx - currentIdx) < (currentIdx - nextIdx)){
				tmpInfo.direction = Direction.Forward;
				tmpInfo.moveCnt = size + nextIdx - currentIdx;
			} else {
				tmpInfo.direction = Direction.Backward;
				tmpInfo.moveCnt = currentIdx - nextIdx;
			}
		} else {
			if((size + currentIdx - nextIdx) < (nextIdx - currentIdx)){
				tmpInfo.direction = Direction.Backward;
				tmpInfo.moveCnt = size + currentIdx - nextIdx;
			} else {
				tmpInfo.direction = Direction.Forward;
				tmpInfo.moveCnt = nextIdx - currentIdx;
			}
		}
		
		return tmpInfo;
	}
	
	public static class MoveInfo{
		Direction direction;
		int moveCnt = 0;
	}
	
	private static class PageAnimatorQueue{
		PageAnimator animator;
		Direction direction;
		int targetIdx;
	}
	
	
	
	private class TouchLayout extends FrameLayout{

		public TouchLayout(Context context) {
			super(context);
		}
		
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

		private MotionEvent downEvent;
		
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			Log.d("Intercept"," true");
			touchStartX = (int) ev.getRawX();
			touchStartY = (int) ev.getRawY();

			verticalScroll = false;
			horizenScroll = false;

			if(ev.getAction() == MotionEvent.ACTION_DOWN){
				downEvent = MotionEvent.obtain(ev);
			}
			
			return false;
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			int action = ev.getAction();

			if(pageAnimator == null)
				return false;

			if(isStartedAnimation)
				return false;

			if(horizenScroll != true){
				Log.d("pass", "true");
				layoutRealView.dispatchTouchEvent(ev);
			}
			
			if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
				if(verticalScroll){
					return false;
				}

				beforeX = currentX;

				currentX = (int) ev.getRawX();
				currentY = (int) ev.getRawY();

				if(horizenScroll){
					if(touchStartX - currentX > 0){
						touchDirectionCurrent = FragmentAnimator.Direction.Forward;
						movePercent = ((touchStartX - currentX) / (float) layoutWidth);

						if(touchDirectionBefore != touchDirectionCurrent){
							Log.d("reverse b->f", "true");
							tabAnimatorListener.onAnimationEndReverse(null);
							
							fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Forward);

							int tmpTabIndexNext = (tabIndexCurrent + 1) % tabAdapter.getCount();
							tabIndexNext = tmpTabIndexNext;

							tabAnimatorListener.onAnimationStart(fragmentAnimator);
						}
					} else {
						touchDirectionCurrent = FragmentAnimator.Direction.Backward;
						movePercent = ((currentX - touchStartX) / (float) layoutWidth);

						if(touchDirectionBefore != touchDirectionCurrent){
							Log.d("reverse f->b", "true");
							
							tabAnimatorListener.onAnimationEndReverse(null);
							
							int tmpTabIndexNext = 0;

							if(tabIndexCurrent == 0){
								tmpTabIndexNext = tabAdapter.getCount() - 1;
							} else {
								tmpTabIndexNext = tabIndexCurrent - 1;
							}

							fragmentAnimator.setPageAnimator(pageAnimator, FragmentAnimator.Direction.Backward);
							tabIndexNext = tmpTabIndexNext;
							
							tabAnimatorListener.onAnimationStart(fragmentAnimator);
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
					
					MotionEvent upEvent = MotionEvent.obtain(downEvent);
					upEvent.setAction(MotionEvent.ACTION_CANCEL);
					
					layoutRealView.dispatchTouchEvent(upEvent);
					
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

					fragmentAnimator.setTarget(arrViewAnimation[0], arrViewAnimation[1]);

					return true;
				} else if(Math.abs(currentY - touchStartY) > scaledTouchSlop){
					verticalScroll = true;
					Log.d("scrollType", "Vertical");
					
					return false;
				}
			} else if(action == MotionEvent.ACTION_UP){
				if(!horizenScroll){
					return false;
				}
				
//				isStartedAnimation = true;

				if(movePercent > 0.5 || Math.abs(beforeX - currentX) > scaledMinFlingVelocity){
					fragmentAnimator.resume();
				} else {
					fragmentAnimator.setDuration(100);
					fragmentAnimator.reverse();
				}
				horizenScroll = false;
				verticalScroll = false;
				
//				layoutRealView.dispatchTouchEvent(ev);
				
				return true;
			}
			return true;
		}

		
	}
	
	private class FragmentView extends FrameLayout{
		Fragment fragment;
		
		public FragmentView(Context context, Fragment fragment) {
			super(context);
			
			this.fragment = fragment;
		}
		
		
	}
}


