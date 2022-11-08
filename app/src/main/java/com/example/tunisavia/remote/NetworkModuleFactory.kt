package com.example.tunisavia.remote

import okhttp3.OkHttpClient

object NetworkModuleFactory : BaseNetworkModuleFactory() {

    const val LOG_INTERCEPTOR = "LogInterceptor"
    const val REQUEST_INTERCEPTOR = "RequestInterceptor"

    fun makeService(): ServiceEndPoint = makeService(makeOkHttpClient())

    private fun makeService(okHttpClient: OkHttpClient): ServiceEndPoint {
        val retrofit = buildRetrofitObject(okHttpClient)
        return retrofit.create(ServiceEndPoint::class.java)
    }

    fun makeServiceNotif(): NotifServiceEndPoint = makeServiceNotif(makeOkHttpClient())

    private fun makeServiceNotif(okHttpClient: OkHttpClient): NotifServiceEndPoint {
        val retrofit = buildRetrofitObjectNotif(okHttpClient)
        return retrofit.create(NotifServiceEndPoint::class.java)
    }
}
