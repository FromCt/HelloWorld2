package com.crunii.android.fxpt.combinationGoodsActivity;

import java.io.Serializable;

/**
 * Created by speedingsnail on 15/12/2.
 */
public class ItemCommon implements Serializable{

    public String id;
    public String name;
    public String upperId;

    public ItemCommon(){

    }
    public ItemCommon(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ItemCommon(String id, String name, String upperId) {
        this.id = id;
        this.name = name;
        this.upperId = upperId;
    }
}
