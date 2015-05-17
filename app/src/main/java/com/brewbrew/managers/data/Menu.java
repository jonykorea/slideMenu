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
    // �̺�Ʈ �޴�(time sale) ����
    public int evtflag; // 0: �Ϲ� �޴� / 1: �̺�Ʈ �޴�
    public long evtstime; // �̺�Ʈ ���� �ð�
    public long evtetime; // �̺�Ʈ ���� �ð�
    public int evtcount; // �̺�Ʈ ���� ����

    public ArrayList<ArrayOptionData> option;

}
