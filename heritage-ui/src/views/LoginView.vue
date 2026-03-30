<template>
  <div class="login-container">
    <div class="glow-bg"></div>

    <div class="login-box">
      <div class="login-header">
        <h2 class="title">社区遗产资源共享平台</h2>
        <p class="subtitle">Heritage Resource Curation Platform</p>
      </div>

      <el-form :model="loginForm" :rules="loginRules" ref="loginRef" size="large">
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="账号或注册邮箱"
              prefix-icon="User"
              clearable>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="isLoading" @click="handleLogin">
            立即登录
          </el-button>

          <div class="flex-between" style="width: 100%; margin-top: 15px;">
            <el-link :underline="false" type="info" @click="showReg = true">加入非遗传承计划</el-link>
            <el-link :underline="false" type="warning" @click="openForgotPwd">忘记密码？</el-link>
          </div>
        </el-form-item>
      </el-form>

      <div class="login-footer"><span>© 2026 XJTLU CPT202 Project</span></div>
    </div>

    <el-dialog v-model="showReg" title="申请成为非遗贡献者" width="400px" center class="reg-dialog" append-to-body>
      <el-form :model="regForm" :rules="regRules" ref="regRef" label-position="top">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="regForm.username" placeholder="建议使用英文或数字" />
        </el-form-item>
        <el-form-item label="注册邮箱 (重要：找回密码凭证)" prop="email">
          <el-input v-model="regForm.email" placeholder="例如：user@xjtlu.edu.cn" />
        </el-form-item>
        <el-form-item label="登录密码" prop="password">
          <el-input v-model="regForm.password" type="password" show-password placeholder="请设置您的密码" />
        </el-form-item>
        <el-form-item label="真实姓名/昵称" prop="realName">
          <el-input v-model="regForm.realName" placeholder="我们将如何称呼您" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReg = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="isRegLoading">提交注册申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="forgotDialog.visible" title="找回密码" width="400px" center append-to-body>

      <div v-if="forgotDialog.step === 1">
        <p style="color: #666; font-size: 14px; margin-bottom: 20px;">请输入您注册时使用的邮箱，系统将为您发送包含验证码的邮件。</p>
        <el-input v-model="forgotDialog.email" placeholder="请输入注册邮箱" size="large" />
        <el-button type="primary" size="large" style="width: 100%; margin-top: 20px;" :loading="forgotDialog.loading" @click="sendCode">
          发送重置邮件
        </el-button>
      </div>

      <div v-else>
        <p style="color: #67C23A; font-size: 14px; margin-bottom: 20px;">验证码已发送至您的邮箱（本次请在 Java 控制台查看模拟邮件）。</p>
        <el-input v-model="forgotDialog.code" placeholder="请输入 6 位验证码" size="large" style="margin-bottom: 15px;" />
        <el-input v-model="forgotDialog.newPassword" type="password" show-password placeholder="请设置新密码" size="large" />
        <el-button type="success" size="large" style="width: 100%; margin-top: 20px;" @click="resetPwd">
          确认重置密码
        </el-button>
      </div>

    </el-dialog>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const isLoading = ref(false)
const isRegLoading = ref(false)

