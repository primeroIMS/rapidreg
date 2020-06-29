package org.unicef.rapidreg.service;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.unicef.rapidreg.BuildConfig;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseRetrofitService<T> {
    private T repository;
    private Retrofit retrofit;
    private X509TrustManager trustManager;

    ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();

    private Retrofit createRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
        builder.connectTimeout(90, TimeUnit.SECONDS);
        builder.connectionSpecs(Collections.singletonList(spec));
        builder.sslSocketFactory(getSSLContext().getSocketFactory(), trustManager);

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();
    }

    protected T getRepository(Class<T> repositoryClass) {
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
            sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



