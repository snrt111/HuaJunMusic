# ============================================
# 华军音乐 GitHub Actions 自动打包指南
# ============================================

## 📋 功能概述
本系统提供完整的 Android 应用在线打包解决方案，支持：
- ✅ **Debug APK 构建** - 无需签名，推送代码即可
- ✅ **Release APK 构建** - 可选自动签名
- ✅ **自动产物归档** - APK 自动上传至 Artifacts
- ✅ **GitHub Release 发布** - 打 tag 自动创建 Release
- ✅ **智能缓存加速** - Gradle 依赖智能缓存
- ✅ **构建摘要** - 自动生成美观的构建报告

## 📁 项目文件结构

```
HuaJunMusic/
├── .github/
│   └── workflows/
│       └── android-build.yml      ← GitHub Actions 工作流配置
├── app/
│   ├── build.gradle                ← 已配置签名支持（环境变量/本地文件）
│   └── keystore/
│       ├── keystore.properties.example  ← 签名配置模板
│       └── release.keystore        ← 你的签名证书（不要提交到 Git！）
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties  ← Gradle 版本配置（必须提交）
├── .gitignore                      ← 已配置安全忽略规则
├── build.gradle
└── settings.gradle
```

## 🚀 快速开始 - 3 步即可自动打包

### 步骤 1：推送到 GitHub

```bash
cd HuaJunMusic
git add .
git commit -m "feat: 更新华军音乐应用"
git push origin main
```

👉 自动触发 **Debug APK** 构建！

### 步骤 2：查看构建结果

1. 访问 GitHub 仓库 → **Actions** 页面
2. 看到名为 **Android Build** 的工作流正在运行
3. 等待构建完成（约 3-5 分钟）
4. 点击构建记录，滚动到底部 **Artifacts** 区域
5. 下载 APK 文件安装到手机！

### 步骤 3（可选）：配置 Release 签名

如需构建正式签名的 Release APK，继续阅读下方配置。

## 🔐 配置 Release APK 签名（重要）

### A. 生成签名证书（首次使用）

```bash
cd HuaJunMusic

# 创建 keystore 目录
mkdir -p app/keystore

# 生成 release keystore
keytool -genkeypair -v \
  -keystore app/keystore/release.keystore \
  -alias huajunmusic \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass your_strong_password \
  -keypass your_strong_password \
  -dname "CN=HuaJunMusic, OU=Mobile, O=HuaJun, L=Beijing, ST=Beijing, C=CN"
```

### B. 本地测试签名（可选）

```bash
# 复制配置模板
cp app/keystore/keystore.properties.example app/keystore/keystore.properties

# 编辑 keystore.properties，填入你的签名信息
# STORE_FILE=keystore/release.keystore
# STORE_PASSWORD=your_strong_password
# KEY_ALIAS=huajunmusic
# KEY_PASSWORD=your_strong_password

# 本地测试构建 Release APK
./gradlew assembleRelease
```

### C. 配置 GitHub Secrets（关键步骤！）

在 GitHub 仓库页面，进入：

**Settings → Secrets and variables → Actions**

配置以下 4 个 Secrets 和 1 个 Variable：

#### 1️⃣ Repository Variables（变量）

| 变量名 | 说明 |
|--------|------|
| `KEYSTORE_BASE64` | keystore 文件的 Base64 编码 |

生成 Base64 命令：

```bash
# macOS / Linux
base64 -i app/keystore/release.keystore | pbcopy

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("app\keystore\release.keystore")) | clip
```

#### 2️⃣ Repository Secrets（密钥）

| Secret 名称 | 说明 | 示例 |
|-------------|------|------|
| `KEYSTORE_PASSWORD` | keystore 的密码 | `your_strong_password` |
| `KEY_ALIAS` | 密钥别名 | `huajunmusic` |
| `KEY_PASSWORD` | 密钥的密码 | `your_strong_password` |

配置完成后，Release 构建将自动使用正式签名证书！

## 🎯 3 种触发构建的方式

### 方式 1：推送代码自动触发

```bash
# 推送到 main 或 master 分支
git push origin main
```

自动执行：
- ✅ 构建 Debug APK
- ✅ 构建 Release APK（如果配置了签名）
- ✅ 上传产物至 Artifacts

### 方式 2：手动触发（灵活选择）

1. 访问 GitHub **Actions** 页面
2. 选择 **Android Build** 工作流
3. 点击 **Run workflow** 按钮
4. 选择参数：
   - **build_type**: `both` (默认) / `debug` / `release`
   - **upload_artifacts**: `true` (默认)
5. 点击 **Run workflow** 运行！

### 方式 3：打 Tag 自动发布 Release

```bash
# 创建版本标签
git tag -a v1.0.0 -m "Release v1.0.0 - 首个正式版本"

# 推送标签
git push origin v1.0.0
```

自动执行：
- ✅ 构建 Debug APK
- ✅ 构建 Release APK（已签名）
- ✅ 自动创建 **GitHub Release**
- ✅ APK 自动附加到 Release Assets

👉 访问 Releases 页面：`https://github.com/你的用户名/HuaJunMusic/releases`

## 📦 获取构建产物

### 方式 A：从 Artifacts 下载（每次构建）

1. 在 **Actions** 页面点击构建记录
2. 滚动到页面底部 **Artifacts** 区域
3. 看到类似：
   - `HuaJunMusic-Debug-APK-1234567890` （保留 30 天）
   - `HuaJunMusic-Release-APK-1234567890` （保留 90 天）
4. 下载 → 解压 → 安装 APK 到手机！

