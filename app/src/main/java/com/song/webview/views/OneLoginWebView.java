package com.song.webview.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.geetest.onelogin.OneLoginHelper;
import com.geetest.onelogin.config.OneLoginThemeConfig;
import com.geetest.onelogin.listener.AbstractOneLoginListener;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.song.webview.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class OneLoginWebView extends BridgeWebView {

    private final static String CALLBACK_ID_STR = "callbackId";
    private final static String RESPONSE_ID_STR = "responseId";
    private final static String RESPONSE_DATA_STR = "responseData";
    private final static String DATA_STR = "data";
    private final static String HANDLER_NAME_STR = "handlerName";

    public OneLoginWebView(Context context) {
        super(context);
        init();
    }

    public OneLoginWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OneLoginWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void init() {
        // 基础设置
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        // 安全设置项
        settings.setAllowFileAccess(false);
        settings.setSavePassword(false);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setGeolocationEnabled(false);
        // UI 设置项
        settings.setSupportZoom(true);
        settings.setTextZoom(100);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setScrollContainer(false);
        // 缓存设置项
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        setWebChromeClient(new MyWebChromeClient());
        setWebViewClient(new MyWebViewClient());
        addJavascriptInterface(new JSBridge(this), "WebViewJavascriptBridge");
        Log.e(Constants.TAG, "init: ");
    }

    private static class JSBridge {

        private final OneLoginWebView webView;

        public JSBridge(OneLoginWebView webView) {
            this.webView = webView;
        }

        @JavascriptInterface
        public void init(String data) {
            Log.e(Constants.TAG, "init: " + data);
            OneLoginHelper.with()
                    .setLogEnable(true)
                    .init(webView.getContext(), "b41a959b5cac4dd1277183e074630945")
                    .register("", 10000);
        }

//        @JavascriptInterface
//        public String handleInit(String data) {
//            Log.e(Constants.TAG, "handleInit: ");
//            OneLoginHelper.with()
//                    .setLogEnable(true)
//                    .init(context, "b41a959b5cac4dd1277183e074630945");
//            return "";
//        }

        @JavascriptInterface
        public void callHandler(String name, String data, String s) {
            Log.e(Constants.TAG, "callHandler: " + name + ", " + data + ", " + s);
            switch (name) {
                case "preGetToken":
                    String phone = OneLoginHelper.with().getSecurityPhone();
                    Log.e(Constants.TAG, "callHandler: phone " + phone);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("number", phone);
                        jsonObject.put("operatorType", OneLoginHelper.with().getSimOperator(webView.getContext()));
                    } catch (Exception e) {
                    }

                    new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                JSONObject content = new JSONObject();
                                try {
//                                    content.put(CALLBACK_ID_STR, getCallbackId());
                                    content.put(DATA_STR, jsonObject.toString());
                                    content.put(HANDLER_NAME_STR, "");
//                                    content.put(RESPONSE_DATA_STR, getResponseData());
//                                    content.put(RESPONSE_ID_STR, getResponseId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String messageJson = content.toString();
                                messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
                                messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
                                String javascriptCommand = String.format("javascript:WebViewJavascriptBridge._handleMessageFromNative('%s')", messageJson);
//                                webView.loadUrl(javascriptCommand);
                                webView.callHandler("setPreGetTokenResult", jsonObject.toString(), null);
//                                webView.evaluateJavascript("javascript:jsBridge.registerHandler('setPreGetTokenResult', '" + jsonObject.toString() + "')", value -> Log.d(Constants.TAG, value));
//                                webView.evaluateJavascript("javascript:jsBridge.registerHandler('setPreGetTokenResult', '183****7113')", value -> Log.d(Constants.TAG, value));
//                                webView.evaluateJavascript("javascript:jsBridge.setPreGetTokenResult('183****7113')", value -> Log.d(Constants.TAG, value));
                            } else {
                                webView.loadUrl("javascript:jsBridge.registerHandler('setPreGetTokenResult', '" + phone + "')");
                            }
                        }
                    }.sendEmptyMessage(1);
                    break;
                case "requestToken":
                    OneLoginHelper.with().requestToken(new UiOneLoginListener(webView.getContext()));
                    break;
                case "handleInit":
                default:
                    break;
            }

        }

//        public void registerHandler(String name, String data, Object o) {
//            Log.e(Constants.TAG, "registerHandler: ");
//        }

