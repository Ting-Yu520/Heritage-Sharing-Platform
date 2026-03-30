<template>
  <div class="detail-container" v-loading="loading">
    <el-button @click="router.back()" style="margin-bottom: 20px;">🔙 返回列表</el-button>

    <div v-if="resource" class="content-box">
      <div class="header-area">
        <el-tag type="warning" size="large" style="margin-bottom: 15px;">{{ resource.category }}</el-tag>
        <h1 class="title">{{ resource.title }}</h1>
        <div class="meta-info">
          <span>✍️ 贡献者: <b>{{ resource.contributorUsername }}</b></span>
          <span>📍 坐标: <b>{{ resource.location || '未知' }}</b></span>
          <span>📅 发布时间: <b>{{ formatDate(resource.createdAt) }}</b></span>
        </div>
      </div>

      <div class="media-area" v-if="resource.thumbnail || resource.mediaUrl">
        <img v-if="resource.thumbnail" :src="resource.thumbnail" class="main-image" />
        <div v-if="resource.mediaUrl" style="margin-top: 15px; text-align: center;">
          <el-link type="primary" :href="resource.mediaUrl" target="_blank">🔗 点击查看外部媒体/附件</el-link>
        </div>
      </div>
      <div class="desc-area">
        <h3>📖 项目详情</h3>
        <p class="desc-text">{{ resource.description }}</p>
        <div v-if="resource.tags" class="tags-wrap">
          <span style="font-weight: bold; margin-right: 10px;">🏷️ 标签:</span>
          <el-tag v-for="tag in resource.tags.split(',')" :key="tag" style="margin-right: 8px; cursor: pointer;" @click="searchTag(tag)">{{ tag }}</el-tag>
        </div>
      </div>

      <div class="action-area">
        <el-button size="large" :type="isLiked ? 'danger' : 'default'" :plain="!isLiked" @click="toggleAction('like')" class="action-btn">
          ❤️ {{ isLiked ? '已点赞' : '点赞' }} ({{ likeCount }})
        </el-button>
        <el-button size="large" :type="isFavorited ? 'warning' : 'default'" :plain="!isFavorited" @click="toggleAction('favorite')" class="action-btn">
          ⭐ {{ isFavorited ? '已收藏' : '收藏' }} ({{ favCount }})
        </el-button>
      </div>

      <el-divider content-position="center"><span style="font-size: 18px; color: #909399;">评论与讨论区</span></el-divider>

      <div class="comment-section">
        <div class="comment-input-box">
          <el-input
              v-model="newCommentText"
              type="textarea" :rows="3"
              maxlength="1000" show-word-limit
              placeholder="分享您的观点，参与文化传承讨论..."
          />
          <div style="text-align: right; margin-top: 10px;">
            <el-button type="primary" @click="submitComment(0, null)" :disabled="!newCommentText.trim()">发表评论</el-button>
          </div>
        </div>

        <div v-if="comments.length === 0" style="text-align: center; color: #999; margin-top: 20px;">暂无评论，快来抢沙发吧！</div>

        <div v-for="c in comments" :key="c.id" class="comment-thread">
          <div class="comment-item">
            <el-avatar :size="40" style="background: #409EFF;">{{ c.username === '[Deleted]' ? '无' : c.username.charAt(0).toUpperCase() }}</el-avatar>
            <div class="comment-main">
              <div class="comment-header">
                <span class="c-user">{{ c.username }}</span>
                <span class="c-time">{{ formatDate(c.createdAt) }} <em v-if="c.isEdited === 1" style="color:#E6A23C; font-size:12px;">(已编辑)</em></span>
              </div>
              <div class="c-content" :class="{ 'is-deleted': c.username === '[Deleted]' }">{{ c.content }}</div>

              <div class="c-actions" v-if="c.username !== '[Deleted]'">
                <span class="action-btn" @click="handleCommentAction(c.id, 'like')">👍 {{ c.likes }}</span>
                <span class="action-btn" @click="handleCommentAction(c.id, 'dislike')">👎 {{ c.dislikes }}</span>
                <span class="action-btn" @click="openReply(c.id, c.username)">💬 回复</span>
                <span class="action-btn text-danger" @click="openReport(c.id)">🚨 举报</span>
                <template v-if="c.username === currentUser">
                  <span class="action-btn text-primary" @click="openEdit(c)">✏️ 编辑</span>
                  <span class="action-btn text-danger" @click="deleteComment(c.id)">🗑️ 删除</span>
                </template>
              </div>
            </div>
          </div>

          <div class="replies-box" v-if="c.children && c.children.length > 0">
            <div v-for="child in c.children" :key="child.id" class="comment-item reply-item">
              <el-avatar :size="30" style="background: #67C23A;">{{ child.username === '[Deleted]' ? '无' : child.username.charAt(0).toUpperCase() }}</el-avatar>
              <div class="comment-main">
                <div class="comment-header">
                  <span class="c-user">{{ child.username }}</span>
                  <span style="margin: 0 5px; color: #909399; font-size: 12px;">回复 @{{ child.replyTo }}</span>
                  <span class="c-time">{{ formatDate(child.createdAt) }} <em v-if="child.isEdited === 1" style="color:#E6A23C; font-size:12px;">(已编辑)</em></span>
                </div>
                <div class="c-content" :class="{ 'is-deleted': child.username === '[Deleted]' }">{{ child.content }}</div>

                <div class="c-actions" v-if="child.username !== '[Deleted]'">
                  <span class="action-btn" @click="handleCommentAction(child.id, 'like')">👍 {{ child.likes }}</span>
                  <span class="action-btn" @click="handleCommentAction(child.id, 'dislike')">👎 {{ child.dislikes }}</span>
                  <span class="action-btn" @click="openReply(c.id, child.username)">💬 回复</span>
                  <span class="action-btn text-danger" @click="openReport(child.id)">🚨 举报</span>
                  <template v-if="child.username === currentUser">
                    <span class="action-btn text-primary" @click="openEdit(child)">✏️ 编辑</span>
                    <span class="action-btn text-danger" @click="deleteComment(child.id)">🗑️ 删除</span>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="replyDialog.visible" :title="'回复 @' + replyDialog.targetUser" width="500px">
      <el-input v-model="replyDialog.content" type="textarea" :rows="3" maxlength="1000" show-word-limit placeholder="写下你的回复..." />
      <template #footer>
        <el-button @click="replyDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitComment(replyDialog.parentId, replyDialog.targetUser)">发送回复</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialog.visible" title="编辑评论 (30分钟内有效)" width="500px">
      <el-input v-model="editDialog.content" type="textarea" :rows="3" maxlength="1000" show-word-limit />
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存修改</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reportDialog.visible" title="🚨 举报不良评论" width="500px">
      <el-form :model="reportDialog" label-width="80px">
        <el-form-item label="举报原因">
          <el-select v-model="reportDialog.reason" placeholder="请选择原因" style="width: 100%;">
            <el-option label="垃圾广告 (Spam)" value="Spam" />
            <el-option label="人身攻击/骚扰 (Harassment)" value="Harassment" />
            <el-option label="虚假信息 (Misinformation)" value="Misinformation" />
            <el-option label="其他违规 (Other)" value="Other" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input v-model="reportDialog.details" type="textarea" :rows="3" placeholder="请提供更多细节帮助管理员审核..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialog.visible = false">取消</el-button>
        <el-button type="danger" @click="submitReport">确认举报</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const loading = ref(true)

