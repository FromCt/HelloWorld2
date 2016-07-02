package com.crunii.android.fxpt.util;

import android.os.Environment;

public class Constant {

    public static final Boolean TEST_FLAG =true;
    public static final String CTX_PATH;
    public static final String CTX_ACTIVITY;
    public static final String CTX_BESTPAYWAP;

    //第三方APK下载地址
    public static final String DOWNLOAD_PATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/download/";

    public static final String localKeyStr = "KeyTimeLuanQiBaZao";

    static {
        if (TEST_FLAG) {
            CTX_PATH = "http://wap.crunii.com/ceds-server/";
            CTX_ACTIVITY = "http://wap.crunii.com/ceds/";
            CTX_BESTPAYWAP = "http://222.177.210.230:232/ceds-bank/servlet/";
        } else {
            CTX_PATH = "http://cq.189.cn/ceds-server/";
            CTX_ACTIVITY = "http://cq.189.cn/fx/";
            CTX_BESTPAYWAP = "http://dls.cq.ct10000.com/ceds-bank/servlet/";
        }
    }

    public static int loginTimes=0;//记录登录的次数；

    public static class URL {
        //1.0.4之前的版本："http://cq.189.cn/cedsUpload/upfile/update.txt"
        public static final String UPDATE = "http://img.cq.ct10000.com/static/system/apkupdate/update.txt"; //升级服务器，仅针对生产版本

        public static final String UPLOAD_URL = "http://img.cq.ct10000.com/img-manager/ceds/apkUpload"; //图片服务器，不区分生产和测试

        public static final String VERIFY_IMAGE_URL = "http://img.cq.ct10000.com/img-manager/emall/acceptIdUpload"; //身份证识别接口，不区分生产和测试

        public static final String BESTPAYWAPPOST = CTX_BESTPAYWAP + "bestpayWapPost"; //翼支付WAP

        // define other URL here:
        public static final String GETCODE = CTX_PATH + "getcode";
        public static final String LOGIN = CTX_PATH + "login";
        public static final String LOGIN_CHOOSE = CTX_PATH + "loginChoose";

