<template>
  <div class="detail-page" v-loading="loading">
    <el-button @click="router.back()" class="back-btn">← Back to List</el-button>

    <div v-if="resource" class="content-box">
      <div class="header-area">
        <el-tag type="warning" size="large" style="margin-bottom: 15px;">{{ resource.category }}</el-tag>
        <h1 class="title">{{ resource.title }}</h1>
        <div class="meta-info">
          <span>Contributor: <b>{{ resource.contributorUsername }}</b></span>
          <span>Location: <b>{{ resource.location || 'Unknown' }}</b></span>
          <span>Publish Time: <b>{{ formatDate(resource.createdAt) }}</b></span>
        </div>
      </div>

      <div class="media-area" v-if="resource.thumbnail || resource.mediaUrl">
        <img v-if="resource.thumbnail" :src="resource.thumbnail" class="main-image" />
        <div v-if="resource.mediaUrl" style="margin-top: 15px; text-align: center;">
          <el-link type="primary" :href="resource.mediaUrl" target="_blank">
            <el-icon style="margin-right: 4px;"><Link /></el-icon> View external media
          </el-link>
        </div>
      </div>

      <div class="desc-area">
        <h3>Project Details</h3>
        <p class="desc-text">{{ resource.description }}</p>
        <div v-if="resource.tags" class="tags-wrap">
          <span style="font-weight: bold; margin-right: 10px;">Tags:</span>
          <el-tag v-for="tag in resource.tags.split(',')" :key="tag" style="margin-right: 8px; cursor: pointer;" @click="searchTag(tag)">{{ tag }}</el-tag>
        </div>
      </div>

      <!-- Top like & favorite buttons -->
      <div class="action-area">
        <el-button
          size="large"
          :type="isLiked ? 'danger' : 'default'"
          :class="{ 'action-btn': true, 'is-active': isLiked }"
          @click="toggleAction('like')"
        >
          <el-icon style="margin-right: 6px;"><CaretTop /></el-icon>
          {{ isLiked ? 'Liked' : 'Like' }} ({{ likeCount }})
        </el-button>
        <el-button
          size="large"
          :type="isFavorited ? 'warning' : 'default'"
          :class="{ 'action-btn': true, 'is-active': isFavorited }"
          @click="toggleAction('favorite')"
        >
          <el-icon style="margin-right: 6px;"><Star /></el-icon>
          {{ isFavorited ? 'Favorited' : 'Favorite' }} ({{ favCount }})
        </el-button>
      </div>

      <el-divider content-position="center"><span style="font-size: 18px; color: #909399;">Comments & Discussion</span></el-divider>

      <div class="comment-section">
        <div class="comment-input-box">
          <el-input
              v-model="newCommentText"
              type="textarea" :rows="3"
              maxlength="1000" show-word-limit
              placeholder="Share your views and participate in cultural heritage discussions..."
          />
          <div style="text-align: right; margin-top: 10px;">
            <el-button type="primary" @click="submitComment(0, null)" :disabled="!newCommentText.trim()">Post Comment</el-button>
          </div>
        </div>

        <div v-if="comments.length === 0" style="text-align: center; color: #999; margin-top: 20px;">No comments yet, be the first to comment!</div>

        <div v-for="c in comments" :key="c.id" class="comment-thread">
          <div class="comment-item">
            <el-avatar :size="40" style="background: #409EFF;">{{ c.username === '[Deleted]' ? 'N' : c.username.charAt(0).toUpperCase() }}</el-avatar>
            <div class="comment-main">
              <div class="comment-header">
                <span class="c-user">{{ c.username }}</span>
                <span class="c-time">{{ formatDate(c.createdAt) }} <em v-if="c.isEdited === 1" style="color:#E6A23C; font-size:12px;">(Edited)</em></span>
              </div>
              <div class="c-content" :class="{ 'is-deleted': c.username === '[Deleted]' }">{{ c.content }}</div>

              <div class="c-actions" v-if="c.username !== '[Deleted]'">
                <span class="action-btn" @click="handleCommentAction(c.id, 'like')">
                  <el-icon :color="c.isLiked ? '#409EFF' : ''"><CaretTop /></el-icon>
                  <span>Like {{ c.likes }}</span>
                </span>
                <span class="action-btn" @click="handleCommentAction(c.id, 'dislike')">
                  <el-icon :color="c.isDisliked ? '#E6A23C' : ''"><CaretBottom /></el-icon>
                  <span>Dislike {{ c.dislikes }}</span>
                </span>
                <span class="action-btn" @click="openReply(c.id, c.username)">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>Reply</span>
                </span>
                <span class="action-btn" @click="openReport(c.id)" style="color: #F56C6C;">
                  <el-icon><WarningFilled /></el-icon>
                  <span>Report</span>
                </span>
                <template v-if="c.username === currentUser">
                  <span class="action-btn text-primary" @click="openEdit(c)">
                    <el-icon><Edit /></el-icon>
                    <span>Edit</span>
                  </span>
                  <span class="action-btn text-danger" @click="deleteComment(c.id)">
                    <el-icon><Delete /></el-icon>
                    <span>Delete</span>
                  </span>
                </template>
              </div>
            </div>
          </div>

          <div class="replies-box" v-if="c.children && c.children.length > 0">
            <div v-for="child in c.children" :key="child.id" class="comment-item reply-item">
              <el-avatar :size="30" style="background: #67C23A;">{{ child.username === '[Deleted]' ? 'N' : child.username.charAt(0).toUpperCase() }}</el-avatar>
              <div class="comment-main">
                <div class="comment-header">
                  <span class="c-user">{{ child.username }}</span>
                  <span style="margin: 0 5px; color: #909399; font-size: 12px;">Reply to @{{ child.replyTo }}</span>
                  <span class="c-time">{{ formatDate(child.createdAt) }} <em v-if="child.isEdited === 1" style="color:#E6A23C; font-size:12px;">(Edited)</em></span>
                </div>
                <div class="c-content" :class="{ 'is-deleted': child.username === '[Deleted]' }">{{ child.content }}</div>

                <div class="c-actions" v-if="child.username !== '[Deleted]'">
                  <span class="action-btn" @click="handleCommentAction(child.id, 'like')">
                    <el-icon :color="child.isLiked ? '#409EFF' : ''"><CaretTop /></el-icon>
                    <span>Like {{ child.likes }}</span>
                  </span>
                  <span class="action-btn" @click="handleCommentAction(child.id, 'dislike')">
                    <el-icon :color="child.isDisliked ? '#E6A23C' : ''"><CaretBottom /></el-icon>
                    <span>Dislike {{ child.dislikes }}</span>
                  </span>
                  <span class="action-btn" @click="openReply(c.id, child.username)">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>Reply</span>
                  </span>
                  <span class="action-btn" @click="openReport(child.id)" style="color: #F56C6C;">
                    <el-icon><WarningFilled /></el-icon>
                    <span>Report</span>
                  </span>
                  <template v-if="child.username === currentUser">
                    <span class="action-btn text-primary" @click="openEdit(child)">
                      <el-icon><Edit /></el-icon>
                      <span>Edit</span>
                    </span>
                    <span class="action-btn text-danger" @click="deleteComment(child.id)">
                      <el-icon><Delete /></el-icon>
                      <span>Delete</span>
                    </span>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Reply dialog -->
    <el-dialog v-model="replyDialog.visible" :title="'Reply to @' + replyDialog.targetUser" width="500px">
      <el-input v-model="replyDialog.content" type="textarea" :rows="3" maxlength="1000" show-word-limit placeholder="Write your reply..." />
      <template #footer>
        <el-button @click="replyDialog.visible = false">Cancel</el-button>
        <el-button type="primary" @click="submitComment(replyDialog.parentId, replyDialog.targetUser)">Send Reply</el-button>
      </template>
    </el-dialog>

    <!-- Edit comment dialog -->
    <el-dialog v-model="editDialog.visible" title="Edit Comment (Valid within 30 minutes)" width="500px">
      <el-input v-model="editDialog.content" type="textarea" :rows="3" maxlength="1000" show-word-limit />
      <template #footer>
        <el-button @click="editDialog.visible = false">Cancel</el-button>
        <el-button type="primary" @click="submitEdit">Save Changes</el-button>
      </template>
    </el-dialog>

    <!-- Report dialog -->
    <el-dialog v-model="reportDialog.visible" title="Report Inappropriate Comment" width="500px">
      <el-form :model="reportDialog" label-width="80px">
        <el-form-item label="Report Reason">
          <el-select v-model="reportDialog.reason" placeholder="Please select reason" style="width: 100%;">
            <el-option label="Spam" value="Spam" />
            <el-option label="Harassment" value="Harassment" />
            <el-option label="Misinformation" value="Misinformation" />
            <el-option label="Other Violation" value="Other" />
          </el-select>
        </el-form-item>
        <el-form-item label="Detailed Explanation">
          <el-input v-model="reportDialog.details" type="textarea" :rows="3" placeholder="Please provide more details to help administrators review..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialog.visible = false">Cancel</el-button>
        <el-button type="danger" @click="submitReport">Confirm Report</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  CaretTop, CaretBottom, ChatDotRound, WarningFilled, Edit, Delete, Star, Link
} from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const loading = ref(true)

