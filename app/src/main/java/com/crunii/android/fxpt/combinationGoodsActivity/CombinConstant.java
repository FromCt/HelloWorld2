package com.crunii.android.fxpt.combinationGoodsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by speedingsnail on 15/11/24.
 */
public class CombinConstant {

    public static final String TEMPLATE = "template";
    public static final String CRMID = "CRMId";
    public static final String SHELLCODE = "shellCode";


    /**
     * 固话（新装，已有,无需宽带）
     */
    public static class Fixedline{
        public static final String NONE = "none";
        public static final String ADD = "add";
        public static final String NEW = "new";
    }

    /**
     * 宽带（新装，已有）
     */
    public static class Broadband{
        public static final String ADD = "add";
        public static final String NEW = "new";
    }

    /**
     * 手机（新装，已有）
     */
    public static class Phone{
        public static final String ADD = "add";
        public static final String NEW = "new";
    }

    /**
     * (合约机)入网方式（新装，已有）
     */
    public static class Contractmode{
        public static final String ADD = "add";
        public static final String NEW = "new";
    }

    /**
     * 担保方式
     */
    public static class Guarantee{//none,fixedline,broadband
        public static final String NONE = "none";
        public static final String FIXESLINE = "fixedline";
        public static final String BROADBAND = "broadband";
    }


    /**
     * ITV 无需ITV    新装ITV    改资费
     * selectItvId  0 = none,1 = new ,2 = modify
     */
    public static class ITV{
        public static final String NONE = "0";
        public static final String NEW = "1";
        public static final String MODIFY = "2";
    }

    /**
     * 合帐方式（不合帐,合帐）
     */
    public static class ContractAccountType{
        public static final String NO = "no";
        public static final String YES = "yes";
    }

    public static List<ItemCommon> getFixedlineCommons(String value){
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        if(value == null){
            return list;
        }
        if(value.contains(Fixedline.NONE)){
            ItemCommon common = new ItemCommon();
            common.id = Fixedline.NONE;
            common.name = "无需固话";
            list.add(common);
        }
        if(value.contains(Fixedline.NEW)){
            ItemCommon common = new ItemCommon();
            common.id = Fixedline.NEW;
            common.name = "新用户入网";
            list.add(common);
        }
        if(value.contains(Fixedline.ADD)){
            ItemCommon common = new ItemCommon();
            common.id = Fixedline.ADD;
            common.name = "老用户加装";
            list.add(common);
        }
        return list;
    }

    public static List<ItemCommon> getBroadband(String value){
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        if(value == null){
            return list;
        }
        if(value.contains(Fixedline.NEW)){
            ItemCommon common = new ItemCommon();
            common.id = Fixedline.NEW;
            common.name = "新用户入网";
            list.add(common);
        }
        if(value.contains(Fixedline.ADD)){
            ItemCommon common = new ItemCommon();
            common.id = Fixedline.ADD;
            common.name = "老用户加装";
            list.add(common);
        }
        return list;
    }

    public static List<ItemCommon> getPhone(String value){
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        if(value == null){
            return list;
        }
        if(value.contains(Fixedline.NEW)){
            ItemCommon common = new ItemCommon();
            common.id = Fixedline.NEW;
            common.name = "新用户入网";
            list.add(common);
        }
        return list;
    }

    /**
     * selectItvId  0 = none,1 = new ,2 = modify
     * @param value
     * @return
     */
    public static List<ItemCommon> getITV(String value){
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        if(value == null){
            return list;
        }
        if(value.contains("none")){
            ItemCommon common = new ItemCommon();
            common.id = ITV.NONE;
            common.name = "无需ITV";
            list.add(common);
        }
        if(value.contains("new")){
            ItemCommon common = new ItemCommon();
            common.id = ITV.NEW;
            common.name = "新装ITV";
            list.add(common);
        }
        if(value.contains("modify")){
            ItemCommon common = new ItemCommon();
            common.id = ITV.MODIFY;
            common.name = "改资费";
            list.add(common);
        }
        return list;
    }

    public static List<ItemCommon> getContractAccountType(String value){
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        if(value == null){
            return list;
        }
        if(value.contains(ContractAccountType.NO)){
            ItemCommon common = new ItemCommon();
            common.id = ContractAccountType.NO;
            common.name = "不合账";
            list.add(common);
        }
        if(value.contains(ContractAccountType.YES)){
            ItemCommon common = new ItemCommon();
            common.id = ContractAccountType.YES;
            common.name = "合账";
            list.add(common);
        }
        return list;
    }


    public static List<ItemCommon> getGuarantee(String value) {//none,fixedline,broadband

        List<ItemCommon> list = new ArrayList<ItemCommon>();
        if(value.contains(CombinConstant.Guarantee.NONE)){
            ItemCommon common = new ItemCommon();
            common.id = CombinConstant.Guarantee.NONE;
            common.name = "无担保";
            list.add(common);
        }
        if(value.contains(CombinConstant.Guarantee.FIXESLINE)){
            ItemCommon common = new ItemCommon();
            common.id = Guarantee.FIXESLINE;
            common.name = "固话担保";
            list.add(common);
        }
        if(value.contains(Guarantee.BROADBAND)){
            ItemCommon common = new ItemCommon();
            common.id = Guarantee.BROADBAND;
            common.name = "宽带担保";
            list.add(common);
        }
        return list;
    }

    public static List<ItemCommon> getContractmode(String value){
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        if(value.contains(CombinConstant.Fixedline.NEW)){
            ItemCommon common = new ItemCommon();
            common.id = CombinConstant.Fixedline.NEW;
            common.name = "新用户入网";
            list.add(common);
        }
        if(value.contains(CombinConstant.Fixedline.ADD)){
            ItemCommon common = new ItemCommon();
            common.id = CombinConstant.Fixedline.ADD;
            common.name = "老用户加装";
            list.add(common);
        }
        return list;
    }
}