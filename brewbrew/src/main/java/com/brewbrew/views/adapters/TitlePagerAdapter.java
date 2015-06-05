package com.brewbrew.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.brewbrew.R;
import com.brewbrew.views.layout.CuzPagerSlidingTabStrip;

import java.util.List;

/**
 * The <code>PagerAdapter</code> serves the fragments when paging.
 * @author mwho
 */
public class TitlePagerAdapter extends FragmentPagerAdapter implements CuzPagerSlidingTabStrip.IconTabProvider {
 
    private List<Fragment> mFragments;
    
    private final int[] TITLES_IMG = { R.drawable.selector_title_gnb_01,R.drawable.selector_title_gnb_02, R.drawable.selector_title_gnb_03};
	
    
    /**
     * @param fm
     * @param fragments
     */
    public TitlePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        return this.mFragments.get(position);
    }
 
    public List<Fragment> getFragments() {
		return mFragments;
	}
    
    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return this.mFragments.size();
    }
	@Override
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return TITLES_IMG[position];
		
	}
    
    
}






