package com.crunii.android.fxpt.combinationGoodsActivity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by speedingsnail on 15/12/4.
 */
public class MyList<E>   implements Serializable  {

    private List<E> list;

    public MyList(List<E> list) {
        this.list = list;

    }

}
