package org.unicef.rapidreg.service;

import org.unicef.rapidreg.model.User;

public interface AppDataService {

    public void loadAppData(AppDataService.LoadCallback callback, User.Role roleType, boolean forceUpdate);

    public interface LoadCallback {
        void onSuccess();

        void onFailure();
    }
}
