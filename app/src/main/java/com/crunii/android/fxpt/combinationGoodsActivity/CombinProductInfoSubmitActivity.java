package com.crunii.android.fxpt.combinationGoodsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.activity.OrderResultActivity;
import com.crunii.android.fxpt.activity.ProductDetailBatch;
import com.crunii.android.fxpt.activity.ProductDetailTerminal;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.business.Item;
import com.crunii.android.fxpt.util.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinProductInfoSubmitActivity extends BaseActivity {
    View customerinfoView, logisticsView, installerView, citizenView, addressView;
    EditText contactNumber, address, installerNumber, receiverName, receiverNumber, receiverAddress, remark, name, citizenId, citizenAddress;
    String ocr_cardnum, ocr_name, ocr_address; //实名制身份验证信息
    String image1Id, image2Id, image3Id, image4Id, readIDCardType;
    CheckBox instantInstall;
    boolean isOfflinePay;

    //支付方式选择   1代表在线支付 ，2代表线下支付
    String payType = "onLine";
    JSONArray addressList;
    List<Item> itemList = new ArrayList<Item>();
    CreateOrderDemo createOrderDemo;
    ProductDetailResultDemo productDetailResultDemo;
    NumberVerifyResult numberVerifyResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combinproductinfosubmit);

        Bundle bundle = getIntent().getExtras();
        createOrderDemo = (CreateOrderDemo) bundle.getSerializable("createOrderDemo");
        productDetailResultDemo = (ProductDetailResultDemo) bundle.getSerializable("productDetailResultDemo");
        numberVerifyResult = (NumberVerifyResult) bundle.getSerializable("numberVerifyResult");

        ocr_cardnum = getIntent().getExtras().getString("ocr_cardnum");
        ocr_name = getIntent().getExtras().getString("ocr_name");
        ocr_address = getIntent().getExtras().getString("ocr_address");
        image1Id = getIntent().getExtras().getString("image1Id");
        image2Id = getIntent().getExtras().getString("image2Id");
        image3Id = getIntent().getExtras().getString("image3Id");
        image4Id = getIntent().getExtras().getString("image4Id");
        readIDCardType = getIntent().getExtras().getString("readIDCardType");

        customerinfoView = findViewById(R.id.customerinfo_view);
        logisticsView = findViewById(R.id.logistics_view);
        installerView = findViewById(R.id.installerView);
        citizenView = findViewById(R.id.citizenView);
        addressView = findViewById(R.id.addressView);

        contactNumber = (EditText) findViewById(R.id.number);


        address = (EditText) findViewById(R.id.address);
        installerNumber = (EditText) findViewById(R.id.installerNumber);
        //20141027修改:安装人员电话不再默认设为登陆手机号码
        //installerNumber.setText(CRApplication.getPhone(this));
        receiverName = (EditText) findViewById(R.id.receiverName);
        receiverNumber = (EditText) findViewById(R.id.receiverNumber);
        receiverAddress = (EditText) findViewById(R.id.receiverAddress);
        remark = (EditText) findViewById(R.id.remark);
        instantInstall = (CheckBox) findViewById(R.id.instantInstall);
        name = (EditText) findViewById(R.id.name);
        citizenId = (EditText) findViewById(R.id.citizenId);
        citizenAddress = (EditText) findViewById(R.id.citizenAddress);

        //是否需要展示物流信息
        if (!getIntent().getExtras().getBoolean("requireLogistics")) {
            logisticsView.setVisibility(View.GONE);
        }

        String fixedlineContact =  createOrderDemo.fixedlineContact;
        String broadbandContact = createOrderDemo.broadbandContact;
        String phoneContact =  createOrderDemo.phoneContact;
        String contractmodeContact =  createOrderDemo.contractmodeContact;
        String guaranteeContact =  createOrderDemo.guaranteeContact;
        if (createOrderDemo.fixedline.equals("add")) {
            contactNumber.setText(fixedlineContact);
        }
        if (createOrderDemo.broadband.equals("add")) { //宽带联系电话覆盖固话联系电话
            contactNumber.setText(broadbandContact);
        }
        if (createOrderDemo.phone.equals("add")) { //手机联系电话覆盖宽带联系电话
            contactNumber.setText(phoneContact);
        }
        if (createOrderDemo.contractmode.equals("add")) { //合约机联系电话覆盖手机联系电话
            contactNumber.setText(contractmodeContact);
        }
        if (!createOrderDemo.guaranteeNumber.equals("")) { //担保联系电话覆盖之前所有的联系电话
            contactNumber.setText(guaranteeContact);
        }
        //如果在上传身份证时填写了联系电话，则此联系电话覆盖之前的联系电话
        if (getIntent().getExtras().getString("contactNumber") != null) {
            if (!getIntent().getExtras().getString("contactNumber").equals("")) {
                contactNumber.setText(getIntent().getExtras().getString("contactNumber"));
            }
        }

        String fixedlineAddress = createOrderDemo.fixedlineAddress;
        String broadbandAddress = createOrderDemo.broadbandAddress;
        String phoneAddress = createOrderDemo.phoneAddress;
        String contractmodeAddress = createOrderDemo.contractmodeAddress;
        if (createOrderDemo.fixedline.equals("add")) {
            address.setText(fixedlineAddress);
        }
        if (createOrderDemo.broadband.equals("add")) { //宽带安装地址覆盖固话安装地址
            address.setText(broadbandAddress);
        }
        if (createOrderDemo.phone.equals("add")) { //手机安装地址覆盖宽带安装地址
            address.setText(phoneAddress);
        }
        if (createOrderDemo.contractmode.equals("add")) { //合约机安装地址覆盖手机安装地址
            address.setText(contractmodeAddress);
        }
        if (!contractmodeAddress.equals("")) { //担保安装地址覆盖之前所有的安装地址
            address.setText(contractmodeAddress);
        }

        //是否显示安装地址
        if (needAddressView()) {
            addressView.setVisibility(View.VISIBLE);
        } else {
            addressView.setVisibility(View.GONE);
        }

        //是否需要显示姓名和身份证号去CRM占号？
        if (needCitizenView()) {
            citizenView.setVisibility(View.VISIBLE);
        } else {
            citizenView.setVisibility(View.GONE);
        }

        //身份证信息
        if (hasAdd(createOrderDemo)) {
            //如果是加装，默认情况下展示脱敏信息
            name.setText(numberVerifyResult.displayName);
            citizenId.setText(numberVerifyResult.displayCitizenId);
            citizenAddress.setText(numberVerifyResult.displayCitizenAddress);

            if (productDetailResultDemo.uploadCitizenPhoto) {
                //如果需要上传照片，则使用扫描身份证信息
                createOrderDemo.custName = ocr_name;
                createOrderDemo.custId = ocr_cardnum;
                createOrderDemo.custAddress = ocr_address;

                name.setText(createOrderDemo.custName);
                citizenId.setText(createOrderDemo.custId);
                citizenAddress.setText(createOrderDemo.custAddress);

                //在需要上传照片的前提下，身份证信息是否允许编辑，根据后台配置决定
                name.setEnabled(getIntent().getExtras().getBoolean("modifyOcrName"));
                citizenId.setEnabled(getIntent().getExtras().getBoolean("modifyOcrCarNum"));
                citizenAddress.setEnabled(getIntent().getExtras().getBoolean("modifyOcrAddress"));
            } else {
                //加装如果不需要上传照片，则不允许编辑身份证信息
                name.setEnabled(false);
                citizenId.setEnabled(false);
                citizenAddress.setEnabled(false);
            }

        } else {
            //如果是新装，则使用扫描身份证信息
            createOrderDemo.custName = ocr_name;
            createOrderDemo.custId = ocr_cardnum;
            createOrderDemo.custAddress = ocr_address;

            name.setText(createOrderDemo.custName);
            citizenId.setText(createOrderDemo.custId);
            citizenAddress.setText(createOrderDemo.custAddress);

            if (productDetailResultDemo.uploadCitizenPhoto) {
                //在需要上传照片的前提下，身份证信息是否允许编辑，根据后台配置决定
                name.setEnabled(productDetailResultDemo.modifyOcrName);
                citizenId.setEnabled(productDetailResultDemo.modifyOcrCarNum);
                citizenAddress.setEnabled(productDetailResultDemo.modifyOcrAddress);
            } else {
                //加装如果不需要上传照片，则允许编辑身份证信息
                name.setEnabled(true);
                citizenId.setEnabled(true);
                citizenAddress.setEnabled(true);
            }
        }

        //如果允许用户输入身份证信息，则在输入完毕之后立即验证身份证信息
        if (citizenId.isEnabled()) {
            citizenId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {//失去焦点时校验
                        verifyUserInputIDCard(false);
                    }
                }
            });
        }

        //20150311 需要身份证上传的商品，信息填写界面不展示客户信息，存在安装的客户信息就改成安装信息并要求填写安装地址
        if (getIntent().getExtras().getBoolean("uploadCitizenPhoto")) {
            findViewById(R.id.citizenView).setVisibility(View.GONE);
            findViewById(R.id.contactNumberView).setVisibility(View.VISIBLE);
            if (findViewById(R.id.addressView).getVisibility() == View.VISIBLE) {
                ((TextView) findViewById(R.id.customerinfoTitleTv)).setText("安装信息");
            } else {
                findViewById(R.id.customerinfoTitleView).setVisibility(View.GONE);
            }
        }

        //收货方式默认为代客收货
        ((RadioButton) findViewById(R.id.agent_receive)).setChecked(true);
        updateAddressList();

        //发票信息默认为不需发票
        ((RadioButton) findViewById(R.id.no_receipt)).setChecked(true);
        ((LinearLayout) findViewById(R.id.ll_receipt_head)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.ll_companyName)).setVisibility(View.GONE);

    }

    public void doNoReceipt(View view) {
        ((LinearLayout) findViewById(R.id.ll_receipt_head)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.ll_companyName)).setVisibility(View.GONE);
    }

    public void doNeedReceipt(View view) {
        ((LinearLayout) findViewById(R.id.ll_receipt_head)).setVisibility(View.VISIBLE);
        ((RadioButton) findViewById(R.id.rb_person)).setChecked(true);
    }

    public void doPerson(View view) {
        ((LinearLayout) findViewById(R.id.ll_companyName)).setVisibility(View.GONE);
    }

    public void doCompany(View view) {
        ((LinearLayout) findViewById(R.id.ll_companyName)).setVisibility(View.VISIBLE);
    }

    private void verifyUserInputIDCard(final boolean submit) {

        new BaseTask<String, String, Boolean>(this, "正在校验身份证...") {

            @Override
            protected Boolean doInBack(String... params) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().IDCardValidate(getApplicationContext(),
                        name.getText().toString(),
                        citizenId.getText().toString());
            }

            @Override
            protected void onSuccess(Boolean result) {

                //身份证实名制校验
                if (!result) {
                    //System.out.print("isIDCardValidate="+isIDCardValidate);
                    Toast.makeText(CombinProductInfoSubmitActivity.this, "身份证校验失败,请正确填写姓名和身份证号码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    createOrderDemo.custName = name.getText().toString();
                    createOrderDemo.custId  = citizenId.getText().toString();

                    if (submit) {
                        submit();
                    }
                }
            }

        }.execute("");
    }

    private boolean needCitizenView() {
        boolean isNeed = false;

        //没有选号信息则不需要填写身份证
        if (createOrderDemo.choosedNumber.length() == 0) {
            return false;
        }

        //选号数据库如果属于CRM则需要身份证占号，否则不需要
        String databaseId = productDetailResultDemo.databaseId;
        if (databaseId.equals("-1") ||
                databaseId.equals("-2") ||
                databaseId.equals("-3") ||
                databaseId.equals("-4") ||
                databaseId.equals("-5") ||
                databaseId.equals("-6") ||
                databaseId.equals("-7")) {
            return true;
        }

        return isNeed;
    }

    private boolean needAddressView() {
        boolean isNeed = false;

        if (!createOrderDemo.fixedline.equals("") ||
                !createOrderDemo.broadband.equals("") ||
                !createOrderDemo.guaranteeNumber.equals("")) {
            isNeed = true;
        }

        return isNeed;
    }

    //是否加装？
    public static boolean hasAdd(CreateOrderDemo createOrderDemo) {
        if (createOrderDemo.fixedline.contains(CombinConstant.Fixedline.ADD) ||
                createOrderDemo.broadband.contains(CombinConstant.Broadband.ADD) ||
                createOrderDemo.phone.contains(CombinConstant.Phone.ADD) ||
                createOrderDemo.contractmode.contains(CombinConstant.Contractmode.ADD) ||
                createOrderDemo.contractAccount.contains(CombinConstant.ContractAccountType.YES) ||
                createOrderDemo.guarantee.contains("fixedline") ||
                createOrderDemo.guarantee.contains("broadband")) {
            return true;
        } else {
            return false;
        }


    }

    /*
    private boolean verifyCitizenInfo() {
		if (!CitizenUtil.verifyName(name.getText().toString())) {
			Toast.makeText(this, "客户姓名必须是中文", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!CitizenUtil.verifyCitizenId(citizenId.getText().toString())) {
			Toast.makeText(this, "身份证号码无效", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	*/

    public void doCustomerReceive(View v) {
        findViewById(R.id.address_btn_view).setVisibility(View.GONE);

        receiverName.setText("");
        receiverNumber.setText("");
        receiverAddress.setText("");
    }

    public void doAgentReceive(View v) {
        findViewById(R.id.address_btn_view).setVisibility(View.VISIBLE);

        for (int i = 0; i < addressList.length(); i++) {
            JSONObject json = addressList.optJSONObject(i);
            if (json.optString("isDefault").equals("1")) {
                receiverName.setText(json.optString("name"));
                receiverNumber.setText(json.optString("phone"));
                receiverAddress.setText(json.optString("addr"));
            }
        }
    }

    public void doShowAddressList(View v) {

        itemList.clear();
        for (int i = 0; i < addressList.length(); i++) {
            itemList.add(new Item(addressList.optJSONObject(i).optString("id"), addressList.optJSONObject(i).optString("addr")));
        }

        ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, itemList);

        new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int position) {
                Item item = itemList.get(position);
                String id = item.getId();
                JSONObject json = new JSONObject();
                for (int i = 0; i < addressList.length(); i++) {
                    if (addressList.optJSONObject(i).optString("id").equals(id)) {
                        json = addressList.optJSONObject(i);
                        break;
                    }
                }
                receiverName.setText(json.optString("name"));
                receiverNumber.setText(json.optString("phone"));
                receiverAddress.setText(json.optString("addr"));

                dlg.dismiss();
            }
        }).show();
    }

    public void doAddAddress(View v) {

        new BaseTask<String, String, Boolean>(this, "请稍后...") {

            @Override
            protected Boolean doInBack(String... params) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().addressAdd(getApplicationContext(),
                        receiverName.getText().toString(),
                        receiverAddress.getText().toString(),
                        receiverNumber.getText().toString(),
                        false);
            }

            @Override
            protected void onSuccess(Boolean result) {
                Dialog alertDialog = new AlertDialog.Builder(CombinProductInfoSubmitActivity.this).setTitle("提示").setMessage("添加成功。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateAddressList();
                            }
                        })
                        .setCancelable(false)
                        .create();
                alertDialog.show();
            }

        }.execute("");
    }

    private void updateAddressList() {

        new BaseTask<String, String, JSONObject>(this, "请稍后...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().personalinfo(CombinProductInfoSubmitActivity.this);
            }

            @Override
            protected void onSuccess(JSONObject result) {
                addressList = result.optJSONArray("address");

                //如果没有手动输入地址，则自动填入默认地址
                if (receiverName.getText().toString().equals("")) {
                    doAgentReceive(null);
                }
            }

        }.execute("");
    }

    public void doBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("是否终止录入?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }

    public void doSubmit(View v) {

        if (customerinfoView.getVisibility() == View.VISIBLE) {
            if (contactNumber.getText().toString().length() != 11) {
                Toast.makeText(this, "联系电话必须是11位", Toast.LENGTH_SHORT).show();
                return;
            }

            if (addressView.getVisibility() == View.VISIBLE) {
                if (address.getText().toString().equals("")) {
                    Toast.makeText(this, "请输入安装地址", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (logisticsView.getVisibility() == View.VISIBLE) {

            if (receiverName.getText().toString().equals("")) {
                Toast.makeText(this, "请输入收件人", Toast.LENGTH_SHORT).show();
                return;
            } else if (receiverNumber.getText().toString().length() != 11) {
                Toast.makeText(this, "收件电话必须是11位", Toast.LENGTH_SHORT).show();
                return;
            } else if (receiverAddress.getText().toString().equals("")) {
                Toast.makeText(this, "请输入收件地址", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (installerView.getVisibility() == View.VISIBLE) {
            if (installerNumber.getText().toString().length() != 11) {
                Toast.makeText(this, "安装人员电话必须是11位", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //发票
        if (((LinearLayout) findViewById(R.id.ll_companyName)).getVisibility() == View.VISIBLE) {
            if (((EditText) findViewById(R.id.et_companyName)).getText().toString().equals("")) {
                Toast.makeText(this, "请输入单位名称", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //不需要发票
        if (((RadioButton) findViewById(R.id.no_receipt)).isChecked()) {
            createOrderDemo.isNeedInvoice = "0";
        } else {
            createOrderDemo.isNeedInvoice = "1";
            //个人发票
            if (((RadioButton) findViewById(R.id.rb_person)).isChecked()) {
                createOrderDemo.invoiceType = "1";
            } else {
                createOrderDemo.invoiceType = "2";
                createOrderDemo.invoiceUnitName = ((EditText) findViewById(R.id.et_companyName)).getText().toString();
            }
        }

        //TODO
        if ((citizenView.getVisibility() == View.VISIBLE) && citizenId.isEnabled()) {
            verifyUserInputIDCard(true);
        } else {
            submit();
        }

    }

    public void onInstantInstallClicked(View v) {
        CheckBox cb = (CheckBox) v;
        if (cb.isChecked()) {
            installerView.setVisibility(View.VISIBLE);
        } else {
            installerView.setVisibility(View.GONE);
        }
    }

    private void submit() {

        createOrderDemo.number = contactNumber.getText().toString();
        createOrderDemo.address = address.getText().toString();
        createOrderDemo.receiverName = receiverName.getText().toString();
        createOrderDemo.receiverNumber = receiverNumber.getText().toString();
        createOrderDemo.receiverAddress = receiverAddress.getText().toString();
        createOrderDemo.remark = remark.getText().toString();

        if (instantInstall.isChecked()) {
            createOrderDemo.instantInstall = "1";
        } else {
            createOrderDemo.instantInstall = "0";
        }
        createOrderDemo.installerNumber = installerNumber.getText().toString();
        createOrderDemo.image1Id = image1Id;
        createOrderDemo.image2Id = image2Id;
        createOrderDemo.image3Id = image3Id;
        createOrderDemo.image4Id = image4Id;
        createOrderDemo.readIDCardType = readIDCardType;
        createOrderDemo.payType = payType;

        sendPost(Constant.CTX_PATH + "createorder", createOrderDemo, new HttpPostProp() {
            @Override
            public void successJson(JSONObject result) {
                super.successJson(result);

                Intent i = new Intent(CombinProductInfoSubmitActivity.this, CombinOrderResultActivity.class);
                i.putExtra("orderId", result.optString("orderId"));
                i.putExtra("orderName", result.optString("orderName"));
                i.putExtra("needPay", result.optBoolean("needPay"));
                i.putExtra("payValue", result.optString("payValue"));
                i.putExtra("originalPrice", result.optString("originalPrice"));
                i.putExtra("buyNumber", result.optString("buyNumber"));
                i.putExtra("hasDiscount", result.optBoolean("hasDiscount"));
                i.putExtra("discountPrice", result.optString("discountPrice"));
                i.putExtra("templateId", productDetailResultDemo.templateId);
                i.putExtra("payType", payType);
                startActivity(i);
                finish();


            }
        });

    }

    //支付方式  1代表在线支付 ，2代表线下支付
    public void doOnlinePay(View view) {
        payType = "onLine";
    }

    public void doOfflinePay(View view) {
        payType = "offLine";
    }
}
