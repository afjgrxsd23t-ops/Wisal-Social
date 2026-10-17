package com.example.data.model

data class Post(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val timeAgo: String,
    val content: String,
    val imageResName: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class Comment(
    val id: String,
    val postId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timeAgo: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Story(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val mediaResName: String,
    val caption: String = "",
    val timeAgo: String = "الآن",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isViewed: Boolean = false,
    val isMine: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class Conversation(
    val id: String,
    val participantName: String,
    val participantAvatar: String,
    val lastMessage: String,
    val timestampStr: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderName: String,
    val text: String,
    val isFromMe: Boolean,
    val timestampStr: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, MENTION, GROUP
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val avatar: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String,
    val displayName: String,
    val handle: String,
    val avatar: String,
    val coverImage: String? = null,
    val bio: String,
    val postsCount: Int = 0,
    val followersCount: String = "0",
    val followingCount: String = "0",
    val isFollowed: Boolean = false,
    val isCurrentUser: Boolean = false
)