### 方式 B：从 GitHub Releases 下载（打 Tag 后）

1. 访问仓库主页 → 点击右侧 **Releases**
2. 看到带版本号的 Release，如 **华军音乐 v1.0.0**
3. 在 **Assets** 区域下载 APK

## ⚙️ Workflow 工作原理详解

### Job 1: build-debug（Debug APK 构建）

- 运行环境：Ubuntu 最新版
- 步骤：
  1. 检出代码
  2. 安装 Java 17 (Temurin)
  3. 配置 Gradle 7.5 + 智能缓存
  4. 执行 `gradle assembleDebug`
  5. 上传 APK 到 Artifacts
  6. 生成构建摘要 Summary

### Job 2: build-release（Release APK 构建）

- 依赖：必须在 Debug 构建成功后执行
- 步骤：
  1. 检出代码
  2. 安装 Java 17
  3. 配置 Gradle 7.5
  4. **检测并解码签名密钥**（如果配置了 Secrets）
  5. 执行 `gradle assembleRelease`（注入签名环境变量）
  6. 验证签名
  7. 上传 Release APK

### Job 3: create-release（打 Tag 时创建 Release）

- 触发条件：推送 `v*` 格式的 Tag（如 `v1.0.0`）
- 步骤：
  1. 下载 Debug/Release APK
  2. 生成 Release Notes
  3. 创建 GitHub Release
  4. 将 APK 附加到 Release Assets

## 🔧 app/build.gradle 签名配置逻辑

`app/build.gradle` 中的签名配置采用**智能优先级策略**：

```
1. 优先读取环境变量（CI/CD 构建使用）
   ├─ KEYSTORE_FILE
   ├─ KEYSTORE_PASSWORD
   ├─ KEY_ALIAS
   └─ KEY_PASSWORD
   
2. 如果未配置环境变量 → 尝试读取本地文件
   └─ app/keystore/keystore.properties
   
3. 如果都未配置 → 构建不会失败
   └─ 提示警告，使用默认 debug 签名
```

这种设计确保：
- ✅ CI/CD 环境能无缝注入签名
- ✅ 本地开发者可选配置签名
- ✅ 缺少配置时**不会导致构建失败**
- ✅ 敏感信息不会硬编码在代码中

## ⚠️ 安全最佳实践

### ❌ 绝对不能提交到 Git 的文件

1. `app/keystore/release.keystore` - 签名证书
2. `app/keystore/keystore.properties` - 签名密码

👉 `.gitignore` 已自动保护这些文件，不要强制提交！

### ✅ 应该提交的配置文件

1. `.github/workflows/android-build.yml` - 工作流配置
2. `gradle/wrapper/gradle-wrapper.properties` - Gradle 版本配置
3. `app/keystore/keystore.properties.example` - 配置模板

### 💡 安全建议

1. **定期备份 keystore 文件**到安全位置
2. **使用强密码**（至少 16 位，混合字符）
3. **不要在多项目间共用同一个 keystore**
4. **丢失 keystore = 无法更新应用**，务必妥善保管！

## 🔍 常见问题 FAQ

### Q1: 第一次构建很慢？
**A**: 正常现象！首次构建需要下载全部 Gradle 依赖（约 200-500MB）。
后续构建启用缓存后，时间缩短至 2-3 分钟。

### Q2: 如何只构建 Debug APK（不配置签名）？
**A**: 推送代码即可自动构建 Debug APK！无需配置任何 Secrets。
Debug APK 使用 Android 默认 debug 签名，可安装但不能上架应用市场。

### Q3: Release APK 安装后提示签名错误？
**A**: 检查 4 个 Secrets 是否正确，特别是：
- `KEYSTORE_BASE64` 是否完整（不要有换行）
- 密码是否与生成 keystore 时一致
- `KEY_ALIAS` 是否正确（默认是 `huajunmusic`）

### Q4: 如何在本地测试构建？
**A**: 使用 Android Studio 打开项目，或运行命令：
```bash
# Debug 构建
./gradlew assembleDebug

# Release 构建（需配置签名）
./gradlew assembleRelease
```

### Q5: 构建失败，如何查看日志？
**A**: 在 Actions 页面点击失败的 Job → 展开失败步骤 → 查看完整日志。
常见错误原因：
- 网络问题（重试即可）
- 签名配置错误
- 代码编译错误

### Q6: Gradle Wrapper JAR 需要提交吗？
**A**: 本项目使用 `gradle/actions/setup-gradle@v3`，**不需要**提交 gradle-wrapper.jar！
只需提交 `gradle-wrapper.properties`，Action 会自动下载对应版本的 Gradle。

## 📊 构建时间参考

| 构建类型 | 首次构建（无缓存） | 重复构建（有缓存） |
|---------|-------------------|-------------------|
| Debug APK | 3-5 分钟 | 1-2 分钟 |
| Release APK | 4-6 分钟 | 2-3 分钟 |
| 完整流程 | 7-11 分钟 | 3-5 分钟 |

## 🔗 参考链接

- [GitHub Actions 官方文档](https://docs.github.com/en/actions)
- [Android 应用签名官方指南](https://developer.android.com/studio/publish/app-signing)
- [Gradle 构建系统指南](https://developer.android.com/studio/build)
- [setup-gradle Action](https://github.com/gradle/actions)

## 📝 修改记录

- **2026-06-12**: 重构工作流，使用 gradle/actions/setup-gradle，移除本地 wrapper jar 依赖
- **2026-06-11**: 添加签名智能配置逻辑，支持环境变量/本地文件双模式

---
*本文档随项目同步更新*
