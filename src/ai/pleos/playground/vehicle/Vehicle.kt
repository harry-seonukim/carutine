package ai.pleos.playground.vehicle

import ai.pleos.playground.vehicle.api.Brake
import ai.pleos.playground.vehicle.api.CarInfo
import ai.pleos.playground.vehicle.api.Display
import ai.pleos.playground.vehicle.api.Door
import ai.pleos.playground.vehicle.api.DrivingMode
import ai.pleos.playground.vehicle.api.EvBattery
import ai.pleos.playground.vehicle.api.Fuel
import ai.pleos.playground.vehicle.api.Gear
import ai.pleos.playground.vehicle.api.HVAC
import ai.pleos.playground.vehicle.api.Light
import ai.pleos.playground.vehicle.api.Odometer
import ai.pleos.playground.vehicle.api.Safety
import ai.pleos.playground.vehicle.api.Seat
import ai.pleos.playground.vehicle.api.SideMirror
import ai.pleos.playground.vehicle.api.SteeringWheel
import ai.pleos.playground.vehicle.api.Tire
import ai.pleos.playground.vehicle.api.Toll
import ai.pleos.playground.vehicle.api.TurnSignal
import ai.pleos.playground.vehicle.api.Window
import ai.pleos.playground.vehicle.api.Wiper
import ai.pleos.playground.vehicle.api.implement.BrakeImpl
import ai.pleos.playground.vehicle.api.implement.CarInfoImpl
import ai.pleos.playground.vehicle.api.implement.DisplayImpl
import ai.pleos.playground.vehicle.api.implement.DoorImpl
import ai.pleos.playground.vehicle.api.implement.DrivingModeImpl
import ai.pleos.playground.vehicle.api.implement.EvBatteryImpl
import ai.pleos.playground.vehicle.api.implement.FuelImpl
import ai.pleos.playground.vehicle.api.implement.GearImpl
import ai.pleos.playground.vehicle.api.implement.HVACImpl
import ai.pleos.playground.vehicle.api.implement.LightImpl
import ai.pleos.playground.vehicle.api.implement.OdometerImpl
import ai.pleos.playground.vehicle.api.implement.SafetyImpl
import ai.pleos.playground.vehicle.api.implement.SeatImpl
import ai.pleos.playground.vehicle.api.implement.SideMirrorImpl
import ai.pleos.playground.vehicle.api.implement.SteeringWheelImpl
import ai.pleos.playground.vehicle.api.implement.TireImpl
import ai.pleos.playground.vehicle.api.implement.TollImpl
import ai.pleos.playground.vehicle.api.implement.TurnSignalImpl
import ai.pleos.playground.vehicle.api.implement.WindowImpl
import ai.pleos.playground.vehicle.api.implement.WiperImpl
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.Log
import ai.pleos.playground.vehicle.utils.StringUtils
import android.content.Context

