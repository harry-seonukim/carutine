package com.carutine.app

import android.app.Application

/** 루틴 엔진은 앱 프로세스에 하나만 둔다(화면 회전·창 크기 변경과 무관하게 자동 실행이 이어지도록). */
class CarutineApp : Application() {
    lateinit var controller: AppController
        private set

    override fun onCreate() {
        super.onCreate()
        controller = AppController(this)
    }
}
