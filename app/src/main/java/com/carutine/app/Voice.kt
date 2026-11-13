package com.carutine.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

/** 자동·음성 실행 결과를 읽는 TTS. 엔진의 shouldSpeak가 true일 때만 부른다(버튼 실행은 화면만). */
class Speaker(context: Context) : TextToSpeech.OnInitListener {
    private var ready = false
    private val pending = mutableListOf<String>()
    private val tts: TextToSpeech? = try { TextToSpeech(context.applicationContext, this) } catch (t: Throwable) { null }

    override fun onInit(status: Int) {
        val t = tts ?: return
        if (status != TextToSpeech.SUCCESS) return
        runCatching { t.setLanguage(Locale.KOREAN) }
        ready = true
        pending.forEach { speakNow(it, add = true) }
        pending.clear()
    }

    fun speak(text: String, add: Boolean = false) {
        if (text.isBlank()) return
        if (!ready) { pending += text; return }
        speakNow(text, add)
    }

    private fun speakNow(text: String, add: Boolean) {
        val mode = if (add) TextToSpeech.QUEUE_ADD else TextToSpeech.QUEUE_FLUSH
        runCatching { tts?.speak(text, mode, null, "carutine-${System.nanoTime()}") }
    }

    fun shutdown() { runCatching { tts?.shutdown() } }
}

/**
 * '음성으로 실행': 한 번 듣고 결과 문장 후보를 돌려준다. 인식기가 없는 기기에서는 버튼을 숨긴다.
 * 메인 스레드에서만 만들고 부른다(SpeechRecognizer 제약).
 */
class Listener(private val context: Context) {
    private var recognizer: SpeechRecognizer? = null

    val available: Boolean
        get() = runCatching { SpeechRecognizer.isRecognitionAvailable(context) }.getOrDefault(false)

    fun listen(onPartial: (String) -> Unit, onResult: (List<String>) -> Unit, onError: (String) -> Unit) {
        cancel()
        val r = try { SpeechRecognizer.createSpeechRecognizer(context) } catch (t: Throwable) { null }
        if (r == null) { onError("음성 인식을 쓸 수 없어요"); return }
        recognizer = r
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = onPartial("듣고 있어요…")
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onError(error: Int) {
                onError(
                    when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "잘 듣지 못했어요"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "마이크 권한이 필요해요"
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "음성 인식 서버에 연결하지 못했어요"
                        else -> "음성 인식 오류 ($error)"
                    }
                )
                cancel()
            }
            override fun onResults(results: Bundle?) {
                val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.toList() ?: emptyList()
                onResult(list)
                cancel()
            }
            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { onPartial(it) }
            }
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }
        try { r.startListening(intent) } catch (t: Throwable) { onError("음성 인식을 시작하지 못했어요"); cancel() }
    }

    fun cancel() {
        recognizer?.let { runCatching { it.cancel() }; runCatching { it.destroy() } }
        recognizer = null
    }

    companion object {
        private fun norm(s: String) = s.replace(Regex("[\\s'\"“”‘’.,!?~]"), "").lowercase()

        /** 인식 후보 중 루틴 이름과 가장 잘 맞는 것. 정확히 같음 > 포함 관계(이름 2자 이상). */
        fun <T> match(candidates: List<String>, items: List<T>, name: (T) -> String): T? {
            val c = candidates.map { norm(it) }.filter { it.isNotEmpty() }
            for (cand in c) items.firstOrNull { norm(name(it)) == cand }?.let { return it }
            for (cand in c) {
                items.filter { val n = norm(name(it)); n.length >= 2 && (cand.contains(n) || n.contains(cand) && cand.length >= 2) }
                    .maxByOrNull { norm(name(it)).length }?.let { return it }
            }
            return null
        }

        fun isUndo(candidates: List<String>) = candidates.any { val n = norm(it); n.contains("원래대로") || n.contains("되돌려") }
    }
}
