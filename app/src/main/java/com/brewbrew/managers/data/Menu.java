package com.brewbrew.managers.data;

import com.brewbrew.network.data.ArrayOptionData;

import java.util.ArrayList;

/**
 * Created by Jony on 2015-01-11.
 */

public class Menu {

    public String code;

    public String name;

    public String image;

    public String image_thumb;

    public String comment;
    public String comment_write;

    public int price;
    public int saleprice;

    public int count;

    // event
    // 이벤트 메뉴(time sale) 여부
    public int evtflag; // 0: 일반 메뉴 / 1: 이벤트 메뉴
    public long evtstime; // 이벤트 시작 시간
    public long evtetime; // 이벤트 종료 시간
    public int evtcount; // 이벤트 남은 개수

    public ArrayList<ArrayOptionData> option;

}
