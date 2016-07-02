package com.crunii.android.fxpt.business;

import java.io.Serializable;

/**
 * Created by speedingsnail on 15/8/24.
 */
public class PreAd implements Serializable {


    private String image;
    private String target;

    public PreAd(String image,String target){
        this.image = image;
        this.target = target;
    }

    public String getImage(){
        return image;
    }

}
