package com.crunii.android.fxpt.combinationGoodsActivity;

import com.crunii.android.fxpt.business.TibmGoodsPlan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by speedingsnail on 15/12/1.
 */
public class ProductDetailResultDemo implements Serializable{
    public String name;//商品名称
    public String goodsCode;//商品编码
    public String price;//商品价格
    public String payPrice;//点击后展示的商品价格
    public String stock; //商品库存
    public String fixedline;//new,add,none",固话（新装，已有,无需宽带）
    public String broadband;//宽带
    public String phone;//手机
    public String contractmode;//new,add,none"(合约机)入网方式（新装，已有）
    public String guarantee;//none,fixedline,broadband 担保方式
    public String contractAccountType;//no,yes 合帐方式（不合帐,合帐）
    public String defaultSn;    //默认终端串号调拨合作伙伴门户工号
    public boolean hasDetailContent;    //是否需要展示图文详情
    public String dlsName;     //经营主体校验字段
    public String branchCompany;    //分公司校验字段
    public boolean verifyOldInfo;    //20150304分销平台建档及流程调整需求 增加“老客户信息是否需要校验”的管理项
    public boolean modifyOcrName;//是否允许修改扫描出来的身份证姓名
    public boolean modifyOcrCarNum;//是否允许修改扫描出来的身份证号码
    public boolean modifyOcrAddress; //是否允许修改扫描出来的身份证地址
    public boolean uploadCitizenPhoto;//是否需要上传身份证照片
    public boolean isContractAccount;//合同账号是否可配置展示
    public String templateId;
    public String databaseId = "";//数据库id
    public ArrayList<String> businePhone ;//商家电话


    public TibmGoodsPlan contractAccount;
    public ArrayList<Product> products;//产品列表, 数量不定
    public ArrayList<Common> contract;  //模式(合约)
    public ArrayList<Common> contractPackage;  //第一个模式（合约）所对应的套餐
    public ArrayList<Common> salemode;  //售卖方式（酬金分月结，酬金一次结）


    public String ITVType;//none,new,modify   无需ITV    新装ITV    改资费
    public ArrayList<Common> ITV;//ITV套餐（只有合约和固宽模版）
    /**
     * 选号部分
     */
    public ChoooseNumberDemo choooseNumberDemo;



    public class Product implements Serializable{
        public String name;
        public String productCode;
        public String stock;
        public String stockNumber;
        public boolean requireSn; //是否需要填写串码
        public ArrayList<Detailimg> detailimg; //商品细节图

    }
    public class Detailimg implements Serializable{
        public String image;
    }
    public class Common  implements Serializable{

        public String id;
        public String name;
        public String desc;
    }

    //差选号部分

    public ProductDetailResultDemo(){}

    public ProductDetailResultDemo(JSONObject jsonObject){
        name = jsonObject.optString("name");
        goodsCode = jsonObject.optString("goodsCode");
        price = jsonObject.optString("price");
        payPrice = jsonObject.optString("payPrice");
        stock = jsonObject.optString("stock");
        fixedline = jsonObject.optString("fixedline");
        broadband = jsonObject.optString("broadband");
        phone = jsonObject.optString("phone");
        contractmode = jsonObject.optString("contractmode");
        guarantee = jsonObject.optString("guarantee");
        contractAccountType = jsonObject.optString("contractAccountType");
        defaultSn = jsonObject.optString("defaultSn");
        hasDetailContent = jsonObject.optBoolean("hasDetailContent");
        dlsName = jsonObject.optString("dlsName");
        databaseId = jsonObject.optString("databaseId");
        templateId = jsonObject.optString("templateId");
        businePhone = initStringList(jsonObject.optJSONArray("businePhone"));
        branchCompany = jsonObject.optString("branchCompany");
        verifyOldInfo = jsonObject.optBoolean("verifyOldInfo");
        modifyOcrName = jsonObject.optBoolean("modifyOcrName");
        modifyOcrCarNum = jsonObject.optBoolean("modifyOcrCarNum");
        modifyOcrAddress = jsonObject.optBoolean("modifyOcrAddress");
        uploadCitizenPhoto = jsonObject.optBoolean("uploadCitizenPhoto");
        isContractAccount = jsonObject.optBoolean("isContractAccount");
        contractAccount = new TibmGoodsPlan(jsonObject.optJSONObject("contractAccount"));
        initProduct(jsonObject.optJSONArray("products"));
        contract = initCommonList(jsonObject.optJSONArray("contract"));
        contractPackage = initCommonList(jsonObject.optJSONArray("contractPackage"));
        salemode = initCommonList(jsonObject.optJSONArray("salemode"));


        choooseNumberDemo = new ChoooseNumberDemo();
        choooseNumberDemo.needChooseNumber = jsonObject.optBoolean("needChooseNumber");
        choooseNumberDemo.areaList = initAreaList(jsonObject.optJSONArray("areaList"));
        choooseNumberDemo.databaseId = jsonObject.optString("databaseId");
        choooseNumberDemo.sectionList = initStringList(jsonObject.optJSONArray("sectionList"));

        ITVType = jsonObject.optString("ITVType");
        ITV = initCommonList(jsonObject.optJSONArray("ITV"));

    }

    private ArrayList<String> initStringList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<String>();
        if(jsonArray == null){
            return list;
        }
        for(int i = 0 ; i < jsonArray.length(); i++){
            list.add(jsonArray.optString(i));
        }
        return list;
    }

    private ArrayList<ItemCommon> initAreaList(JSONArray jsonArray){
        ArrayList<ItemCommon> list = new ArrayList<ItemCommon>();
        ItemCommon common;
        for(int i = 0; i < jsonArray.length(); i++) {
            common = new ItemCommon();
            common.id = jsonArray.optJSONObject(i).optString("id");
            common.name = jsonArray.optJSONObject(i).optString("name");
            list.add(common);
        }
        return list;
    }

    private void initProduct(JSONArray jsonArray){
        products = new ArrayList<Product>();
        Product product;
        for(int i = 0; i < jsonArray.length(); i++){
            product = new Product();
            product.name = jsonArray.optJSONObject(i).optString("name");
            product.productCode = jsonArray.optJSONObject(i).optString("productCode");
            product.stock = jsonArray.optJSONObject(i).optString("stock");
            product.stockNumber = jsonArray.optJSONObject(i).optString("stockNumber");
            product.requireSn = jsonArray.optJSONObject(i).optBoolean("requireSn");
            product.detailimg = initDetailimg(jsonArray.optJSONObject(i).optJSONArray("detailimg"));
            products.add(product);
        }
    }
    private ArrayList<Detailimg> initDetailimg(JSONArray jsonArray){
        ArrayList<Detailimg> list = new ArrayList<Detailimg>();
        Detailimg detailimg ;
        for(int i = 0; i < jsonArray.length(); i++) {
            detailimg = new Detailimg();
            detailimg.image = jsonArray.optJSONObject(i).optString("image");
            list.add(detailimg);
        }
        return list;
    }
    public ArrayList<Common> initCommonList(JSONArray jsonArray){
        ArrayList<Common> list = new ArrayList<Common>();
        Common common;
        for(int i = 0; i < jsonArray.length(); i++) {
            common = new Common();
            common.id = jsonArray.optJSONObject(i).optString("id");
            common.name = jsonArray.optJSONObject(i).optString("name");
            common.desc = jsonArray.optJSONObject(i).optString("desc");
            list.add(common);
        }
        return list;
    }

    public Product getProductByProductCode(ArrayList<Product> products , String productCode){
        for(Product product : products){
            if(product.productCode.equals(productCode))
                return product;
        }
        return new Product();
    }
}