/**
 * The entry point of the Vehicle SDK.
 *
 * Create an instance with `Vehicle(context)`, then call [initialize] to connect
 * to the vehicle service. Use factory methods such as [getDoor] and [getDisplay]
 * to obtain domain API objects ([Door], [Display], and other interfaces in
 * `ai.pleos.playground.vehicle.api`), then call property, capability, or callback
 * APIs on those objects. Call [release] when you no longer need the SDK.
 *
 * Typical flow:
 * 1. `Vehicle(context)` → [initialize]
 * 2. `getXxx()` → domain API (for example [Door])
 * 3. Capability / get / set / callback on that domain API
 * 4. [release]
 *
 * @param context Android [android.content.Context] used to bind to the vehicle service.
 *
 * ### Kotlin
 *
 * **Get Property API example**
 * ```kotlin
 * // Create Vehicle instance
 * val vehicle = Vehicle(context)
 *
 * // Start Vehicle
 * vehicle.initialize()
 *
 * val door = vehicle.getDoor()
 * door.getCurrentDoorPosition(
 *   area = DoorArea(DoorValue.ROW_1_LEFT),
 *   onComplete = { pos: Int? ->
 *     // do something
 *   },
 *   onFailed = { e: Exception ->
 *     // do something
 *   }
 * )
 *
 * val display = vehicle.getDisplay()
 * display.getCurrentSpeedDisplayUnit(
 *   onComplete = { unit: SpeedDisplayUnit? ->
 *     // do something
 *   },
 *   onFailed = { e: Exception ->
 *     // do something
 *   }
 * )
 *
 * // End Vehicle
 * vehicle.release()
 * ```
 *
 * **Set Property API example**
 * ```kotlin
 * // Create Vehicle instance
 * val vehicle = Vehicle(context)
 *
 * // Start Vehicle
 * vehicle.initialize()
 *
 * val door = vehicle.getDoor()
 * door.setDoorPosition(
 *   area = DoorArea(DoorValue.ROW_1_LEFT),
 *   value = 1, // Integer type
 *   onFailed = { e: Exception ->
 *     // do something
 *   }
 * )
 *
 * // End Vehicle
 * vehicle.release()
 * ```
 *
 * **Callback API example**
 * ```kotlin
 * // Create Vehicle instance
 * val vehicle = Vehicle(context)
 *
 * // Start Vehicle
 * vehicle.initialize()
 *
 * // Create callback listener
 * val callbackListener = object : GearSelectionListener {
 *   override fun onSelected(selection: VehicleGear?) {
 *     // do something
 *   }
 *
 *   override fun onFailed(e: Exception) {
 *     // do something
 *   }
 * }
 *
 * val gear = vehicle.getGear()
 *
 * // register callback
 * gear.registerGearSelection(
 *   gearSelectionListener = callbackListener
 * )
 * // unregister callback
 * gear.unregisterGearSelection(
 *   gearSelectionListener = callbackListener
 * )
 *
 * // End Vehicle
 * vehicle.release()
 * ```
 *
 * **Capability API example**
 *
 * Check whether a property is supported on the vehicle and inspect its
 * [ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig]
 * (for example access type and value bounds) before get/set calls.
 * ```kotlin
 * // Create Vehicle instance
 * val vehicle = Vehicle(context)
 *
 * // Start Vehicle
 * vehicle.initialize()
 *
 * val door = vehicle.getDoor()
 * door.checkDoorPositionCapabilities(
 *   onComplete = { config, areaMap ->
 *     when (config.accessType) {
 *       AccessType.READ, AccessType.READ_WRITE -> {
 *         val bounds = areaMap[DoorArea(DoorValue.ROW_1_LEFT)]
 *         // Use bounds?.minValue / bounds?.maxValue, then call getCurrentDoorPosition
 *       }
 *       else -> {
 *         // NONE or WRITE only — disable UI or use an alternate flow
 *       }
 *     }
 *   },
 *   onFailed = { e: Exception ->
 *     if (e is VehicleException) {
 *       // Capability lookup failed: e.errorCode
 *     }
 *   }
 * )
 *
 * // End Vehicle
 * vehicle.release()
 * ```
 *
 * ### Java
 *
 * **Get Property API example**
 * ```java
 * // Create Vehicle instance
 * Vehicle vehicle = new Vehicle(context);
 *
 * // Start Vehicle
 * vehicle.initialize();
 *
 * Door door = vehicle.getDoor();
 * door.getCurrentDoorPosition(
 *   new DoorArea(DoorValue.ROW_1_LEFT),
 *   pos -> {
 *     // do something
 *     return Unit.INSTANCE;
 *   },
 *   e -> {
 *     // do something
 *     return Unit.INSTANCE;
 *   }
 * );
 *
 * Display display = vehicle.getDisplay();
 * display.getCurrentSpeedDisplayUnit(
 *   unit -> {
 *     // do something
 *     return Unit.INSTANCE;
 *   },
 *   e -> {
 *     // do something
 *     return Unit.INSTANCE;
 *   }
 * );
 *
 * // End Vehicle
 * vehicle.release();
 * ```
 *
 * **Set Property API example**
 * ```java
 * // Create Vehicle instance
 * Vehicle vehicle = new Vehicle(context);
 *
 * // Start Vehicle
 * vehicle.initialize();
 *
 * Door door = vehicle.getDoor();
 * door.setDoorPosition(
 *   new DoorArea(DoorValue.ROW_1_LEFT),
 *   1,
 *   e -> {
 *     // do something
 *     return Unit.INSTANCE;
 *   }
 * );
 *
 * // End Vehicle
 * vehicle.release();
 * ```
 *
 * **Callback API example**
 * ```java
 * // Create Vehicle instance
 * Vehicle vehicle = new Vehicle(context);
 *
 * // Start Vehicle
 * vehicle.initialize();
 *
 * // Create callback listener
 * GearSelectionListener callbackListener = new GearSelectionListener() {
 *   @Override
 *   public void onSelected(@Nullable VehicleGear gear) {
 *     // do something
 *   }
 *
 *   @Override
 *   public void onFailed(@NonNull Exception e) {
 *     // do something
 *   }
 * };
 *
 * Gear gear = vehicle.getGear();
 *
 * // register callback
 * gear.registerGearSelection(callbackListener);
 *
 * // unregister callback
 * gear.unregisterGearSelection(callbackListener);
 *
 * // End Vehicle
 * vehicle.release();
 * ```
 */
