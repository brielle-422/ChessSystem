package com.chesssystem.ui;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.chesssystem.BaseActivity;
import com.chesssystem.alipay.H5PayDemoActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
/**
 * 后台H5页面
 * @author lyg
 * @time 2016-7-28上午11:08:04
 */
public class H5MerchantsJoinActivity extends BaseActivity {
	private WebView mWebView;
	private String url="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
		
		LinearLayout layout = new LinearLayout(getApplicationContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout, params);
		url=getIntent().getStringExtra("url");
		mWebView = new WebView(getApplicationContext());
		params.weight = 1;
		mWebView.setVisibility(View.VISIBLE);
		layout.addView(mWebView, params);

		WebSettings settings = mWebView.getSettings();
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setSupportMultipleWindows(true);
		settings.setJavaScriptEnabled(true);
		settings.setSavePassword(false);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
		settings.setAllowFileAccess(false);
		settings.setTextSize(WebSettings.TextSize.NORMAL);
		mWebView.setVerticalScrollbarOverlay(true);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setWebViewClient(new WebViewClient()

        {   
                                @Override
                                public void onPageFinished(WebView view, String url) 
                                {
                                	if (progressDialog != null) {
        								progressDialog.dismiss();
        							}
//                                    webView.setVisibility(View.VISIBLE);
                                        super.onPageFinished(view, url);
                                }
        });
		mWebView.loadUrl(url);
	}
	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {
			if (!(url.startsWith("http") || url.startsWith("https"))) {
				return true;
			}

			final PayTask task = new PayTask(H5MerchantsJoinActivity.this);
			final String ex = task.fetchOrderInfoFromH5PayUrl(url);
			if (!TextUtils.isEmpty(ex)) {
				System.out.println("paytask:::::" + url);
				new Thread(new Runnable() {
					public void run() {
						System.out.println("payTask:::" + ex);
						final H5PayResultModel result = task.h5Pay(ex, true);
						if (!TextUtils.isEmpty(result.getReturnUrl())) {
							H5MerchantsJoinActivity.this.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									view.loadUrl(result.getReturnUrl());
								}
							});
						}
					}
				}).start();
			} else {
				view.loadUrl(url);
			}
			return true;
		}
	}
	
}
