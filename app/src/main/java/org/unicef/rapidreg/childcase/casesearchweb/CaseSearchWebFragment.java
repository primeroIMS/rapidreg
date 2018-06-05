package org.unicef.rapidreg.childcase.casesearchweb;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseView;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;

import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CaseSearchWebFragment extends MvpFragment<BaseView, CaseSearchWebPresenter> implements BaseView {
    private static final int HAVE_RESULT_LIST = 0;
    private static final int HAVE_NO_RESULT = 1;

    @Inject
    CaseSearchWebPresenter caseSearchWebPresenter;

    @BindView(R.id.web_view)
    protected WebView webView;

    @BindView(R.id.view_switcher)
    protected ViewSwitcher viewSwitcher;

    @BindView(R.id.search_result)
    protected ViewSwitcher searchResultSwitcher;

    @BindView(R.id.id)
    protected org.unicef.rapidreg.widgets.ClearableEditText idField;

    @Override
    public CaseSearchWebPresenter createPresenter() {
        return caseSearchWebPresenter;
    }

    public CaseSearchWebFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_case_search_web, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.done)
    public void onDoneClicked() {
        String selectedId = idField.getText().toString();

        if (!selectedId.isEmpty()) {
            caseSearchWebPresenter.hasResults(selectedId, new WebSearchCallback() {
                @Override
                public void onSuccess(boolean hasResults) {
                    viewSwitcher.showPrevious();

                    int resultIndex = hasResults ? HAVE_RESULT_LIST : HAVE_NO_RESULT;
                    searchResultSwitcher.setDisplayedChild(resultIndex);

                    if (resultIndex == HAVE_RESULT_LIST) {
                        openWebClient(selectedId);
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.remote_id_required, Toast.LENGTH_LONG).show();
        }
    }

    public FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getContext()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    public void openWebClient(String id) {
//        CookieManager.getInstance().removeAllCookie();

        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectCSS();
            }
        });
        webView.getSettings().setDomStorageEnabled(true);
        webView.setClickable(true);
        webView.loadUrl(PrimeroAppConfiguration.getApiBaseUrl() + "/cases?utf8=✓&query=" + id + "&id_search=true&mobile=true");

        Toast.makeText(getActivity(), R.string.remote_record_not_found, Toast.LENGTH_LONG).show();
    }

    private void injectCSS() {
        try {
            InputStream inputStream = getActivity().getAssets().open("mobile_overrides.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "document.body.style.opacity = 0;" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style);" +
                    "document.body.style.opacity = 1;" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void promoteSyncFormsError() {

    }

    public interface WebSearchCallback {
        void onSuccess(boolean hasResult);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (webView.isEnabled()) {
            webView.destroy();
        }
    }
}
