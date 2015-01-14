package com.tws.soul.soulbrown.data;

/**
 * Created by Jony on 2015-01-11.
 */

import com.tws.soul.soulbrown.R;

import java.util.ArrayList;
import java.util.List;

public class MenuDataManager {

    private static String[] arrayMenu_HARU = {"아메리카노(Hot)", "레몬꿀차(Hot)", "구운 고구마 라떼(Hot)", "카페라떼(Hot)", "자몽주스(Hot)", "하루다크초크(Hot)"};
    private static int[] arrayPrice_HARU = {2800,4800,4800,3300,4800,5000};
    private static int[] arrayImage_HARU = {R.drawable.haru_01,R.drawable.haru_02,R.drawable.haru_03,R.drawable.haru_04,R.drawable.haru_05,R.drawable.haru_06};

    private static String[] arrayMenu_1022 = {"아메리카노(Hot)", "애플유자(Hot)", "리얼핫초코(Hot)", "클럽샌드위치(단품)", "에그베이컨(Hot)", "햄치즈"};
    private static int[] arrayPrice_1022 = {2500,4000,4500,2900,2900,2900};
    private static int[] arrayImage_1022 = {R.drawable.i1022_01,R.drawable.i1022_02,R.drawable.i1022_03,R.drawable.i1022_04,R.drawable.i1022_05,R.drawable.i1022_06};

    private static String[] arrayMenu_2FLAT = {"아메리카노(Hot)", "카페라떼(Hot)", "바밤바라떼(Hot)", "밀크티(Hot)", "블루베리레몬에이드(Hot)", "트리플베리요거트스무디(Hot)"};
    private static int[] arrayPrice_2FLAT = {3000,3500,4500,4000,5000,5000};
    private static int[] arrayImage_2FLAT = {R.drawable.flat_01,R.drawable.flat_02,R.drawable.flat_03,R.drawable.flat_04,R.drawable.flat_05,R.drawable.flat_06};

    private static MenuDataManager mInstance;
    private List<Menu> menuHaru;
    private List<Menu> menu1022;
    private List<Menu> menuFLAT;

    public static MenuDataManager getInstance() {
        if (mInstance == null) {
            mInstance = new MenuDataManager();
        }

        return mInstance;
    }

    public List<Menu> getMenuHARU() {

            menuHaru = new ArrayList<Menu>();

            for (int i = 0 ; i < arrayMenu_HARU.length ; i++) {
                Menu info = new Menu();
                info.name = arrayMenu_HARU[i];
                info.price = arrayPrice_HARU[i];
                info.image = arrayImage_HARU[i];
                menuHaru.add(info);
            }


        return  menuHaru;
    }


    public List<Menu> getMenu1022() {

            menu1022 = new ArrayList<Menu>();

            for (int i = 0 ; i < arrayMenu_1022.length ; i++) {
                Menu info = new Menu();
                info.name = arrayMenu_1022[i];
                info.price = arrayPrice_1022[i];
                info.image = arrayImage_1022[i];
                menu1022.add(info);
            }


        return  menu1022;
    }

    public List<Menu> getMenu2FLAT() {

            menuFLAT = new ArrayList<Menu>();

            for (int i = 0 ; i < arrayMenu_2FLAT.length ; i++) {
                Menu info = new Menu();
                info.name = arrayMenu_2FLAT[i];
                info.price = arrayPrice_2FLAT[i];
                info.image = arrayImage_2FLAT[i];
                menuFLAT.add(info);
            }


        return  menuFLAT;
    }

}
