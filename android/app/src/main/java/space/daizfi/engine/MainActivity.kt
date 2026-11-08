package space.daizfi.engine

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import io.livekit.android.LiveKit
import io.livekit.android.room.Room
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.screencapture.ScreenCaptureParams
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var serverUrl: EditText
    private lateinit var accessToken: EditText
    private lateinit var roomName: EditText
    private lateinit var status: TextView
    private lateinit var start: Button
    private lateinit var stop: Button
    private var room: Room? = null

    private val capturePermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        if (result.resultCode != Activity.RESULT_OK || data == null) {
            setStatus("Screen capture permission was not granted.")
            start.isEnabled = true
            stopKeepAlive()
            disconnect()
            return@registerForActivityResult
        }
        lifecycleScope.launch {
            try {
                val notification = buildCaptureNotification()
                val params = ScreenCaptureParams(
                    mediaProjectionPermissionResultData = data,
                    notificationId = 3322,
                    notification = notification,
                    onStop = { runOnUiThread {
                        setStatus("Android ended screen capture. Start a new session to share again.")
                        start.isEnabled = true
                        stop.isEnabled = false
                    } }
                )
                val published = room?.localParticipant?.setScreenShareEnabled(true, params) ?: false
                check(published) { "LiveKit did not start screen capture." }
                check(room?.localParticipant?.getTrackPublication(Track.Source.SCREEN_SHARE)?.track != null) {
                    "Screen track was not published."
                }
                setStatus("DAIZFI IS LIVE · Screen track published. Switch to the screen you want to share.")
                start.isEnabled = false
                stop.isEnabled = true
            } catch (e: Exception) {
                setStatus("Could not start screen share: ${e.message}")
                start.isEnabled = true
                stop.isEnabled = false
                stopKeepAlive()
                disconnect()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(26, 38, 26, 28)
            setBackgroundColor(android.graphics.Color.rgb(5, 12, 8))
        }
        fun label(value: String, size: Float, color: Int = 0xfff5f8f6.toInt()) =
            TextView(this).apply { text = value; textSize = size; setTextColor(color); gravity = Gravity.CENTER }
        root.addView(label("DAIZFI ENGINE", 27f, 0xfff4c542.toInt()))
        root.addView(label("SSA · SCREEN / STREAM / DEVICE", 10f, 0xff20e17c.toInt()))
        serverUrl = EditText(this).apply {
            hint = "LiveKit URL (wss://your-project.livekit.cloud)"
            setSingleLine()
            setTextColor(-1); setHintTextColor(0xff83928a.toInt())
        }
        root.addView(serverUrl, ViewGroup.LayoutParams(-1, 58))
        roomName = EditText(this).apply {
            hint = "Space / room name"
            setSingleLine()
            setText("birthday-space")
            setTextColor(-1); setHintTextColor(0xff83928a.toInt())
        }
        root.addView(roomName, ViewGroup.LayoutParams(-1, 58))
        accessToken = EditText(this).apply {
            hint = "Publisher access token"
            setSingleLine()
            inputType = 129
            setTextColor(-1); setHintTextColor(0xff83928a.toInt())
        }
        root.addView(accessToken, ViewGroup.LayoutParams(-1, 58))
        status = label("Enter LiveKit connection details. Then start the engine.", 13f, 0xffb5c4ba.toInt())
        status.setPadding(0, 24, 0, 24)
        root.addView(status, ViewGroup.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT))
        start = Button(this).apply {
            text = "START SSA"; textSize = 18f
            setOnClickListener { connectAndCapture() }
        }
        root.addView(start, LinearLayout.LayoutParams(-1, 64).apply { bottomMargin = 12 })
        stop = Button(this).apply {
            text = "STOP ENGINE"; textSize = 16f; isEnabled = false
            setOnClickListener { stopBroadcast() }
        }
        root.addView(stop, LinearLayout.LayoutParams(-1, 58))
        root.addView(label("DaizFi.Space commands. DaizFi APK executes.", 11f, 0xff83928a.toInt()).apply {
            setPadding(0, 28, 0, 0)
        })
        setContentView(root)
    }

    private fun connectAndCapture() {
        val url = serverUrl.text.toString().trim()
        val token = accessToken.text.toString().trim()
        if (!url.startsWith("wss://") || token.isBlank() || roomName.text.isNullOrBlank()) {
            setStatus("Enter a valid wss:// LiveKit URL, room name and publisher token.")
            return
        }
        start.isEnabled = false
        startKeepAlive()
        setStatus("Connecting to LiveKit…")
        lifecycleScope.launch {
            try {
                val liveRoom = LiveKit.create(applicationContext)
                room = liveRoom
                liveRoom.connect(url, token)
                val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                setStatus("Connected. Approve Android's screen-sharing prompt.")
                capturePermission.launch(manager.createScreenCaptureIntent())
            } catch (e: Exception) {
                setStatus("Connection failed: ${e.message}")
                start.isEnabled = true
                stopKeepAlive()
                disconnect()
            }
        }
    }

    private fun buildCaptureNotification(): android.app.Notification {
        val channelId = "daizfi_screen_capture"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(NotificationChannel(channelId, "DaizFi screen sharing", NotificationManager.IMPORTANCE_LOW))
        }
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.presence_video_online)
            .setContentTitle("DaizFi is sharing your screen")
            .setContentText("Your screen stream is active. Return to DaizFi to stop.")
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
    }

    private fun startKeepAlive() {
        val intent = Intent(this, DaizFiKeepAliveService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
    }

    private fun stopKeepAlive() { stopService(Intent(this, DaizFiKeepAliveService::class.java)) }

    private fun stopBroadcast() {
        lifecycleScope.launch {
            try { room?.localParticipant?.setScreenShareEnabled(false) } catch (_: Exception) {}
            disconnect()
            stopKeepAlive()
            start.isEnabled = true
            stop.isEnabled = false
            setStatus("DaizFi engine stopped. Device is offline.")
        }
    }

    private fun disconnect() {
        lifecycleScope.launch {
            try { room?.disconnect() } catch (_: Exception) {}
            room = null
        }
    }

    private fun setStatus(message: String) { runOnUiThread { status.text = message } }
}
