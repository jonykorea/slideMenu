package com.tws.soul.soulbrown.ui;


import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.AppController;
import com.tws.common.listview.adapter.GenericAdapter;
import com.tws.common.listview.domain.SideMenu;
import com.tws.common.listview.viewmapping.SideMenuView;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.lib.StoreInfo;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    public static int ACT_REQUSET_CODE_SETTING = 101;
    public static int ACT_RESULT_CODE_SETTING = 102;
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";


    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    //private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;


    private int mCurrentSelectedPosition = SoulBrownMainActivity.INIT_MENU_POSITION;
    private boolean mFromSavedInstanceState;

    RelativeLayout mRlRootLayout;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            //mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRlRootLayout = (RelativeLayout) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mDrawerListView = (ListView) mRlRootLayout.findViewById(R.id.menu_listview);


        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        List<SideMenuView> list = new ArrayList<SideMenuView>();

        ArrayList<SideMenu> sideMenu = getSideMenu();


        for (int i = 0; i < sideMenu.size(); i++) {

            SideMenuView mv = new SideMenuView(sideMenu.get(i), R.layout.list_sidemenu);

            list.add(mv);
        }
        mDrawerListView.setAdapter(new GenericAdapter(list, getActivity()));

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        // setting button
        Button btnSetting = (Button)mRlRootLayout.findViewById(R.id.menu_btn_setting);

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(intent, ACT_REQUSET_CODE_SETTING);
            }
        });


        return mRlRootLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NavigationDrawerFragment.ACT_REQUSET_CODE_SETTING) {

            if(resultCode == Activity.RESULT_OK) {
                getActivity().setResult(NavigationDrawerFragment.ACT_RESULT_CODE_SETTING);
                getActivity().finish();
            }
        }
    }

    public boolean isDrawerOpen() {

        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F2F4F5")));

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setIcon(null);

        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.actionbar_custom_layout, null);
        getActionBar().setCustomView(actionBarView);
        getActionBar().setDisplayShowCustomEnabled(true);

        LinearLayout ibMenuShow = (LinearLayout) actionBarView.findViewById(R.id.title_btn_menu);

        ibMenuShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCloseDrawerMenu();

            }
        });

        TextView tvMenuTitle = (TextView) actionBarView.findViewById(R.id.title_store_name);

        PrefUserInfo prefUserInfo = new PrefUserInfo(getActivity());
        String userID = prefUserInfo.getUserID();

        if( StoreInfo.getStoreName(userID) > 0)
        {
            tvMenuTitle.setText(StoreInfo.getStoreName(userID));
        }
        else
        {
            tvMenuTitle.setText(userID);
        }



        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {

            }

            @Override
            public void onDrawerClosed(View view) {

                mCallbacks.onChangeDrawerLayout(mCurrentSelectedPosition);

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    public void setListViewItemChecked(int position)
    {
        mDrawerListView.setItemChecked(position, true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

        void onChangeDrawerLayout(int position);
    }

    public void openCloseDrawerMenu()
    {
        if(isDrawerOpen())
        {
            // close
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        else
        {
            // open
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    private ArrayList<SideMenu> getSideMenu()
    {
        ArrayList<SideMenu> sideMenuArrayList= new ArrayList<SideMenu>();

        if(AppController.getInstance().getIsUser()) {

            // 사용자
            sideMenuArrayList.add(  new SideMenu(getString(R.string.orderlist),
                    R.xml.xml_icon_menu_list)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_haru),
                    R.xml.xml_icon_store)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_1022),
                    R.xml.xml_icon_store)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_2flat),
                    R.xml.xml_icon_store)) ;

            return sideMenuArrayList;
        }
        else
        {
            // 점주
            sideMenuArrayList.add(  new SideMenu(getString(R.string.orderlist),
                    R.xml.xml_icon_menu_list)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_all_list),
                    R.xml.xml_icon_menu_list)) ;

            return sideMenuArrayList;
        }
    }
}
