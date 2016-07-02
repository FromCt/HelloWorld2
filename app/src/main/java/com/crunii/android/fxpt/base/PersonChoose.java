package com.crunii.android.fxpt.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ct on 2016/5/23.
 */


public class PersonChoose implements Parcelable
{
    private String name;

    public void  setName(String name){
        this.name=name;
    }
    public String  getName(){
        return this.name;
    }
    private String sale_point;
    public void  setSale_point(String sale_point){
        this.sale_point=sale_point;
    }
    public String getSale_point(){
        return sale_point;
    }
    private String master;
    public void  setMaster(String master){
        this.master=master;
    }
    public String getMaster(){
        return master;
    }
    private String branch;
    public void setBranch(String branch){
        this.branch=branch;
    }
    public String getBranch(){
        return branch;
    }
    private String attribute;
    public void setAttribute(String attribute){
       this.attribute=attribute;
    }
    public String getAttribute(){
        return attribute;
    }
    private String crm;
    public void setCrm(String crm){
        this.crm=crm;
    }

    public String getCrm() {
        return crm;
    }

    private String category;
    public void setCategory(String category){
        this.category=category;
    }

    public String getCategory() {
        return category;
    }

    private String  id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public PersonChoose()
    {
    }




    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(name);
        out.writeString(crm);
        out.writeString(sale_point);
        out.writeString(master);
        out.writeString(branch);
        out.writeString(attribute);
        out.writeString(category);
        out.writeString(id);

    }

    public static final Parcelable.Creator<PersonChoose> CREATOR = new Creator<PersonChoose>()
    {
        @Override
        public PersonChoose[] newArray(int size)
        {
            return new PersonChoose[size];
        }

        @Override
        public PersonChoose createFromParcel(Parcel in)
        {
            return new PersonChoose(in);
        }
    };

    public PersonChoose(Parcel in)
    {
        name = in.readString();
        crm = in.readString();
        sale_point = in.readString();
        master = in.readString();
        branch = in.readString();
        attribute = in.readString();
        category = in.readString();
        id = in.readString();
    }
}