// Resource info
const resource = ref(null)
const likeCount = ref(0)
const favCount = ref(0)
const isLiked = ref(false)
const isFavorited = ref(false)
const currentUser = localStorage.getItem('currentUser')

// Comments
const comments = ref([])
const newCommentText = ref('')

// Dialog state
const replyDialog = ref({ visible: false, parentId: 0, targetUser: '', content: '' })
const editDialog = ref({ visible: false, id: 0, content: '' })
const reportDialog = ref({ visible: false, commentId: 0, reason: '', details: '' })

// Load resource detail
const loadDetail = async () => {
  try {
    const res = await axios.get(`http://116.62.165.182/api/public/resources/${route.params.id}${currentUser ? '?username=' + currentUser : ''}`)
    if (!res.data.success) {
      ElMessage.error(res.data.message)
      setTimeout(() => router.push('/'), 2000)
      return
    }
    resource.value = res.data.data
    likeCount.value = res.data.likeCount
    favCount.value = res.data.favCount
    isLiked.value = res.data.isLiked
    isFavorited.value = res.data.isFavorited
    fetchComments()
  } catch (error) {
    ElMessage.error('Loading failed, please check network')
  } finally {
    loading.value = false
  }
}

// Like / favorite
const toggleAction = async (type) => {
  if (!currentUser) return router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
  try {
    const res = await axios.post(`http://116.62.165.182/api/resources/${route.params.id}/${type}?username=${currentUser}`)
    if (type === 'like') { isLiked.value = res.data === 'liked'; likeCount.value += (isLiked.value ? 1 : -1) }
    else { isFavorited.value = res.data === 'favorited'; favCount.value += (isFavorited.value ? 1 : -1) }
  } catch (error) { ElMessage.error('Operation failed') }
}

