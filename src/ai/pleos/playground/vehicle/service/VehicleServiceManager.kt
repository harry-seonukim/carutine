package ai.pleos.playground.vehicle.service

import ai.pleos.playground.vehicle.IVehicleService
import ai.pleos.playground.vehicle.api.model.area.Area
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.callback.ICapabilityConfigResult
import ai.pleos.playground.vehicle.callback.IGetPropertyResult
import ai.pleos.playground.vehicle.callback.ISetPropertyResult
import ai.pleos.playground.vehicle.callback.PropertyCallback
import ai.pleos.playground.vehicle.callback.exception.VehicleException
import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.common.makeVehicleTypeAndValue
import ai.pleos.playground.vehicle.constant.Constants.VEHICLE_ACTION
import ai.pleos.playground.vehicle.constant.Constants.VEHICLE_SERVICE_NAME
import ai.pleos.playground.vehicle.constant.Constants.VEHICLE_SERVICE_PACKAGE_NAME
import ai.pleos.playground.vehicle.constant.ErrorCode
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.listener.VehicleListener
import ai.pleos.playground.vehicle.utils.CapabilityUtil
import ai.pleos.playground.vehicle.utils.GsonUtil
import ai.pleos.playground.vehicle.utils.Log
import ai.pleos.playground.vehicle.utils.PermissionUtil
import ai.pleos.playground.vehicle.utils.StringUtils
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig as SdkCapabilityConfig
import ai.pleos.playground.vehicle.data.CapabilityConfig as DataCapabilityConfig