        public static final String LOGOUT = CTX_PATH + "logout";
        public static final String HOME = CTX_PATH + "home";
        public static final String MYPROFITNEW= CTX_PATH + "myprofitNew";
        public static final String PROFITDETAILNEW = CTX_PATH + "profitdetailNew";
        public static final String PROFITLISTNEW = CTX_PATH + "profitlistNew";
        public static final String ORDERLIST = CTX_PATH + "orderlist";
        public static final String MESSAGE = CTX_PATH + "message";
        public static final String PERSONALINFO = CTX_PATH + "personalinfo";
        public static final String SAVECRMNUM = CTX_PATH + "savecrmnum";
        public static final String SAVEACTIVITYADDRESS = CTX_PATH + "saveactivityaddress";
        public static final String ACCOUNTINFO = CTX_PATH + "accountinfo";
        public static final String ACCOUNTCODE = CTX_PATH + "accountcode";
        public static final String ACCOUNTUPDATE = CTX_PATH + "accountupdate";
        public static final String INFOQUERY = CTX_PATH + "infoquery";
        public static final String ADDRESSQUERY = CTX_PATH + "addressquery";
        public static final String EPAYCONFIRM = CTX_PATH + "ePayConfirm";
        public static final String GQQUERY = CTX_PATH + "gq_query";
        public static final String CANCELORDER = CTX_PATH + "cancelorder";
        public static final String REFUNDORDER = CTX_PATH + "refundorder";
        public static final String RETURNORDER = CTX_PATH + "returnorder";
        public static final String CONFIRMORDER = CTX_PATH + "confirmorder";
        public static final String ADDRESSDEL = CTX_PATH + "addressdel";
        public static final String ADDRESSADD = CTX_PATH + "addressadd";
        public static final String ADDRESSUPDATE = CTX_PATH + "addressupdate";
        public static final String MODE = CTX_PATH + "mode";
        public static final String SPEEDPACKAGE = CTX_PATH + "speedpackage";
        public static final String PRODUCT = CTX_PATH + "product";
        public static final String PRODUCTDETAIL = CTX_PATH + "productdetail";
        public static final String DETAILCONTENT = CTX_PATH + "detailcontent";
        public static final String SUBMITCARDINFO = CTX_PATH + "submitcardinfo";
        public static final String VERIFYNUMBER = CTX_PATH + "verifynumber";
        public static final String CREATEORDER = CTX_PATH + "createorder";
        public static final String BINDIMAGE = CTX_PATH + "bindimage";
        public static final String PAYSTATUS = CTX_PATH + "paystatus";
        public static final String GOODSPRICE = CTX_PATH + "goodsprice";
        public static final String PARTNER = CTX_PATH + "partner";
        public static final String CLOSEACCOUNT = CTX_PATH + "closeaccount";
        public static final String PARTNERLIST = CTX_PATH + "partnerlist";
        public static final String REMOVEPARTNER = CTX_PATH + "removepartner";
        public static final String ADDPARTNER = CTX_PATH + "addpartner";
        public static final String MODIFYPARTNER = CTX_PATH + "modifypartner";
        public static final String PARTNERVERIFY = CTX_PATH + "partnerverify";
        public static final String CHOOSENUMBER = CTX_PATH + "choosenumber";
        public static final String CONTRACTPACKAGE = CTX_PATH + "contractpackage";
        public static final String IDCARDVALIDATE = CTX_PATH + "IDCardValidate";
        public static final String CHECKCARDNUM = CTX_PATH + "cheakCardNum";
        public static final String ISSUPPORTTRANSCRIBE = CTX_PATH + "isSupportTranscribe";
        public static final String TRANSCRIBE = CTX_PATH + "transcribe";
        public static final String ISPAYORNUMBERTRANSCRIBE = CTX_PATH + "isPayOrNumberTranscribe";
        public static final String SUBMITTRANSCRIBE = CTX_PATH + "submitTranscribe";
        public static final String TRANSCRIBEORDERLIST = CTX_PATH + "transcribeOrderList";
        public static final String BLCANCELORDER = CTX_PATH + "blOrderCancel";
        public static final String CARDSALESAVEORDER = CTX_PATH + "cardSaleSaveOrder";
        public static final String CARDLIST = CTX_PATH + "cardList";
        public static final String CALSERVICEFEE = CTX_PATH + "calServiceFee";
        public static final String DISTRIBUTIONSEARCH = CTX_PATH + "distributionSearch";
        public static final String DISTRIUBTIONDETAIL = CTX_PATH + "distributorDetail";
        public static final String CHECKPARTNERSYSTEM = CTX_PATH + "checkPartnerSystem";
        public static final String ISBUILDPARTNER = CTX_PATH + "isBuildPartner";
        public static final String SAVEPARTNERMSG = CTX_PATH + "savePartnerMsg";
        public static final String SAVEPARTNERSYSTEM =CTX_PATH+"savePartnerSystem";
        public static final String BUILDPARTNERCODE = CTX_PATH + "buildPartnerCode";
        public static final String CHECKBUYLIMIT = CTX_PATH + "cheakbuyLimit";
        public static final String ISTODISTRIBUTOR = CTX_PATH + "isToDistributor";
        public static final String MYPARTNERLISTNEW = CTX_PATH + "myPartnerListNew";
        public static final String ORDETIAL = CTX_PATH + "getNormalOrderDetail";
        public static final String LOGISTICDETIAL = CTX_PATH + "logisticsDetail";
        public static final String EXEPOSEDETIAL = CTX_PATH + "exposeDetial";
        public static final String USERDETAIL = CTX_PATH + "userDetail";
        public static final String WELCOMEPAGE = CTX_PATH + "welcomePage";
        public static final String READMESSAGE = CTX_PATH + "readmessage";
        public static final String MESSAGEDETAIL = CTX_PATH + "messageDetail";
        public static final String ISUPLOADIMAGE = CTX_PATH + "isUploadImage";
        public static final String CHANGEPAYTYPE = CTX_PATH + "changePayType";
        public static final String MYNETSTROE = CTX_PATH + "myNetStroe";

        public static final String PACKAGEINTRODUCE = CTX_PATH + "packageIntroduce";//套餐介绍
        public static final String PACKAGEINTRODUCEDETIAL = CTX_PATH + "packageIntroduceDetail";//套餐介绍详情


        public static final String SHOPMANAGEMENT = CTX_PATH + "shopManagement";//网店开通查询
        public static final String SHOPMANAGEMWNTSMS = CTX_PATH + "shopManagementSmsShare";//网店短息分享
        public static final String SHOPMANAGEMWNTWOWENWEN = CTX_PATH + "shopManagementSetAsk";//网店我问问设置
        public static final String SHOPMANAGEMENTSEECODE = CTX_PATH + "shopManagementSeeCode";//网店二维码
        public static final String SHOPMANAGEMENTDAIXUAN = CTX_PATH + "shopManagementToChoose";//网店待选商品
        public static final String SHOPMANAGEMENTYIXUAN = CTX_PATH + "shopManagementHaveChose";//网店已选商品
        public static final String SHOPMANAGEMENTADD= CTX_PATH + "shopManagementInsert";//网店加入待选区
        public static final String SHOPMANAGEMENTRETURN= CTX_PATH + "shopManagementReturn";//网店撤回已选
        public static final String SHOPMANAGEMENTSETMONEY= CTX_PATH + "shopManagementSetMoney";//设置优惠

        public static final String CBG_BUILDGOODSDETAILDATA = CTX_PATH + "cbg_buildGoodsDetailData";
        public static final String NETSTOREORDERLIST = CTX_PATH + "myNetStoreOrderlist";
        public static final String INFO_PERMISSION = CTX_PATH + "infoPermission";


    }

}
