package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Comment
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.UserProfile

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val timeAgo: String,
    val content: String,
    val imageResName: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val timestamp: Long
) {
    fun toDomain(): Post = Post(
        id = id,
        authorName = authorName,
        authorHandle = authorHandle,
        authorAvatar = authorAvatar,
        timeAgo = timeAgo,
        content = content,
        imageResName = imageResName,
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
        isLiked = isLiked,
        isSaved = isSaved,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(p: Post): PostEntity = PostEntity(
            id = p.id,
            authorName = p.authorName,
            authorHandle = p.authorHandle,
            authorAvatar = p.authorAvatar,
            timeAgo = p.timeAgo,
            content = p.content,
            imageResName = p.imageResName,
            likesCount = p.likesCount,
            commentsCount = p.commentsCount,
            sharesCount = p.sharesCount,
            isLiked = p.isLiked,
            isSaved = p.isSaved,
            timestamp = p.timestamp
        )
    }
}

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timeAgo: String,
    val timestamp: Long
) {
    fun toDomain(): Comment = Comment(
        id = id,
        postId = postId,
        authorName = authorName,
        authorAvatar = authorAvatar,
        content = content,
        timeAgo = timeAgo,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(c: Comment): CommentEntity = CommentEntity(
            id = c.id,
            postId = c.postId,
            authorName = c.authorName,
            authorAvatar = c.authorAvatar,
            content = c.content,
            timeAgo = c.timeAgo,
            timestamp = c.timestamp
        )
    }
}

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorAvatar: String,
    val mediaResName: String,
    val caption: String,
    val timeAgo: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isViewed: Boolean,
    val isMine: Boolean,
    val timestamp: Long
) {
    fun toDomain(): Story = Story(
        id = id,
        authorName = authorName,
        authorAvatar = authorAvatar,
        mediaResName = mediaResName,
        caption = caption,
        timeAgo = timeAgo,
        likesCount = likesCount,
        commentsCount = commentsCount,
        isViewed = isViewed,
        isMine = isMine,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(s: Story): StoryEntity = StoryEntity(
            id = s.id,
            authorName = s.authorName,
            authorAvatar = s.authorAvatar,
            mediaResName = s.mediaResName,
            caption = s.caption,
            timeAgo = s.timeAgo,
            likesCount = s.likesCount,
            commentsCount = s.commentsCount,
            isViewed = s.isViewed,
            isMine = s.isMine,
            timestamp = s.timestamp
        )
    }
}

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val participantName: String,
    val participantAvatar: String,
    val lastMessage: String,
    val timestampStr: String,
    val unreadCount: Int,
    val isOnline: Boolean
) {
    fun toDomain(): Conversation = Conversation(
        id = id,
        participantName = participantName,
        participantAvatar = participantAvatar,
        lastMessage = lastMessage,
        timestampStr = timestampStr,
        unreadCount = unreadCount,
        isOnline = isOnline
    )

    companion object {
        fun fromDomain(c: Conversation): ConversationEntity = ConversationEntity(
            id = c.id,
            participantName = c.participantName,
            participantAvatar = c.participantAvatar,
            lastMessage = c.lastMessage,
            timestampStr = c.timestampStr,
            unreadCount = c.unreadCount,
            isOnline = c.isOnline
        )
    }
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderName: String,
    val text: String,
    val isFromMe: Boolean,
    val timestampStr: String,
    val timestamp: Long
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        conversationId = conversationId,
        senderName = senderName,
        text = text,
        isFromMe = isFromMe,
        timestampStr = timestampStr,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(m: ChatMessage): ChatMessageEntity = ChatMessageEntity(
            id = m.id,
            conversationId = m.conversationId,
            senderName = m.senderName,
            text = m.text,
            isFromMe = m.isFromMe,
            timestampStr = m.timestampStr,
            timestamp = m.timestamp
        )
    }
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val typeName: String,
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val avatar: String,
    val isRead: Boolean,
    val timestamp: Long
) {
    fun toDomain(): NotificationItem = NotificationItem(
        id = id,
        type = try { NotificationType.valueOf(typeName) } catch (_: Exception) { NotificationType.LIKE },
        title = title,
        subtitle = subtitle,
        timeAgo = timeAgo,
        avatar = avatar,
        isRead = isRead,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(n: NotificationItem): NotificationEntity = NotificationEntity(
            id = n.id,
            typeName = n.type.name,
            title = n.title,
            subtitle = n.subtitle,
            timeAgo = n.timeAgo,
            avatar = n.avatar,
            isRead = n.isRead,
            timestamp = n.timestamp
        )
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val handle: String,
    val avatar: String,
    val coverImage: String?,
    val bio: String,
    val postsCount: Int,
    val followersCount: String,
    val followingCount: String,
    val isFollowed: Boolean,
    val isCurrentUser: Boolean
) {
    fun toDomain(): UserProfile = UserProfile(
        id = id,
        displayName = displayName,
        handle = handle,
        avatar = avatar,
        coverImage = coverImage,
        bio = bio,
        postsCount = postsCount,
        followersCount = followersCount,
        followingCount = followingCount,
        isFollowed = isFollowed,
        isCurrentUser = isCurrentUser
    )

    companion object {
        fun fromDomain(u: UserProfile): UserEntity = UserEntity(
            id = u.id,
            displayName = u.displayName,
            handle = u.handle,
            avatar = u.avatar,
            coverImage = u.coverImage,
            bio = u.bio,
            postsCount = u.postsCount,
            followersCount = u.followersCount,
            followingCount = u.followingCount,
            isFollowed = u.isFollowed,
            isCurrentUser = u.isCurrentUser
        )
    }
}
