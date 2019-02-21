package org.unicef.rapidreg.base.record;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;


import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadViewHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public abstract class RecordActivity extends BaseActivity {
    public static final String TAG = RecordActivity.class.getSimpleName();
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    protected Feature currentFeature;

    private String imagePath;
    private CompositeSubscription subscriptions;
    private boolean isSelectAll = false;

    private boolean isDeleteMode = false;

    @Inject
    RecordPresenter recordPresenter;

    @NonNull
    @Override
    public RecordPresenter createPresenter() {
        return recordPresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
        deleteMenu.setOnClickListener(view -> showDeleteMode());
        selectAllMenu.setOnClickListener(view -> onClickedSelectAllButton());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        subscriptions.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK != resultCode) {
            return;
        }

        if (PhotoUploadViewHolder.REQUEST_CODE_GALLERY == requestCode) {
            onSelectFromGalleryResult(data);

        } else if (PhotoUploadViewHolder.REQUEST_CODE_CAMERA == requestCode) {
            onCaptureImageResult(data);
        }
    }

    @Override
    protected void navSyncAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_sync);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            intentSender.showSyncActivity(this, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String permissions[],
                                           final int[] grantResults) {
        if (grantResults.length > 0) {
            boolean hasDeniedPermission = false;
            for(int grantResult : grantResults) {
                if(grantResult == PackageManager.PERMISSION_DENIED) {
                    hasDeniedPermission = true;
                    break;
                }
            }
            switch (requestCode) {
                case PhotoUploadViewHolder.REQUEST_CODE_CAMERA: {
                    if(!hasDeniedPermission) {
                        this.captureImageFromCamera();
                    }
                } break;
                case PhotoUploadViewHolder.REQUEST_CODE_GALLERY: {
                    if(!hasDeniedPermission) {
                        this.captureImageFromGallery();
                    }
                } break;
            }
        }
    }

    public void tryCaptureImageFromCamera() {
        if(this.hasPermissions(Manifest.permission.CAMERA)) {
            this.captureImageFromCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PhotoUploadViewHolder.REQUEST_CODE_CAMERA);
        }
    }

    public void tryCaptureImageFromGallery() {
        if(this.hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.captureImageFromGallery();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PhotoUploadViewHolder.REQUEST_CODE_GALLERY);
        }
    }

    private void captureImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(intent, PhotoUploadViewHolder.REQUEST_CODE_GALLERY);
    }

    private void captureImageFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intent, PhotoUploadViewHolder.REQUEST_CODE_CAMERA);
    }

    public boolean hasPermissions(final String... permissions) {
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this,  permission) == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    protected void showHideDetail() {
        detailState = detailState.getNextState();
        showHideMenu.setBackgroundResource(detailState.getResId());
        RecordListFragment listFragment = getRecordListFragment();
        listFragment.toggleMode(detailState.isDetailShow());
    }

    protected void showDeleteMode() {
        isDeleteMode = true;
        changeToolbarTitle(R.string.delete);
        toolbarMainBtnContent.setVisibility(GONE);
        toolbarSelectAllBtnContent.setVisibility(VISIBLE);
        RecordListFragment listFragment = getRecordListFragment();
        listFragment.toggleDeleteMode(true);
        isSelectAll = false;
        toggleSelectAllButtonState(isSelectAll);
    }

    public void showListMode() {
        isDeleteMode = false;
        changeToolbarTitle(currentFeature.getTitleId());
        changeToolbarIcon(currentFeature);
        isSelectAll = false;
        toggleSelectAllButtonState(isSelectAll);
    }

    public void onClickedSelectAllButton() {
        RecordListFragment listFragment = getRecordListFragment();
        if (listFragment.getPresenter().getSyncedRecordsCount() > 0) {
            toggleSelectAllButtonState(!isSelectAll);
            setSelectAll(!isSelectAll);
            listFragment.toggleSelectAllItems(isSelectAll());
        }
    }

    public void toggleSelectAllButtonState(boolean isChecked) {
        if (isChecked) {
            selectAllMenu.setBackgroundResource(R.drawable.ic_check_box_white_24dp);
        } else {
            selectAllMenu.setBackgroundResource(R.drawable.ic_check_box_outline_blank_white_24dp);
        }
    }

    public void setSelectAll(boolean selectAll) {
        isSelectAll = selectAll;
    }

    public boolean isSelectAll() {
        return isSelectAll;
    }

    public Feature getCurrentFeature() {
        return currentFeature;
    }

    public void turnToFeature(Feature feature, Bundle args, int[] animIds) {
        currentFeature = feature;
        changeToolbarTitle(feature.getTitleId());
        changeToolbarIcon(feature);
        try {
            Fragment fragment = feature.getFragment();
            if (args != null) {
                fragment.setArguments(args);
            } else {
                fragment.setArguments(new Bundle());
            }
            navToFragment(fragment, animIds);
        } catch (Exception e) {
            throw new RuntimeException("Fragment navigation error", e);
        }
    }

    public void showSyncFormDialog(String message) {
        MessageDialog messageDialog = new MessageDialog(this);
        messageDialog.setTitle(R.string.sync_forms);
        messageDialog.setMessage(String.format("%s %s", message, getResources().getString(R.string
                .sync_forms_message)));
        messageDialog.setPositiveButton(R.string.ok, view -> {
            sendSyncFormEvent();
            messageDialog.dismiss();
        });
        messageDialog.setNegativeButton(R.string.cancel, view -> messageDialog.dismiss());
        messageDialog.show();
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if (!TextUtils.isEmpty(uri.getAuthority())) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            postSelectedImagePath();
        }
    }

    private String getOutputMediaFilePath() {
        File mediaStorageDir = new File(PhotoConfig.IMAGES_DIR_NAME);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir.getPath() + File.separator + System.currentTimeMillis() + JPEG_FILE_SUFFIX;
    }

    private void onCaptureImageResult(final Intent data) {
        try {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            File cacheDir = PrimeroApplication.getAppContext().getCacheDir();
            File tmpFile = File.createTempFile("primero_pic", "tmp", cacheDir);
            try(FileOutputStream outStream =  new FileOutputStream(tmpFile)){
                photo.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                Bitmap compressedImage = ImageCompressUtil.compressImage(
                        tmpFile.getPath(), PhotoConfig.MAX_COMPRESS_WIDTH,
                                           PhotoConfig.MAX_COMPRESS_HEIGHT);
                imagePath = getOutputMediaFilePath();
                ImageCompressUtil.storeImage(compressedImage, imagePath);
                clearTemporaryFile(tmpFile);
                photo.recycle();
                compressedImage.recycle();
                postSelectedImagePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearTemporaryFile(final File tempFile) {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    private void postSelectedImagePath() {
        UpdateImageEvent event = new UpdateImageEvent();
        event.setImagePath(imagePath);
        EventBus.getDefault().postSticky(event);
    }

    private void navToFragment(Fragment target, int[] animIds) {
        if (target != null) {
            String tag = target.getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (animIds != null) {
                transaction.setCustomAnimations(animIds[0], animIds[1]);
            }
            transaction.replace(R.id.fragment_content, target, tag).commit();
        }
    }

    public abstract void sendSyncFormEvent();

    protected abstract RecordListFragment getRecordListFragment();

    protected abstract void showQuitDialog(int resId);

}
