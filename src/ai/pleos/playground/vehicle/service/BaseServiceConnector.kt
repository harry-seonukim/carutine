package ai.pleos.playground.vehicle.service

import ai.pleos.playground.vehicle.callback.exception.VehicleException
import ai.pleos.playground.vehicle.constant.ErrorCode
import ai.pleos.playground.vehicle.utils.Log
import ai.pleos.playground.vehicle.utils.StringUtils
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.DeadObjectException
import android.os.IBinder
import android.os.IInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import java.util.LinkedList
import java.util.Queue

internal abstract class BaseServiceConnector<IService : IInterface>(
    private val context: Context,
    private val asBinder: (IBinder) -> IService,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : ServiceConnection {

    private val TAG = StringUtils.TAG(this::class.java.simpleName)

    private val cachedCall: Queue<IService.() -> Unit> = LinkedList()
    private var connectionInfo = ConnectionInfo(reConnection = ::reConnectedWithService)
    protected var serviceBinder: IService? = null

    abstract fun reConnectedWithService()

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Log.i(TAG, "ServiceConnected call")
        connectionInfo = connectionInfo.copy(connectState = ConnectState.CONNECT)
        serviceBinder = asBinder(service)
        tryReConnection()
        onRequestCapability(
            onComplete = { consumeAllCached() },
            onFailed = {
                // Capability fetch failure should not block already cached API requests forever.
                Log.e(TAG, "onRequestCapability failed: ${it.localizedMessage}")
                consumeAllCached()
            }
        )
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Log.e(TAG, "service is reconnect isAbnormal reason")
        connectionInfo = connectionInfo.copy(connectState = ConnectState.CONNECTING)
        serviceBinder = null
    }


    fun connectService(intent: Intent) {
        Log.d(TAG, "Service Connect Api called $intent")

        connectionInfo = connectionInfo.copy(connectState = ConnectState.CONNECTING)

        flowOf(intent)
            .map {
                if (!context.bindService(it, this@BaseServiceConnector, SERVICE_BIND_FLAGS))
                    throw Exception("CAS has not started yet.")
            }.retryWhen { cause, attempt ->
                Log.d(TAG, "connectService currentRetryCount $attempt")

                if (cause is Exception && attempt < MAX_RETRY) {
                    delay(CONNECT_RETRY_TIME)
                    true
                } else false

            }.catch { cause ->
                cause.printStackTrace()
                Log.e(TAG, "Connect Service is Failed cause : ${cause.cause}")
                connectionInfo = connectionInfo.copy(connectState = ConnectState.DISCONNECT)
            }.launchIn(coroutineScope)
    }

    fun disconnectService() {
        runCatching {
            context.unbindService(this@BaseServiceConnector)
        }.onFailure {
            Log.e(TAG, "Disconnected is failed cause : ${it.cause}")
            it.printStackTrace()
        }.also {
            cachedCall.clear()
            serviceBinder = null
            connectionInfo = connectionInfo.copy(connectState = ConnectState.DISCONNECT, isReconnection = false)
        }
    }

    fun request(onComplete: IService.() -> Unit, onFailed: (VehicleException) -> Unit) {
        if (connectionInfo.isUserNotConnected) {
            Log.e(TAG, "sdk not initialize so service is not connected")
            onFailed(VehicleException(ErrorCode.ERROR_SERVICE_CONNECTION_FAILURE.id.toString()))
            return
        }

        sendApi(onSuccess = onComplete, onFailed = { throwable -> onFailed(VehicleException(throwable.localizedMessage)) })
    }

    abstract fun onRequestCapability(onComplete: () -> Unit, onFailed: (VehicleException) -> Unit)

    private fun tryReConnection() {
        if (connectionInfo.isReconnection) {
            connectionInfo.reConnection()
        }

        connectionInfo = connectionInfo.copy(isReconnection = true)
    }

    private fun consumeAllCached() {
        while (cachedCall.isNotEmpty()) {
            if (connectionInfo.isNotAvailableSendApiToService || serviceBinder == null) break
            cachedCall.poll()?.run { sendApi(this) }
        }
    }


    private fun sendApi(onSuccess: IService.() -> Unit, onFailed: (VehicleException) -> Unit = {}) {
        runCatching {
            val binder = serviceBinder ?: throw UnPreparedConnect()
            binder.onSuccess()
        }.onFailure { e ->
            when (e) {
                is UnPreparedConnect -> cachedCall.add(onSuccess)
                is DeadObjectException -> cachedCall.add(onSuccess)
                else -> {
                    onFailed(VehicleException(e.localizedMessage))
                    Log.e(TAG, "failed send error cause : ${e.cause}")
                    e.printStackTrace()
                }
            }
        }
    }

    private data class ConnectionInfo(
        val connectState: ConnectState = ConnectState.DISCONNECT,
        val isReconnection: Boolean = false,
        val reConnection: () -> Unit
    ) {
        val isNotAvailableSendApiToService = connectState == ConnectState.DISCONNECT || connectState == ConnectState.CONNECTING
        val isUserNotConnected = connectState == ConnectState.DISCONNECT
    }

    private enum class ConnectState {
        //state of Connect with service
        CONNECT,

        //state of try connect. When bindService is called or when the service connects again after abnormal termination
        CONNECTING,

        //state of disconnect, user not connect or call disconnect
        DISCONNECT
    }

    private class UnPreparedConnect : Throwable()
    companion object {
        const val MAX_RETRY = 30
        const val CONNECT_RETRY_TIME = 500L
        internal val SERVICE_BIND_FLAGS =
            Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT or Context.BIND_ABOVE_CLIENT
    }
}
