package com.upsc.exam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int FILE_CHOOSER_REQUEST_CODE = 1234;
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    private static final int FILE_CHOOSER_RESULT_CODE = 1;

    WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private String[] fileChooserParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         webView = new WebView(MainActivity.this);
        setContentView(webView);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.getSettings().setUserAgentString(USER_AGENT);
        webView.getSettings().setUserAgentString(String.valueOf(R.string.app_name));
        // Set a WebViewClient to handle redirects and URL loading
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // Handle the download
                downloadFile(url, mimeType);
            }
        });
        webView.setWebViewClient(new WebViewClient());

        // Set WebChromeClient to handle file uploads
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (MainActivity.this.filePathCallback != null) {
                    MainActivity.this.filePathCallback.onReceiveValue(null);
                }
                MainActivity.this.filePathCallback = filePathCallback;

                openImageChooser();
                return true;
            }
        });


        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.addJavascriptInterface(new WebAppInterface2(this), "Android2");
        //webView.loadUrl("file:///android_asset/test.html");
        webView.loadUrl("https://upscexamnotes.com");



    }
    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
    }
    private void openFileChooser() {
        // Add code to open the file chooser (camera or gallery) based on your requirements.
        // For simplicity, let's assume you have a button in your HTML that triggers file selection.
        webView.loadUrl("javascript:document.getElementById('main-file').click();");
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (filePathCallback == null) {
                return;
            }

            Uri[] result = null;
            if (resultCode == RESULT_OK && data != null) {
                result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            }

            filePathCallback.onReceiveValue(result);
            filePathCallback = null;
        }
    }
    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context. */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page. */
        @JavascriptInterface
        public void printAndroid() {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    printWebView();

                }
            });
        }

    }
    public class WebAppInterface2 {
        Context mContext;

        /** Instantiate the interface and set the context. */
        WebAppInterface2(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page. */
        @JavascriptInterface
        public void AndroidChoose() {
        //    Toast.makeText(mContext, "Add Image clicked...", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
        }

    }

    private void printWebView() {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Set a print job name
        String jobName = getString(R.string.app_name) + " Document";

        // Create a PrintDocumentAdapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Start a print job
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }


    private void downloadFile(String url, String mimeType) {
        // Implement download logic here
        // You may use DownloadManager or any other method to handle the download

            // Get the PrintManager service
            PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

            // Name the print job
            String jobName = getString(R.string.app_name) + " Document";

            // Create a PrintDocumentAdapter
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

            // Get the print attributes
            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();
            // Start a print job
            printManager.print(jobName, printAdapter, attributes);


        // For example, using DownloadManager
        DownloadManager.Request request = new DownloadManager.Request(android.net.Uri.parse(url));
        request.setTitle("Download File");
        request.setDescription("Downloading PDF");
        request.setMimeType(mimeType);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Save the file to the Downloads directory
        String fileName = "downloaded_file.pdf";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Get the download service and enqueue the request
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }


    public void onBackPressed(){
        if (webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}