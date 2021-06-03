package com.advengers.mabo.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.R;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.databinding.ActivityWebviewBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

import static com.advengers.mabo.Interfaces.Keys.TITLE;

public class WebViewActivity extends MyActivity  implements OnPageChangeListener, OnLoadCompleteListener {
    File pdfFile;
    Integer pageNumber = 0;
    String pdfFileName;
    ActivityWebviewBinding binding;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(WebViewActivity.this, R.layout.activity_webview);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getStringExtra(TITLE).equals(getString(R.string.str_terms)))
        {
            getSupportActionBar().setTitle(getString(R.string.str_terms));
             pdfFile = new File("res/raw/termsandconditions.pdf");
             pdfFileName = "termsandconditions.pdf";
        }else
        {
            getSupportActionBar().setTitle(getString(R.string.str_privacypolicy));
             pdfFile = new File("res/raw/termsandconditions.pdf");
             pdfFileName = "privacypolicy.pdf";
        }
        /*WebSettings settings = binding.webview.getSettings();
        settings.setJavaScriptEnabled(true);
       // binding.webview.getSettings().setJavaScriptEnabled(true);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) //required for running javascript on android 4.1 or later
        {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        settings.setBuiltInZoomControls(true);
        binding.webview.setWebChromeClient(new WebChromeClient());

        Uri path = Uri.fromFile(pdfFile);
        binding.webview.loadUrl("file:///android_res/raw/termsandconditions.pdf");*/


        binding.pdfView.fromAsset(pdfFileName)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = binding.pdfView.getDocumentMeta();
        printBookmarksTree(binding.pdfView.getTableOfContents(), "-");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            LogUtils.e(String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}