// 资源基础数据
const resource = ref(null)
const likeCount = ref(0)
const favCount = ref(0)
const isLiked = ref(false)
const isFavorited = ref(false)
const currentUser = localStorage.getItem('currentUser')

// 评论数据
const comments = ref([])
const newCommentText = ref('')

// 弹窗状态
const replyDialog = ref({ visible: false, parentId: 0, targetUser: '', content: '' })
const editDialog = ref({ visible: false, id: 0, content: '' })
const reportDialog = ref({ visible: false, commentId: 0, reason: '', details: '' })

// 1. 加载资源详情
const loadDetail = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/public/resources/${route.params.id}${currentUser ? '?username=' + currentUser : ''}`)
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
    fetchComments() // 加载评论树
  } catch (error) {
    ElMessage.error('加载失败，请检查网络')
  } finally {
    loading.value = false
  }
}

// 2. 点赞收藏基础逻辑
const toggleAction = async (type) => {
  if (!currentUser) return router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
  try {
    const res = await axios.post(`http://localhost:8080/api/resources/${route.params.id}/${type}?username=${currentUser}`)
    if (type === 'like') { isLiked.value = res.data === 'liked'; likeCount.value += (isLiked.value ? 1 : -1) }
    else { isFavorited.value = res.data === 'favorited'; favCount.value += (isFavorited.value ? 1 : -1) }
  } catch (error) { ElMessage.error('操作失败') }
}

const searchTag = (tag) => { router.push({ path: '/', query: { keyword: tag } }) }

// ================= 评论核心逻辑 =================
const checkLogin = () => {
  if (!currentUser) {
    ElMessage.warning('请先登录后再参与互动！')
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return false
  }
  return true
}

