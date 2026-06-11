# ============================================
# 在线打包系统使用指南
# ============================================

## 📋 功能概述
本系统提供完整的Android应用在线打包解决方案，支持：
- ✅ 自动版本管理
- ✅ Debug/Release双模式构建
- ✅ Release APK自动签名
- ✅ GitHub Releases自动发布
- ✅ 构建产物归档和下载
- ✅ 构建状态通知

## 🚀 快速开始

### 1️⃣ 配置签名密钥（Release构建必须）

#### 方法A：生成新的Keystore
```bash
# 进入项目根目录
cd HuaJunMusic

# 生成keystore文件
keytool -genkeypair -v \
  -keystore app/keystore/release.keystore \
  -alias huajunmusic \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass your_password \
  -keypass your_password \
  -dname "CN=HuajunMusic, OU=Mobile, O=HuaJun, L=City, ST=State, C=CN"
```

#### 方法B：使用现有Keystore
将现有的`.keystore`或`.jks`文件复制到 `app/keystore/release.keystore`

#### 2️⃣ 配置签名属性
```bash
# 复制配置模板
cp app/keystore/keystore.properties.example app/keystore/keystore.properties

# 编辑配置文件，填入真实值
vim app/keystore/keystore.properties
```

### 3️⃣ 配置GitHub Secrets

在GitHub仓库中配置以下Secrets：

**Settings → Secrets and variables → Actions → New repository secret**

| Secret名称 | 说明 | 示例 |
|-----------|------|------|
| `KEYSTORE_BASE64` | Keystore文件的Base64编码 | （见下方生成方法） |
| `KEYSTORE_PASSWORD` | Keystore密码 | your_password |
| `KEY_ALIAS` | 密钥别名 | huajunmusic |
| `KEY_PASSWORD` | 密钥密码 | your_password |

**生成KEYSTORE_BASE64：**
```bash
# macOS/Linux
base64 -i app/keystore/release.keystore | pbcopy

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("app\keystore\release.keystore")) | clip
```

**可选通知配置：**
| Secret名称 | 说明 |
|-----------|------|
| `DINGTALK_WEBHOOK` | 钉钉机器人Webhook地址 |
| `WECHAT_WEBHOOK` | 企业微信机器人Webhook地址 |

## 🎯 触发构建方式

### 方式1：代码推送自动触发
```bash
# 推送到main/master分支 → 自动构建Debug APK
git push origin main

# 创建版本标签 → 自动构建Release APK并发布
git tag v1.0.0
git push origin v1.0.0
```

### 方式2：手动触发（推荐测试时使用）
1. 访问 GitHub仓库的 **Actions** 页面
2. 选择 **Android CI/CD Pipeline** 工作流
3. 点击 **Run workflow**
4. 选择参数：
   - **Build type**: release 或 debug
   - **Version name**: 可选，例如 1.2.0
5. 点击 **Run workflow**

## 📦 获取构建产物

### 从GitHub Artifacts下载
1. 访问 **Actions** 页面
2. 点击对应的workflow运行记录
3. 滚动到底部的 **Artifacts** 区域
4. 下载对应版本的APK文件
   - Debug版本：`huajunmusic-debug-v*.zip`
   - Release版本：`huajunmusic-release-v*.zip`

### 从GitHub Releases下载（仅Release版本）
访问：`https://github.com/你的用户名/HuaJunMusic/releases`

产物命名规则：
- **Debug**: `huajunmusic-debug-{versionName}.apk`
- **Release**: `huajunmusic-release-{versionName}.apk`

## 🔧 版本管理策略

### 版本号格式
- **Version Name**: 语义化版本，如 `1.2.0`, `2.0.0-beta1`
- **Version Code**: 自动递增整数（基于Git提交数+时间戳）

### 版本发布流程
```bash
# 1. 更新版本号（可选，也可在手动触发时指定）
# 编辑 app/build.gradle 中的 versionName

# 2. 提交代码
git add .
git commit -m("feat: 发布 v1.2.0")

# 3. 创建标签并推送
git tag v1.2.0
git push origin main --tags
```

## ⚠️ 注意事项

### 安全提醒
1. **切勿提交真实的keystore.properties到Git**
2. **妥善保管Keystore文件和密码**
3. **建议定期备份Keystore文件**
4. **丢失Keystore将无法更新已发布的App**

### 构建环境要求
- Java JDK 17+
- Android Gradle Plugin 7.1.3+
- Gradle 7.x+

### 常见问题

**Q: 构建失败提示签名错误？**
A: 检查GitHub Secrets中的签名配置是否正确。

**Q: 如何跳过签名直接构建Debug？**
A: 推送代码即可自动触发Debug构建（无需签名）。

**Q: Version Code冲突？**
A: 系统会自动基于Git提交数和时间戳生成唯一Version Code。

## 🔗 相关链接

- [GitHub Actions文档](https://docs.github.com/en/actions)
- [Android应用签名指南](https://developer.android.com/studio/publish/app-signing)
- [Gradle构建配置](https://developer.android.com/studio/build)

---
*最后更新：2026-06-11*
