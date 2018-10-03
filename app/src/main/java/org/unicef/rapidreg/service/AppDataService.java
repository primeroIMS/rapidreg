package org.unicef.rapidreg.service;

import org.unicef.rapidreg.model.User;

public interface AppDataService {

    public void loadAppData(AppDataService.LoadCallback callback, User.Role roleType);

    public interface LoadCallback {
        void onSuccess();

        void onFailure();
    }
}
