package com.godic.d_ui.e_bookmark;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
 
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    Map<Integer, String> mFragmentTags;
    FragmentManager mFragmentManager;
    FragmentTransaction fragmentTransaction;
    Context mContext;
 
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
        mContext = context;
    }
 
    @Override
    public Fragment getItem(int position) {
 
        switch (position) {
            case 0:
                BookmarkRead tab1 = new BookmarkRead(mContext);
                return tab1;
            case 1:
                BookmarkTest tab2 = new BookmarkTest(mContext);
                return tab2;
            default:
                return null;
        }
    }
 
    @Override
    public int getCount() {
        return 2;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	// TODO Auto-generated method stub
    	Object obj = super.instantiateItem(container, position);
    	if(obj instanceof Fragment){
    		// record the fragment tag here.
    		Fragment fragment = (Fragment) obj;
    		String tag = fragment.getTag();
    		Log.e("@@@Tag", tag+"");
    		mFragmentTags.put(position, tag);
    	}
    	return obj;
    }
    
    public Fragment getFragment(int position){
    	String tag = mFragmentTags.get(position);
    	Log.e("@@@Tag", tag+"");
    	if(tag == null)
    		return null;
    	return mFragmentManager.findFragmentByTag(tag);
    }
}