const searchTag = (tag) => { router.push({ path: '/', query: { keyword: tag } }) }

// Login check
const checkLogin = () => {
  if (!currentUser) {
    ElMessage.warning('Please login first to participate!')
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return false
  }
  return true
}

// Fetch comments (with current user state)
const fetchComments = async () => {
  try {
    const url = `http://116.62.165.182/api/public/resources/${route.params.id}/comments` +
      (currentUser ? `?username=${currentUser}` : '')
    const res = await axios.get(url)
    comments.value = res.data
  } catch (e) { console.error('Failed to get comments') }
}

// Post comment / reply
const submitComment = async (parentId, replyTo) => {
  if (!checkLogin()) return
  const content = parentId === 0 ? newCommentText.value : replyDialog.value.content
  if (!content.trim()) return ElMessage.warning('Content cannot be empty')

  try {
    const res = await axios.post('http://116.62.165.182/api/comments', {
      resourceId: route.params.id, username: currentUser, content: content, parentId: parentId, replyTo: replyTo
    })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      newCommentText.value = ''
      replyDialog.value.visible = false
      replyDialog.value.content = ''
      fetchComments()
    } else { ElMessage.error(res.data.message) }
  } catch (e) { ElMessage.error('Sending failed') }
}

// Comment like/dislike (toggle)
const handleCommentAction = async (id, type) => {
  if (!checkLogin()) return
  try {
    const res = await axios.post(`http://116.62.165.182/api/comments/${id}/action?type=${type}&username=${currentUser}`)
    if (res.data.success) {
      fetchComments()  // Refresh to update state and counts
    }
  } catch (e) { ElMessage.error('Operation failed') }
}

// Delete comment
const deleteComment = (id) => {
  ElMessageBox.confirm('Are you sure you want to delete this comment?', 'Prompt', { type: 'warning' }).then(async () => {
    try {
      await axios.delete(`http://116.62.165.182/api/comments/${id}`)
      ElMessage.success('Deleted')
      fetchComments()
    } catch (e) { ElMessage.error('Deletion failed') }
  }).catch(() => {})
}

