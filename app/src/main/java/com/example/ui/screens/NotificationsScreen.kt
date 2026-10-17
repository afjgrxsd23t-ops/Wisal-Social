package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.components.WisalAvatar
import com.example.ui.theme.WisalCyanGlow
import com.example.ui.theme.WisalGreen
import com.example.ui.theme.WisalHeartRed
import com.example.ui.theme.WisalRoyalBlue

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onMarkAllRead: () -> Unit
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("الكل") }
    val filters = listOf("الكل", "متابعات", "إعجابات", "تعليقات")

    val filteredList = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            "متابعات" -> notifications.filter { it.type == NotificationType.FOLLOW }
            "إعجابات" -> notifications.filter { it.type == NotificationType.LIKE }
            "تعليقات" -> notifications.filter { it.type == NotificationType.COMMENT }
            else -> notifications
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الإشعارات",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = WisalRoyalBlue
                )
            )

            IconButton(
                onClick = {
                    onMarkAllRead()
                    Toast.makeText(context, "تم تحديد جميع الإشعارات كمقروءة", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("mark_all_read_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "تحديد الكل كمقروء",
                    tint = WisalRoyalBlue
                )
            }
        }

        // Filter chips row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WisalRoyalBlue,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Notifications List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد إشعارات جديدة في هذا القسم",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList, key = { it.id }) { notif ->
                    NotificationRow(item = notif)
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 74.dp, end = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationItem) {
    val (icon, iconColor) = when (item.type) {
        NotificationType.LIKE -> Icons.Default.Favorite to WisalHeartRed
        NotificationType.COMMENT -> Icons.Default.ChatBubble to WisalCyanGlow
        NotificationType.FOLLOW -> Icons.Default.PersonAdd to WisalRoyalBlue
        NotificationType.MENTION -> Icons.Default.Tag to WisalGreen
        NotificationType.GROUP -> Icons.Default.Group to WisalRoyalBlue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!item.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent)
            .clickable { /* Tap notification */ }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("notification_item_${item.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with badge overlay
        Box {
            WisalAvatar(imageName = item.avatar, sizeDp = 48)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.timeAgo,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = WisalRoyalBlue
                )
            )
        }

        if (!item.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(WisalRoyalBlue)
            )
        }
    }
}
