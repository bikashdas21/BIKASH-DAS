package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.IOException

enum class VoiceRecordState {
    IDLE,
    RECORDING,
    RECORDED_PREVIEW
}

class VoiceEngine(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentRecordFile: File? = null

    private val _recordState = MutableStateFlow(VoiceRecordState.IDLE)
    val recordState: StateFlow<VoiceRecordState> = _recordState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _recordedDurationSec = MutableStateFlow(0)
    val recordedDurationSec: StateFlow<Int> = _recordedDurationSec.asStateFlow()

    fun startRecording(): Boolean {
        try {
            stopPlaying()
            val outputDir = File(context.cacheDir, "voice_notes").apply { mkdirs() }
            val file = File(outputDir, "rec_${System.currentTimeMillis()}.m4a")
            currentRecordFile = file

            val rec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            rec.setAudioSource(MediaRecorder.AudioSource.MIC)
            rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            rec.setAudioEncodingBitRate(64000)
            rec.setAudioSamplingRate(44100)
            rec.setOutputFile(file.absolutePath)
            rec.prepare()
            rec.start()

            recorder = rec
            _recordState.value = VoiceRecordState.RECORDING
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            _recordState.value = VoiceRecordState.IDLE
            return false
        }
    }

    fun stopRecording(): File? {
        return try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            _recordState.value = VoiceRecordState.RECORDED_PREVIEW
            currentRecordFile
        } catch (e: Exception) {
            cancelRecording()
            null
        }
    }

    fun cancelRecording() {
        try {
            recorder?.stop()
        } catch (e: Exception) {
            // ignore
        }
        recorder?.release()
        recorder = null
        currentRecordFile?.delete()
        currentRecordFile = null
        _recordState.value = VoiceRecordState.IDLE
        stopPlaying()
    }

    fun playRecordedPreview() {
        val file = currentRecordFile ?: return
        playFile(file.absolutePath)
    }

    fun playFile(filePath: String) {
        try {
            stopPlaying()
            player = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                }
                start()
            }
            _isPlaying.value = true
        } catch (e: IOException) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun stopPlaying() {
        try {
            if (player?.isPlaying == true) {
                player?.stop()
            }
            player?.release()
            player = null
        } catch (e: Exception) {
            // ignore
        }
        _isPlaying.value = false
    }

    fun getRecordedFile(): File? = currentRecordFile
}
