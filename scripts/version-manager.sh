#!/bin/bash

# ============================================
# 华军音乐 - 版本管理工具
# 功能：自动更新版本号、创建标签、触发发布
# ============================================

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD_GRADLE="${PROJECT_ROOT}/app/build.gradle"

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  华军音乐 - 版本管理工具${NC}"
echo -e "${BLUE}============================================${NC}"

# 获取当前版本号
get_current_version() {
    local version_name=$(grep 'versionName' "${BUILD_GRADLE}" | head -1 | sed "s/.*'\(.*\)'.*/\1/")
    local version_code=$(grep 'versionCode' "${BUILD_GRADLE}" | head -1 | sed "s/.*\(.*\).*/\1/")
    echo -e "${GREEN}当前版本:${NC} v${version_name} (Build: ${version_code})"
}

# 更新版本号
update_version() {
    local new_version=$1

    if [ -z "$new_version" ]; then
        echo -e "${RED}错误: 请提供新版本号${NC}"
        echo "用法: $0 update <版本号>"
        echo "示例: $0 update 1.2.0"
        exit 1
    fi

    # 验证版本号格式（语义化版本）
    if ! [[ "$new_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9.]+)?$ ]]; then
        echo -e "${RED}错误: 版本号格式不正确${NC}"
        echo "正确格式: 主版本.次版本.修订版 (例如: 1.2.0, 2.0.0-beta1)"
        exit 1
    fi

    # 备份原文件
    cp "${BUILD_GRADLE}" "${BUILD_GRADLE}.backup"

    # 更新versionName
    sed -i.bak "s/versionName .*/versionName '${new_version}'/" "${BUILD_GRADLE}"

    # 自动递增versionCode（基于Git提交数）
    local new_code=$(git rev-list --count HEAD)
    local timestamp=$(date +%y%m%d%H)
    new_code="${new_code}${timestamp}"

    sed -i.bak "s/versionCode .*/versionCode ${new_code}/" "${BUILD_GRADLE}"

    # 清理备份文件
    rm -f "${BUILD_GRADLE}.bak"

    echo -e "${GREEN}✅ 版本已更新:${NC}"
    echo -e "   Version Name: v${new_version}"
    echo -e "   Version Code: ${new_code}"

    # 提示下一步操作
    echo ""
    echo -e "${YELLOW}下一步操作:${NC}"
    echo "  1. 提交更改: git add app/build.gradle && git commit -m('chore: bump version to v${new_version}')"
    echo "  2. 创建标签: git tag v${new_version}"
    echo "  3. 推送代码: git push origin main --tags"
}

# 创建Release标签并推送
create_release() {
    local version=$1

    if [ -z "$version" ]; then
        # 如果未指定版本，从build.gradle读取
        version=$(grep 'versionName' "${BUILD_GRADLE}" | head -1 | sed "s/.*'\(.*\)'.*/\1/")
        echo -e "${YELLOW}未指定版本号，使用当前版本: v${version}${NC}"
    fi

    # 检查是否已有该标签
    if git rev-parse "v${version}" >/dev/null 2>&1; then
        echo -e "${RED}错误: 标签 v${version} 已存在${NC}"
        exit 1
    fi

    # 检查是否有未提交的更改
    if [ -n "$(git status --porcelain)" ]; then
        echo -e "${YELLOW}警告: 存在未提交的更改${NC}"
        read -p "是否继续？(y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi

    # 创建标签
    git tag -a "v${version}" -m "Release v${version}"
    echo -e "${GREEN}✅ 已创建标签: v${version}${NC}"

    # 推送标签
    echo -e "${BLUE}正在推送到远程仓库...${NC}"
    git push origin "v${version}"
    echo -e "${GREEN}✅ 标签已推送，将自动触发在线打包！${NC}"
    echo ""
    echo -e "${YELLOW}查看构建状态:${NC}"
    echo "  https://github.com/$(git remote get-url origin | sed 's/.*://;s/.git$//')/actions"
}

# 显示帮助信息
show_help() {
    echo ""
    echo "用法: $0 <命令> [参数]"
    echo ""
    echo "命令:"
    echo "  show              显示当前版本号"
    echo "  update <版本>     更新版本号"
    echo "  release [版本]    创建Release标签并触发打包"
    echo "  help              显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 show                    # 查看当前版本"
    echo "  $0 update 1.2.0           # 更新到v1.2.0"
    echo "  $0 release 1.2.0          # 发布v1.2.0版本"
    echo "  $0 release                # 使用当前版本发布"
    echo ""
}

# ============================================
# 主程序入口
# ============================================
case "$1" in
    show|status)
        get_current_version
        ;;
    update|bump)
        update_version "$2"
        ;;
    release|publish|tag)
        create_release "$2"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        if [ -z "$1" ]; then
            echo -e "${RED}错误: 未指定命令${NC}"
        else
            echo -e "${RED}错误: 未知命令 '$1'${NC}"
        fi
        show_help
        exit 1
        ;;
esac

echo ""
echo -e "${BLUE}============================================${NC}"
