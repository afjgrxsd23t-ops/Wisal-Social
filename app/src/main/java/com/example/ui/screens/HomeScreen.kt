package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.ui.components.PostCard
import com.example.ui.components.StoryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    posts: List<Post>,
    stories: List<Story>,
    onLikePost: (Post) -> Unit,
    onSavePost: (Post) -> Unit,
    onCommentPost: (Post) -> Unit,
    onStoryClick: (Int) -> Unit,
    onAddStory: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onUserClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    var selectedPostForOptions by remember { mutableStateOf<Post?>(null) }
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var newStoryCaption by remember { mutableStateOf("") }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                delay(1000)
                isRefreshing = false
                Toast.makeText(context, "تم تحديث المنشورات بنجاح", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Stories Horizontal Tray
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(stories) { index, story ->
                            StoryItem(
                                name = story.authorName,
                                avatarName = story.authorAvatar,
                                isMine = story.isMine,
                                isViewed = story.isViewed,
                                onClick = {
                                    if (story.isMine) {
                                        showAddStoryDialog = true
                                    } else {
                                        onStoryClick(index)
                                    }
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // 2. Posts Feed
            if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد منشورات حاليًا. كن أول من يشارك لحظاته!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${post.authorName} عبر وِصال:\n${post.content}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة المنشور"))
                        },
                        onSaveClick = {
                            onSavePost(post)
                            val msg = if (!post.isSaved) "تم حفظ المنشور في المفضلة" else "تمت إزالة المنشور من المفضلة"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onUserClick = onUserClick,
                        onOptionsClick = { selectedPostForOptions = post }
                    )
                }
            }
        }
    }

    // Post Options Dialog
    selectedPostForOptions?.let { post ->
        AlertDialog(
            onDismissRequest = { selectedPostForOptions = null },
            title = { Text(text = "خيارات المنشور") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "منشور وِصال: ${post.content}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "مشاركة"))
                                selectedPostForOptions = null
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("مشاركة المنشور")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "تم إرسال البلاغ لمراجعة المحتوى", Toast.LENGTH_SHORT).show()
                                selectedPostForOptions = null
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("إبلاغ عن محتوى غير لائق")
                    }

                    if (post.authorHandle == "@ahmed123") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDeletePost(post.id)
                                    Toast.makeText(context, "تم حذف المنشور", Toast.LENGTH_SHORT).show()
                                    selectedPostForOptions = null
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("حذف من حسابي", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPostForOptions = null }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Add Story Dialog
    if (showAddStoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddStoryDialog = false },
            title = { Text("إضافة قصة جديدة 📸") },
            text = {
                Column {
                    Text("شارك لحظتك اليومية مع أصدقائك في وِصال:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newStoryCaption,
                        onValueChange = { newStoryCaption = it },
                        placeholder = { Text("اكتب وصفًا للقصة...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newStoryCaption.isNotBlank()) {
                            onAddStory(newStoryCaption.trim())
                            newStoryCaption = ""
                            showAddStoryDialog = false
                            Toast.makeText(context, "تمت إضافة قصتك بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("نشر القصة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStoryDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