// 获取评论树
const fetchComments = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/public/resources/${route.params.id}/comments`)
    comments.value = res.data
  } catch (e) { console.error('获取评论失败') }
}

// 发送评论 / 回复
const submitComment = async (parentId, replyTo) => {
  if (!checkLogin()) return
  const content = parentId === 0 ? newCommentText.value : replyDialog.value.content
  if (!content.trim()) return ElMessage.warning('内容不能为空')

  try {
    const res = await axios.post('http://localhost:8080/api/comments', {
      resourceId: route.params.id, username: currentUser, content: content, parentId: parentId, replyTo: replyTo
    })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      newCommentText.value = ''
      replyDialog.value.visible = false
      replyDialog.value.content = ''
      fetchComments()
    } else { ElMessage.error(res.data.message) }
  } catch (e) { ElMessage.error('发送失败') }
}

// 点赞/踩评论
const handleCommentAction = async (id, type) => {
  if (!checkLogin()) return
  try {
    await axios.post(`http://localhost:8080/api/comments/${id}/action?type=${type}`)
    fetchComments() // 偷懒做法：刷新全表以更新数字
  } catch (e) { ElMessage.error('操作失败') }
}

// 删除评论
const deleteComment = (id) => {
  ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await axios.delete(`http://localhost:8080/api/comments/${id}`)
      ElMessage.success('已删除')
      fetchComments()
    } catch (e) { ElMessage.error('删除失败') }
  }).catch(() => {})
}

// 弹窗控制
const openReply = (parentId, targetUser) => { replyDialog.value = { visible: true, parentId, targetUser, content: '' } }
const openEdit = (c) => { editDialog.value = { visible: true, id: c.id, content: c.content } }
const openReport = (id) => { if (checkLogin()) reportDialog.value = { visible: true, commentId: id, reason: '', details: '' } }

// 提交编辑
const submitEdit = async () => {
  try {
    const res = await axios.put(`http://localhost:8080/api/comments/${editDialog.value.id}`, { content: editDialog.value.content })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      editDialog.value.visible = false
      fetchComments()
    } else { ElMessage.error(res.data.message) }
  } catch (e) { ElMessage.error('修改失败') }
}

// 提交举报
const submitReport = async () => {
  if (!reportDialog.value.reason) return ElMessage.warning('请选择举报原因')
  try {
    const res = await axios.post('http://localhost:8080/api/comments/report', {
      commentId: reportDialog.value.commentId, reporterUsername: currentUser,
      reason: reportDialog.value.reason, details: reportDialog.value.details
    })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      reportDialog.value.visible = false
    }
  } catch (e) { ElMessage.error('举报提交失败') }
}

const formatDate = (dateStr) => { return dateStr ? dateStr.substring(0, 16).replace('T', ' ') : '' }

onMounted(() => { loadDetail() })
</script>

<style scoped>
.detail-container { max-width: 900px; margin: 0 auto; padding: 20px; }
.content-box { background: #fff; border-radius: 12px; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
.header-area { text-align: center; border-bottom: 1px solid #f0f0f0; padding-bottom: 20px; margin-bottom: 30px; }
.title { font-size: 32px; color: #303133; margin: 10px 0 20px; }
.meta-info { display: flex; justify-content: center; gap: 30px; color: #606266; font-size: 14px; }
.media-area { text-align: center; margin-bottom: 30px; }
.main-image { max-width: 100%; max-height: 500px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.desc-area { font-size: 16px; color: #333; line-height: 1.8; margin-bottom: 40px; }
.desc-text { white-space: pre-wrap; background: #f9fafc; padding: 20px; border-radius: 8px; border-left: 4px solid #409EFF; }
.tags-wrap { margin-top: 20px; }
.action-area { display: flex; justify-content: center; gap: 20px; margin-bottom: 40px; }
.action-btn { width: 160px; font-size: 16px; font-weight: bold; border-radius: 25px; transition: transform 0.2s; }
.action-btn:hover { transform: scale(1.05); }

/* 评论区样式 */
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
.c-actions { display: flex; gap: 15px; font-size: 13px; color: #909399; }
.c-actions .action-btn { cursor: pointer; transition: color 0.2s; width: auto; font-weight: normal;}
.c-actions .action-btn:hover { color: #409EFF; }
.text-danger:hover { color: #F56C6C !important; }
.text-primary:hover { color: #409EFF !important; }

/* 楼中楼样式 */
.replies-box { margin-left: 55px; background: #fafafa; padding: 15px; border-radius: 8px; }
.reply-item { margin-bottom: 10px; border-bottom: 1px solid #f0f0f0; padding-bottom: 10px; }
.reply-item:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
</style>