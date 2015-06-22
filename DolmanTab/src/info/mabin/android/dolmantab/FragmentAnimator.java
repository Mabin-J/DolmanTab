package info.mabin.android.dolmantab;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import info.mabin.android.bundleanimator.AnimatorInfoSet;
import info.mabin.android.bundleanimator.BundleAnimator;
import info.mabin.android.bundleanimator.BundleAnimatorListener;
import info.mabin.android.dolmantab.DolmanTabInterface.PageAnimator;

public class FragmentAnimator implements FragmentAnimatorListener{
	public static enum Direction{
		Forward, Backward;
	}
	private BundleAnimator animatorCurrent = new BundleAnimator();
	private BundleAnimator animatorNext = new BundleAnimator();
	
	private BundleAnimator handlerEvent;
	
	
	private boolean isStarted = false;
	
	private List<FragmentAnimatorListener> listListener = new ArrayList<FragmentAnimatorListener>();
	private List<BundleAnimatorListener> listTmpBundleListener = new ArrayList<BundleAnimatorListener>();
	
	private long duration = 0;
	private Direction direction;
	
	public void setPageAnimator(PageAnimator pageAnimator, Direction direction){
		AnimatorInfoSet currentSet, nextSet;

		currentSet = new AnimatorInfoSet();
		nextSet = new AnimatorInfoSet();
		
		this.direction = direction;
		
		if(direction == Direction.Forward){
			pageAnimator.animationForward(currentSet, nextSet);
		} else {
			pageAnimator.animationBackward(currentSet, nextSet);
		}
		
		animatorCurrent.setArrAnimatorInfo(currentSet.getInfos());
		animatorNext.setArrAnimatorInfo(nextSet.getInfos());
		
		duration = animatorCurrent.getDuration();
		handlerEvent = animatorCurrent;
		if(animatorNext.getDuration() > duration){
			duration = animatorNext.getDuration();
			handlerEvent = animatorNext;
		}
		
		for(BundleAnimatorListener listener: listTmpBundleListener){
			handlerEvent.addListener(listener);
		}
		
		listTmpBundleListener.clear();
	}
	
	public void setTarget(Object currentTarget, Object nextTarget){
		animatorCurrent.setTarget(currentTarget);
		animatorNext.setTarget(nextTarget);
	}

	public void setCurrentPlayTime(long targetMilliSec) {
		if(isStarted == false)
			this.onAnimationStart(this);
		
		animatorCurrent.setCurrentPlayTime(targetMilliSec);		
		animatorNext.setCurrentPlayTime(targetMilliSec);
	}

	public void setDuration(long newDuration){
		float ratio = newDuration / (float) duration;
		
		animatorCurrent.setDuration((long) (animatorCurrent.getDuration() * ratio));
		animatorNext.setDuration((long) (animatorNext.getDuration() * ratio));

		duration = newDuration;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public Direction getDirection(){
		return this.direction;
	}

	public void addListener(final FragmentAnimatorListener listener) {
		this.listListener.add(listener);
		
		BundleAnimatorListener tmpListener = new BundleAnimatorListener() {
			@Override
			public void onAnimationPlaying(BundleAnimator animation, long currentTime) {
				FragmentAnimator.this.onAnimationPlaying(FragmentAnimator.this, currentTime);
			}
			
			@Override
			public void onAnimationEnd(BundleAnimator animation) {
				FragmentAnimator.this.onAnimationEnd(FragmentAnimator.this);
			}


			@Override
			public void onAnimationEndReverse(BundleAnimator animator) {
				FragmentAnimator.this.onAnimationEndReverse(FragmentAnimator.this);
			}
			
			@Override
			public void onAnimationCancel(BundleAnimator animation) {}

			@Override
			public void onAnimationStart(BundleAnimator animation) {}
			
			@Override
			public void onAnimationRepeat(BundleAnimator animation) {}
		};
		
		if(handlerEvent != null){
			handlerEvent.addListener(tmpListener);
		} else {
			listTmpBundleListener.add(tmpListener);
		}
	}

	public void start() {
		this.onAnimationStart(this);
		
		animatorCurrent.start();
		animatorNext.start();		
	}

	public void cancel() {
		animatorCurrent.cancel();
		animatorNext.cancel();
		
		this.onAnimationCancel(this);
	}
	
	public void resume() {
		if(isStarted == false)
			this.onAnimationStart(this);

		animatorCurrent.resume();
		animatorNext.resume();		
	}

	public void reverse() {
		animatorCurrent.reverse();
		animatorNext.reverse();
		
		isStarted = false;
	}

	@Override
	public void onAnimationStart(FragmentAnimator animation) {
		this.isStarted = true;
		
		for(FragmentAnimatorListener listener: listListener){
			listener.onAnimationStart(animation);
		}
	}

	@Override
	public void onAnimationEnd(FragmentAnimator animation) {
		this.isStarted = false;
		
		for(FragmentAnimatorListener listener: listListener){
			listener.onAnimationEnd(animation);
		}
	}

	@Override
	public void onAnimationCancel(FragmentAnimator animation) {
		for(FragmentAnimatorListener listener: listListener){
			listener.onAnimationCancel(animation);
		}
	}

	@Override
	public void onAnimationRepeat(FragmentAnimator animation) {
		for(FragmentAnimatorListener listener: listListener){
			listener.onAnimationRepeat(animation);
		}
	}

	@Override
	public void onAnimationPlaying(FragmentAnimator animation, long currentTime) {
		for(FragmentAnimatorListener listener: listListener){
			listener.onAnimationPlaying(animation, currentTime);
		}
	}
	
	@Override
	public void onAnimationEndReverse(FragmentAnimator animation) {
		for(FragmentAnimatorListener listener: listListener){
			listener.onAnimationEndReverse(animation);
		}
	}
}
