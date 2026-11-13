package com.carutine.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.carutine.app.ui.AppRoot
import com.carutine.app.ui.theme.CarutineTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = (application as CarutineApp).controller
        setContent {
            CarutineTheme {
                AppRoot(
                    c = controller,
                    openDiagnostics = {
                        // V1 검증 화면(기존 spike 패키지 그대로, 런처에서는 뺌)
                        startActivity(Intent(this, com.carutine.spike.MainActivity::class.java))
                    },
                )
            }
        }
    }
}
