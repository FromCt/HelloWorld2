package com.crunii.android.fxpt.combinationGoodsActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 选号部分
 * Created by speedingsnail on 15/12/4.
 */
public class ChoooseNumberDemo implements Serializable{

    public ArrayList<ItemCommon> areaList;//选号归属地列表
    public ArrayList<String> sectionList;//选号号段列表
    public String databaseId;//选号数据库id
    public boolean needChooseNumber;//是否需要选号标志位，仅针对固宽模板

}
