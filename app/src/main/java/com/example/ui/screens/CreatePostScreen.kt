package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.components.WisalAvatar
import com.example.ui.theme.WisalRoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    currentUser: UserProfile?,
    onPublish: (String, String?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var postText by remember { mutableStateOf("") }
    var selectedImageName by remember { mutableStateOf<String?>("wisal_city_bg") }
    var privacy by remember { mutableStateOf("عام") }
    var showPrivacyMenu by remember { mutableStateOf(false) }
    var locationTag by remember { mutableStateOf<String?>(null) }

    val sampleMediaOptions = listOf(
        "wisal_city_bg" to "صورة المدينة والغروب",
        "wisal_sarah_story" to "صورة بورتريه",
        "wisal_icon_fg" to "رمز وِصال"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "إنشاء منشور",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "إلغاء")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "إعدادات خصوصية النشر", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Tune, contentDescription = "إعدادات النشر")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            // User Header & Privacy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                WisalAvatar(
                    imageName = currentUser?.avatar ?: "wisal_icon_fg",
                    sizeDp = 48
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = currentUser?.displayName ?: "أحمد محمد",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    // Privacy Chip
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { showPrivacyMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = WisalRoyalBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = privacy, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        DropdownMenu(
                            expanded = showPrivacyMenu,
                            onDismissRequest = { showPrivacyMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("عام 🌍") },
                                onClick = { privacy = "عام"; showPrivacyMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("الأصدقاء فقط 👥") },
                                onClick = { privacy = "الأصدقاء فقط"; showPrivacyMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("خاص بي 🔒") },
                                onClick = { privacy = "خاص بي"; showPrivacyMenu = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post content text field
            OutlinedTextField(
                value = postText,
                onValueChange = { postText = it },
                placeholder = {
                    Text(
                        "ماذا يدور في بالك؟ شارك أفكارك وتجاربك مع مجتمع وِصال...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("create_post_text_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Location tag badge (if added)
            locationTag?.let { loc ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = WisalRoyalBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(loc, style = MaterialTheme.typography.bodySmall.copy(color = WisalRoyalBlue))
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "حذف الموقع",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { locationTag = null }
                    )
                }
            }

            // Media attachment preview strip (matching the grid in mockup)
            Text(
                text = "اختر صورة لإرفاقها بالمنشور:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // "No image" option
                item {
                    Card(
                        modifier = Modifier
                            .size(76.dp)
                            .clickable { selectedImageName = null },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedImageName == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (selectedImageName == null) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WisalRoyalBlue)) else null
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("بدون صورة", fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }

                // Sample image choices
                items(sampleMediaOptions.size) { index ->
                    val (imgKey, label) = sampleMediaOptions[index]
                    val isSelected = selectedImageName == imgKey
                    val resId = remember(imgKey) {
                        val id = context.resources.getIdentifier(imgKey, "drawable", context.packageName)
                        if (id != 0) id else R.drawable.wisal_city_bg
                    }

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) WisalRoyalBlue else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedImageName = imgKey }
                    ) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = label,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Attachment bar: Image, Video, Location, Emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = {
                    Toast.makeText(context, "إرفاق صورة من المعرض", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Image, contentDescription = "إرفاق صورة", tint = WisalRoyalBlue)
                }

                IconButton(onClick = {
                    Toast.makeText(context, "إرفاق مقطع فيديو", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Videocam, contentDescription = "إرفاق فيديو", tint = WisalRoyalBlue)
                }

                IconButton(onClick = {
                    locationTag = "الرياض، المملكة العربية السعودية 📍"
                }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "إضافة موقع", tint = WisalRoyalBlue)
                }

                IconButton(onClick = {
                    postText += " ✨❤️"
                }) {
                    Icon(Icons.Default.Mood, contentDescription = "إضافة تعبير", tint = WisalRoyalBlue)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Publish Button
            Button(
                onClick = {
                    if (postText.isBlank() && selectedImageName == null) {
                        Toast.makeText(context, "الرجاء كتابة نص أو اختيار صورة للنشر", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onPublish(postText.trim(), selectedImageName)
                    Toast.makeText(context, "تم نشر منشورك بنجاح في وِصال!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("publish_post_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WisalRoyalBlue)
            ) {
                Text(
                    text = "نشر",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
