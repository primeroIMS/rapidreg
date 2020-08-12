package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.AppDataService;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.service.LookupService;
import org.unicef.rapidreg.service.SystemSettingsService;
import org.unicef.rapidreg.service.impl.LoginServiceImpl;
import org.unicef.rapidreg.utils.TextUtils;

import javax.inject.Inject;

import dagger.Lazy;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();

    private AppDataService appDataService;
    private LoginService loginService;
    private SystemSettingsService systemSettingsService;
    private LookupService lookupService;

    @Inject
    public LoginPresenter(Lazy<LoginService> loginService, Lazy<SystemSettingsService> systemSettingsServiceLazy, AppDataService appDataService, Lazy<LookupService> lookupServiceLazy) {
        this.appDataService = appDataService;
        this.loginService = loginService.get();
        this.systemSettingsService = systemSettingsServiceLazy.get();
        this.lookupService = lookupServiceLazy.get();
    }

    public String loadLastLoginUrl() {
        return loginService.loadLastLoginServerUrl();
    }

    public void doLogin(String username, String password, String url, String imei) {
        if (!validate(username, password, url)) {
            return;
        }
        PrimeroAppConfiguration.setApiBaseUrl(TextUtils.lintUrl(url));
        try {
            getView().showLoading(true);
            if (loginService.isOnline()) {
                doLoginOnline(username, password, PrimeroAppConfiguration.getApiBaseUrl(), imei);
            } else {
                doLoginOffline(username, password, PrimeroAppConfiguration.getApiBaseUrl());
            }
        } catch (Exception e) {
            getView().showLoginErrorByToast(e.getMessage());
        }
    }

    public boolean validate(String username, String password, String url) {
        boolean isValid = true;
        if (!loginService.isUsernameValid(username)) {
            getView().showUserNameInvalid();
            isValid = false;
        }
        if (!loginService.isUsernameValid(password)) {
            getView().showPasswordInvalid();
            isValid = false;
        }

        if (!loginService.isUrlValid(url)) {
            getView().showUrlInvalid();
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        loginService.destroy();
    }

    private void doLoginOnline(final String username,
                               final String password,
                               final String url,
                               String imei) {
        loginService.loginOnline(username, password, url, imei, new LoginServiceImpl.LoginCallback() {
            @Override
            public void onSuccessful(String cookie, User user) {
                if (isViewAttached()) {
                    PrimeroAppConfiguration.setCookie(cookie);
                    PrimeroAppConfiguration.setCurrentUser(user);

                    getView().configAppRuntimeEvent();

                    loadAppData(user.getRoleType());

                    PrimeroApplication.getAppRuntime().storeLastLoginServerUrl(url);
                }
            }

            @Override
            public void onFailed(Throwable error) {
                if (isViewAttached()) {
                    doLoginOffline(username, password, url);
                }
            }

            @Override
            public void onError() {
                getView().showLoading(false);
                getView().showCredentialErrorMsg();
            }
        });
    }

    private void loadAppData(User.Role roleType) {
        AppDataService.LoadCallback callback = new AppDataService.LoadCallback() {
            @Override
            public void onSuccess() {
                getView().showLoading(false);
                if (PrimeroAppConfiguration.isAuthorized()){
                    getView().showOnlineLoginSuccessful();
                } else{
                    getView().showOnlineLoginSuccessfulAgain();
                    PrimeroAppConfiguration.setAuthorized(true);
                }
                getView().navigateToLoginSucceedPage();
            }

            @Override
            public void onFailure() {
                getView().showLoading(false);
                getView().showLoginErrorByToast("Error loading application data. Please try again.");
            }
        };

        appDataService.loadAppData(callback, roleType, false);
    }

    private void doLoginOffline(String username, String password, String url) {
        loginService.loginOffline(username, password, url, new LoginService.LoginCallback() {
            @Override
            public void onSuccessful(String cookie, User user) {
                PrimeroAppConfiguration.setCurrentUser(user);
                getView().showLoading(false);
                getView().showOfflineLoginSuccessful();
                getView().configAppRuntimeEvent();
                getView().navigateToLoginSucceedPage();
                systemSettingsService.setGlobalSystemSettings();
                lookupService.setLookups();

                PrimeroAppConfiguration.setDefaultLanguage(user.getLanguage());
                PrimeroApplication.getAppRuntime().storeLastLoginServerUrl(url);
            }

            @Override
            public void onFailed(Throwable error) {
                getView().showLoading(false);
                getView().showServerConnectionErrorMsg();
            }

            @Override
            public void onError() {
                getView().showLoading(false);
                getView().showCredentialErrorMsg();
            }
        });
    }
}
