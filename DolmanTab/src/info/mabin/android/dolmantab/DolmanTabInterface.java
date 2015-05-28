package info.mabin.android.dolmantab;

import info.mabin.android.bundleanimator.AnimatorInfoSet;
import info.mabin.android.bundleanimator.ObjectAnimatorInfo;

public interface DolmanTabInterface {
	public interface OnPageListener{
		public void onPageSelected(int position, DolmanTabWidget widget);
		public void onPageUnselected(int position, DolmanTabWidget widget);
		public void onPageScrolled(int position, float positionOffset, DolmanTabWidget widget);
	}
	
	public interface OnTabListener{
		public void onTabSelected(int position, DolmanTabLayout layout);
		public void onTabUnselected(int position, DolmanTabLayout layout);
		public void onTabReselected(int position, DolmanTabLayout layout);
	}
	
	public interface PageAnimator{
		public static final PageAnimator CARD_FLIP = new PageAnimator(){
			@Override
			public void animationForward(AnimatorInfoSet currentSet,
					AnimatorInfoSet nextSet) {
				currentSet.playTogether(
						ObjectAnimatorInfo.ofFloat("rotationY", 0, -180).setDuration(300),
						ObjectAnimatorInfo.ofFloat("alpha", 1, 0).setDuration(1).setStartDelay(150)
				);
				nextSet.playTogether(
						ObjectAnimatorInfo.ofFloat("alpha", 1, 0).setDuration(1),
						ObjectAnimatorInfo.ofFloat("rotationY", 180, 0).setDuration(300),
						ObjectAnimatorInfo.ofFloat("alpha", 0, 1).setDuration(1).setStartDelay(150)
				);
			}
			
			@Override
			public void animationBackward(AnimatorInfoSet currentSet,
					AnimatorInfoSet nextSet) {
				currentSet.playTogether(
						ObjectAnimatorInfo.ofFloat("rotationY", 0, 180).setDuration(300),
						ObjectAnimatorInfo.ofFloat("alpha", 1, 0).setDuration(1).setStartDelay(150)
				);
				nextSet.playTogether(
						ObjectAnimatorInfo.ofFloat("alpha", 1, 0).setDuration(1),
						ObjectAnimatorInfo.ofFloat("rotationY", -180, 0).setDuration(300),
						ObjectAnimatorInfo.ofFloat("alpha", 0, 1).setDuration(1).setStartDelay(150)
				);
			}
		};
		
		public void animationForward(AnimatorInfoSet currentSet, AnimatorInfoSet nextSet);
		public void animationBackward(AnimatorInfoSet currentSet, AnimatorInfoSet nextSet);
	}
}
