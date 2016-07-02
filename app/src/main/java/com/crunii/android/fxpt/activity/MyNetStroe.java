package com.crunii.android.fxpt.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.util.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by speedingsnail on 16/2/1.
 */
public class MyNetStroe extends Fragment {

//    private MyWebView wv_main;
//    private ProgressBar progressBar;
    private TextView tv_message;
    private ScrollView srl_message_bg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_net_stroe, container, false);
//        wv_main = (MyWebView) view.findViewById(R.id.wv_main);
//        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//        wv_main.getSettings().setJavaScriptEnabled(true);
//        wv_main.setWebViewClient(new MyWebViewClient());
//        wv_main.setWebChromeClient(new MyWebChromeClient());

        tv_message = (TextView) view.findViewById(R.id.tv_message);
        srl_message_bg = (ScrollView) view.findViewById(R.id.srl_message_bg);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    public void refresh() {
        final Map<String, String> map = new HashMap<>();
        HttpTool.sendPost(getActivity(), Constant.CTX_PATH + "myNetStroe", map, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {

                boolean isFlag = (boolean) record.get("isFlag");
                if (isFlag) {
                    String url = (String) record.get("url");
                    tv_message.setVisibility(View.GONE);
                    srl_message_bg.setVisibility(View.GONE);
//                    wv_main.setVisibility(View.VISIBLE);
//                    wv_main.loadUrl(url);
                } else {
                    String desc = "您尚未开通“我的网店”\n" +
                            "请在电脑登录分销平台\n" +
                            "（cq.189.cn/fx）\n" +
                            "完成“我的网店”开通流程后再使用";//(String) record.get("desc");
                    tv_message.setText(desc);
                    tv_message.setVisibility(View.VISIBLE);
                    srl_message_bg.setVisibility(View.VISIBLE);
//                    wv_main.setVisibility(View.GONE);
                }
            }
        });
    }




//    protected class MyWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (url.startsWith("crun://")) {
//                Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            return false;
//        }
//    }

//    protected class MyWebChromeClient extends WebChromeClient {
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//            if (newProgress == 100) {
//                progressBar.setVisibility(View.INVISIBLE);
//            } else {
//                progressBar.setVisibility(View.VISIBLE);
//            }
//            progressBar.setProgress(newProgress);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

        //切换用户刷新登陆名
        TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
        title.setText(CRApplication.getName(getActivity()));
    }

}
