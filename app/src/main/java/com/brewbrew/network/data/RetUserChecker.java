package com.brewbrew.network.data;

/**
 * Created by jonychoi on 15. 1. 14..
 */
public class RetUserChecker extends RetCode{

    // type = 0 : 사용자 / 1: 점주
    public int type;

    // 버젼정보 & URL
    public String appver;
    public String appurl;

    // 점포 정보
    public String storename;


}