internal class VehicleServiceManager(
    private val context: Context,
    asBinder: (android.os.IBinder) -> IVehicleService = IVehicleService.Stub::asInterface,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : BaseServiceConnector<IVehicleService>(
    context = context,
    asBinder = asBinder,
    coroutineScope = coroutineScope
) {
    private val tag = StringUtils.TAG(this::class.java.simpleName)
    private val gson: Gson
        get() = GsonUtil.gson

    private val propertyCallback by lazyOf(
        PropertyCallback(::registerCallback, ::unregisterCallback)
    )

    private lateinit var propConfigMap: Map<String, DataCapabilityConfig<Any>>


    override fun reConnectedWithService() {
        Log.d(tag, "reConnectedWithService() called. ServiceBinder field: $serviceBinder")
        val service = serviceBinder ?: return

        propertyCallback.requestedRegistrations.forEach { (property, areaType) ->
            service.registerPropertyCallback(
                context.packageName,
                property,
                areaType,
                propertyCallback
            )
        }
    }

    fun connectService() {
        Log.d(tag, "connectService")
        connectService(
            Intent().apply {
                component = ComponentName(VEHICLE_SERVICE_PACKAGE_NAME, VEHICLE_SERVICE_NAME)
                action = VEHICLE_ACTION
            }
        )
    }

    fun release() {
        Log.d(tag, "disconnectService --> clear")
        serviceBinder?.let { service ->
            Log.d(tag, "disconnectService() called with: service = $service")
            propertyCallback.requestedRegistrations.forEach { (propertyName, areaType) ->
                service.unregisterPropertyCallback(
                    context.packageName,
                    propertyName,
                    areaType,
                    propertyCallback
                )
            }
        }

        propertyCallback.clearAllListeners()

        disconnectService()
    }

    private fun registerCallback(propId: PleosPropertyIds, areaType: Int) {
        request(
            onComplete = {
                registerPropertyCallback(
                    context.packageName,
                    propId.name,
                    areaType,
                    propertyCallback
                )
                propertyCallback.saveRequest(propId.name, areaType)
            },
            onFailed = {
                Log.e(tag, "registerCallback SDK Error")
            }
        )
    }

    private fun unregisterCallback(propId: PleosPropertyIds, areaType: Int) {
        request(
            onComplete = {
                unregisterPropertyCallback(
                    context.packageName,
                    propId.name,
                    areaType,
                    propertyCallback
                )
                propertyCallback.removeRequest(propId.name, areaType)
            },
            onFailed = {
                Log.e(tag, "unregisterCallback SDK Error")
            }
        )
    }

    internal fun <T> addListener(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        listener: VehicleListener
    ) {
        PermissionUtil.getVehiclePermissionException(context, instance)?.let {
            throw it
        }
        addListener(propId, areaType, areaId, listener)
    }

    internal fun addListener(
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        listener: VehicleListener
    ) {
        val alreadyRegistered = !propertyCallback.isEmptyPleosPropertyId(propId)
        propertyCallback.addListener(propId, areaType, areaId, listener)

        if (alreadyRegistered) {
            getProperty(propId, areaType, areaId, {
                propertyCallback.onChangeEvent(propId.name, areaId, it, listener)
            }, {
                propertyCallback.onErrorEvent(propId.name, it.localizedMessage, listener)
            })
        }
    }

    internal fun <T> removeListener(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        listener: VehicleListener
    ) {
        PermissionUtil.getVehiclePermissionException(context, instance)?.let {
            throw it
        }
        removeListener(propId, areaType, areaId, listener)
    }

    internal fun removeListener(
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        listener: VehicleListener
    ) {
        propertyCallback.removeListener(propId, areaType, areaId, listener)
    }

    internal fun <T> getProperty(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        action: (String?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        PermissionUtil.getVehiclePermissionException(context, instance)?.let {
            it.localizedMessage?.let { it1 -> Log.e(tag, it1) }
            onFailed(it)
            return
        }

        getProperty(propId, areaType, areaId, action, onFailed)
    }

    internal fun getProperty(
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        action: (String?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        if (areaId == VehicleAreaValue.INVALID) {
            val errorMessage = "areaId is Invalid"
            Log.e(tag, errorMessage)
            onFailed(Exception(errorMessage))
            return
        }
        request(
            onComplete = {
                catching("getProperty") {
                    getPropertyValue(
                        propId.name,
                        areaType,
                        areaId,
                        object : IGetPropertyResult.Stub() {
                            override fun onResult(
                                eventPropKey: String,
                                eventAreaId: Int,
                                eventValue: String?
                            ) {
                                Log.i(tag, "getProperty_onResult eventPropKey $eventPropKey eventAreaId $eventAreaId eventValue $eventValue")
                                action(eventValue)
                            }

                            override fun onFailed(propId: String, areaId: Int, error: String?) {
                                Log.i(tag, "getProperty_onFailed propId $propId areaId $areaId error $error")
                                onFailed(Exception(error))
                            }
                        })
                }.onFailure {
                    onFailed(Exception(it))
                }
            },
            onFailed = {
                onFailed(Exception("Service connection failed"))
            }
        )
    }

    internal fun <T, R : Any> setProperty(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        value: R,
        onFailed: (Exception) -> Unit,
    ) {
        PermissionUtil.getVehiclePermissionException(context, instance)?.let {
            it.localizedMessage?.let { it1 -> Log.e(tag, it1) }
            onFailed(it)
            return
        }

        setProperty(propId, areaType, areaId, value, onFailed)
    }

    private fun <R : Any> setProperty(
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        value: R,
        onFailed: (Exception) -> Unit,
    ) {
        if (areaId == VehicleAreaValue.INVALID) {
            val errorMessage = "areaId is Invalid"
            Log.e(tag, errorMessage)
            onFailed(Exception(errorMessage))
            return
        }
        request(
            onComplete = {
                catching("setProperty") {
                    val (type, parseValue) = value.makeVehicleTypeAndValue(gson)
                    setPropertyValue(
                        propId.name,
                        areaType,
                        areaId,
                        type,
                        parseValue,
                        object : ISetPropertyResult.Stub() {
                            override fun onResult(propKey: String, areaId: Int, result: String?) {
                                Log.i(
                                    tag,
                                    "setProperty onResult() called with: propKey = $propKey, areaId = $areaId, result = $result"
                                )
                            }

                            override fun onFailed(propId: String, areaId: Int, error: String?) {
                                Log.i(
                                    tag,
                                    "setProperty onFailed() called with: propId = $propId, areaId = $areaId, error = $error"
                                )
                                onFailed(Exception(error))
                            }

                        })
                }.onFailure {
                    onFailed(Exception(it))
                }
            },
            onFailed = {
                onFailed(Exception("Service connection failed"))
            }
        )
    }

    internal fun <T, R : Any> setPropertyAsync(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        value: R,
        onComplete: () -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        PermissionUtil.getVehiclePermissionException(context, instance)?.let {
            it.localizedMessage?.let { it1 -> Log.e(tag, it1) }
            onFailed(it)
            return
        }

        setPropertyAsync(propId, areaType, areaId, value, onComplete, onFailed)
    }

    internal fun <R : Any> setPropertyAsync(
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        value: R,
        onComplete: () -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        if (areaId == VehicleAreaValue.INVALID) {
            val errorMessage = "areaId is Invalid"
            Log.e(tag, errorMessage)
            onFailed(Exception(errorMessage))
            return
        }
        request(
            onComplete = {
                catching("setProperty") {
                    val (type, parseValue) = value.makeVehicleTypeAndValue(gson)
                    setPropertyAsyncValue(
                        propId.name,
                        areaType,
                        areaId,
                        type,
                        parseValue,
                        object : ISetPropertyResult.Stub() {
                            override fun onResult(propKey: String, areaId: Int, result: String?) {
                                Log.d(
                                    tag,
                                    "onResult() called with: propKey = $propKey, areaId = $areaId, result = $result"
                                )
                                onComplete()
                            }

                            override fun onFailed(propId: String, areaId: Int, error: String?) {
                                onFailed(Exception(error))
                            }

                        })
                }.onFailure {
                    onFailed(Exception(it))
                }
            },
            onFailed = {
                onFailed(Exception("Service connection failed"))
            }
        )
    }

    override fun onRequestCapability(onComplete: () -> Unit, onFailed: (VehicleException) -> Unit) {
        catching {
            serviceBinder?.getCapabilityConfig(
                object : ICapabilityConfigResult.Stub() {
                    override fun onResult(r: String?) {
                        catching {
                            propConfigMap = CapabilityUtil.capabilityGson.fromJson(
                                r,
                                object :
                                    TypeToken<Map<String, DataCapabilityConfig<Any>>>() {}.type
                            )

                            CapabilityUtil.setConfigMap(propConfigMap)
                            onComplete()
                        }.onFailure { t ->
                            onFailed(VehicleException(t.localizedMessage))
                        }
                    }

                    override fun onFailed(t: String?) {
                        onFailed(VehicleException(t))
                    }
                }
            ) ?: onFailed(VehicleException(ErrorCode.ERROR_INTERNAL_SERVICE.id.toString()))
        }.onFailure { onFailed(VehicleException(ErrorCode.ERROR_INTERNAL_SERVICE.id.toString())) }
    }

    internal fun <T> requestCapability(
        instance: T,
        propId: PleosPropertyIds,
        action: (DataCapabilityConfig<Any>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        Log.i(tag, "getCapabilityConfig instance $instance , propId $propId")
        if (instance != null) {
            PermissionUtil.getVehiclePermissionException(context, instance)?.let {
                it.localizedMessage?.let { it1 -> Log.e(tag, it1) }
                onFailed(it)
                return
            }
        }
        Log.i(tag, "getCapabilityConfig request")
        request(
            onComplete = {
                Log.i(tag, "getCapabilityConfig propId $propId")
                getCapabilityConfigByKey(propId.name,
                    object : ICapabilityConfigResult.Stub() {
                        override fun onResult(r: String?) {
                            catching {
                                action(
                                    CapabilityUtil.capabilityGson.fromJson(
                                        r, object :
                                            TypeToken<DataCapabilityConfig<Any>>() {}.type
                                    )
                                )
                            }.onFailure { t ->
                                onFailed(VehicleException(t.localizedMessage))
                            }
                        }

                        override fun onFailed(t: String?) {
                            onFailed(VehicleException(t))
                        }
                    }
                )
            },
            onFailed = onFailed
        )
    }

    internal fun <T> requestCapability(
        instance: T,
        action: (Map<String, DataCapabilityConfig<Any>>?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        Log.i(tag, "getCapabilityConfig called()")
        if (instance != null) {
            PermissionUtil.getVehiclePermissionException(context, instance)?.let {
                it.localizedMessage?.let { it1 -> Log.e(tag, it1) }
                onFailed(it)
                return
            }
        }

        if (this@VehicleServiceManager::propConfigMap.isInitialized) {
            catching { action(propConfigMap) }.onFailure { t -> onFailed(Exception(t)) }
        } else {
            request(
                onComplete = {
                    getCapabilityConfig(
                        object : ICapabilityConfigResult.Stub() {
                            override fun onResult(r: String?) {
                                catching {
                                    propConfigMap = CapabilityUtil.capabilityGson.fromJson(
                                        r,
                                        object :
                                            TypeToken<Map<String, DataCapabilityConfig<Any>>>() {}.type
                                    )

                                    CapabilityUtil.setConfigMap(propConfigMap)
                                    action(propConfigMap)
                                }.onFailure { t ->
                                    onFailed(VehicleException(t.localizedMessage))
                                }
                            }

                            override fun onFailed(t: String?) {
                                onFailed(VehicleException(t))
                            }
                        }
                    )
                },
                onFailed = onFailed
            )
        }
    }

    internal inline fun <reified A : Area> requestAreaListCapability(
        instance: Any,
        propId: PleosPropertyIds,
        crossinline onComplete: (SdkCapabilityConfig, List<A>) -> Unit,
        noinline onFailed: (Exception) -> Unit
    ) {
        requestCapability(instance = instance, propId = propId, action = { ret ->
            val areas = CapabilityUtil.getAreaIds<A>(ret)
            if (areas.isNotEmpty()) {
                onComplete(SdkCapabilityConfig(CapabilityUtil.getAccess(ret)), areas)
            } else {
                onFailed(Exception(CapabilityUtil.FAILED_RETRIEVE_CAPABILITIES))
            }
        }, onFailed = onFailed)
    }

    internal inline fun <reified A : Area, R> requestAreaBoundsCapability(
        instance: Any,
        propId: PleosPropertyIds,
        crossinline onComplete: (SdkCapabilityConfig, Map<A, AreaBounds<R>>) -> Unit,
        noinline onFailed: (Exception) -> Unit
    ) {
        requestCapability(instance = instance, propId = propId, action = { ret ->
            val configs = CapabilityUtil.getAreaConfigs<A, R>(ret)
            val retMap = mutableMapOf<A, AreaBounds<R>>()
            catching {
                for (config in configs) {
                    val areaBounds: AreaBounds<R>? = CapabilityUtil.createAreaBounds(config.value)
                    areaBounds?.let { retMap[config.key] = it }
                }
            }.onSuccess {
                onComplete(SdkCapabilityConfig(CapabilityUtil.getAccess(ret)), retMap)
            }.onFailure {
                onFailed(Exception(it))
            }
        }, onFailed = onFailed)
    }

    internal inline fun <reified A : Area, reified V> requestAreaSupportedListCapability(
        instance: Any,
        propId: PleosPropertyIds,
        crossinline idResolver: (String) -> V?,
        crossinline onComplete: (SdkCapabilityConfig, Map<A, List<V>>) -> Unit,
        noinline onFailed: (Exception) -> Unit
    ) {
        requestCapability(instance = instance, propId = propId, action = { ret ->
            val configs = CapabilityUtil.getAreaConfigs<A, Nothing>(ret)
            val retMap = mutableMapOf<A, List<V>>()
            catching {
                for (config in configs) {
                    retMap[config.key] = CapabilityUtil.convertList(config.value.supportedValues, idResolver)
                }
            }.onSuccess {
                onComplete(SdkCapabilityConfig(CapabilityUtil.getAccess(ret)), retMap)
            }.onFailure {
                onFailed(Exception(it))
            }
        }, onFailed = onFailed)
    }

    internal fun <V> requestGlobalSupportedListCapability(
        instance: Any,
        propId: PleosPropertyIds,
        idResolver: (String) -> V?,
        onComplete: (SdkCapabilityConfig, List<V>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        requestCapability(instance = instance, propId = propId, action = { ret ->
            val config = CapabilityUtil.getGlobalAreaConfig<Nothing>(ret)
            catching {
                val list = config?.supportedValues?.mapNotNull(idResolver) ?: emptyList()
                onComplete(SdkCapabilityConfig(CapabilityUtil.getAccess(ret)), list)
            }.onFailure {
                onFailed(Exception(it))
            }
        }, onFailed = onFailed)
    }

    internal inline fun <reified R> requestGlobalBoundsCapability(
        instance: Any,
        propId: PleosPropertyIds,
        crossinline onComplete: (SdkCapabilityConfig, AreaBounds<R>) -> Unit,
        noinline onFailed: (Exception) -> Unit
    ) {
        requestCapability(instance = instance, propId = propId, action = { ret ->
            val config = CapabilityUtil.getGlobalAreaConfig<R>(ret)
            catching {
                val bounds = config?.let { CapabilityUtil.createAreaBounds(it) }
                    ?: throw VehicleException(ErrorCode.ERROR_NOT_SUPPORTED_PROPERTY.id.toString())
                onComplete(SdkCapabilityConfig(CapabilityUtil.getAccess(ret)), bounds)
            }.onFailure {
                onFailed(Exception(it))
            }
        }, onFailed = onFailed)
    }

    internal fun requestAccessCapability(
        instance: Any,
        propId: PleosPropertyIds,
        onComplete: (SdkCapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        requestCapability(instance = instance, propId = propId, action = { ret ->
            onComplete(SdkCapabilityConfig(CapabilityUtil.getAccess(ret)))
        }, onFailed = onFailed)
    }

    internal fun <T, A : Area> getAreaProperty(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        area: A,
        areaIdResolver: (A) -> Int,
        action: (String?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        getProperty(
            instance = instance,
            propId = propId,
            areaType = areaType,
            areaId = areaIdResolver(area),
            action = action,
            onFailed = onFailed
        )
    }

    internal fun <T, A : Area, R : Any> setAreaPropertyAsync(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        area: A,
        areaIdResolver: (A) -> Int,
        value: R,
        onComplete: () -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        setPropertyAsync(
            instance = instance,
            propId = propId,
            areaType = areaType,
            areaId = areaIdResolver(area),
            value = value,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    internal fun <T, A : Area, R : Any> setAreaProperty(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        area: A,
        areaIdResolver: (A) -> Int,
        value: R,
        onFailed: (Exception) -> Unit,
    ) {
        setProperty(
            instance = instance,
            propId = propId,
            areaType = areaType,
            areaId = areaIdResolver(area),
            value = value,
            onFailed = onFailed
        )
    }

    internal fun <T, A : Area, L : VehicleListener> addAreaListener(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        area: A,
        areaIdResolver: (A) -> Int,
        listener: L
    ) {
        addListener(
            instance = instance,
            propId = propId,
            areaType = areaType,
            areaId = areaIdResolver(area),
            listener = listener
        )
    }

    internal fun <T, A : Area, L : VehicleListener> removeAreaListener(
        instance: T,
        propId: PleosPropertyIds,
        areaType: Int,
        area: A,
        areaIdResolver: (A) -> Int,
        listener: L
    ) {
        removeListener(
            instance = instance,
            propId = propId,
            areaType = areaType,
            areaId = areaIdResolver(area),
            listener = listener
        )
    }
}
