package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.VoiceEngine
import com.example.audio.VoiceRecordState
import com.example.database.Message
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.OfflineGray
import com.example.ui.theme.RelayGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    messages: List<Message>,
    activePeerId: String, // "BROADCAST" or specific Node ID
    activePeerName: String,
    myNodeId: String,
    voiceEngine: VoiceEngine,
    isBangla: Boolean,
    onSendMessage: (text: String) -> Unit,
    onSendVoiceMessage: (audioPath: String, durationSec: Int) -> Unit,
    onSelectChannel: (peerId: String, peerName: String) -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    var isRecordingAudio by remember { mutableStateOf(false) }
    var hasAudioRecorded by remember { mutableStateOf(false) }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val started = voiceEngine.startRecording()
            isRecordingAudio = started
        }
    }

    // Auto-scroll on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("chat_screen")
    ) {
        // Channel Selector: Public Broadcast vs Direct Message
        TabRow(
            selectedTabIndex = if (activePeerId == "BROADCAST") 0 else 1,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activePeerId == "BROADCAST",
                onClick = { onSelectChannel("BROADCAST", if (isBangla) "পাবলিক মেশ ব্রডকাস্ট" else "Public Mesh Broadcast") },
                text = {
                    Text(
                        text = if (isBangla) "পাবলিক মেশ ব্রডকাস্ট" else "Public Mesh Broadcast",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
            Tab(
                selected = activePeerId != "BROADCAST",
                onClick = { /* Keep active or pick from nearby */ },
                text = {
                    Text(
                        text = if (activePeerId == "BROADCAST") (if (isBangla) "ব্যক্তিগত বার্তা" else "Direct Chat") else activePeerName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }

        // Active Channel Subheader Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activePeerId == "BROADCAST") {
                        if (isBangla) "📡 মেশ ব্রডকাস্ট (কাছাকাছি সকল নোড গ্রহণ করবে)"
                        else "📡 Mesh Broadcast (All nearby nodes will receive)"
                    } else {
                        if (isBangla) "🔒 সরাসরি এনক্রিপ্টেড চ্যাট: $activePeerName"
                        else "🔒 Direct Encrypted Chat: $activePeerName"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isBangla) "অফলাইন মোড" else "Offline Mode",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MeshGreen
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.messageId }) { msg ->
                val isMine = msg.senderId == myNodeId
                ChatMessageBubble(
                    message = msg,
                    isMine = isMine,
                    isBangla = isBangla,
                    onPlayAudio = { path ->
                        voiceEngine.playFile(path)
                    }
                )
            }
        }

        // Quick Emoji Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("👋", "👍", "🚨", "📍", "🔋", "📶", "❤️", "🤝").forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { inputText += emoji }
                        .padding(4.dp)
                )
            }
        }

        // Voice Recording Panel when active
        AnimatedVisibility(visible = isRecordingAudio || hasAudioRecorded) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(EmergencyRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRecordingAudio) (if (isBangla) "ভয়েস রেকর্ড হচ্ছে…" else "Recording voice…")
                            else (if (isBangla) "ভয়েস প্রিভিউ প্রস্তুত" else "Voice preview ready"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row {
                        if (isRecordingAudio) {
                            IconButton(onClick = {
                                val recorded = voiceEngine.stopRecording()
                                isRecordingAudio = false
                                hasAudioRecorded = (recorded != null)
                            }) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop", tint = EmergencyRed)
                            }
                        } else if (hasAudioRecorded) {
                            IconButton(onClick = { voiceEngine.playRecordedPreview() }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Preview", tint = MeshGreen)
                            }
                            IconButton(onClick = {
                                val file = voiceEngine.getRecordedFile()
                                if (file != null) {
                                    onSendVoiceMessage(file.absolutePath, 4)
                                }
                                hasAudioRecorded = false
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = ElectricCyan)
                            }
                        }

                        IconButton(onClick = {
                            voiceEngine.cancelRecording()
                            isRecordingAudio = false
                            hasAudioRecorded = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = OfflineGray)
                        }
                    }
                }
            }
        }

        // Bottom Input Area
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Microphone Record Button
                IconButton(
                    onClick = {
                        val hasMic = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!hasMic) {
                            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            if (!isRecordingAudio && !hasAudioRecorded) {
                                val ok = voiceEngine.startRecording()
                                isRecordingAudio = ok
                            }
                        }
                    },
                    modifier = Modifier.testTag("btn_mic")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Record",
                        tint = if (isRecordingAudio) EmergencyRed else MaterialTheme.colorScheme.primary
                    )
                }

                // Text Input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (isBangla) "বার্তা লিখুন (অফলাইনে যাবে)…" else "Type offline message…",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_message"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("btn_send_message")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: Message,
    isMine: Boolean,
    isBangla: Boolean,
    onPlayAudio: (String) -> Unit
) {
    val bubbleColor = when {
        message.isSos -> EmergencyRed.copy(alpha = 0.2f)
        isMine -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when {
        message.isSos -> EmergencyRed
        isMine -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("message_${message.messageId}"),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        if (!isMine) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMine) 16.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.isSos) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = EmergencyRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "জরুরি সংকেত (SOS)" else "EMERGENCY SOS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = EmergencyRed
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (message.isVoice && !message.mediaUri.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onPlayAudio(message.mediaUri) }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message.plaintextCache,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Text(
                        text = message.plaintextCache,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Footer: Timestamp + Delivery Status
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        when (message.status) {
                            "SENDING" -> Icon(
                                Icons.Default.HourglassEmpty,
                                contentDescription = "Sending",
                                tint = OfflineGray,
                                modifier = Modifier.size(12.dp)
                            )
                            "RELAYING" -> Icon(
                                Icons.Default.Sync,
                                contentDescription = "Relaying",
                                tint = RelayGold,
                                modifier = Modifier.size(12.dp)
                            )
                            "DELIVERED" -> Icon(
                                Icons.Default.DoneAll,
                                contentDescription = "Delivered",
                                tint = MeshGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            "OFFLINE" -> Icon(
                                Icons.Default.Check,
                                contentDescription = "Offline Saved",
                                tint = OfflineGray,
                                modifier = Modifier.size(12.dp)
                            )
                            else -> Icon(
                                Icons.Default.Close,
                                contentDescription = "Failed",
                                tint = EmergencyRed,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
