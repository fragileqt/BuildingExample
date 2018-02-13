package com.project.school.buildingexample

import android.app.Application
import eu.kudan.kudan.*

/**
 * Created by jozef on 13.2.2018.
 */

val ARActivity.app get() = (application as App)

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ARAPIKey.getInstance().setAPIKey(getString(R.string.kudan_key))
    }
}
