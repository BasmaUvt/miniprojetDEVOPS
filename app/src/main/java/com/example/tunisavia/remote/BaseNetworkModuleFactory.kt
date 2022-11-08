package com.example.tunisavia.remote

import com.example.tunisavia.base.Constants.Companion.BASE_URL
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


open class BaseNetworkModuleFactory {
    companion object {
        val CONNECT_TIMEOUT by lazy {
            30L
        }
        val READ_TIMEOUT by lazy {
            30L
        }
        val WRITE_TIMEOUT by lazy {
            30L
        }
        val SUCCESS_RESPONSE_CODE by lazy {
            200
        }
    }

    fun makeOkHttpClient(): OkHttpClient {
        /*val clientCertificates = HandshakeCertificates.Builder()
            .addPlatformTrustedCertificates()
            .addInsecureHost("192.168.0.8")
            .build()*/

        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .supportsTlsExtensions(true)
            .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
            .build()

        var obsoleteSpecBuilder = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
        obsoleteSpecBuilder = obsoleteSpecBuilder.cipherSuites("TLS_RSA_WITH_3DES_EDE_CBC_SHA")
        val obsoleteSpec: ConnectionSpec = obsoleteSpecBuilder.build()

        val lists: List<ConnectionSpec> =
            listOf(spec, ConnectionSpec.CLEARTEXT)

        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            //.connectionSpecs(listOf(obsoleteSpec))
            .addInterceptor(provideLogInterceptor())
            .addInterceptor(provideRequestInterceptor())
            //.connectionSpecs(lists)
            //.connectionSpecs(Collections.singletonList(spec))
            // .sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager)
            .build()
    }

    internal fun provideLogInterceptor(): Interceptor {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return logInterceptor
    }

    internal fun provideRequestInterceptor(): Interceptor = RequestInterceptor()

    ///////////////////////////////////////////////////////////////////////////
    // RETROFIT BUILDER
    ///////////////////////////////////////////////////////////////////////////
    fun buildRetrofitObject(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        //.baseUrl("http://192.168.1.137:3000/")
        .baseUrl("https://staravion.herokuapp.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .callbackExecutor(Executors.newSingleThreadExecutor())
        .build()

    fun buildRetrofitObjectNotif(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .callbackExecutor(Executors.newSingleThreadExecutor())
        .build()
}
