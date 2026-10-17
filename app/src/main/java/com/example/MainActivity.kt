package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.MainTab
import com.example.ui.WisalViewModel
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.WisalBottomBar
import com.example.ui.components.WisalTopBar
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StoryViewerScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.WisalTheme

class MainActivity : ComponentActivity() {
    private val viewModel: WisalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            WisalTheme(darkTheme = isDarkMode) {
                // Enforce Right-to-Left (RTL) Arabic layout
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    WisalApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun WisalApp(viewModel: WisalViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeConversationMessages.collectAsStateWithLifecycle()

    val activeCommentPost by viewModel.activeCommentPost.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()

    // Android Hardware / Gesture Back button handler
    BackHandler(enabled = currentScreen != AppScreen.Splash && currentScreen != AppScreen.Auth) {
        viewModel.navigateBack()
    }

    when (val screen = currentScreen) {
        is AppScreen.Splash -> {
            SplashScreen(
                onTimeout = {
                    viewModel.navigateTo(AppScreen.Main)
                }
            )
        }

        is AppScreen.Auth -> {
            AuthScreen(
                onAuthSuccess = {
                    viewModel.login()
                }
            )
        }

        is AppScreen.ChatList -> {
            ChatListScreen(
                conversations = conversations,
                onConversationClick = { conv -> viewModel.openChat(conv) },
                onBack = { viewModel.navigateBack() }
            )
        }

        is AppScreen.ChatDetail -> {
            ChatDetailScreen(
                conversationId = screen.conversationId,
                participantName = screen.name,
                participantAvatar = screen.avatar,
                messages = activeMessages,
                onSendMessage = { text ->
                    viewModel.sendMessage(screen.conversationId, text, screen.name)
                },
                onBack = { viewModel.navigateBack() }
            )
        }

        is AppScreen.StoryViewer -> {
            StoryViewerScreen(
                stories = stories,
                initialIndex = screen.storyIndex,
                onClose = { viewModel.navigateBack() },
                onLikeStory = { storyId -> viewModel.likeStory(storyId) }
            )
        }

        is AppScreen.Settings -> {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onLogout = { viewModel.logout() },
                onBack = { viewModel.navigateBack() }
            )
        }

        is AppScreen.EditProfile -> {
            EditProfileScreen(
                currentUser = currentUser,
                onSave = { name, handle, bio ->
                    viewModel.updateProfile(name, handle, bio)
                },
                onBack = { viewModel.navigateBack() }
            )
        }

        is AppScreen.Main -> {
            Scaffold(
                topBar = {
                    if (selectedTab == MainTab.HOME) {
                        WisalTopBar(
                            onSearchClick = { viewModel.selectTab(MainTab.DISCOVER) },
                            onNotificationsClick = { viewModel.selectTab(MainTab.NOTIFICATIONS) },
                            onChatClick = { viewModel.navigateTo(AppScreen.ChatList) },
                            unreadNotificationsCount = notifications.count { !it.isRead },
                            unreadChatsCount = conversations.count { it.unreadCount > 0 }
                        )
                    }
                },
                bottomBar = {
                    WisalBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab -> viewModel.selectTab(tab) },
                        unreadNotifications = notifications.count { !it.isRead }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                posts = posts,
                                stories = stories,
                                onLikePost = { post -> viewModel.togglePostLike(post) },
                                onSavePost = { post -> viewModel.togglePostSave(post) },
                                onCommentPost = { post -> viewModel.openCommentsFor(post) },
                                onStoryClick = { index -> viewModel.openStoryViewer(index) },
                                onAddStory = { caption -> viewModel.addStory(caption) },
                                onDeletePost = { postId -> viewModel.deletePost(postId) },
                                onUserClick = { viewModel.selectTab(MainTab.PROFILE) }
                            )
                        }

                        MainTab.DISCOVER -> {
                            DiscoverScreen(
                                users = allUsers,
                                posts = posts,
                                onToggleFollow = { u -> viewModel.toggleFollowUser(u) },
                                onLikePost = { p -> viewModel.togglePostLike(p) },
                                onCommentPost = { p -> viewModel.openCommentsFor(p) },
                                onSavePost = { p -> viewModel.togglePostSave(p) }
                            )
                        }

                        MainTab.CREATE_POST -> {
                            CreatePostScreen(
                                currentUser = currentUser,
                                onPublish = { content, img ->
                                    viewModel.createPost(content, img)
                                },
                                onCancel = {
                                    viewModel.selectTab(MainTab.HOME)
                                }
                            )
                        }

                        MainTab.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onMarkAllRead = { viewModel.markAllNotificationsRead() }
                            )
                        }

                        MainTab.PROFILE -> {
                            ProfileScreen(
                                user = currentUser,
                                posts = posts,
                                onEditProfileClick = { viewModel.navigateTo(AppScreen.EditProfile) },
                                onSettingsClick = { viewModel.navigateTo(AppScreen.Settings) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Active Comments Bottom Sheet (available globally when clicking on comments of any post)
    activeCommentPost?.let { post ->
        CommentBottomSheet(
            post = post,
            comments = activeComments,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { text -> viewModel.addComment(post.id, text) }
        )
    }
}
