# Pleos Vehicle SDK 매핑 (PleosVehicleGateway)

요약
- `app/src/main/java/com/carutine/vehicle/pleos/PleosVehicleGateway.kt`가 Vehicle SDK 2.3.2로 `VehicleGateway`를 구현한다.
- API·권한 이름은 SDK 소스(`Vehicle-2.3.2-sources`)의 인터페이스와 `api/implement/*Impl.kt`의 `@RequiresCustomPermission`(런타임 검사 기준)으로 확인했다.
- 에뮬레이터 실측 전이다. "확인 필요" 항목은 Virtual Console에서 값을 바꿔 가며 확인해야 한다.

## 1. Cap ↔ SDK API ↔ 권한

| Cap | 지원 판정 (check API → 조건) | 읽기 | 쓰기 / 구독 | 권한 (읽기 / 쓰기) |
|---|---|---|---|---|
| `time` | 항상 | - | - | - |
| `tts` | 항상 (Android `TextToSpeech`, 앱이 담당) | - | - | - |
| `location` | ACCESS_FINE/COARSE 중 하나 허용 + LocationManager 있음 | - | `LocationManager.requestLocationUpdates`(GPS→NETWORK→PASSIVE, 5초/20m) | `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` (dangerous, 런타임 요청) |
| `hvac.temp` | `HVAC.checkHvacTemperatureCapabilities` (3인자: Config, Map<SeatArea, AreaBounds<Float>>, HvacTemperatureConfigArray?) → WRITE/READ_WRITE, 영역 있음 | `getHvacTemperature(area, CELSIUS)` | `setHvacTemperature(area, value, CELSIUS)` 모든 영역 | `CAR_CLIMATE` / `CONTROL_CAR_CLIMATE` |
| `hvac.fan` | `HVAC.checkHvacFanSpeedCapabilities` → WRITE/READ_WRITE | `getHvacFanSpeed(area)` | `setHvacFanSpeed(area, level)` 모든 영역 | `CAR_CLIMATE` / `CONTROL_CAR_CLIMATE` |
| `hvac.air` | `HVAC.checkHvacRecirculationCapabilities` → WRITE/READ_WRITE | `getHvacRecirculationState(area)` | `setHvacRecirculation(area, recirc)` 모든 영역 | `CAR_CLIMATE` / `CONTROL_CAR_CLIMATE` |
| `hvac.cabinTemp` | `HVAC.checkHvacTemperatureCurrentCapabilities` → READ/READ_WRITE | `getHvacTemperatureCurrent(area, CELSIUS)` (property `HVAC_TEMPERATURE_CURRENT`) | - | `CAR_CLIMATE` |
| `seat.heater` | `Seat.checkSeatTemperatureCapabilities` → WRITE/READ_WRITE | `Seat.getHvacSeatTemperature(area)` | `Seat.setHvacSeatTemperature(area, level)` | `CAR_SEATS` / `CONTROL_CAR_SEATS` |
| `steeringwheel.heater` | `SteeringWheel.checkHvacSteeringWheelHeatCapabilities` → WRITE/READ_WRITE | `getHvacSteeringWheelHeatState()` | `setHvacSteeringWheelHeat(level)` | `CAR_CLIMATE` / `CONTROL_CAR_CLIMATE` |
| `sidemirror.heater` | `SideMirror.checkHvacSideMirrorHeatCapabilities` → WRITE/READ_WRITE | `getHvacSideMirrorHeatState(area)` | `setHvacSideMirrorHeat(area, level)` 모든 영역 | `CAR_MIRRORS` / `CONTROL_CAR_MIRRORS` |
| `light.ambient` | `Light.checkAmbientLightSwitchCapabilities` → WRITE/READ_WRITE, ON·OFF 지원 (색·밝기는 `checkAmbientLightBrightnessColorCapabilities`, `checkAmbientLightBrightnessCapabilities`로 따로 판정, 없으면 건너뜀) | `getAmbientLightSwitch`, `getAmbientLightBrightnessColor`, `getAmbientLightBrightness` | `setAmbientLightSwitch(ON/OFF)` → `setAmbientLightBrightnessColor(ARGB)` → `setAmbientLightBrightness` | `CAR_LIGHTS` / `CONTROL_CAR_LIGHTS` (*) |
| `ev.battery` | `EvBattery.checkEvBatteryStateOfChargeCapabilities` → READ/READ_WRITE | `getEvBatteryStateOfCharge()` (%) | 구독 `registerEvBatteryStateOfCharge` | `CAR_ENERGY` |
| `fuel.level` | `Fuel.checkFuelPercentCapabilities` → READ/READ_WRITE | `getFuelPercent()` (%) | 구독 `registerFuelPercent` | `CAR_ENERGY` |
| `wiper.state` | `Wiper.checkWiperStateCapabilities` → READ/READ_WRITE, FRONT_WINDSHIELD 영역 우선 | `getCurrentWiperState(area)` | 구독 `registerWiperState(area)` | `CAR_WINDOWS` |
| `navi.waypoint` | 제외(NaviHelper SDK 미포함) → `Unsupported` | - | - | - |
| (주행 상태) | Cap 아님, 항상 시도 | `Gear.getCurrentGearSelectionState()` | 구독 `registerGearSelection` | `CAR_DRIVE` |

