package info.mabin.android.dolmantab.example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment2 extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	return inflater.inflate(R.layout.fragment2, null);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	//first saving my state, so the bundle wont be empty. 
    	outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE"); 
    	super.onSaveInstanceState(outState); 
    }
}
