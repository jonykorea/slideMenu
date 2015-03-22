package com.tws.soul.soulbrown.gcm;

/**
 * Created by Jony on 2015-03-20.
 */
public class GcmDefine {

    // 신규 주문
    public static final String PUSH_NEW_ORDER = "neworder";

    // 주문 상태 변경
    public static final String PUSH_CHG_ORDER = "chgorder";

    // 사용자 접근중
    public static final String PUSH_APPROACH_USER = "approachuser";

    // 점주/점원 수신자 변경
    public static final String PUSH_CHG_PUSHKEY = "chgstorepushkey";

    // 주문 취소
    public static final String PUSH_CANCEL_ORDER = "cancelorder";
}
