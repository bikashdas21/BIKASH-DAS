package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.RelayGold

data class OfflineBulletin(
    val id: String,
    val title: String,
    val category: String,
    val content: String,
    val authorNode: String,
    val timestamp: String,
    val priority: String // "NORMAL", "HIGH", "EMERGENCY"
)

data class OfflineGuide(
    val id: String,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val summary: String,
    val fullText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreedomServicesScreen(
    language: String,
    myNodeId: String,
    onNavigateToChat: () -> Unit,
    onNavigateToFiles: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedGuide by remember { mutableStateOf<OfflineGuide?>(null) }
    var showNewBulletinDialog by remember { mutableStateOf(false) }

    val initialBulletins = remember {
        mutableStateListOf(
            OfflineBulletin(
                id = "BLTN-01",
                title = if (language == "bn") "সৌর চার্জিং স্টেশন খোলা আছে" else "Solar Charging Station Open",
                category = if (language == "bn") "শক্তি" else "Power",
                content = if (language == "bn") "কমিউনিটি নোড ৩ এর পাশে ১২টি ইউএসবি সোলার পোর্ট চালু আছে সকাল ৮টা থেকে বিকাল ৫টা পর্যন্ত।" else "12 USB solar charging ports active near Community Node 3 from 08:00 to 17:00.",
                authorNode = "NODE-SOLAR-03",
                timestamp = "Today 09:15",
                priority = "NORMAL"
            ),
            OfflineBulletin(
                id = "BLTN-02",
                title = if (language == "bn") "বন্যার পানি সতর্কবার্তা - নদী অববাহিকা" else "Flash Flood Alert - River Basin",
                category = if (language == "bn") "জরুরী" else "Emergency",
                content = if (language == "bn") "নদীর তীরবর্তী এলাকা থেকে উঁচু স্থানে আশ্রয় নিন। অফলাইন জিরো-গ্রিড দিয়ে পরিবারের অবস্থান শেয়ার করুন।" else "River levels rising rapidly. Move to higher ground. Share GPS offline via Zero-Grid.",
                authorNode = "NODE-REPEATER-01",
                timestamp = "Today 07:30",
                priority = "EMERGENCY"
            ),
            OfflineBulletin(
                id = "BLTN-03",
                title = if (language == "bn") "অফলাইন ওষুধ তালিকা প্রস্তুত" else "Emergency Medical Supplies Staged",
                category = if (language == "bn") "স্বাস্থ্য" else "Health",
                content = if (language == "bn") "কমিউনিটি সেন্টারে খাবার স্যালাইন ও ব্যথানাশক ওষুধ মজুত রয়েছে।" else "Oral rehydration salts and basic analgesics stocked at local community clinic.",
                authorNode = "NODE-CLINIC-09",
                timestamp = "Yesterday 18:20",
                priority = "HIGH"
            )
        )
    }

    val offlineGuides = remember(language) {
        listOf(
            OfflineGuide(
                id = "GUIDE-01",
                title = if (language == "bn") "ঘূর্ণিঝড় ও বন্যা নিরাপত্তা গাইড" else "Cyclone & Flood Survival Manual",
                category = if (language == "bn") "দুর্যোগ" else "Disaster",
                icon = Icons.Default.Storm,
                summary = if (language == "bn") "বিদ্যুৎ ও মোবাইল নেটওয়ার্ক ছাড়া কিভাবে নিরাপদ থাকবেন।" else "Crucial survival steps during infrastructure blackout.",
                fullText = if (language == "bn")
                    "১. সকল ইলেকট্রনিক ডিভাইস প্লাস্টিক ব্যাগে ওয়াটারপ্রুফ করে রাখুন।\n২. পরিষ্কার খাবার পানি বোতলে সংরক্ষণ করুন।\n৩. উঁচু স্থানে আশ্রয় নিন এবং জিরো-গ্রিড দিয়ে নির্দিষ্ট সময় পরপর নোড সিগন্যাল পিং করুন।\n৪. রাতে সংকেত দেওয়ার জন্য ফ্ল্যাশলাইট বা আয়না ব্যবহার করুন।"
                else
                    "1. Seal battery packs and electronics in waterproof plastic bags.\n2. Store at least 3 liters of purified water per person per day.\n3. Move to marked high ground; emit periodic Zero-Grid beacon pings.\n4. Avoid crossing flood currents higher than knee level."
            ),
            OfflineGuide(
                id = "GUIDE-02",
                title = if (language == "bn") "জরুরী প্রাথমিক চিকিৎসা ও সিপিআর" else "Field First-Aid & CPR Protocol",
                category = if (language == "bn") "স্বাস্থ্য" else "Medical",
                icon = Icons.Default.MedicalServices,
                summary = if (language == "bn") "রক্তপাত বন্ধ ও অচেতন রোগীর তাৎক্ষণিক পরিচর্যা।" else "Bleeding control, shock management, and chest compressions.",
                fullText = if (language == "bn")
                    "১. রক্তপাত বন্ধে সরাসরি ক্ষতস্থানে পরিষ্কার কাপড় দিয়ে শক্ত চাপ দিন।\n২. রোগী শ্বাস না নিলে বুকে প্রতি মিনিটে ১০০-১২০ বার জোর চাপ (সিপিআর) দিন।\n৩. পোড়া স্থানে প্রচুর স্বাভাবিক ঠান্ডা পানি ঢালুন, বরফ দেবেন না।"
                else
                    "1. Apply direct firm pressure to severe bleeding with sterile cloth.\n2. For unresponsive non-breathing casualty: deliver 100-120 compressions/min at 5-6 cm depth.\n3. Keep casualty warm to combat shock. Never give liquids to unconscious persons."
            ),
            OfflineGuide(
                id = "GUIDE-03",
                title = if (language == "bn") "বিদ্যুৎহীন পানি বিশুদ্ধকরণ পদ্ধতি" else "Emergency Water Purification",
                category = if (language == "bn") "বেঁচে থাকা" else "Survival",
                icon = Icons.Default.WaterDrop,
                summary = if (language == "bn") "বন্যার ময়লা পানি নিরাপদ খাবার পানিতে রূপান্তর।" else "Methods to sterilize drinking water without municipal supply.",
                fullText = if (language == "bn")
                    "১. বালি ও কাপড়ের স্তর দিয়ে ঘোলা পানি ফিল্টার করুন।\n২. সম্ভব হলে পানি কমপক্ষে ৩ মিনিট টগবগ করে ফুটান।\n৩. ফুটানো সম্ভব না হলে প্রতি লিটারে ২ ফোঁটা আনসেন্টেড গৃহস্থালি ব্লিচ দিয়ে ৩০ মিনিট রাখুন।"
                else
                    "1. Filter silt through dense multi-layered cotton or sand.\n2. Rolling boil for minimum 3 minutes if fuel exists.\n3. Chemical disinfection: 2 drops of 6% unscented household bleach per liter; wait 30 min."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == "bn") "স্বাধীনতা পরিসেবা" else "Freedom Services",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (language == "bn") "অফলাইন লোকাল ইন্টারনেট লেয়ার" else "Offline-First Mesh Services Layer",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(
                            onClick = { showNewBulletinDialog = true },
                            modifier = Modifier.testTag("btn_post_bulletin")
                        ) {
                            Icon(Icons.Default.AddComment, contentDescription = "Post Bulletin", tint = ElectricCyan)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("freedom_services_screen")
        ) {
            // Service Architecture Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (language == "bn")
                            "এই পরিসেবাগুলি কোনো মোবাইল ডাটা বা কেন্দ্রীয় সার্ভার ছাড়াই লোকাল ডিভাইসের মেশ সংযোগে সক্রিয় থাকে।"
                        else
                            "These services operate over peer radio mesh without mobile data, subscriptions, or central cloud servers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (language == "bn") "নোটিশ বোর্ড" else "Bulletins") },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (language == "bn") "অফলাইন জ্ঞানভাণ্ডার" else "Knowledge Base") },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (language == "bn") "লোকাল হাব" else "Local Hub") },
                    icon = { Icon(Icons.Default.Hub, contentDescription = null) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Bulletins List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(initialBulletins) { bulletin ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = if (bulletin.priority == "EMERGENCY") androidx.compose.foundation.BorderStroke(1.5.dp, EmergencyRed) else null
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = when (bulletin.priority) {
                                                "EMERGENCY" -> EmergencyRed.copy(alpha = 0.2f)
                                                "HIGH" -> RelayGold.copy(alpha = 0.2f)
                                                else -> ElectricCyan.copy(alpha = 0.15f)
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = bulletin.category,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = when (bulletin.priority) {
                                                    "EMERGENCY" -> EmergencyRed
                                                    "HIGH" -> RelayGold
                                                    else -> ElectricCyan
                                                }
                                            )
                                        }

                                        Text(
                                            text = bulletin.timestamp,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = bulletin.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = bulletin.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Source: ${bulletin.authorNode}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = "Flood-Relayed",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MeshGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Offline Survival Guides
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(offlineGuides) { guide ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                onClick = { selectedGuide = guide }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(guide.icon, contentDescription = null, tint = ElectricCyan)
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = guide.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = guide.summary,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )
                                    }

                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Local Hub & Store-and-Forward
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Outbox, contentDescription = null, tint = RelayGold)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (language == "bn") "স্টোর-অ্যান্ড-ফরওয়ার্ড ড্রপবক্স" else "Store-and-Forward Drop-Box",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (language == "bn")
                                        "যেসব নোড বর্তমানে সরাসরি রেডিও সংযোগে নেই, তাদের জন্য মেসেজ স্থানীয় মেমরিতে সংরক্ষিত থাকে। অন্য নোড কাছাকাছি এলে স্বয়ংক্রিয়ভাবে রিলে হবে।"
                                    else
                                        "Holds queued packets for unreachable destination nodes until passing intermediary devices establish line-of-sight contact.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onNavigateToChat,
                                    colors = ButtonDefaults.buttonColors(containerColor = RelayGold)
                                ) {
                                    Text(if (language == "bn") "অফলাইন কিউ দেখুন" else "View Offline Queue", color = Color.Black)
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FolderShared, contentDescription = null, tint = ElectricCyan)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (language == "bn") "লোকাল ফাইল ও মিডিয়া শেয়ারিং" else "Local Mesh File & Media Sharing",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (language == "bn")
                                        "বড় ফাইলকে ছোট চাঙ্কে বিভক্ত করে অফলাইন মেশের মাধ্যমে স্থানান্তর করুন।"
                                    else
                                        "Transfers binary files, offline maps, and voice notes chunk-by-chunk with SHA-256 integrity verification.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onNavigateToFiles,
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                                ) {
                                    Text(if (language == "bn") "ফাইল ট্রান্সফার খুলুন" else "Open File Transfers", color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Guide Full-Text Dialog
    selectedGuide?.let { guide ->
        AlertDialog(
            onDismissRequest = { selectedGuide = null },
            icon = { Icon(guide.icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(36.dp)) },
            title = {
                Text(
                    text = guide.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Category: ${guide.category} • Offline Stored",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = guide.fullText,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedGuide = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Post Bulletin Dialog
    if (showNewBulletinDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewBulletinDialog = false },
            title = { Text(if (language == "bn") "নতুন বুলেটিন প্রকাশ করুন" else "Post Mesh Bulletin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Announcement Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                            initialBulletins.add(
                                0,
                                OfflineBulletin(
                                    id = "BLTN-${System.currentTimeMillis().toString().takeLast(4)}",
                                    title = newTitle,
                                    category = "Community",
                                    content = newContent,
                                    authorNode = myNodeId,
                                    timestamp = "Just Now",
                                    priority = "NORMAL"
                                )
                            )
                            showNewBulletinDialog = false
                        }
                    },
                    enabled = newTitle.isNotBlank() && newContent.isNotBlank()
                ) {
                    Text("Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewBulletinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