const openReply = (parentId, targetUser) => { replyDialog.value = { visible: true, parentId, targetUser, content: '' } }
const openEdit = (c) => { editDialog.value = { visible: true, id: c.id, content: c.content } }
const openReport = (id) => { if (checkLogin()) reportDialog.value = { visible: true, commentId: id, reason: '', details: '' } }

// Submit edit
const submitEdit = async () => {
  try {
    const res = await axios.put(`http://116.62.165.182/api/comments/${editDialog.value.id}`, { content: editDialog.value.content })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      editDialog.value.visible = false
      fetchComments()
    } else { ElMessage.error(res.data.message) }
  } catch (e) { ElMessage.error('Modification failed') }
}

// Submit report
const submitReport = async () => {
  if (!reportDialog.value.reason) return ElMessage.warning('Please select report reason')
  try {
    const res = await axios.post('http://116.62.165.182/api/comments/report', {
      commentId: reportDialog.value.commentId, reporterUsername: currentUser,
      reason: reportDialog.value.reason, details: reportDialog.value.details
    })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      reportDialog.value.visible = false
    }
  } catch (e) { ElMessage.error('Report submission failed') }
}

const formatDate = (dateStr) => { return dateStr ? dateStr.substring(0, 16).replace('T', ' ') : '' }

onMounted(() => { loadDetail() })
</script>

<style scoped>
.detail-page {
  padding: 40px;
  max-width: 1100px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
}

.back-btn {
  font-size: 15px;
  color: #666;
  padding: 8px 16px;
  border: 1px solid #ddd;
}

.content-box {
  background: #fff;
  border-radius: 16px;
  padding: 50px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.06);
}

.header-area { text-align: center; border-bottom: 1px solid #f0f0f0; padding-bottom: 30px; margin-bottom: 40px; }
.title { font-size: 2.8rem; font-weight: 300; letter-spacing: -2px; color: #111; margin: 15px 0 25px; }
.meta-info { display: flex; justify-content: center; gap: 40px; color: #606266; font-size: 15px; flex-wrap: wrap; }

.media-area { text-align: center; margin-bottom: 40px; }
.main-image { max-width: 100%; max-height: 560px; border-radius: 12px; box-shadow: 0 8px 25px rgba(0,0,0,0.1); }

.desc-area h3 { font-size: 1.7rem; font-weight: 400; color: #111; margin-bottom: 20px; }
.desc-text { font-size: 1.15rem; line-height: 1.9; color: #333; background: #fafafa; padding: 30px; border-radius: 12px; border-left: 5px solid #111; white-space: pre-wrap; }

.tags-wrap { margin-top: 25px; }

.action-area {
  display: flex;
  justify-content: center;
  gap: 25px;
  margin: 50px 0;
}
.action-btn {
  width: auto;
  padding: 12px 28px;
  font-size: 16px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}
.action-btn.is-active {
  background-color: #ecf5ff;
  border-color: #409EFF;
  color: #409EFF;
}

.comment-section { margin-top: 20px; }
.comment-input-box { background: #f5f7fa; padding: 20px; border-radius: 8px; margin-bottom: 30px; }
.comment-thread { margin-bottom: 25px; border-bottom: 1px dashed #ebeef5; padding-bottom: 15px; }
.comment-item { display: flex; gap: 15px; margin-bottom: 15px; }
.comment-main { flex: 1; }
.comment-header { margin-bottom: 8px; }
.c-user { font-weight: bold; color: #303133; font-size: 14px; margin-right: 10px; }
.c-time { color: #909399; font-size: 12px; }
.c-content { font-size: 14px; color: #333; line-height: 1.6; margin-bottom: 10px; white-space: pre-wrap; }
.c-content.is-deleted { color: #c0c4cc; font-style: italic; background: #f4f4f5; padding: 5px 10px; border-radius: 4px; display: inline-block; }

.c-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 12px;
  font-size: 12px;
  color: #909399;
}
.c-actions .action-btn {
  display: flex;
  align-items: center;
  gap: 3px;
  cursor: pointer;
  transition: color 0.2s;
  white-space: nowrap;
}
.c-actions .action-btn:hover { color: #409EFF; }
.text-danger { color: #F56C6C !important; }
.text-primary { color: #409EFF !important; }

.replies-box { margin-left: 55px; background: #fafafa; padding: 15px; border-radius: 8px; }
.reply-item { margin-bottom: 10px; border-bottom: 1px solid #f0f0f0; padding-bottom: 10px; }
.reply-item:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
</style>
