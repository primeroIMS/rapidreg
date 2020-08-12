package org.unicef.rapidreg.base;


import androidx.fragment.app.Fragment;

import org.unicef.rapidreg.exception.FragmentSwitchException;

public interface Feature {
    int getTitleId();

    Fragment getFragment() throws FragmentSwitchException;

    boolean isEditMode();

    boolean isListMode();

    boolean isDetailMode();

    boolean isAddMode();

    boolean isDeleteMode();

    boolean isWebMode();
}