// --- 登录逻辑 (兼容账号与邮箱) ---
const loginRef = ref(null)
const loginForm = ref({ username: '', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入账号或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginRef.value.validate(async (valid) => {
    if (valid) {
      isLoading.value = true
      try {
        const res = await axios.post('http://localhost:8080/api/login', loginForm.value)
        if (res.data.success) {
          ElMessage.success(`欢迎回来！`)
          localStorage.setItem('currentUser', res.data.username)
          localStorage.setItem('userRole', res.data.role)
          router.push('/admin')
        } else {
          ElMessage.error(res.data.message)
        }
      } catch (err) {
        ElMessage.error('无法连接到服务器')
      } finally {
        isLoading.value = false
      }
    }
  })
}

// --- PBI 1: 注册逻辑 (新增邮箱字段) ---
const showReg = ref(false)
const regRef = ref(null)
const regForm = ref({ username: '', email: '', password: '', realName: '' })
const regRules = {
  username: [{ required: true, message: '账号是必填项', trigger: 'blur' }],
  email: [
    { required: true, message: '邮箱是必填项', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入您的称呼', trigger: 'blur' }]
}

const handleRegister = () => {
  regRef.value.validate(async (valid) => {
    if (valid) {
      isRegLoading.value = true
      try {
        const res = await axios.post('http://localhost:8080/api/users/register', regForm.value)
        if (res.data.success) {
          ElMessage.success(res.data.message)
          showReg.value = false
          loginForm.value.username = regForm.value.username
        } else {
          ElMessage.warning(res.data.message) // 账号或邮箱重复提示
        }
      } catch (err) {
        ElMessage.error('注册失败，请检查网络')
      } finally {
        isRegLoading.value = false
      }
    }
  })
}

// --- PBI 4: 找回密码逻辑 ---
const forgotDialog = ref({ visible: false, step: 1, email: '', code: '', newPassword: '', loading: false })

const openForgotPwd = () => {
  forgotDialog.value = { visible: true, step: 1, email: '', code: '', newPassword: '', loading: false }
}

const sendCode = async () => {
  if (!forgotDialog.value.email) return ElMessage.warning('请输入邮箱')
  forgotDialog.value.loading = true
  try {
    // 调用后端发邮件接口
    const res = await axios.post(`http://localhost:8080/api/users/forgot-password?email=${forgotDialog.value.email}`)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      forgotDialog.value.step = 2 // 进入输入验证码那一步
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (err) {
    ElMessage.error('发送失败')
  } finally {
    forgotDialog.value.loading = false
  }
}

const resetPwd = async () => {
  if (!forgotDialog.value.code || !forgotDialog.value.newPassword) return ElMessage.warning('请填写完整')
  try {
    const res = await axios.post(`http://localhost:8080/api/users/reset-password?email=${forgotDialog.value.email}&code=${forgotDialog.value.code}&newPassword=${forgotDialog.value.newPassword}`)
    if (res.data.success) {
      ElMessage.success('密码重置成功，请重新登录！')
      forgotDialog.value.visible = false
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (err) {
    ElMessage.error('重置失败')
  }
}
</script>

<style scoped>
/* 保持一致的背景与毛玻璃特效 */
.login-container { height: 100vh; width: 100vw; display: flex; justify-content: center; align-items: center; background: #1a1a2e; position: relative; overflow: hidden; }
.glow-bg { position: absolute; width: 600px; height: 600px; background: radial-gradient(circle, rgba(64,158,255,0.3) 0%, rgba(26,26,46,0) 70%); top: 50%; left: 50%; transform: translate(-50%, -50%); animation: pulse 4s infinite alternate; }
@keyframes pulse { 0% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; } 100% { transform: translate(-50%, -50%) scale(1.2); opacity: 1; } }
.login-box { width: 400px; padding: 40px; background: rgba(255, 255, 255, 0.05); backdrop-filter: blur(15px); -webkit-backdrop-filter: blur(15px); border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 16px; box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3); z-index: 1; }
.login-header { text-align: center; margin-bottom: 30px; }
.title { color: #fff; font-size: 24px; margin: 0 0 10px 0; letter-spacing: 2px; }
.subtitle { color: #a0a0b0; font-size: 13px; margin: 0; }
.login-btn { width: 100%; border-radius: 8px; font-weight: bold; letter-spacing: 2px; background: linear-gradient(90deg, #409EFF, #36cfc9); border: none; transition: all 0.3s ease; height: 45px; }
.login-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4); }
.flex-between { display: flex; justify-content: space-between; align-items: center; }
.login-footer { text-align: center; margin-top: 25px; font-size: 12px; color: #666; }
:deep(.el-input__wrapper) { background-color: rgba(0, 0, 0, 0.2) !important; box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset !important; }
:deep(.el-input__inner) { color: #fff !important; }
:deep(.el-form-item__label) { color: #ccc !important; }
</style>