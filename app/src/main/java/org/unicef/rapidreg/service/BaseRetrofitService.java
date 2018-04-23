package org.unicef.rapidreg.service;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.unicef.rapidreg.BuildConfig;

import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseRetrofitService<T> {
    private T repository;
    private Retrofit retrofit;
    private X509TrustManager trustManager;

    protected Retrofit createRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        return retrofit;
    }

    protected abstract String getBaseUrl();

    private OkHttpClient getClient() {
        buildTrustManager();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(90, TimeUnit.SECONDS);
        builder.writeTimeout(90, TimeUnit.SECONDS);
        builder.sslSocketFactory(getSSLContext().getSocketFactory(), trustManager);

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        return builder.build();
    }

    public T getRepository(Class<T> repositoryClass) {
        if (repository == null || !getBaseUrl().equals(retrofit.baseUrl().toString())){
            repository = createRetrofit().create(repositoryClass);
        }
        return repository;
    }

    private void buildTrustManager() {
       try {
           KeyStore ks = KeyStore.getInstance("AndroidCAStore");

           if (ks != null) {
               ks.load(null, null);
           }

           TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
           trustManagerFactory.init(ks);
           TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

           trustManager = (X509TrustManager) trustManagers[0];
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    private SSLContext getSSLContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



