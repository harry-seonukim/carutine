# Carutine — 카 루틴

**차량판 단축어.** "회사를 나서면 → 추우면 → 핸들·시트 열선을 켜고 한마디 안내"처럼, 차량 상황(장소·배터리·와이퍼·시간·실내 온도)에 맞춰 공조·열선·조명 동작을 묶어 자동/버튼/음성으로 실행하는 Pleos Connect 앱입니다. 다른 사람이 만든 루틴은 **내 차가 지원하는 기능만 골라 보여 주는 갤러리**(Fit-Filtered Sharing)에서 가져옵니다.

- 플랫폼: Pleos Connect (Android Automotive OS), minSdk 28
- 사용 SDK: Pleos Vehicle SDK 2.3.2 (HVAC, Seat, SteeringWheel, SideMirror, Light, EvBattery, Fuel, Wiper, Gear), Android TextToSpeech / SpeechRecognizer

## APK

| 파일 | 설명 |
|---|---|
| [`apk/Carutine_Carutine_release.apk`](apk/Carutine_Carutine_release.apk) | 실행 가능한 release 빌드(서명됨). main에 변경이 합쳐질 때마다 GitHub Actions가 자동으로 다시 빌드해 갱신합니다. |

## 실행 방법

1. Pleos 에뮬레이터(또는 Android 9 이상 기기/에뮬레이터)를 켭니다.
2. APK를 설치합니다: 에뮬레이터 창에 APK 파일을 끌어다 놓거나
   ```bash
   adb install -r apk/Carutine_Carutine_release.apk
   ```
3. 앱 목록에서 **Carutine**을 실행합니다. 화면 상단 배지로 동작 모드를 확인할 수 있습니다.

| 배지 | 조건 | 동작 |
|---|---|---|
| **실제 차량** | Pleos 차량 서비스가 있는 환경(Pleos 에뮬레이터·차량) | Vehicle SDK로 차량 지원 기능을 조회하고 실제로 제어합니다. Virtual Console에서 값(와이퍼, 배터리, 기어 등)을 바꾸면 루틴이 자동 실행됩니다. |
| **시뮬레이터** | 일반 안드로이드 에뮬레이터·기기 | 앱 안의 가상 차량으로 동작합니다. 시뮬레이터 패널의 버튼(회사 출발, 집 도착, 와이퍼 켜짐, 배터리 14% 등)으로 상황을 만들어 모든 흐름을 확인할 수 있습니다. |

### 시연 순서 (5분)
1. 빈 홈 → **루틴 갤러리** → '퇴근길 겨울' 상세(내 차 호환 배지) → **가져오기** → 회사 위치 지정
2. 홈에서 **실행**(버튼 실행) → 결과 배너 → **원래대로**
3. 시뮬레이터 패널: 실내 온도 '추움' → **회사 출발** → 자동 실행 + 음성 안내
4. 차량 사양 전환 → 갤러리 배지가 '일부 동작 제외'로 바뀜(Fit-Filtered Sharing)
5. **주행 중** 토글 → 큰 실행 버튼만 남는 주행 중 화면 → 정차 후 **직접 만들기** → **공유** 미리보기

## 테스트 환경과 필요한 설정
- 별도 계정·서버·API 키가 필요 없습니다. 갤러리는 앱에 포함된 공식 루틴 20개로 동작합니다(서버 미연결 시 공유는 업로드 내용 미리보기까지).
- 권한: 차량 권한(`pleos.car.permission.*`)은 설치 시 부여됩니다. 위치(장소 조건)·마이크(음성 실행)는 실행 중 요청하며, 거부해도 나머지 기능은 동작합니다.
- 확인 환경: GitHub Actions에서 단위 테스트, release 빌드, Android 14 에뮬레이터 스모크 테스트(설치·실행·주요 화면)를 자동 실행합니다.

## 구조

```
app/                 Android 앱 (Kotlin, Jetpack Compose)
  model/             루틴 데이터 계약 v0.1 (트리거·조건·동작, 호환 판정)
  engine/            루틴 엔진 (사건 평가, 충돌 우선순위, 원래대로 10분, 재실행 방지)
  vehicle/pleos/     Pleos Vehicle SDK 연동  ← docs/pleos-sdk-mapping.md
  vehicle/fake/      시뮬레이터용 가상 차량
  app/               화면·저장·차량 연결
backend/             루틴 갤러리 API (Node.js + TypeScript + PostgreSQL) — backend/README.md
docs/                설계 문서
```

## 소스에서 빌드
```bash
./gradlew :app:testDebugUnitTest :app:assembleRelease
```
Android Studio(AGP 9.5, JDK 21+)에서 열어도 됩니다. Pleos SDK는 `settings.gradle.kts`에 등록된 Pleos Maven 저장소에서 받습니다.

## 개인정보
위치 좌표는 기기 안에만 저장하고 서버로 보내지 않습니다. 공유되는 루틴에는 작성자 정보와 좌표가 없고, 장소는 '집/회사' 같은 키로만 표현됩니다.
