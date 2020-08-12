package org.unicef.rapidreg.base.record.recordregister;

import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

public class RecordPhotoPageChangeListener implements ViewPager.OnPageChangeListener {
    private RecordPhotoAdapter recordPhotoAdapter;
    private ItemValuesMap itemValuesVerifyList;
    private FragmentStatePagerItemAdapter adapter;

    public RecordPhotoPageChangeListener() {
        this.itemValuesVerifyList = new ItemValuesMap();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int
            positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (this.adapter != null) {
            RecordRegisterFragment currentPage = (RecordRegisterFragment) adapter.getPage
                    (position);
            // ensure we are not trying to ask pages before they are loaded
            if (this.recordPhotoAdapter != null && currentPage != null) {
                currentPage.setFieldValueVerifyResult(this.itemValuesVerifyList);
                this.recordPhotoAdapter = currentPage.getPhotoAdapter();
                this.recordPhotoAdapter.setItems(currentPage.getPhotoPathsData());
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public RecordPhotoAdapter getRecordPhotoAdapter() {
        return this.recordPhotoAdapter;
    }

    public void setRecordPhotoAdapter(final RecordPhotoAdapter recordPhotoAdapter) {
        this.recordPhotoAdapter = recordPhotoAdapter;
    }

    public void setItemValuesVerifyList(final ItemValuesMap itemValuesVerifyList) {
        this.itemValuesVerifyList = itemValuesVerifyList;
    }

    public ItemValuesMap getFieldValueVerifyResult() {
        return this.itemValuesVerifyList;
    }

    public void setAdapter(final FragmentStatePagerItemAdapter adapter) {
        this.adapter = adapter;
    }
}
