<template>
  <!-- Back to home button -->
  <el-button class="back-home-btn" @click="router.push('/')">← Back to Home</el-button>

  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <h2>HERITAGE</h2>
        <p>Community Heritage Resource Sharing Platform</p>
      </div>

      <!-- Login form -->
      <el-form :model="loginForm" :rules="loginRules" ref="loginRef" size="large">
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="Username or registered email"
              clearable>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="Password"
              show-password
              @keyup.enter="handleLogin">
          </el-input>
        </el-form-item>

        <!-- Privacy policy checkbox (login) -->
        <el-form-item prop="agreePrivacy">
          <el-checkbox v-model="loginAgreePrivacy">
            <span class="privacy-check-text">
              I have read and agree to the
              <el-button type="primary" link @click.stop="showPrivacy = true">Privacy Policy & User Agreement</el-button>
            </span>
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              class="login-btn"
              :loading="isLoading"
              @click="handleLogin"
              :disabled="!loginAgreePrivacy">
            Login Now
          </el-button>

          <div class="flex-between" style="width: 100%; margin-top: 15px;">
            <el-link :underline="false" type="info" @click="showReg = true">Join Heritage Inheritance Program</el-link>
            <el-link :underline="false" type="warning" @click="openForgotPwd">Forgot password?</el-link>
          </div>
        </el-form-item>
      </el-form>

      <div class="login-footer"><span>© 2026 XJTLU CPT202 Project</span></div>
    </div>

    <!-- Registration dialog -->
    <el-dialog v-model="showReg" title="Apply to be Heritage Contributor" width="500px" center>
      <el-form :model="regForm" :rules="regRules" ref="regRef" label-position="top">
        <el-form-item label="Login Username" prop="username">
          <el-input v-model="regForm.username" placeholder="Recommended: English or numbers" />
        </el-form-item>
        <el-form-item label="Registration Email" prop="email">
          <el-input v-model="regForm.email" placeholder="Example: user@xjtlu.edu.cn" />
        </el-form-item>
        <el-form-item label="Login Password" prop="password">
          <el-input v-model="regForm.password" type="password" show-password placeholder="Please set your password" />
        </el-form-item>
        <el-form-item label="Real Name/Nickname" prop="realName">
          <el-input v-model="regForm.realName" placeholder="How we will address you" />
        </el-form-item>
        <el-form-item label="Birthday" prop="birthday">
          <el-date-picker
              v-model="regForm.birthday"
              type="date"
              placeholder="Please select your birthday"
              style="width: 100%"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <!-- Privacy policy checkbox (registration) -->
        <el-form-item prop="agreePrivacy">
          <el-checkbox v-model="regAgreePrivacy">
            <span class="privacy-check-text">
              I have read and agree to the
              <el-button type="primary" link @click.stop="showPrivacy = true">Privacy Policy & User Agreement</el-button>
            </span>
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReg = false">Cancel</el-button>
        <el-button type="primary" @click="handleRegister" :loading="isRegLoading" :disabled="!regAgreePrivacy">
          Submit Registration Application
        </el-button>
      </template>
    </el-dialog>

    <!-- Forgot password dialog (unchanged) -->
    <el-dialog v-model="forgotDialog.visible" title="Recover Password" width="400px" center>
      <div v-if="forgotDialog.step === 1">
        <p style="color: #666; font-size: 14px; margin-bottom: 20px;">
          Please enter the email you used for registration.
        </p>
        <el-input v-model="forgotDialog.email" placeholder="Please enter your registration email" size="large" />
        <el-button type="primary" size="large" style="width: 100%; margin-top: 20px;" :loading="forgotDialog.loading" @click="sendCode">
          Send Reset Email
        </el-button>
      </div>

      <div v-else>
        <p style="color: #67C23A; font-size: 14px; margin-bottom: 20px;">
          Verification code has been sent to your email.
        </p>
        <el-input v-model="forgotDialog.code" placeholder="Please enter 6-digit verification code" size="large" style="margin-bottom: 15px;" />
        <el-input v-model="forgotDialog.newPassword" type="password" show-password placeholder="Please set a new password" size="large" />
        <el-button type="success" size="large" style="width: 100%; margin-top: 20px;" @click="resetPwd">
          Confirm Password Reset
        </el-button>
      </div>
    </el-dialog>

    <!-- Privacy policy full text dialog -->
    <el-dialog v-model="showPrivacy" title="Privacy Policy & User Agreement" width="700px" top="5vh">
      <div class="privacy-content">
        <p>Welcome to Heritage Sharing Platform. By using this platform, you agree to the following terms:</p>
        <h3>1. User Information Collection & Usage</h3>
        <p>We collect your username, email, nickname, and optionally birthday for account creation and password recovery. Your password is encrypted and never shared with third parties.</p>
        <h3>2. Resource Copyright & License</h3>
        <p>As a contributor, all content you post must be original or legally authorized. You grant us a non-exclusive, royalty-free license to display and promote your content on the platform. You retain full ownership.</p>
        <h3>3. Comments & Interactions</h3>
        <p>Your comments and actions are recorded. You agree not to post any illegal, harassing, or false content. Administrators may remove violating content.</p>
        <h3>4. Geographical Information</h3>
        <p>You can optionally provide location info for resources. We do not actively collect precise GPS data from your device.</p>
        <h3>5. Data Security</h3>
        <p>We use industry-standard encryption, but cannot guarantee 100% security. You accept this risk when using the platform.</p>
        <h3>6. Policy Updates</h3>
        <p>This policy may change. Continued use means you accept the updated terms.</p>
        <p>If you have questions, contact: admin@heritage.org</p>
        <p><strong>Last updated: May 4, 2026</strong></p>
      </div>
      <template #footer>
        <el-button type="primary" @click="showPrivacy = false">Close</el-button>
      </template>
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

