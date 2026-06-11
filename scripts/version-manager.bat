@echo off
:: ============================================
:: HuaJunMusic - Version Manager (Windows)
:: ASCII compatible for CMD
:: ============================================

setlocal enabledelayedexpansion

set "PROJECT_ROOT=%~dp0.."
set "BUILD_GRADLE=%PROJECT_ROOT%\app\build.gradle"

echo ============================================
echo   HuaJunMusic - Version Manager
echo ============================================
echo.

:: Get current version
:get_current_version
for /f "tokens=2 delims='" %%a in ('findstr /C:"versionName" "%BUILD_GRADLE%"') do (
    set "VERSION_NAME=%%a"
    goto :got_version
)
:got_version
for /f "tokens=2 delims= " %%a in ('findstr /C:"versionCode" "%BUILD_GRADLE%"') do (
    set "VERSION_CODE=%%a"
    goto :got_code
)
:got_code
echo Current version: v%VERSION_NAME% (Build: %VERSION_CODE%)
goto :eof

:: Parse command line arguments
if "%1"=="" goto :show_help
if "%1"=="show" goto :do_show
if "%1"=="update" goto :do_update
if "%1"=="release" goto :do_release
if "%1"=="help" goto :show_help
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help

echo Error: Unknown command '%1'
goto :show_help

:do_show
call :get_current_version
goto :eof

:do_update
if "%2"=="" (
    echo Error: Please provide new version
    echo Usage: %0 update ^<version^>
    echo Example: %0 update 1.2.0
    exit /b 1
)

set "NEW_VERSION=%2"

:: Backup original file
copy "%BUILD_GRADLE%" "%BUILD_GRADLE%.backup" >nul

:: Update versionName
powershell -Command "(Get-Content '%BUILD_GRADLE%') -replace 'versionName .*', 'versionName ''%NEW_VERSION%''' | Set-Content '%BUILD_GRADLE%'"

:: Generate new versionCode based on timestamp
for /f %%a in ('powershell -Command "Get-Date -Format 'yyMMddHHmm'"') do set "TIMESTAMP=%%a"
set "NEW_CODE=%TIMESTAMP%"

:: Update versionCode
powershell -Command "(Get-Content '%BUILD_GRADLE%') -replace 'versionCode .*', 'versionCode %NEW_CODE%' | Set-Content '%BUILD_GRADLE%'"

echo.
echo [OK] Version updated:
echo    Version Name: v%NEW_VERSION%
echo    Version Code: %NEW_CODE%
echo.
echo Next steps:
echo    1. Commit changes: git add app/build.gradle ^&^& git commit -m("chore: bump version to v%NEW_VERSION%")
echo    2. Create tag: git tag v%NEW_VERSION%
echo    3. Push code: git push origin main --tags
goto :eof

:do_release
set "RELEASE_VERSION=%2"

if "%RELEASE_VERSION%"=="" (
    :: If not specified, read from build.gradle
    for /f "tokens=2 delims='" %%a in ('findstr /C:"versionName" "%BUILD_GRADLE%"') do (
        set "RELEASE_VERSION=%%a"
        goto :use_current_version
    )
)

:use_current_version
echo Will release version: v%RELEASE_VERSION%

:: Check if tag exists
git rev-parse "v%RELEASE_VERSION%" >nul 2>&1
if %errorlevel% equ 0 (
    echo Error: Tag v%RELEASE_VERSION% already exists
    exit /b 1
)

:: Create tag
git tag -a "v%RELEASE_VERSION%" -m "Release v%RELEASE_VERSION%"
echo [OK] Created tag: v%RELEASE_VERSION%

:: Push tag
echo Pushing to remote...
git push origin "v%RELEASE_VERSION%"
echo.
echo [OK] Tag pushed, will trigger online build!
echo.
echo Check build status:
for /f %%a in ('git remote get-url origin') do set "REMOTE_URL=%%a"
echo    https://github.com/%REMOTE_URL:~19,-4%/actions
goto :eof

:show_help
echo.
echo Usage: %0 ^<command^> [arguments]
echo.
echo Commands:
echo    show              Show current version
echo    update ^<version^>     Update version number
echo    release [version]    Create Release tag and trigger build
echo    help              Show this help message
echo.
echo Examples:
echo    %0 show                    :: Show current version
echo    %0 update 1.2.0           :: Update to v1.2.0
echo    %0 release 1.2.0          :: Release v1.2.0
echo    %0 release                :: Release with current version
echo.
goto :eof

endlocal
