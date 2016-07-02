package com.crunii.android.fxpt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class MyWebView extends WebView {
	/**
	 * 监听滑动到顶部事件
	 */
	private OnScrollToTopListener onScrollToTop;  

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 监听滑动到顶部事件
	 */
    @Override  
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,  
            boolean clampedY) {  
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);  
        if(scrollY == 0 && null != onScrollToTop){  
            onScrollToTop.onScrollTopListener(clampedY);  
        }  
    }  
      
    public void setOnScrollToTopLintener(OnScrollToTopListener listener){  
        onScrollToTop = listener;  
    }  
  
    public interface OnScrollToTopListener{  
        public void onScrollTopListener(boolean isTop);  
    }  
}
