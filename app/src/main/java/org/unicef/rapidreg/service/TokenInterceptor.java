package org.unicef.rapidreg.service;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException, HttpException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == 401) {
            Log.d("intercept", "401 unauthorized");
        }
        return response;
    }
}