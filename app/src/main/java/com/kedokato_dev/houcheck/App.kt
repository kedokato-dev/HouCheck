package com.kedokato_dev.houcheck

import android.app.Application
import android.util.Log
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class App: Application() {
    companion object {
        lateinit var mmkv: MMKV
    }
    override fun onCreate() {
        super.onCreate()
        Log.d("App", "Application started")
        MMKV.initialize(this,"$filesDir/Data",MMKVLogLevel.LevelInfo)
        mmkv = MMKV.mmkvWithID("data",MMKV.MULTI_PROCESS_MODE,"demo")
    }
}