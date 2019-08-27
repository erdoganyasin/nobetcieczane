package com.example.nobetcieczane;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    String tokenText;

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsBridge(),"Android");
        this.getToken();
    }

    public void  getToken()
    {
        webView.setWebViewClient(new WebViewClient()
                                 {
                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         super.onPageFinished(view,  url);
                                         view.loadUrl("javascript:window.Android.htmlContentForToken("+
                                        "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');" );
                                     }
                                 }
                                



        );
        webView.loadUrl("http://apps.istanbulsaglik.gov.tr/Eczane");

    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                tokenText = (String) msg.obj;
            }
        }
    };




    class JsBridge extends MainActivity{

        @JavascriptInterface
        public void htmlContentForToken(String str)
        {

           String token[] = str.split("token");

           if (token.length>1)
           {

            String token2[] = token[1].split(Pattern.quote("}"));
               tokenText = token2[0].replaceAll("","").replaceAll(":","").replaceAll("\"","");
            Message message = new Message();
            message.what=1;
            message.obj=tokenText;
            handler.sendMessage(message);
           }
        }
    }

}
