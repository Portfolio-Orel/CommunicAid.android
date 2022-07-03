package com.orelzman.mymessages

import android.app.Application
import dagger.hilt.android.HiltAndroidApp




@HiltAndroidApp
class MainApplication: Application() {

    init {
//        val mixpanel: MixpanelAPI =
//            MixpanelAPI.getInstance(this, "1922ffdaa9090167dd5b313cdd3a64b6")
    }
}