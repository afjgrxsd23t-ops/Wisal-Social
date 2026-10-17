package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.Comment
import com.example.data.model.Conversation
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.UserProfile
import com.example.data.repository.WisalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Splash : AppScreen()
    object Auth : AppScreen()
    object Main : AppScreen()
    object ChatList : AppScreen()
    data class ChatDetail(val conversationId: String, val name: String, val avatar: String) : AppScreen()
    data class StoryViewer(val storyIndex: Int) : AppScreen()
    object Settings : AppScreen()
    object EditProfile : AppScreen()
}

enum class MainTab {
    HOME, DISCOVER, CREATE_POST, NOTIFICATIONS, PROFILE
}

class WisalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WisalRepository(application)

    // Navigation & Screen Stack
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    // Preferences
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Data Flows from Repository
    val posts: StateFlow<List<Post>> = repository.getPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<Story>> = repository.getStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<Conversation>> = repository.getConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserProfile?> = repository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserProfile>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter & Search states
    val searchQuery = MutableStateFlow("")
    val notificationFilter = MutableStateFlow("الكل") // "الكل", "متابعات", "إعجابات", "تعليقات"
    val discoverFilter = MutableStateFlow("الكل") // "الكل", "أشخاص", "منشورات", "مجموعات"

    // Active Comments Bottom Sheet
    private val _activeCommentPost = MutableStateFlow<Post?>(null)
    val activeCommentPost: StateFlow<Post?> = _activeCommentPost.asStateFlow()

    private val _activeComments = MutableStateFlow<List<Comment>>(emptyList())
    val activeComments: StateFlow<List<Comment>> = _activeComments.asStateFlow()

    // Active Chat detail
    private val _activeConversationMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeConversationMessages: StateFlow<List<ChatMessage>> = _activeConversationMessages.asStateFlow()

    // Screen navigation actions
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            is AppScreen.ChatDetail -> _currentScreen.value = AppScreen.ChatList
            is AppScreen.ChatList, is AppScreen.Settings, is AppScreen.EditProfile, is AppScreen.StoryViewer -> {
                _currentScreen.value = AppScreen.Main
            }
            is AppScreen.Auth -> {
                // Stay on Auth or exit
            }
            else -> {
                if (_selectedTab.value != MainTab.HOME) {
                    _selectedTab.value = MainTab.HOME
                }
            }
        }
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
        _currentScreen.value = AppScreen.Main
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentScreen.value = AppScreen.Auth
    }

    fun login() {
        _isLoggedIn.value = true
        _currentScreen.value = AppScreen.Main
    }

    // Post Actions
    fun togglePostLike(post: Post) {
        viewModelScope.launch {
            repository.toggleLike(post)
        }
    }

    fun togglePostSave(post: Post) {
        viewModelScope.launch {
            repository.toggleSave(post)
        }
    }

    fun createPost(content: String, imageResName: String?) {
        viewModelScope.launch {
            repository.createPost(content, imageResName)
            _selectedTab.value = MainTab.HOME
            _currentScreen.value = AppScreen.Main
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

    // Comment Actions
    fun openCommentsFor(post: Post) {
        _activeCommentPost.value = post
        viewModelScope.launch {
            repository.getComments(post.id).collect {
                _activeComments.value = it
            }
        }
    }

    fun closeComments() {
        _activeCommentPost.value = null
        _activeComments.value = emptyList()
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(postId, text)
        }
    }

    // Story Actions
    fun openStoryViewer(index: Int) {
        val s = stories.value.getOrNull(index) ?: return
        viewModelScope.launch {
            repository.markStoryViewed(s.id)
        }
        _currentScreen.value = AppScreen.StoryViewer(index)
    }

    fun likeStory(storyId: String) {
        viewModelScope.launch {
            repository.likeStory(storyId)
        }
    }

    fun addStory(caption: String) {
        viewModelScope.launch {
            repository.addStory(caption)
        }
    }

    // Chat Actions
    fun openChat(conversation: Conversation) {
        _currentScreen.value = AppScreen.ChatDetail(conversation.id, conversation.participantName, conversation.participantAvatar)
        viewModelScope.launch {
            repository.getMessages(conversation.id).collect {
                _activeConversationMessages.value = it
            }
        }
    }

    fun sendMessage(conversationId: String, text: String, participantName: String) {
        viewModelScope.launch {
            repository.sendMessage(conversationId, text, participantName)
        }
    }

    // Notifications
    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    // Profile & Following
    fun updateProfile(name: String, handle: String, bio: String) {
        viewModelScope.launch {
            repository.updateProfile(name, handle, bio)
            _currentScreen.value = AppScreen.Main
        }
    }

    fun toggleFollowUser(user: UserProfile) {
        viewModelScope.launch {
            repository.toggleFollow(user.id, user.isFollowed)
        }
    }
}
