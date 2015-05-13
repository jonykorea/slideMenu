package com.brewbrew.network.data;

import java.util.ArrayList;

/**
 * Created by Jony on 2015-01-15.
 */
public class ArrayMenuData {

    public String code;
    public String name;
    public int price;
    public int saleprice;
    public String img;
    public String imgtb;
    public String comment;
    public String commentwriter;

    // event
    // 이벤트 메뉴(time sale) 여부
    public int evtflag; // 0: 일반 메뉴 / 1: 이벤트 메뉴
    public long evtstime; // 이벤트 시작 시간
    public long evtetime; // 이벤트 종료 시간
    public int evtcount; // 이벤트 남은 개수

    public ArrayList<ArrayOptionData> option;
}