// Privacy policy dialog visibility
const showPrivacy = ref(false)
// Checkbox states for login and registration
const loginAgreePrivacy = ref(false)
const regAgreePrivacy = ref(false)

// Login logic
const loginRef = ref(null)
const loginForm = ref({ username: '', password: '' })
const loginRules = {
  username: [{ required: true, message: 'Please enter username or email', trigger: 'blur' }],
  password: [{ required: true, message: 'Please enter password', trigger: 'blur' }]
}

const handleLogin = () => {
  if (!loginAgreePrivacy.value) {
    ElMessage.warning('Please agree to the Privacy Policy first')
    return
  }
  loginRef.value.validate(async (valid) => {
    if (valid) {
      isLoading.value = true
      try {
        const res = await axios.post('http://116.62.165.182:8080/api/login', loginForm.value)
        if (res.data.success) {
          ElMessage.success('Welcome back!')
          localStorage.setItem('currentUser', res.data.username)
          localStorage.setItem('userRole', res.data.role)
          router.push('/admin')
        } else {
          ElMessage.error(res.data.message)
        }
      } catch (err) {
        ElMessage.error('Cannot connect to server')
      } finally {
        isLoading.value = false
      }
    }
  })
}

// Registration logic
const showReg = ref(false)
const regRef = ref(null)
const regForm = ref({
  username: '',
  email: '',
  password: '',
  realName: '',
  birthday: ''
})
const regRules = {
  username: [{ required: true, message: 'Username is required', trigger: 'blur' }],
  email: [
    { required: true, message: 'Email is required', trigger: 'blur' },
    { type: 'email', message: 'Please enter correct email format', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: 'Please set password', trigger: 'blur' },
    { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' }
  ],
  realName: [{ required: true, message: 'Please enter your name', trigger: 'blur' }]
}

const handleRegister = () => {
  if (!regAgreePrivacy.value) {
    ElMessage.warning('Please agree to the Privacy Policy first')
    return
  }
  regRef.value.validate(async (valid) => {
    if (valid) {
      isRegLoading.value = true
      try {
        const res = await axios.post('http://116.62.165.182:8080/api/users/register', regForm.value)
        if (res.data.success) {
          ElMessage.success(res.data.message)
          showReg.value = false
          loginForm.value.username = regForm.value.username
          regAgreePrivacy.value = false // Reset checkbox after successful registration
        } else {
          ElMessage.warning(res.data.message)
        }
      } catch (err) {
        ElMessage.error('Registration failed')
      } finally {
        isRegLoading.value = false
      }
    }
  })
}

// Forgot password logic
const forgotDialog = ref({ visible: false, step: 1, email: '', code: '', newPassword: '', loading: false })

const openForgotPwd = () => {
  forgotDialog.value = { visible: true, step: 1, email: '', code: '', newPassword: '', loading: false }
}

const sendCode = async () => {
  if (!forgotDialog.value.email) return ElMessage.warning('Please enter email')
  forgotDialog.value.loading = true
  try {
    const res = await axios.post(`http://116.62.165.182:8080/api/users/forgot-password?email=${forgotDialog.value.email}`)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      forgotDialog.value.step = 2
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (err) {
    ElMessage.error('Sending failed')
  } finally {
    forgotDialog.value.loading = false
  }
}

const resetPwd = async () => {
  if (!forgotDialog.value.code || !forgotDialog.value.newPassword) return ElMessage.warning('Please fill in completely')
  try {
    const res = await axios.post(`http://116.62.165.182:8080/api/users/reset-password?email=${forgotDialog.value.email}&code=${forgotDialog.value.code}&newPassword=${forgotDialog.value.newPassword}`)
    if (res.data.success) {
      ElMessage.success('Password reset successfully, please login again!')
      forgotDialog.value.visible = false
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (err) {
    ElMessage.error('Reset failed')
  }
}
</script>

<style scoped>
/* Back to home button */
.back-home-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
  font-size: 15px;
  color: #666;
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
}
.back-home-btn:hover {
  color: #111;
  border-color: #999;
}

/* Login page layout */
.login-page {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #ffffff;
}

.login-box {
  width: 420px;
  padding: 50px 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 2.4rem;
  font-weight: 700;
  letter-spacing: 6px;
  color: #111;
  margin-bottom: 8px;
}

.login-header p {
  color: #666;
  font-size: 15px;
}

.login-btn {
  width: 100%;
  height: 52px;
  font-size: 17px;
  font-weight: 500;
  letter-spacing: 1px;
  background-color: #1e3a5f !important;
  border: none;
  border-radius: 8px;
  color: #fff;
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  font-size: 13px;
  color: #999;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Privacy policy checkbox text styling */
.privacy-check-text {
  white-space: normal; /* Allow text to wrap */
  line-height: 1.5;
}

/* Privacy policy full text dialog */
.privacy-content {
  max-height: 50vh;
  overflow-y: auto;
  line-height: 1.8;
  padding: 20px;
  color: #333;
  font-size: 14px;
}
.privacy-content h3 {
  margin-top: 20px;
  font-size: 16px;
  color: #1e3a5f;
}
</style>