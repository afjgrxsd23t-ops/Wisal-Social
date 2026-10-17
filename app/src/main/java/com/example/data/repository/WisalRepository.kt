package com.example.data.repository

import android.content.Context
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CommentEntity
import com.example.data.local.ConversationEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PostEntity
import com.example.data.local.StoryEntity
import com.example.data.local.UserEntity
import com.example.data.local.WisalDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.Comment
import com.example.data.model.Conversation
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class WisalRepository(context: Context) {
    private val db = WisalDatabase.getDatabase(context)
    private val dao = db.wisalDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val currentPosts = dao.getAllPosts().firstOrNull()
        if (currentPosts.isNullOrEmpty()) {
            // Seed current user
            val currentUser = UserEntity(
                id = "user_me",
                displayName = "أحمد محمد",
                handle = "@ahmed123",
                avatar = "wisal_icon_fg",
                coverImage = "wisal_city_bg",
                bio = "مستقبل أجمل.. يبدأ من هنا ✨\nمصور ومحب للطبيعة والتواصل الإنساني.",
                postsCount = 24,
                followersCount = "8.5K",
                followingCount = "356",
                isFollowed = false,
                isCurrentUser = true
            )

            // Seed other suggested users
            val otherUsers = listOf(
                UserEntity(
                    id = "user_sarah",
                    displayName = "سارة أحمد",
                    handle = "@sarah_ahmed",
                    avatar = "wisal_sarah_story",
                    coverImage = "wisal_city_bg",
                    bio = "كل شيء يأتي في وقته الجميل 💙",
                    postsCount = 18,
                    followersCount = "12.4K",
                    followingCount = "420",
                    isFollowed = true,
                    isCurrentUser = false
                ),
                UserEntity(
                    id = "user_omar",
                    displayName = "عمر خالد",
                    handle = "@omar_khaled",
                    avatar = "wisal_icon_fg",
                    coverImage = null,
                    bio = "مطور ومصمم واجهات | محب للتقنية",
                    postsCount = 9,
                    followersCount = "8 مشترك",
                    followingCount = "150",
                    isFollowed = false,
                    isCurrentUser = false
                ),
                UserEntity(
                    id = "user_reem",
                    displayName = "ريم القحطاني",
                    handle = "@reem_alqahtani",
                    avatar = "wisal_sarah_story",
                    coverImage = null,
                    bio = "كاتبة وباحثة في الأدب والتراث",
                    postsCount = 15,
                    followersCount = "14 مشترك",
                    followingCount = "210",
                    isFollowed = false,
                    isCurrentUser = false
                )
            )
            dao.insertUsers(listOf(currentUser) + otherUsers)

            // Seed Posts
            val initialPosts = listOf(
                PostEntity(
                    id = "post_1",
                    authorName = "أحمد محمد",
                    authorHandle = "@ahmed123",
                    authorAvatar = "wisal_icon_fg",
                    timeAgo = "منذ 2 ساعة",
                    content = "أجمل اللحظات هي تلك التي نقضيها مع من نحب ❤️ مدينة ساحرة وتفاصيل تعيد للروح سكينتها في وِصال.",
                    imageResName = "wisal_city_bg",
                    likesCount = 1240,
                    commentsCount = 256,
                    sharesCount = 34,
                    isLiked = false,
                    isSaved = false,
                    timestamp = System.currentTimeMillis() - 7200000L
                ),
                PostEntity(
                    id = "post_2",
                    authorName = "ليلى محمد",
                    authorHandle = "@layla_m",
                    authorAvatar = "wisal_sarah_story",
                    timeAgo = "منذ 4 ساعات",
                    content = "الطبيعة دائمًا قادرة على إبهارنا.. نسيم المساء في هذا المكان لا يُقدّر بثمن 🌿✨",
                    imageResName = "wisal_city_bg",
                    likesCount = 850,
                    commentsCount = 74,
                    sharesCount = 19,
                    isLiked = true,
                    isSaved = true,
                    timestamp = System.currentTimeMillis() - 14400000L
                ),
                PostEntity(
                    id = "post_3",
                    authorName = "سارة أحمد",
                    authorHandle = "@sarah_ahmed",
                    authorAvatar = "wisal_sarah_story",
                    timeAgo = "منذ 6 ساعات",
                    content = "«أكثر من مجرد تواصل».. سعيدة جدًا بانضمامي لمنصة وِصال، فخورون بمنتج عربي بهوية فاخرة!",
                    imageResName = null,
                    likesCount = 2410,
                    commentsCount = 312,
                    sharesCount = 88,
                    isLiked = false,
                    isSaved = false,
                    timestamp = System.currentTimeMillis() - 21600000L
                )
            )
            dao.insertPosts(initialPosts)

            // Seed Comments for post_1
            val comments = listOf(
                CommentEntity(
                    id = "c_1",
                    postId = "post_1",
                    authorName = "سارة أحمد",
                    authorAvatar = "wisal_sarah_story",
                    content = "ما شاء الله، لقطة غاية في الروعة والإتقان! 👏",
                    timeAgo = "منذ ساعة",
                    timestamp = System.currentTimeMillis() - 3600000L
                ),
                CommentEntity(
                    id = "c_2",
                    postId = "post_1",
                    authorName = "محمد علي",
                    authorAvatar = "wisal_icon_fg",
                    content = "مكان ساحر فعلاً، أين تم التقاط هذه الصورة يا أحمد؟",
                    timeAgo = "منذ 45 دقيقة",
                    timestamp = System.currentTimeMillis() - 2700000L
                )
            )
            dao.insertComments(comments)

            // Seed Stories
            val stories = listOf(
                StoryEntity(
                    id = "story_me",
                    authorName = "قصتك",
                    authorAvatar = "wisal_icon_fg",
                    mediaResName = "wisal_city_bg",
                    caption = "يوم جديد وإنجازات قادمة بإذن الله ☀️",
                    timeAgo = "الآن",
                    likesCount = 12,
                    commentsCount = 3,
                    isViewed = false,
                    isMine = true,
                    timestamp = System.currentTimeMillis()
                ),
                StoryEntity(
                    id = "story_sarah",
                    authorName = "سارة",
                    authorAvatar = "wisal_sarah_story",
                    mediaResName = "wisal_sarah_story",
                    caption = "كل شيء يأتي في وقته الجميل 💙",
                    timeAgo = "منذ 3 ساعات",
                    likesCount = 2400,
                    commentsCount = 312,
                    isViewed = false,
                    isMine = false,
                    timestamp = System.currentTimeMillis() - 10800000L
                ),
                StoryEntity(
                    id = "story_hussein",
                    authorName = "حسين",
                    authorAvatar = "wisal_icon_fg",
                    mediaResName = "wisal_city_bg",
                    caption = "جلسة هادئة وقراءة ملهمة ☕📖",
                    timeAgo = "منذ 5 ساعات",
                    likesCount = 180,
                    commentsCount = 21,
                    isViewed = false,
                    isMine = false,
                    timestamp = System.currentTimeMillis() - 18000000L
                ),
                StoryEntity(
                    id = "story_maryam",
                    authorName = "مريم",
                    authorAvatar = "wisal_sarah_story",
                    mediaResName = "wisal_city_bg",
                    caption = "غروب شمس ساحر من أعلى التلال 🌄",
                    timeAgo = "منذ 7 ساعات",
                    likesCount = 420,
                    commentsCount = 45,
                    isViewed = false,
                    isMine = false,
                    timestamp = System.currentTimeMillis() - 25200000L
                )
            )
            dao.insertStories(stories)

            // Seed Conversations
            val conversations = listOf(
                ConversationEntity(
                    id = "conv_sarah",
                    participantName = "سارة أحمد",
                    participantAvatar = "wisal_sarah_story",
                    lastMessage = "كيف حالك؟ هل اطلعت على التحديث الجديد؟",
                    timestampStr = "9:12 ص",
                    unreadCount = 1,
                    isOnline = true
                ),
                ConversationEntity(
                    id = "conv_mohamed",
                    participantName = "محمد علي",
                    participantAvatar = "wisal_icon_fg",
                    lastMessage = "أرسل لك صورة من المؤتمر اليوم",
                    timestampStr = "8:45 ص",
                    unreadCount = 0,
                    isOnline = true
                ),
                ConversationEntity(
                    id = "conv_noor",
                    participantName = "نور القاضي",
                    participantAvatar = "wisal_sarah_story",
                    lastMessage = "متى نلتقي لمناقشة التفاصيل؟",
                    timestampStr = "7:30 ص",
                    unreadCount = 0,
                    isOnline = false
                ),
                ConversationEntity(
                    id = "conv_khaled",
                    participantName = "خالد عبدالله",
                    participantAvatar = "wisal_icon_fg",
                    lastMessage = "تم، بارك الله فيك 👍",
                    timestampStr = "6:50 ص",
                    unreadCount = 1,
                    isOnline = true
                ),
                ConversationEntity(
                    id = "conv_mawaheb",
                    participantName = "مواهب",
                    participantAvatar = "wisal_sarah_story",
                    lastMessage = "شكرًا لك على الدعم والتشجيع المستمر",
                    timestampStr = "أمس",
                    unreadCount = 0,
                    isOnline = false
                ),
                ConversationEntity(
                    id = "conv_hala",
                    participantName = "هلا وسهلا",
                    participantAvatar = "wisal_icon_fg",
                    lastMessage = "أهلاً وسهلاً بك في شبكة وِصال!",
                    timestampStr = "أمس",
                    unreadCount = 1,
                    isOnline = true
                ),
                ConversationEntity(
                    id = "conv_group",
                    participantName = "مجموعة الأصدقاء",
                    participantAvatar = "wisal_icon_fg",
                    lastMessage = "رسالة جديدة: اللقاء غدًا بإذن الله",
                    timestampStr = "1:02 ص",
                    unreadCount = 0,
                    isOnline = true
                )
            )
            dao.insertConversations(conversations)

            // Seed Messages for conv_sarah
            val sarahMessages = listOf(
                ChatMessageEntity(
                    id = "msg_1",
                    conversationId = "conv_sarah",
                    senderName = "سارة أحمد",
                    text = "السلام عليكم ورحمة الله، مرحبًا أحمد!",
                    isFromMe = false,
                    timestampStr = "9:10 ص",
                    timestamp = System.currentTimeMillis() - 120000L
                ),
                ChatMessageEntity(
                    id = "msg_2",
                    conversationId = "conv_sarah",
                    senderName = "أحمد محمد",
                    text = "وعليكم السلام يا سارة، أهلاً بكِ في وِصال 🌟",
                    isFromMe = true,
                    timestampStr = "9:11 ص",
                    timestamp = System.currentTimeMillis() - 60000L
                ),
                ChatMessageEntity(
                    id = "msg_3",
                    conversationId = "conv_sarah",
                    senderName = "سارة أحمد",
                    text = "كيف حالك؟ هل اطلعت على التحديث الجديد؟ التصميم رائع جدًا!",
                    isFromMe = false,
                    timestampStr = "9:12 ص",
                    timestamp = System.currentTimeMillis()
                )
            )
            dao.insertChatMessages(sarahMessages)

            // Seed Notifications matching design
            val notifications = listOf(
                NotificationEntity(
                    id = "notif_1",
                    typeName = NotificationType.FOLLOW.name,
                    title = "بدأت سارة أحمد بمتابعتك",
                    subtitle = "سارة أحمد تتابع حسابك الآن",
                    timeAgo = "منذ 5 دقائق",
                    avatar = "wisal_sarah_story",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 300000L
                ),
                NotificationEntity(
                    id = "notif_2",
                    typeName = NotificationType.LIKE.name,
                    title = "أعجبت بمنشورك",
                    subtitle = "أعجب 14 شخصًا بمنشورك الأخير",
                    timeAgo = "منذ 12 دقيقة",
                    avatar = "wisal_city_bg",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 720000L
                ),
                NotificationEntity(
                    id = "notif_3",
                    typeName = NotificationType.COMMENT.name,
                    title = "قام محمد علي بالتعليق على منشورك",
                    subtitle = "«مكان ساحر فعلاً، أين تم التقاط هذه الصورة؟»",
                    timeAgo = "منذ 20 دقيقة",
                    avatar = "wisal_icon_fg",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 1200000L
                ),
                NotificationEntity(
                    id = "notif_4",
                    typeName = NotificationType.FOLLOW.name,
                    title = "أصبح لديك متابع جديد",
                    subtitle = "قام عمر خالد بمتابعة ملفك الشخصي",
                    timeAgo = "منذ 35 دقيقة",
                    avatar = "wisal_icon_fg",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - 2100000L
                ),
                NotificationEntity(
                    id = "notif_5",
                    typeName = NotificationType.MENTION.name,
                    title = "تم الإشارة إليك في منشور",
                    subtitle = "أشار إليك @layla_m في منشور جديد",
                    timeAgo = "منذ ساعة",
                    avatar = "wisal_sarah_story",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - 3600000L
                ),
                NotificationEntity(
                    id = "notif_6",
                    typeName = NotificationType.GROUP.name,
                    title = "دعوة للانضمام إلى مجموعة",
                    subtitle = "تمت دعوتك للانضمام إلى «ملتقى المبدعين العرب»",
                    timeAgo = "منذ ساعتين",
                    avatar = "wisal_icon_fg",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - 7200000L
                )
            )
            dao.insertNotifications(notifications)
        }
    }

    // --- Posts ---
    fun getPosts(): Flow<List<Post>> = dao.getAllPosts().map { list -> list.map { it.toDomain() } }

    suspend fun toggleLike(post: Post) {
        val newIsLiked = !post.isLiked
        val newCount = if (newIsLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        dao.updatePostLike(post.id, newIsLiked, newCount)
    }

    suspend fun toggleSave(post: Post) {
        dao.updatePostSave(post.id, !post.isSaved)
    }

    suspend fun createPost(content: String, imageResName: String?) {
        val newPost = PostEntity(
            id = "post_" + UUID.randomUUID().toString().take(8),
            authorName = "أحمد محمد",
            authorHandle = "@ahmed123",
            authorAvatar = "wisal_icon_fg",
            timeAgo = "الآن",
            content = content,
            imageResName = imageResName,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = false,
            isSaved = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertPost(newPost)
    }

    suspend fun deletePost(postId: String) {
        dao.deletePost(postId)
    }

    // --- Comments ---
    fun getComments(postId: String): Flow<List<Comment>> =
        dao.getCommentsForPost(postId).map { list -> list.map { it.toDomain() } }

    suspend fun addComment(postId: String, text: String) {
        val comment = CommentEntity(
            id = "comment_" + UUID.randomUUID().toString().take(8),
            postId = postId,
            authorName = "أحمد محمد",
            authorAvatar = "wisal_icon_fg",
            content = text,
            timeAgo = "الآن",
            timestamp = System.currentTimeMillis()
        )
        dao.insertComment(comment)
        dao.incrementCommentsCount(postId)
    }

    // --- Stories ---
    fun getStories(): Flow<List<Story>> = dao.getAllStories().map { list -> list.map { it.toDomain() } }

    suspend fun addStory(caption: String, imageResName: String = "wisal_city_bg") {
        val story = StoryEntity(
            id = "story_" + UUID.randomUUID().toString().take(8),
            authorName = "قصتك",
            authorAvatar = "wisal_icon_fg",
            mediaResName = imageResName,
            caption = caption,
            timeAgo = "الآن",
            likesCount = 0,
            commentsCount = 0,
            isViewed = false,
            isMine = true,
            timestamp = System.currentTimeMillis()
        )
        dao.insertStory(story)
    }

    suspend fun markStoryViewed(storyId: String) {
        dao.markStoryViewed(storyId)
    }

    suspend fun likeStory(storyId: String) {
        dao.likeStory(storyId)
    }

    // --- Conversations & Messages ---
    fun getConversations(): Flow<List<Conversation>> =
        dao.getAllConversations().map { list -> list.map { it.toDomain() } }

    fun getMessages(conversationId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForConversation(conversationId).map { list -> list.map { it.toDomain() } }

    suspend fun sendMessage(conversationId: String, text: String, participantName: String) {
        val message = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            conversationId = conversationId,
            senderName = "أحمد محمد",
            text = text,
            isFromMe = true,
            timestampStr = "الآن",
            timestamp = System.currentTimeMillis()
        )
        dao.insertChatMessage(message)
        dao.updateConversationLastMessage(conversationId, text, "الآن")

        // Trigger realistic reply after a short delay
        scope.launch {
            delay(2000)
            val replies = listOf(
                "أهلاً بك يا أحمد! سررت برسالتك 💙",
                "تمام، يسعدني تواصلك دائمًا!",
                "بالتأكيد، متفق معك تمامًا في ذلك.",
                "شكرًا لك على المشاركة القيمة ✨"
            )
            val replyMessage = ChatMessageEntity(
                id = "msg_" + UUID.randomUUID().toString().take(8),
                conversationId = conversationId,
                senderName = participantName,
                text = replies.random(),
                isFromMe = false,
                timestampStr = "الآن",
                timestamp = System.currentTimeMillis()
            )
            dao.insertChatMessage(replyMessage)
            dao.updateConversationLastMessage(conversationId, replyMessage.text, "الآن")
        }
    }

    // --- Notifications ---
    fun getNotifications(): Flow<List<NotificationItem>> =
        dao.getAllNotifications().map { list -> list.map { it.toDomain() } }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    // --- Users & Profile ---
    fun getCurrentUser(): Flow<UserProfile?> =
        dao.getCurrentUser().map { it?.toDomain() }

    fun getAllUsers(): Flow<List<UserProfile>> =
        dao.getAllUsers().map { list -> list.map { it.toDomain() } }

    suspend fun updateProfile(name: String, handle: String, bio: String) {
        val user = dao.getCurrentUser().firstOrNull() ?: return
        dao.updateUser(
            user.copy(
                displayName = name,
                handle = handle,
                bio = bio
            )
        )
    }

    suspend fun toggleFollow(userId: String, isCurrentlyFollowed: Boolean) {
        dao.updateFollowStatus(userId, !isCurrentlyFollowed)
    }
}
