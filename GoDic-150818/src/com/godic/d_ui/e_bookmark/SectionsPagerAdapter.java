package com.godic.d_ui.e_bookmark;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
 
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Context mContext;
 
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
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
}
