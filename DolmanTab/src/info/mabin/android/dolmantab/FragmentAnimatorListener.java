package info.mabin.android.dolmantab;

public interface FragmentAnimatorListener{
	public void onAnimationStart(FragmentAnimator animation);
	public void onAnimationEnd(FragmentAnimator animation);
	public void onAnimationCancel(FragmentAnimator animation);
	public void onAnimationRepeat(FragmentAnimator animation);
	public void onAnimationPlaying(FragmentAnimator animation, long currentTime);
}