(*) `Light.kt` 인터페이스 KDoc은 set도 `CAR_LIGHTS`라고 적혀 있지만, `LightImpl`의 `@RequiresCustomPermission`(실제 런타임 검사)은
`setAmbientLightSwitch/BrightnessColor/Brightness` 모두 `CONTROL_CAR_LIGHTS`다. 둘 다 선언했다.

모든 `pleos.car.permission.*`는 SDK 문서상 protection level normal이라 매니페스트 선언만으로 된다. `CAR_DOORS`, `CAR_INFO`는 V1 검증 화면용으로 이미 있던 것이다.

## 2. 값 매핑

| 항목 | 규칙 |
|---|---|
| 영역 선택 | 읽기: 운전석(`SeatValue.ROW_1_LEFT` 포함) 영역, 없으면 첫 영역. 쓰기(온도·팬·내외기·미러): check가 돌려준 **모든** 영역에 같은 값. 영역 id는 값들의 OR이라 check가 돌려준 SeatArea 객체를 그대로 쓴다. |
| 온도 | ℃. `HvacTemperatureConfigArray`의 min/max/incrementCelsius가 있으면 그 범위와 증분으로 clamp·반올림, 없으면 운전석 AreaBounds로 clamp. DELTA가 오면 현재 설정 온도 + delta (엔진은 원래 SET으로 바꿔서 넘긴다). |
| 팬 | 루틴 0..10 → AreaBounds clamp. 0이 범위 밖이면 최저 단계(팬을 끄지 않는다. HVAC 전원 끄기는 범위 밖). |
| 내외기 | recirc → `true`, fresh → `false`. |
| 시트 열선 | driver = ROW_1_LEFT, passenger = ROW_1_RIGHT (좌핸들 가정). 단계는 [max(0, min), max]. 음수(통풍)는 쓰지 않고, 읽은 음수는 0으로 본다. |
| 스티어링·미러 열선 | on = AreaBounds 최대, off = 0. 읽기: 값 > 0이면 on. |
| 앰비언트 색 (ARGB, 알파 0xFF) | warm `0xFFFF9933`, cool `0xFFCCE6FF`, blue `0xFF1E5BFF`, red `0xFFFF2A2A`. 읽기는 RGB 거리로 가장 가까운 색. |
| 앰비언트 밝기 | low = min + 30%, default = min + 70% (반올림, low는 최소 min+1). 읽기: 중간 이하 low, 초과 default. |
| 앰비언트 on/off | `LightSwitch.ON`/`OFF`. 읽기: ON·AUTOMATIC이면 on. |
| 기어 → DriveState | GEAR_PARK → PARKED, D·R·N·1~9단 → DRIVING, GEAR_UNKNOWN·null·실패 → UNKNOWN(엔진은 주행 취급). CHARGING은 판정 안 함. |
| 와이퍼 | ON → on, OFF·SERVICE → off, OTHER·null → 이벤트 안 보냄. |

순수 매핑 함수는 `PleosMapping.kt`, 테스트는 `app/src/test/java/com/carutine/vehicle/pleos/PleosMappingTest.kt`.

## 3. 연결·오류 처리

- `connect(timeoutMs = 4000)`: `Vehicle(context).initialize()` 후 11개 check를 병렬 호출. timeout 안에 하나라도 SDK 성공 콜백이 오면 `true`, capability를 캐시한다.
- SDK는 서비스 연결 전 호출을 내부 큐에 쌓아 두었다가 연결되면 보낸다(`BaseServiceConnector.sendApi`). 연결이 안 되면 콜백이 아예 오지 않으므로 모든 호출에 타임아웃(get/set 3초)을 둔다.
- `register*`는 권한이 없으면 `SecurityException`을 던진다(`VehicleServiceManager.addListener`). 모두 try/catch로 감싼다.
- `VehicleException`은 AAR에서 난독화돼 있어 `Exception`으로만 받는다.

## 4. 확인 필요 (에뮬레이터 실측)

