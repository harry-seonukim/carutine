package ai.pleos.playground.vehicle.utils

import ai.pleos.playground.vehicle.api.model.area.Area
import ai.pleos.playground.vehicle.api.model.area.DoorArea
import ai.pleos.playground.vehicle.api.model.area.GlobalArea
import ai.pleos.playground.vehicle.api.model.area.MirrorArea
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.area.TurnSignalArea
import ai.pleos.playground.vehicle.api.model.area.WheelArea
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.area.AreaValue
import ai.pleos.playground.vehicle.constant.area.DoorValue
import ai.pleos.playground.vehicle.constant.area.MirrorValue
import ai.pleos.playground.vehicle.constant.area.SeatValue
import ai.pleos.playground.vehicle.constant.area.TurnSignalValue
import ai.pleos.playground.vehicle.constant.area.WheelValue
import ai.pleos.playground.vehicle.constant.area.WindowValue
import org.jetbrains.annotations.TestOnly

internal object ImplementUtil {
    fun getAreaId(area: Area): Int {
        return when (area) {
            is SeatArea -> calculateEnumAreaId(area.values)
            is DoorArea -> calculateEnumAreaId(area.values)
            is WindowArea -> calculateEnumAreaId(area.values)
            is MirrorArea -> calculateEnumAreaId(area.values)
            is TurnSignalArea -> calculateEnumAreaId(area.values)
            is WheelArea -> calculateEnumAreaId(area.values)
            is GlobalArea -> {
                return VehicleAreaValue.DEFAULT
            }
        }
    }

    private fun <T> calculateEnumAreaId(values: Array<out T>): Int where T : Enum<T>, T : AreaValue {
        if (values.isEmpty()) return VehicleAreaValue.INVALID
        return values.fold(0) { acc, value -> acc or value.id }
    }


    @TestOnly
    @Throws(IllegalArgumentException::class)
    inline fun <reified T : Area> getSdkAreaInstance(serviceId: Int): T? {
        return when (T::class) {
            SeatArea::class -> {
                val seatValues = mutableListOf<SeatValue>()
                SeatValue.values().forEach { v ->
                    if (serviceId and v.id == v.id) {
                        seatValues.add(v)
                    }
                }

                SeatArea(*seatValues.toTypedArray())
            }

            DoorArea::class -> {
                val doorValues = mutableListOf<DoorValue>()
                DoorValue.values().forEach { v ->
                    if (serviceId and v.id == v.id) {
                        doorValues.add(v)
                    }
                }

                DoorArea(*doorValues.toTypedArray())
            }

            WindowArea::class -> {
                val windowValues = mutableListOf<WindowValue>()
                WindowValue.values().forEach { v ->
                    if (serviceId and v.id == v.id) {
                        windowValues.add(v)
                    }
                }

                WindowArea(*windowValues.toTypedArray())
            }

            MirrorArea::class -> {
                val mirrorValues = mutableListOf<MirrorValue>()
                MirrorValue.values().forEach { v ->
                    if (serviceId and v.id == v.id) {
                        mirrorValues.add(v)
                    }
                }

                MirrorArea(*mirrorValues.toTypedArray())
            }

            TurnSignalArea::class -> {
                val turnSignalValues = mutableListOf<TurnSignalValue>()
                TurnSignalValue.values().forEach { v ->
                    if (serviceId and v.id == v.id) {
                        turnSignalValues.add(v)
                    }
                }

                TurnSignalArea(*turnSignalValues.toTypedArray())
            }

            WheelArea::class -> {
                val wheelValues = mutableListOf<WheelValue>()
                WheelValue.values().forEach { v ->
                    if (serviceId and v.id == v.id) {
                        wheelValues.add(v)
                    }
                }

                WheelArea(*wheelValues.toTypedArray())
            }

            GlobalArea::class -> {
                GlobalArea
            }

            else -> null
        } as? T
    }
}