package info.mabin.android.dolmantab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class DolmanTabAdapter implements DolmanTabInterface.OnTabListener, DolmanTabInterface.OnPageListener{
	protected Context parent;
	protected DolmanTabLayout layout;
	
	public DolmanTabAdapter(Context context, DolmanTabLayout layout){
		this.parent = context;
		this.layout = layout;
		layout.setTabAdapter(this);
	}
	
	public void addTab(DolmanTabWidget.Tab tab, Class<?> clss, Bundle args){
		layout.addTab(tab);
	}
	
	public abstract Fragment getItem(int position);
	
	public abstract int getCount();
	
	public DolmanTabLayout getLayout(){
		return layout;
	}
}