| # | 항목 | 확인 방법 |
|---|---|---|
| 1 | 각 check의 accessType·영역·AreaBounds 실제 값 (특히 팬 범위, 시트 열선 범위의 음수 여부, 스티어링 열선 단계 수, 앰비언트 밝기 범위) | 로그 태그 `PleosGateway`의 `connect: ok=… caps=…`와 실패 로그 확인. 필요하면 범위를 로그로 추가 |
| 2 | `checkHvacTemperatureCapabilities`의 AreaBounds 단위(℃인지) | configArray가 null일 때만 쓰인다 |
| 3 | `register*` 직후 현재값 이벤트가 오는지 | 오지 않을 수 있어 등록 직후 get을 한 번 더 부른다(중복 이벤트는 엔진 TriggerDetector가 걸러야 함) |
| 4 | 기어 리스너가 에뮬레이터에서 동작하는지 | Virtual Console 기어 변경. 안 되면 계속 UNKNOWN → UI가 주행 제한 상태로 남는다 |
| 5 | 앰비언트 색이 ARGB 그대로 반영되는지, 차량이 팔레트로 반올림하는지 | Virtual Console 앰비언트 카드 |
| 6 | 운전석 영역이 ROW_1_LEFT인지(우핸들 차종 아님) | 시트 열선 driver/passenger 적용 결과 |
| 7 | 에뮬레이터에 위치 provider(GPS)가 있는지, 모의 위치 주입 방법 | `adb emu geo fix` 또는 Virtual Console |
| 8 | release(R8) 빌드에서 SDK의 권한 검사(`PermissionUtil`, kotlin-reflect로 Impl 애노테이션 조회)가 정상 동작하는지 | release APK로 check 호출. `Permission metadata not found` 오류가 나면 keep 규칙 필요 |
| 9 | Vehicle 서비스 최소 버전(SDK 매니페스트 `min_version_name=2.3.13`)과 에뮬레이터 서비스 버전 | 연결 실패 시 확인 |

## 5. 반영 확인·HVAC 전원·진단 로그 (fe/vehicle-apply-verify)

에뮬레이터에서 "앱은 성공으로 표시되는데 차량 값이 안 바뀜", "와이퍼를 켜도 자동 실행 안 됨"이 보고되어 추가했다.

- **반영 확인(read-back)**: SDK set의 onComplete는 `setPropertyAsync`의 `ISetPropertyResult.onResult`일 뿐이라(`HVACImpl.setHvacFanSpeed` 등) 값 반영을 보장하지 않는다.
  set 뒤 300ms 간격으로 최대 5번(1.5초) 다시 읽어 목표값이면 `Success`, 끝내 다르면 `Failed("차량에 반영되지 않음(현재값 X)")`.
  읽기 접근이 없는(access WRITE 전용) 항목만 확인을 생략한다. 온도는 증분의 절반까지 오차 허용, 앰비언트 색은 가장 가까운 루틴 색이 같으면 통과.
- **HVAC 전원**: hvac_temp/fan/air 전에 `checkHvacPowerCapabilities` 영역마다 `getHvacPowerState`를 읽고, false면 `setHvacPower(true)` 후 켜졌는지 확인한다.
  권한은 기존 `CAR_CLIMATE`/`CONTROL_CAR_CLIMATE`. 열선(시트·스티어링·미러)과 앰비언트가 HVAC 전원·시동에 의존하는지는 SDK 소스에 언급이 없다(확인 필요, 진단 로그로 판단).
- **시동 상태**: `IgnitionState`는 `@hide` 열거형만 있고 조회 API가 없어 기록하지 않는다.
- **영역**: 모든 get/set은 check가 돌려준 area 객체를 그대로 쓴다. SDK는 area를 좌석 비트 OR(`ImplementUtil.getAreaId`)로 보내므로 임의로 만든 area는 다른 영역이 된다.
  각 check의 영역·범위는 진단 로그(`hvac.temp access=… areas=[ROW_1_LEFT|…:17.0..27.0]`)에 남는다. 영역이 비면(GLOBAL 등) SDK가 `areaId is Invalid`로 실패시키므로 조용히 무시되지는 않는다.
- **와이퍼**: `registerWiperState(WindowArea, WiperStateListener)`, 값은 `WiperState`(OTHER·OFF·ON·SERVICE). ON만 켜짐, OTHER면 `getCurrentWiperSwitch`로 보조 판단(OFF 외 켜짐).
  등록 직후 현재값을 읽어 기준선(보통 OFF)을 먼저 보내고, 리스너 콜백이 안 오는 경우를 대비해 1초마다 현재값을 읽는다. 값이 바뀔 때만 방출(`WiperSignal`, 테스트 `WiperSignalTest`).
- **기준선 유실 수정**: 앱의 `SharedGateway`가 `shareIn(Eagerly, replay = 0)`이라 엔진이 구독하기 전에 나간 첫 값(와이퍼 OFF 기준선)이 사라질 수 있었다.
  그러면 엔진 TriggerDetector가 첫 ON을 기준선으로만 쓰고 무시한다. `replay = 16`으로 바꾸고 `SharedGatewayTest`로 고정했다.
- **진단 로그**: `PleosDiagnostics.lines()`(최근 200줄, 메모리). 모든 SDK 호출의 이름·area·값·onComplete/onFailed/timeout·소요 ms, check 결과, HVAC 전원, 반영 확인, 리스너 등록, 와이퍼 원시값→매핑→방출. 좌표는 남기지 않는다. 설정 화면 "최근 차량 호출 로그"에서 본다.
