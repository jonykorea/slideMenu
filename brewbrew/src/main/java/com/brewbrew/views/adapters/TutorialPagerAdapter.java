package com.brewbrew.views.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brewbrew.R;

/**
 * Created by jony on 15. 6. 2..
 */
public class TutorialPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context mContext;
    String[] mTitleColor;
    String[] mTitle;
    String[] mContents;
    int[] mImage;
    LayoutInflater mInflater;

    public TutorialPagerAdapter(Context context, String[] titleColor, String[] title,
                                String[] contents, int[] image) {
        this.mContext = context;
        this.mTitleColor = titleColor;
        this.mTitle = title;
        this.mContents = contents;
        this.mImage = image;
    }

    @Override
    public int getCount() {
        return mTitleColor.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables
        TextView tvTitle;
        TextView tvContents;
        ImageView ivTutorial;

        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = mInflater.inflate(R.layout.viewpager_item, container,
                false);


        tvTitle = (TextView) itemView.findViewById(R.id.title);
        tvContents = (TextView) itemView.findViewById(R.id.contents);

        //txtrank.setText(titleColor[position]);
        tvTitle.setText(mTitle[position]);
        tvContents.setText(mContents[position]);

        ivTutorial = (ImageView) itemView.findViewById(R.id.image);

        ivTutorial.setImageResource(mImage[position]);

        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}
