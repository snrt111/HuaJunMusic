@echo off
:: ============================================
:: HuaJunMusic - Debug Build Helper
:: Fix SDK license and dependency issues
:: ASCII compatible for CMD
:: ============================================

echo.
echo +==========================================+
echo |    HuaJunMusic - Build Helper            |
echo +==========================================+
echo.

set "ANDROID_SDK_ROOT=D:\yjr\tools\Android\SDK"
set "LICENSES_DIR=%ANDROID_SDK_ROOT%\licenses"

:: Check SDK path
if not exist "%ANDROID_SDK_ROOT%" (
    echo [ERROR] Android SDK path not found
    echo   Current: %ANDROID_SDK_ROOT%
    echo   Check ANDROID_SDK_ROOT env variable
    pause
    exit /b 1
)

echo [OK] Android SDK: %ANDROID_SDK_ROOT%
echo.

:: Create licenses directory
if not exist "%LICENSES_DIR%" (
    mkdir "%LICENSES_DIR%"
    echo [OK] Created licenses directory
)

:: Write license files
echo Configuring SDK licenses...
(
    echo 24333f8a63b6825ea9c5514f83c2829b004d59fee
) > "%LICENSES_DIR%\android-sdk-license" 2>nul

(
    echo 84831b9409646a918e30573bab4c9c91346d8abd
) > "%LICENSES_DIR%\android-sdk-preview-license" 2>nul

(
    echo d975f751698a77b662f1254ddbeed3901e976f5a
) > "%LICENSES_DIR%\android-sdk-preview-sysimg-license" 2>nul

if exist "%LICENSES_DIR%\android-sdk-license" (
    echo [OK] SDK license configured
) else (
    echo [WARN] Cannot write license file automatically
    echo   Manual steps:
    echo.
    echo   1. Open Android Studio
    echo   2. Go to Tools ^> SDK Manager
    echo   3. Click Edit and accept all licenses
    echo   4. Or run: sdkmanager --licenses
    echo.
)

echo.
echo ===========================================
echo   Building Debug APK
echo ===========================================
echo.

:: Execute Gradle build
call gradlew.bat assembleDebug --no-daemon --stacktrace

if %errorlevel% equ 0 (
    echo.
    echo +==========================================+
    echo |           BUILD SUCCESSFUL!              |
    echo +==========================================+
    echo.
    echo APK Location:
    dir /b app\build\outputs\apk\debug\*.apk
    
    echo.
    echo Tips:
    echo   - Debug APK can be installed directly on phone
    echo   - For Release version, configure signing key first
    echo   - Run online-build.bat for more options
) else (
    echo.
    echo +==========================================+
    echo |             BUILD FAILED!               |
    echo +==========================================+
    echo.
    echo Possible causes:
    echo   1. Android SDK not installed correctly
    echo   2. Missing SDK components (build-tools, platform)
    echo   3. JAVA_HOME environment variable not set
    echo.
    echo Suggested actions:
    echo   1. Open Android Studio and sync project
    echo   2. Or manually install missing SDK components
)

echo.
pause
