package com.crunii.android.fxpt.business;

import android.view.View;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 合同账号配置展示
 * Created by Administrator on 2015/4/28.
 */
public class TibmGoodsPlan implements Serializable {

    public static final String YZHM_DB ="db";
    public static final String YZHM_JZ ="jz";
    public static final String YZHM_HZ ="hz";


    private DB db;//担保
    private JZ jz;//加装
    private HZ hz;//合帐

    public TibmGoodsPlan(JSONObject jsonObject) {
        if (jsonObject != null) {
            db = new DB(jsonObject);
            jz = new JZ(jsonObject);
            hz = new HZ(jsonObject);
        }

    }


    public DB getDb() {
        return db;
    }

    public JZ getJz() {
        return jz;
    }

    public HZ getHz() {
        return hz;
    }

    private int transportToView(int i){
        if(i == 1){
            return View.VISIBLE;
        }else{
            return View.GONE;
        }
    }


    public class RW implements  Serializable{

    }

    public class DB implements Serializable{
        private int db_cust_name;//
        private int db_blance;   //
        private int db_combo_name;//
        private int db_eff_time;  //
        private int db_net_time;  //
        private int db_cal;       //
        private int db_days;      //

        public DB(JSONObject jsonObject){
            if(jsonObject != null){
                db_cust_name = jsonObject.optInt("db_cust_name");
                db_blance = jsonObject.optInt("db_blance");
                db_combo_name = jsonObject.optInt("db_combo_name");
                db_eff_time = jsonObject.optInt("db_eff_time");
                db_net_time = jsonObject.optInt("db_net_time");
                db_cal = jsonObject.optInt("db_cal");
                db_days = jsonObject.optInt("db_days");
            }
        }
        public int getDb_cust_name() {
            return db_cust_name;
        }

        public int getDb_blance() {
            return db_blance;
        }

        public int getDb_combo_name() {
            return db_combo_name;
        }

        public int getDb_eff_time() {
            return db_eff_time;
        }

        public int getDb_net_time() {
            return db_net_time;
        }

        public int getDb_cal() {
            return db_cal;
        }

        public int getDb_days() {
            return db_days;
        }
    }

    public class JZ implements Serializable{
        private int jz_cust_name; //
        private int jz_blance;    //
        private int jz_combo_name;//
        private int jz_eff_time;  //
        private int jz_net_time;  //
        private int jz_cal;       //
        private int jz_days;      //

        public JZ(JSONObject jsonObject){
            if(jsonObject != null){
                jz_cust_name = jsonObject.optInt("jz_cust_name");
                jz_blance = jsonObject.optInt("jz_blance");
                jz_combo_name = jsonObject.optInt("jz_combo_name");
                jz_eff_time = jsonObject.optInt("jz_eff_time");
                jz_net_time = jsonObject.optInt("jz_net_time");
                jz_cal = jsonObject.optInt("jz_cal");
                jz_days = jsonObject.optInt("jz_days");
            }
        }
        public int getJz_cust_name() {
            return jz_cust_name;
        }

        public int getJz_blance() {
            return jz_blance;
        }

        public int getJz_combo_name() {
            return jz_combo_name;
        }

        public int getJz_eff_time() {
            return jz_eff_time;
        }

        public int getJz_net_time() {
            return jz_net_time;
        }

        public int getJz_cal() {
            return jz_cal;
        }

        public int getJz_days() {
            return jz_days;
        }
    }

    public class HZ implements Serializable{
        private int hz_cust_name; //
        private int hz_blance;    //
        private int hz_combo_name;//
        private int hz_eff_time;  //
        private int hz_net_time;  //
        private int hz_cal;       //
        private int hz_days;      //

        public HZ(JSONObject jsonObject){
            if(jsonObject != null){
                hz_cust_name = jsonObject.optInt("hz_cust_name");
                hz_blance = jsonObject.optInt("hz_blance");
                hz_combo_name = jsonObject.optInt("hz_combo_name");
                hz_eff_time = jsonObject.optInt("hz_eff_time");
                hz_net_time = jsonObject.optInt("hz_net_time");
                hz_cal = jsonObject.optInt("hz_cal");
                hz_days = jsonObject.optInt("hz_days");
            }
        }
        public int getHz_cust_name() {
            return hz_cust_name;
        }

        public int getHz_blance() {
            return hz_blance;
        }

        public int getHz_combo_name() {
            return hz_combo_name;
        }

        public int getHz_eff_time() {
            return hz_eff_time;
        }

        public int getHz_net_time() {
            return hz_net_time;
        }

        public int getHz_cal() {
            return hz_cal;
        }

        public int getHz_days() {
            return hz_days;
        }
    }











}
