package id.co.indomobil.CustWorkorderInfoEksternal;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    String url = "file:///android_asset/lihat.html";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI(getWindow());   //this hides NavigationBar before showing the activity
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.hide();
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        /*webview = (WebView)findViewById(R.id.webView);
        webview.loadUrl(url);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new SSLTolerentWebViewClient());
        //webView.loadUrl("http://livedms.indomobil.co.id/dms-aftersales/GeneralRepair/CustWorkInfo.aspx");
        //webView.loadUrl("http://testingdms.indomobil.co.id/dms-aftersales/GeneralRepair/CustWorkInfo.aspx");
        while (!isServerReachable(mContext,getString(R.string.Internal_CustWorkInfo_Page)) && !isServerReachable(mContext,getString(R.string.External_CustWorkInfo_Page))) {
            if (isServerReachable(mContext, getString(R.string.Internal_CustWorkInfo_Page))) {
                webView.loadUrl(getString(R.string.Internal_CustWorkInfo_Page));
            } else if (isServerReachable(mContext, getString(R.string.External_CustWorkInfo_Page))) {
                webView.loadUrl(getString(R.string.External_CustWorkInfo_Page));
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //webView.loadUrl("http://118.97.70.76:128/dms-aftersales/GeneralRepair/CustWorkInfo.aspx);*/

        new LoadUrl() {
            @Override
            public void onPostExecute(String result) {
                super.onPostExecute(result);
                if (!result.equals("")) {
                    webView = findViewById(R.id.webView);
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setDomStorageEnabled(true);

                    // Tiga baris di bawah ini agar laman yang dimuat dapat
                    // melakukan zoom.
                    webView.getSettings().setSupportZoom(true);
                    webView.getSettings().setBuiltInZoomControls(true);
                    webView.getSettings().setDisplayZoomControls(false);
                    // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.setWebViewClient(new SSLTolerentWebViewClient());
                    webView.loadUrl(result);
                }
            }
        }.execute();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI(getWindow());
    }

    public static void hideSystemUI(Window window) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    // SSL Error Tolerant Web View Client
    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }
       /* public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }
            message += " Do you want to continue anyway?";

            builder.setTitle("SSL Certificate Error");
            builder.setMessage(message);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public boolean isServerReachable(Context context, String serveraddress) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                //URL url = new URL("http://192.168.1.13");   // Change to "http://google.com" for www  test.
                trustAllHosts();
                URL url = new URL(serveraddress);
                HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
                urlc.setConnectTimeout(5 * 1000);          // 5 s.
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.wtf("Connection", "Success !");
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public class LoadUrl extends AsyncTask<String, String, String> {
        NotificationCompat.Builder builder;
        String CHANNEL_ID = "Channel_01";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            while (result.equals("")) {
                try {
                    if (isServerReachable(mContext, getString(R.string.External_CustWorkInfo_Page))) {
                        //webView.loadUrl(getString(R.string.Internal_CustWorkInfo_Page));
                        result = getString(R.string.External_CustWorkInfo_Page);
                        return result;
                    } else if (isServerReachable(mContext, getString(R.string.Internal_CustWorkInfo_Page))) {
                        //webView.loadUrl(getString(R.string.External_CustWorkInfo_Page));
                        result = getString(R.string.Internal_CustWorkInfo_Page);
                        return result;
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(mContext, "DMS server unreachable,retrying...", Toast.LENGTH_LONG).show();
                            }
                        });
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

   /*     @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
        }*/
    }
}
