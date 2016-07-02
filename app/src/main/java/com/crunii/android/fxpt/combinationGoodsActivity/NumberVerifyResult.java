package com.crunii.android.fxpt.combinationGoodsActivity;

import java.io.Serializable;

/**
 * Created by speedingsnail on 15/12/1.
 */
public class NumberVerifyResult implements Serializable{

    public  enum verifyType{

        fixedline,//固话
        broadband,//宽带
        contractAccount,//合账
        contractmode,//入网
    }

    public String number;
    public String type;
    public String address;
    public String bestContact;
    public String displayName;
    public String displayCitizenId;
    public String displayCitizenAddress;
    public String verifiedName;
    public String verifiedCitizenId;
    public String verifiedCitizenAddress;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBestContact() {
        return bestContact;
    }

    public void setBestContact(String bestContact) {
        this.bestContact = bestContact;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayCitizenId() {
        return displayCitizenId;
    }

    public void setDisplayCitizenId(String displayCitizenId) {
        this.displayCitizenId = displayCitizenId;
    }

    public String getDisplayCitizenAddress() {
        return displayCitizenAddress;
    }

    public void setDisplayCitizenAddress(String displayCitizenAddress) {
        this.displayCitizenAddress = displayCitizenAddress;
    }

    public String getVerifiedName() {
        return verifiedName;
    }

    public void setVerifiedName(String verifiedName) {
        this.verifiedName = verifiedName;
    }

    public String getVerifiedCitizenId() {
        return verifiedCitizenId;
    }

    public void setVerifiedCitizenId(String verifiedCitizenId) {
        this.verifiedCitizenId = verifiedCitizenId;
    }

    public String getVerifiedCitizenAddress() {
        return verifiedCitizenAddress;
    }

    public void setVerifiedCitizenAddress(String verifiedCitizenAddress) {
        this.verifiedCitizenAddress = verifiedCitizenAddress;
    }
}
