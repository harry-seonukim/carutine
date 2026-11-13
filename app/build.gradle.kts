plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.carutine.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.carutine.app"
        minSdk = 28
        targetSdk = 37
        versionCode = 2
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 갤러리·공유 서버 주소. 비어 있으면 앱은 오프라인 모드(시드 갤러리, 공유 미리보기만)로 동작한다.
        // CI/로컬에서 환경 변수 CARUTINE_API_BASE_URL 또는 gradle 속성 carutine.apiBaseUrl로 넣는다.
        val apiBaseUrl = (System.getenv("CARUTINE_API_BASE_URL")
            ?: (project.findProperty("carutine.apiBaseUrl") as String?) ?: "").trim().trimEnd('/')
        buildConfigField("String", "API_BASE_URL", "\"" + apiBaseUrl + "\"")
    }

    // release 서명: CI는 GitHub Secrets에서 받은 keystore를 환경 변수로 넘긴다.
    // keystore가 없으면(로컬 빌드) debug 키로 서명해 설치 가능한 APK를 만든다.
    val releaseKeystore = System.getenv("CARUTINE_KEYSTORE")?.let { file(it) }?.takeIf { it.exists() }
    signingConfigs {
        if (releaseKeystore != null) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = System.getenv("CARUTINE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("CARUTINE_KEY_ALIAS")
                keyPassword = System.getenv("CARUTINE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = if (releaseKeystore != null) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
            // R8 최적화 끔: 일부 패키지만 최적화(packageScope)하면 앱 클래스가 최적화된 kotlin 클래스를 상속할 때
            // IllegalAccessError로 시작 즉시 죽는다(에뮬레이터 스모크 테스트에서 확인). Pleos SDK 권한 메타데이터 보호도 겸한다.
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.pleos.vehicle)
    // 루틴 엔진: 코루틴(Flow·Mutex), 계약 JSON(JsonElement 트리 API만 사용 → 직렬화 플러그인 불필요)
    // (루트 gradle/libs.versions.toml은 공용 파일이라 담당 폴더 규칙에 따라 app/ 안에서 좌표로 선언)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}