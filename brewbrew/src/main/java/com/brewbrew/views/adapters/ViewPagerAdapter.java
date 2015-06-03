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
public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    String[] titleColor;
    String[] title;
    String[] contents;
    int[] image;
    LayoutInflater inflater;

    public ViewPagerAdapter(Context context, String[] titleColor, String[] title,
                            String[] contents, int[] image) {
        this.context = context;
        this.titleColor = titleColor;
        this.title = title;
        this.contents = contents;
        this.image = image;
    }

    @Override
    public int getCount() {
        return titleColor.length;
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

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);


        tvTitle = (TextView) itemView.findViewById(R.id.title);
        tvContents = (TextView) itemView.findViewById(R.id.contents);

        //txtrank.setText(titleColor[position]);
        tvTitle.setText(title[position]);
        tvContents.setText(contents[position]);

        ivTutorial = (ImageView) itemView.findViewById(R.id.image);

        ivTutorial.setImageResource(image[position]);

        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}
