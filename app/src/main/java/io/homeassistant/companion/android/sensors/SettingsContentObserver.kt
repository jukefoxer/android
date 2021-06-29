package io.homeassistant.companion.android.sensors

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.util.Log
import io.homeassistant.companion.android.HomeAssistantApplication
import io.homeassistant.companion.android.common.dagger.GraphComponentAccessor
import io.homeassistant.companion.android.common.data.integration.IntegrationRepository
import io.homeassistant.companion.android.database.AppDatabase
import kotlinx.coroutines.*
import javax.inject.Inject

class SettingsContentObserver(handler: Handler?) : ContentObserver(handler) {

    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private var context: Context? = null

    @Inject
    lateinit var integrationUseCase: IntegrationRepository

    constructor(handler: Handler?, context: Context) : this(handler) {
        this.context = context
    }

    override fun onChange(selfChange: Boolean) {
        DaggerSensorComponent.builder()
            .appComponent((this@SettingsContentObserver.context!! as GraphComponentAccessor).appComponent)
            .build()
            .inject(this)
        ioScope.launch {
            SensorReceiver().updateSensors(
                this@SettingsContentObserver.context!!,
                integrationUseCase
            )
        }
    }

}