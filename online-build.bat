@echo off
:: ============================================
:: HuaJunMusic - Online Build Tool (Windows)
:: Compatible with CMD (ASCII only)
:: ============================================

echo.
echo +==========================================+
echo |    HuaJunMusic - Online Build System     |
echo +==========================================+
echo.

:: Check Git
where git >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Git not found
    echo   Download: https://git-scm.com/downloads
    pause
    exit /b 1
)

:menu
echo Please select:
echo.
echo [1] Show current version
echo [2] Update version number
echo [3] Create Release and trigger build
echo [4] Generate signing key (first time)
echo [5] Open GitHub Actions page
echo [6] Show download instructions
echo [7] Open documentation
echo [0] Exit
echo.
set /p choice=Enter option (0-7):

if "%choice%"=="1" goto :show_version
if "%choice%"=="2" goto :update_version
if "%choice%"=="3" goto :create_release
if "%choice%"=="4" goto :generate_keystore
if "%choice%"=="5" goto :open_actions
if "%choice%"=="6" goto :show_download_help
if "%choice%"=="7" goto :open_docs
if "%choice%"=="0" goto :exit
goto :menu

:show_version
echo.
call scripts\version-manager.bat show
pause
goto :menu

:update_version
echo.
set /p new_version=Enter new version (e.g: 1.2.0):
if "%new_version%"=="" (
    echo [ERROR] Version cannot be empty
    pause
    goto :menu
)
call scripts\version-manager.bat update %new_version%
pause
goto :menu

:create_release
echo.
set /p release_ver=Enter release version (empty for current):
call scripts\version-manager.bat release %release_ver%
pause
goto :menu

:generate_keystore
echo.
echo ===========================================
echo   Signing Key Generation Wizard
echo ===========================================
echo.

:: Check keytool
where keytool >nul 2>&1
if %errorlevel% neq 0 (
    echo Searching for keytool...
    for /f "delims=" %%i in ('dir /s /b "%JAVA_HOME%\bin\keytool.exe" 2^>nul') do set "KEYTOOL_PATH=%%i"
    
    if not defined KEYTOOL_PATH (
        echo [ERROR] keytool not found
        echo   Please install JDK and set JAVA_HOME
        pause
        goto :menu
    )
) else (
    set "KEYTOOL_PATH=keytool"
)

set /p keystore_pass=Enter Keystore password (12+ chars recommended):
if "%keystore_pass%"=="" (
    echo [ERROR] Password cannot be empty
    pause
    goto :menu
)

set /p key_alias=Enter key alias (default: huajunmusic):
if "%key_alias%"=="" set "key_alias=huajunmusic"

set /p key_pass=Enter key password (same as keystore):
if "%key_pass%"=="" set "key_pass=%keystore_pass%"

echo.
echo Generating signing key...
echo.

:: Create keystore directory
if not exist "app\keystore" mkdir app\keystore

:: Generate keystore
"%KEYTOOL_PATH%" -genkeypair -v ^
  -keystore app\keystore\release.keystore ^
  -alias %key_alias% ^
  -keyalg RSA ^
  -keysize 2048 ^
  -validity 10000 ^
  -storepass %keystore_pass% ^
  -keypass %key_pass% ^
  -dname "CN=HuajunMusic, OU=Mobile, O=HuaJun, L=City, ST=State, C=CN"

if %errorlevel% equ 0 (
    echo.
    echo [OK] Signing key generated!
    echo   File location: app\keystore\release.keystore
    
    :: Create keystore.properties
    (
        echo STORE_FILE=keystore/release.keystore
        echo STORE_PASSWORD=%keystore_pass%
        echo KEY_ALIAS=%key_alias%
        echo KEY_PASSWORD=%key_pass%
    ) > app\keystore\keystore.properties
    
    echo   Config file: app\keystore\keystore.properties
    echo.
    echo [IMPORTANT]
    echo   1. Backup the Keystore file safely
    echo   2. Lost Keystore = cannot update published app
) else (
    echo.
    echo [FAILED] Key generation failed
)
echo.
pause
goto :menu

:open_actions
echo.
echo Opening GitHub Actions...
for /f %%a in ('git remote get-url origin') do set "REMOTE_URL=%%a"
start https://github.com/%REMOTE_URL:~19,-4%/actions
goto :menu

:show_download_help
echo.
echo ===========================================
echo   Build Artifact Download Guide
echo ===========================================
echo.
echo Method 1: GitHub Artifacts (Recommended)
echo   1. Go to repository Actions page
echo   2. Click on the build record
echo   3. Download APK from Artifacts section at bottom
echo.
echo Method 2: GitHub Releases (Release only)
echo   URL: https://github.com/username/HuaJunMusic/releases
echo.
echo Naming convention:
echo   Debug: HuajunMusic-debug-v{version}.apk
echo   Release: HuajunMusic-release-v{version}.apk
echo.
pause
goto :menu

:open_docs
echo.
echo Opening documentation...
start "" ONLINE_BUILD_GUIDE.md
goto :menu

:exit
echo.
echo Thank you for using HuaJunMusic Online Build!
echo.
timeout /t 2 >nul
exit /b 0
