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
            placeholder="账号 (admin / guest01)"
            prefix-icon="User"
            clearable>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码 (123456)"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="isLoading"
            @click="handleLogin">
            立即登录
          </el-button>

          <div class="reg-link">
            <el-link :underline="false" type="info" @click="showReg = true">
              没有账号？加入非遗传承计划
            </el-link>
          </div>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>© 2026 XJTLU CPT202 Project</span>
      </div>
    </div>

    <el-dialog
      v-model="showReg"
      title="申请成为非遗贡献者"
      width="400px"
      center
      class="reg-dialog"
      append-to-body
    >
      <el-form :model="regForm" :rules="regRules" ref="regRef" label-position="top">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="regForm.username" placeholder="建议使用英文或数字" />
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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const isLoading = ref(false)
const isRegLoading = ref(false)

// --- 登录逻辑 ---
const loginRef = ref(null)
const loginForm = ref({ username: '', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginRef.value.validate(async (valid) => {
    if (valid) {
      isLoading.value = true
      try {
        const res = await axios.post('http://localhost:8080/api/login', loginForm.value)
        if (res.data.success) {
          ElMessage.success(`欢迎回来，${res.data.username}！`)
          localStorage.setItem('currentUser', res.data.username)
          localStorage.setItem('userRole', res.data.role)
          router.push('/admin') // 登录成功跳转到后台
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

// --- 注册逻辑 ---
const showReg = ref(false)
const regRef = ref(null)
const regForm = ref({ username: '', password: '', realName: '' })
const regRules = {
  username: [{ required: true, message: '账号是必填项', trigger: 'blur' }],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入您的称呼', trigger: 'blur' }]
}

const handleRegister = () => {
  regRef.value.validate(async (valid) => {
    if (valid) {
      isRegLoading.value = true
      try {
        await axios.post('http://localhost:8080/api/users/register', regForm.value)
        ElMessage.success('恭喜！注册成功，请开始登录吧')
        showReg.value = false
        // 注册成功后把账号填入登录框，方便用户直接登录
        loginForm.value.username = regForm.value.username
      } catch (err) {
        ElMessage.error('该账号可能已被占用')
      } finally {
        isRegLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 保持一致的背景与毛玻璃特效 */
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #1a1a2e;
  position: relative;
  overflow: hidden;
}

.glow-bg {
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(64,158,255,0.3) 0%, rgba(26,26,46,0) 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse 4s infinite alternate;
}

@keyframes pulse {
  0% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  100% { transform: translate(-50%, -50%) scale(1.2); opacity: 1; }
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3);
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  color: #fff;
  font-size: 24px;
  margin: 0 0 10px 0;
  letter-spacing: 2px;
}

.subtitle {
  color: #a0a0b0;
  font-size: 13px;
  margin: 0;
}

.login-btn {
  width: 100%;
  border-radius: 8px;
  font-weight: bold;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #409EFF, #36cfc9);
  border: none;
  transition: all 0.3s ease;
  height: 45px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.reg-link {
  text-align: center;
  margin-top: 15px;
}

.login-footer {
  text-align: center;
  margin-top: 25px;
  font-size: 12px;
  color: #666;
}

:deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.2) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset !important;
}
:deep(.el-input__inner) { color: #fff !important; }
:deep(.el-form-item__label) { color: #ccc !important; }
</style>