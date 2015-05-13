package com.brewbrew.views;


import android.app.Activity;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.AppController;
import com.app.define.LOG;
import com.brewbrew.listview.adapter.GenericAdapter;
import com.brewbrew.listview.domain.SideMenu;
import com.brewbrew.listview.viewmapping.SideMenuView;
import com.brewbrew.network.data.RetMenuList;
import com.brewbrew.managers.pref.PrefUserInfo;
import com.brewbrew.R;

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
    private ImageView mIvPushStatus;

    private RetMenuList mMenuList = null;


    private int mCurrentSelectedPosition = SoulBrownMainActivity.INIT_MENU_POSITION;
    private boolean mFromSavedInstanceState;

    private RelativeLayout mRlRootLayout;

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

        LOG.d("mNavigationDrawerFragment : 3 : ");

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(PushStatusSync, new IntentFilter("push_status"));

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(PushStatusSync);

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

        ArrayList<SideMenu> sideMenu = getSideMenu(mMenuList);


        for (int i = 0; i < sideMenu.size(); i++) {

            SideMenuView mv = new SideMenuView(sideMenu.get(i), R.layout.list_sidemenu);

            list.add(mv);
        }
        mDrawerListView.setAdapter(new GenericAdapter(list, getActivity()));

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        // setting button
        LinearLayout llSetting = (LinearLayout)mRlRootLayout.findViewById(R.id.menu_setting);

        llSetting.setOnClickListener(new View.OnClickListener() {
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

        LOG.d("mNavigationDrawerFragment : 2 : ");
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

        // push status
        mIvPushStatus = (ImageView) actionBarView.findViewById(R.id.title_push_status);
        mIvPushStatus.setVisibility(View.GONE);

        PrefUserInfo prefUserInfo = new PrefUserInfo(getActivity());
        String userID = prefUserInfo.getUserID();

        String userName = prefUserInfo.getUserName();

        if(!TextUtils.isEmpty(userName))
        {
            tvMenuTitle.setText(userName);
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

                mCallbacks.onChangeDrawerLayoutOpened(mCurrentSelectedPosition);

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

        LOG.d("mNavigationDrawerFragment : 4 : ");
        try {

            mMenuList = ((SoulBrownMainActivity)activity).getMenuList();

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

        void onChangeDrawerLayoutOpened(int position);
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

    private ArrayList<SideMenu> getSideMenu(RetMenuList menuList)
    {
        ArrayList<SideMenu> sideMenuArrayList= new ArrayList<SideMenu>();

        if(AppController.getInstance().getIsUser()) {

            // 사용자
            sideMenuArrayList.add(  new SideMenu(getString(R.string.orderlist),
                    R.xml.xml_icon_menu_list)) ;

            if( menuList != null)
            {
                LOG.d("getSideMenu : "+ menuList.store.size());

                for(int i = 0;i<menuList.store.size();i++)
                {
                    String name = menuList.store.get(i).storename;

                    sideMenuArrayList.add(  new SideMenu(name,
                            R.xml.xml_icon_store)) ;
                }
            }

            /*
            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_haru),
                    R.xml.xml_icon_store)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_1022),
                    R.xml.xml_icon_store)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_2flat),
                    R.xml.xml_icon_store)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_tws),
                    R.xml.xml_icon_store)) ;
            */

            return sideMenuArrayList;
        }
        else
        {
            // 점주
            sideMenuArrayList.add(  new SideMenu(getString(R.string.orderlist),
                    R.xml.xml_icon_menu_list)) ;

            sideMenuArrayList.add(  new SideMenu(getString(R.string.store_all_list),
                    R.xml.xml_icon_menu_list)) ;

            // Time Sale
            PrefUserInfo prefUserInfo = new PrefUserInfo(getActivity());
            String storeID = prefUserInfo.getUserID();
            // 1022만 처리
            if( storeID.equals("PGA010002")) {
                sideMenuArrayList.add(new SideMenu(getString(R.string.time_sale),
                        R.xml.xml_icon_menu_list));
            }

            return sideMenuArrayList;
        }
    }


    // mIvPushStatus
    private BroadcastReceiver PushStatusSync = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra("status",0);

            if( mIvPushStatus != null )
            {
                if( status == 0)
                {
                    mIvPushStatus.setImageResource(R.drawable.push_off);
                    mIvPushStatus.setVisibility(View.VISIBLE);
                }
                else if( status == 1)
                {
                    mIvPushStatus.setImageResource(R.drawable.push_on);
                    mIvPushStatus.setVisibility(View.VISIBLE);
                }
            }

        }
    };
}
