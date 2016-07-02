package com.crunii.android.fxpt.combinationGoodsActivity;

import com.crunii.android.fxpt.activity.VerifyNumberActivity;

import java.io.Serializable;

/**
 * 创建订单需要提交的数据
 *
 * Created by speedingsnail on 15/12/3.
 */
public  class  CreateOrderDemo implements Serializable {

    public String isSdOrder = "1";
    public String CRMId = "";//CRM工号
    public String goodsCode = ""; //(必填) 商品code
    public String productCode = ""; //(必填) 产品code  //以前的goodsId 产品id
    public String fixedline = "";//(必填) 固话安装 new新装 add已有
    public String fixedlineNumber = ""; //固话号码
    public String broadband = "";//(必填) 宽带安装 new新装 add已有
    public String broadbandNumber = "";//宽带号码
    public String phone = ""; //(必填) 手机安装 new新装 add已有
    public String phoneNumber = "";//手机号码

    public String contractmode = "";//合约机入网方式 new新装 add加装已有号码
    public String contractmodeAddType = ""; //合约机入网加装的已有号码类型 fixedline 固话 broadband 宽带 phone 手机
    public String contractmodeAddNumber = ""; //合约机入网加装的已有号码


    public String choosedNumber = ""; //新选择的手机号码
    public String choosedNumberTeleUse = "";//新选择的手机号码之号码仓库
    public String contractPackageId = "";//选择的套餐Id
    public String contractId = ""; //模式（合约）
    public String salemodeId = "2";//售卖方式  //20150610  终端铺货一次结 2
    public String number = "";//联系电话
    public String address = ""; //安装地址
    public String receiverName = "";//收件人姓名
    public String receiverNumber = "";//收件人电话
    public String receiverAddress = "";//收件人地址
    public String remark = "";//备注信息
    public String instantInstall = ""; //1表示选择即销即装 0表示不选择
    public String installerNumber = ""; //安装人员电话
    public String count = ""; //购买数量，仅当产品为批量售卖/终端销售模板时有效
    public String sn = "";//串码，仅当产品为终端销售模板且需要串码时有效
    public String guarantee = "";//担保方式
    public String guaranteeNumber = ""; //担保号码
    public String custName = ""; //客户姓名
    public String custId = ""; //客户身份证号
    public String custAddress = "";   //客户身份证号
    public String isNeedInvoice = "";  // 是否需要发票  1 需要 ，0 不需要
    public String invoiceType = "";  // 抬头  1个人 ,2 企业
    public String invoiceUnitName = ""; // 企业名字
    public String readIDCardType = "";//身份证识别方式  1 是照片 ，2是成都因纳伟盛 ，3是山东信通
    public String image1Id = "";//(必填) 身份证正面
    public String image2Id = "";//(必填) 身份证反面
    public String image3Id = "";//(必填) 预受理订单
    public String image4Id = "";//(必填) 手持证件照
    public String contractAccount = ""; //合帐 no,yes
    public String contractAccountNumber = ""; //存合帐号码到订单中
    public String selectItvId = "";//选择ITV类型 0代表无需ITV，1代表新装，2代表修改资费
    public String selectItvComboId = "";//ITV套餐ID
    public String payType = "";//支付方式  onLine代表在线支付 ，offLine代表线下支付
    public String templateId = "";

    //后面下单需要覆盖的值，取值为NumberVerifyResult.bestContact
    public String fixedlineContact = ""; //固话
    public  String broadbandContact = "";//宽带
    public  String phoneContact = "";//手机
    public  String contractmodeContact = "";//合约
    public  String guaranteeContact = "";//担保

    //后面下单需要覆盖的值，取值为NumberVerifyResult.address
    public String fixedlineAddress = "";
    public String broadbandAddress = "";
    public String phoneAddress = "";
    public String contractmodeAddress = "";
    public String guaranteeAddress = "";



}