@Suppress("unused")
class Vehicle(context: Context) {
    private val tag = StringUtils.TAG(this::class.java.simpleName)
    internal val serviceManager = VehicleServiceManager(context)

    private val evBattery: EvBattery = EvBatteryImpl(serviceManager)
    private val fuel: Fuel = FuelImpl(serviceManager)
    private val hvac: HVAC = HVACImpl(serviceManager)
    private val door: Door = DoorImpl(serviceManager)
    private val seat: Seat = SeatImpl(serviceManager)
    private val window: Window = WindowImpl(serviceManager)
    private val display: Display = DisplayImpl(serviceManager)
    private val odometer: Odometer = OdometerImpl(serviceManager)
    private val light: Light = LightImpl(serviceManager)
    private val safety: Safety = SafetyImpl(serviceManager)
    private val carInfo: CarInfo = CarInfoImpl(serviceManager)
    private val drivingMode: DrivingMode = DrivingModeImpl(serviceManager)
    private val gear: Gear = GearImpl(serviceManager)
    private val steeringWheel: SteeringWheel = SteeringWheelImpl(serviceManager)
    private val toll: Toll = TollImpl(serviceManager)
    private val turnSignal: TurnSignal = TurnSignalImpl(serviceManager)
    private val wiper: Wiper = WiperImpl(serviceManager)
    private val tire: Tire = TireImpl(serviceManager)
    private val sideMirror: SideMirror = SideMirrorImpl(serviceManager)
    private val brake: Brake = BrakeImpl(serviceManager)


    /**
     * Initialize sdk. It is called while the application is created.
     */
    fun initialize() {
        Log.d(tag, "initialize")
        serviceManager.connectService()
    }

    /**
     * Release sdk. It is called while the application is destroyed.
     */
    fun release() {
        Log.d(tag, "release")
        serviceManager.release()
    }

    /**
     * Get the object of EvBattery to use EvBattery APIs.
     */
    fun getEvBattery(): EvBattery {
        return evBattery
    }

    /**
     * Get the object of HVAC to use HVAC APIs.
     */
    fun getHVAC(): HVAC {
        return hvac
    }

    /**
     * Get the object of Door to use Door APIs.
     */
    fun getDoor(): Door {
        return door
    }

    /**
     * Get the object of Seat to use Seat APIs.
     */
    fun getSeat(): Seat {
        return seat
    }

    /**
     * Get the object of Window to use Window APIs.
     */
    fun getWindow(): Window {
        return window
    }

    /**
     * Get the object of Display to use Display APIs.
     */
    fun getDisplay(): Display {
        return display
    }


    /**
     * Get the object of Odometer to use Odometer APIs.
     */
    fun getOdometer(): Odometer {
        return odometer
    }

    /**
     * Get the object of Light to use Light APIs.
     */
    fun getLight(): Light {
        return light
    }

    /**
     * Get the object of Safety to use Safety APIs.
     */
    fun getSafety(): Safety {
        return safety
    }

    /**
     * Get the object of CarInfo to use CarInfo APIs.
     */
    fun getCarInfo(): CarInfo {
        return carInfo
    }

    /**
     * Get the object of DrivingMode to use DrivingMode APIs.
     */
    fun getDrivingMode(): DrivingMode {
        return drivingMode
    }

    /**
     * @suppress
     * @hide
     * Get the object of Gear to use Gear APIs.
     */
    fun getGear(): Gear {
        return gear
    }

    /**
     * Get the object of SteeringWheel to use SteeringWheel APIs.
     */
    fun getSteeringWheel(): SteeringWheel {
        return steeringWheel
    }

    /**
     * Get the object of Toll to use Toll APIs.
     */
    fun getToll(): Toll {
        return toll
    }

    /**
     * Get the object of TurnSignal to use TurnSignal APIs.
     */
    fun getTurnSignal(): TurnSignal {
        return turnSignal
    }

    /**
     * Get the object of Wiper to use Wiper APIs.
     */
    fun getWiper(): Wiper {
        return wiper
    }

    /**
     * Get the object of Tire to use Tire APIs.
     */
    fun getTire(): Tire {
        return tire
    }

    /**
     * Get the object of SideMirror to use SideMirror APIs.
     */
    fun getSideMirror(): SideMirror {
        return sideMirror
    }

    /**
     * Get the object of Brake to use Brake APIs.
     */
    fun getBrake(): Brake {
        return brake
    }

    /**
     * Get the object of Fuel to use Fuel APIs.
     */
    fun getFuel() : Fuel {
        return fuel
    }
}