//        @JavascriptInterface
//        public void preGetToken() {
//            Log.e(Constants.TAG, "preGetToken: ");
//            OneLoginHelper.with().register("", 10000);
//        }
//
//        @JavascriptInterface
//        public void requestToken() {
//            Log.e(Constants.TAG, "requestToken: ");
//            OneLoginHelper.with().requestToken(normalValue(), new UiOneLoginListener(context));
//        }

    }

    static class UiOneLoginListener extends AbstractOneLoginListener {
        private WeakReference<Context> activity;

        @Override
        public void onAuthActivityCreate(Activity activity) {
            super.onAuthActivityCreate(activity);
        }

        public UiOneLoginListener(Context activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void onResult(JSONObject jsonObject) {
            Log.i(Constants.TAG, "onResult:" + jsonObject.toString());
            if (activity.get() == null) {
                Log.i(Constants.TAG, "activity is null");
                return;
            }
//            if(jsonObject.toString().contains("Return key to exit")) {
//                activity.get().isCanceled = true;
//            }
            try {
                int statusResult = jsonObject.getInt("status");
                if (statusResult == 200 && jsonObject.has("token")) {
//                    activity.get().verify(jsonObject.getString("process_id"), jsonObject.getString("token"), jsonObject.optString("authcode"));
                } else {
                    dismiss();
                    Toast.makeText(activity.get(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                dismiss();
                e.printStackTrace();
            }
        }

        @Override
        public void onRequestTokenSecurityPhone(String phone) {
            if (activity.get() != null) {
//                activity.get().textView.setText(phone);
            }
        }

        @Override
        public boolean onRequestOtherVerify() {
            return false;
        }

        @Override
        public void onLoginButtonClick() {
            super.onLoginButtonClick();
            Log.i(Constants.TAG, "onLoginButtonClick");
        }

        private void dismiss() {
//            if (activity.get() != null && activity.get().progressDialog != null) {
//                activity.get().progressDialog.dismiss();
//            }
            OneLoginHelper.with().stopLoading();
        }
    }

    private static OneLoginThemeConfig normalValue() {
        return new OneLoginThemeConfig.Builder()
//                .setAuthNavTextViewTypeface(Typeface.defaultFromStyle(Typeface.BOLD),Typeface.defaultFromStyle(Typeface.BOLD))
//                .setNumberViewTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
//                .setSwitchViewTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
//                .setLogBtnTextViewTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
//                .setSloganViewTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                .setAuthBGImgPath("gt_one_login_bg")
                .setDialogTheme(false, 300, 500, 0, 0, false, false)
                .setStatusBar(0xFFFFFFFF, 0, true)
                //导航栏 颜色、高度、背景是否透明、导航栏是否隐藏
                .setAuthNavLayout(0xFF3973FF, 49, true, false)
                .setAuthNavTextView("一键登录", 0xFF000000, 17, true, "服务条款", 0xFF000000, 16)
                .setAuthNavTextViewTypeface(Typeface.MONOSPACE, Typeface.SERIF)
                .setAuthNavReturnImgView("gt_one_login_ic_chevron_left_black", 48, 48, true, 0)
                .setLogoImgView("gt_one_login_logo", 100, 100, false, 125, 0, 0)
                .setNumberView(0xFF3D424C, 24, 200, 0, 0)
                .setSwitchView("切换账号", 0xFF3973FF, 14, false, 249, 0, 0)
                .setLogBtnLayout("gt_one_login_btn_normal", 268, 36, 324, 0, 0)
                .setLogBtnTextView("一键登录", 0xFFFFFFFF, 15)
                .setLogBtnLoadingView("umcsdk_load_dot_white", 20, 20, 12)
                .setSloganView(0xFFA8A8A8, 10, 382, 0, 0)

                .setPrivacyCheckBox("gt_one_login_unchecked", "gt_one_login_checked", true, 30, 30, 10)
//                .setPrivacyClauseText("", "", "应用自定义服务条款一", "http://a.b.c", "应用自定义服务条款二", "http://x.y.z")
//                .setPrivacyClauseText("应用自定义服务条款一", "http://a.b.c", "", "", "应用自定义服务条款二", "http://x.y.z")
//                .setPrivacyClauseText("隐私政策", "file:///android_asset/private.html", "服务协议", "file:///android_asset/userconceal.html", "", "")
                .setPrivacyLayout(280, 0, 18, 0, true)
                .setPrivacyClauseView(0xff000000, 0xff3973FF, 12)
                .setPrivacyClauseViewTypeface(Typeface.defaultFromStyle(Typeface.BOLD), Typeface.defaultFromStyle(Typeface.NORMAL))
                .setPrivacyTextView("登录即同意", "和", "、", "并使用本机号码登录")
//                .setPrivacyClauseTextStrings("登录即同意", "应用自定义服务条款一", "http://a.b.c", "",
//                        "和", "应用自定义服务条款二", "http://x.y.z", "",
//                        "和", "应用自定义服务条款三", "http://x.y.z", "",
//                        "和", "", "", "并使用本机号码登录")
                .setPrivacyClauseTextStrings("我已阅读并同意", "产品概述", "https://docs.geetest.com/onelogin/overview/prodes/", "",
                        "和", "Android 开发文档", "https://docs.geetest.com/onelogin/deploy/android", "",
                        "和", "常见问题", "https://docs.geetest.com/onelogin/help/faq", "",
                        "和", "", "", "并使用本机号码登录")
                .setPrivacyAddFrenchQuotes(true)
//                .setBlockReturnEvent(true, true)
//                .setPrivacyLineSpacing(200.0f,1.0f)

//                .setPrivacyUnCheckedToastText("hhh")

                .build();
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.e(Constants.TAG, "onReceivedError: ");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(Constants.TAG, "onPageFinished: ");
        }
    }

    private static class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.e(Constants.TAG, "onReceivedTitle: " + title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.e(Constants.TAG, "onProgressChanged: " + newProgress);
        }
    }

}
