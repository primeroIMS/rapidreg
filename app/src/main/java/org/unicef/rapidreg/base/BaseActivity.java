package org.unicef.rapidreg.base;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.BuildConfig;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.event.CreateIncidentThruGBVCaseEvent;
import org.unicef.rapidreg.exception.StringResourceException;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.injection.component.DaggerActivityComponent;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.login.AccountManager;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.Utils;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.unicef.rapidreg.model.User.Role.GBV;

public abstract class BaseActivity extends MvpActivity<BaseView, BasePresenter> {

    public static final String TAG = BaseActivity.class.getSimpleName();

    @BindView(R.id.nav_view)
    protected NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.menu)
    protected ImageButton menu;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toggle)
    protected ImageButton showHideMenu;

    @BindView(R.id.save)
    protected TextView saveMenu;

    @BindView(R.id.back)
    protected LinearLayout backMenu;

    @BindView(R.id.search)
    protected ImageButton searchMenu;

    @BindView(R.id.web_search)
    protected ImageButton searchWeb;

    @BindView(R.id.toolbar_main_button_content)
    protected LinearLayout toolbarMainBtnContent;

    @BindView(R.id.select_all_image_button)
    protected ImageButton selectAllMenu;

    @BindView(R.id.delete_item)
    protected ImageButton deleteMenu;

    @BindView(R.id.toolbar_select_all_button_content)
    protected LinearLayout toolbarSelectAllBtnContent;

    @BindView(R.id.create_incident)
    protected ImageButton createIncidentBtn;

    @BindView(R.id.login_user_label)
    protected TextView textViewLoginUserLabel;

    @BindView(R.id.organization)
    protected TextView organizationView;

    @BindView(R.id.logout_label)
    protected ImageView textViewLogoutLabel;

    @BindView(R.id.nav_cases)
    protected TextView navCasesTV;

    @BindView(R.id.nav_tracing)
    protected TextView navTracingTV;

    @BindView(R.id.nav_incident)
    protected TextView navIncidentTV;

    @BindView(R.id.nav_sync)
    protected TextView navSyncTV;

    @BindView(R.id.nav_load_forms)
    protected TextView navLoadForms;

    @BindColor(R.color.primero_green)
    protected ColorStateList caseColor;

    @BindColor(R.color.primero_red)
    protected ColorStateList tracingColor;

    @BindColor(R.color.primero_blue)
    protected ColorStateList incidentColor;

    @BindColor(R.color.black)
    protected ColorStateList syncColor;

    @BindView(R.id.application_version)
    protected TextView applicationVersion;

    protected IntentSender intentSender = new IntentSender();

    protected DetailState detailState = DetailState.VISIBILITY;

    protected ProgressBar formSyncProgressBar;
    protected TextView formSyncTxt;
    protected AlertDialog syncFormsProgressDialog;

    private Unbinder unbinder;

    private BroadcastReceiver formDownloadProgressRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);

            if (formSyncProgressBar != null && formSyncTxt != null) {
                formSyncProgressBar.setProgress(progress);
                formSyncTxt.setText(getProgressMessageStringID(intent.getStringExtra("resource")));
            }

            if (progress == 100) {
                if (syncFormsProgressDialog != null) {
                    syncFormsProgressDialog.dismiss();
                }

                showToast(R.string.sync_pull_form_success_message);
            }
        }
    };

    @Inject
    BasePresenter basePresenter;

    @NonNull
    @Override
    public BasePresenter createPresenter() {
        return basePresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        doCloseIfNotLogin();
        getComponent().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.unbinder = ButterKnife.bind(this);

        initToolbar();
        initNavigationHeader();
        initNavigationItemMenu();
        initVersion();
        drawer.openDrawer(GravityCompat.START);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(parseLocale());
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        LocalBroadcastManager.getInstance(this).registerReceiver(formDownloadProgressRequestReceiver,
                new IntentFilter("sync_form_progress"));
    }

    private void doCloseIfNotLogin() {
        if (!AccountManager.isSignIn()) {
            AccountManager.doSignOut();
            intentSender.showLoginActivity(this);
            finish();
            return;
        }
    }

    // TODO: Get rid of this method when we upgrade the API
    private Locale parseLocale() {
        String locale = PrimeroAppConfiguration.getDefaultLanguage();
        Locale selectedLocale = new Locale(locale);

        if (locale.contains("-")) {
            String[] args = locale.split("-");

            if (args.length > 2) {
                selectedLocale =  new Locale(args[0], args[1], args[3]);
            }
            else if (args.length > 1) {
                selectedLocale =  new Locale(args[0], args[1]);
            }
            else if (args.length == 1) {
                selectedLocale = new Locale(args[0]);
            }
        }

        return selectedLocale;
    }

    @Override
    protected void onStart() {
        super.onStart();
        doCloseIfNotLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doCloseIfNotLogin();
    }

    private void initNavigationHeader() {
        User currentUser = basePresenter.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getUsername();
            textViewLoginUserLabel.setText(username);

            String organisation = currentUser.getOrganisation();
            organizationView.setText(organisation);
        }

        textViewLogoutLabel.setOnClickListener(view -> {
            if (getContext().getSyncTask() == null) {
                logOut();
            }
        });
    }

    protected void initNavigationItemMenu() {
        navCasesTV.setVisibility(GONE);
        navTracingTV.setVisibility(GONE);
        navIncidentTV.setVisibility(GONE);
        navSyncTV.setVisibility(GONE);

        User user = PrimeroAppConfiguration.getCurrentUser();
        if (user != null) {
            User.Role role = user.getRoleType();
            for (int resId : role.getResIds()) {
                // Hiding tracing request nav option if no fields
                if (resId == navTracingTV.getId() && basePresenter.hasTracingForms()) {
                    findViewById(resId).setVisibility(GONE);
                } else {
                    findViewById(resId).setVisibility(VISIBLE);
                }
            }
        }
    }

    protected void initVersion() {
        applicationVersion.setText("v" + BuildConfig.VERSION_NAME);
    }

    public ActivityComponent getComponent() {
        return DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(PrimeroApplication.get(this).getComponent())
                .build();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            processBackButton();
        }
    }

    @OnClick(R.id.nav_cases)
    public void onNavCasesTVClick() {
        drawer.closeDrawer(GravityCompat.START);
        navCaseAction();
    }

    @OnClick(R.id.nav_tracing)
    public void onNavTracingButtonClick() {
        drawer.closeDrawer(GravityCompat.START);
        navTracingAction();
    }

    @OnClick(R.id.nav_incident)
    public void onNavIncidentButtonClick() {
        drawer.closeDrawer(GravityCompat.START);
        navIncidentAction();
    }

    @OnClick(R.id.nav_sync)
    public void onNavSyncButtonClick() {
        drawer.closeDrawer(GravityCompat.START);
        navSyncAction();
    }

    @OnClick(R.id.nav_load_forms)
    public void onNavUpdateforms() {
        drawer.closeDrawer(GravityCompat.START);

        AlertDialog.Builder syncFormsProgressDialogBuilder = new AlertDialog.Builder(this);
        View inflator = LayoutInflater.from(getContext()).inflate(R.layout.load_forms, null);

        formSyncProgressBar = ButterKnife.findById(inflator, R.id.load_forms_progress_bar);
        formSyncTxt = ButterKnife.findById(inflator, R.id.load_forms_txt);

        syncFormsProgressDialogBuilder.setView(inflator);
        syncFormsProgressDialogBuilder.create();
        syncFormsProgressDialogBuilder.setCancelable(false);
        syncFormsProgressDialog = syncFormsProgressDialogBuilder.show();

        presenter.syncFormData();
    }

    @OnClick(R.id.back)
    public void onWebViewBackButtonClick() {
        navCaseAction();
    }

    public PrimeroApplication getContext() {
        return (PrimeroApplication) getApplication();
    }

    protected void logOut() {
        basePresenter.logOut();
        Utils.showMessageByToast(this, R.string.login_out_successful_text, Toast.LENGTH_SHORT);
        intentSender.showLoginActivity(this);
    }

    private void initToolbar() {
        menu.setOnClickListener(view -> {
            if (getIntent().getBooleanExtra(IntentSender.IS_OPEN_MENU, false)) {
                drawer.openDrawer(GravityCompat.START);
            } else {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        showHideMenu.setOnClickListener(view -> showHideDetail());
        saveMenu.setOnClickListener(view -> save());
        searchMenu.setOnClickListener(view -> search());
        searchWeb.setOnClickListener(view -> searchWeb());
    }

    @OnClick(R.id.create_incident)
    public void onCreateIncident() {
        CreateIncidentThruGBVCaseEvent event = new CreateIncidentThruGBVCaseEvent();
        EventBus.getDefault().postSticky(event);
    }

    protected void changeToolbarTitle(int resId) {
        toolbarTitle.setText(resId);
    }

    protected void changeToolbarIcon(Feature feature) {
        hideAllToolbarIcons();

        if (feature.isListMode()) {
            toolbarMainBtnContent.setVisibility(VISIBLE);
            toolbarSelectAllBtnContent.setVisibility(GONE);
            deleteMenu.setVisibility(VISIBLE);
            showHideMenu.setVisibility(GONE);
            searchMenu.setVisibility(VISIBLE);
            createIncidentBtn.setVisibility(GONE);

            if (this instanceof CaseActivity && ((CaseActivity) this).createPresenter().isOnline()) {
                searchWeb.setVisibility(VISIBLE);
            }
        } else if (feature.isEditMode()) {
            toolbarMainBtnContent.setVisibility(VISIBLE);
            toolbarSelectAllBtnContent.setVisibility(GONE);
            saveMenu.setVisibility(VISIBLE);
        } else if (feature.isDetailMode()) {
            toolbarMainBtnContent.setVisibility(VISIBLE);
            toolbarSelectAllBtnContent.setVisibility(GONE);
            enableCreateIncidentBtn();
        } else if (feature.isDeleteMode()) {
            toolbarMainBtnContent.setVisibility(GONE);
            toolbarSelectAllBtnContent.setVisibility(VISIBLE);
        } else if (feature.isWebMode()) {
            backMenu.setVisibility(VISIBLE);
        }
    }

    private void enableCreateIncidentBtn() {
        if (GBV == PrimeroAppConfiguration.getCurrentUser().getRoleType() &&
                this instanceof CaseActivity) {
            createIncidentBtn.setVisibility(VISIBLE);
        }
    }

    public void enableShowHideSwitcher() {
        showHideMenu.setVisibility(VISIBLE);
    }

    public void setShowHideSwitcherToShowState() {
        detailState = DetailState.VISIBILITY;
        showHideMenu.setBackgroundResource(detailState.getResId());
    }

    protected void hideAllToolbarIcons() {
        searchWeb.setVisibility(GONE);
        showHideMenu.setVisibility(GONE);
        searchMenu.setVisibility(GONE);
        saveMenu.setVisibility(GONE);
        createIncidentBtn.setVisibility(GONE);
        toolbarSelectAllBtnContent.setVisibility(GONE);
        deleteMenu.setVisibility(GONE);
        backMenu.setVisibility(GONE);
    }

    protected void setNavSelectedMenu(int resId, ColorStateList color) {
        TextView view = (TextView) findViewById(resId);
        view.setTextColor(color);
        view.setBackgroundColor(getResources().getColor(R.color.lighter_gray));
    }

    protected abstract void navSyncAction();

    protected abstract void navCaseAction();

    protected abstract void navTracingAction();

    protected abstract void navIncidentAction();

    protected abstract void processBackButton();

    protected abstract void search();

    protected abstract void searchWeb();

    protected abstract void save();

    protected abstract void showHideDetail();

    public abstract Feature getCurrentFeature();

    public enum DetailState {
        VISIBILITY(R.drawable.ic_visibility_white_24dp, true),
        INVISIBILITY(R.drawable.ic_visibility_off_white_24dp, false);

        private final int resId;
        private final boolean isDetailShow;

        DetailState(int resId, boolean isDetailShow) {
            this.resId = resId;
            this.isDetailShow = isDetailShow;
        }

        public DetailState getNextState() {
            return this == VISIBILITY ? INVISIBILITY : VISIBILITY;
        }

        public int getResId() {
            return resId;
        }

        public boolean isDetailShow() {
            return isDetailShow;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            this.unregisterReceiver(formDownloadProgressRequestReceiver);
        } catch (Exception e) {
        }
        super.onDestroy();
        this.unbinder.unbind();
    }

    protected void showToast(int message) {
        Utils.showMessageByToast(this, message, Toast.LENGTH_LONG);
    }

    protected String getProgressMessageStringID(String message) {
        try {
            @StringRes int resID = getResources().getIdentifier(message, "string", getPackageName());
            return getString(resID);
        } catch(Exception e) {
            return message;
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
