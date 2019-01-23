package com.careapp

import android.app.Application
import android.content.Context
import com.careapp.BaseApplication.RetrofitInitialization.initRetrofit
import com.careapp.utils.Constants
import com.careapp.utils.LocaleHelper
import com.careapp.wsutils.WSInterface
import com.careapp.wsutils.WSUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BaseApplication : Application() {
//    var languageToLoad = "hi_IN"

    private var mInstance: BaseApplication? = null

    companion object {
        var wsInterface: WSInterface? = null
        private var okHttpClient: OkHttpClient? = null
    }

    override fun onCreate() {
        super.onCreate()
        initRetrofit()
        changeLanguage(
            applicationContext,
            "hi_IN"
        )

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
    }

    fun changeLanguage(context: Context, languageCode: String): Context {
        return LocaleHelper.setLocale(context, languageCode)
    }

    object RetrofitInitialization {
        fun initRetrofit() {
            val headerInterceptor = Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_FORM_URL_ENCODED)
                chain.proceed(requestBuilder.build())
            }
            okHttpClient = OkHttpClient().newBuilder().addInterceptor(headerInterceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(
                    WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS
                ).connectTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS).writeTimeout(
                    WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS
                ).build()
            wsInterface = buildRetrofitClient(WSUtils.BASE_URL)

        }

        private fun buildRetrofitClient(baseUrl: String): WSInterface {
            return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient!!).build().create(WSInterface::class.java)
        }
    }

    @Synchronized
    fun getInstance(): BaseApplication {
        if (mInstance == null) mInstance = BaseApplication()
        return mInstance as BaseApplication
    }

}