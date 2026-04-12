#!/bin/bash
# cleanup.sh — полная очистка следов PornViewer

set -e

echo "=== Cleanup PornViewer ==="

cd ~/

# All data in ~/PornViewer-pv-linux/* and ~/.PornViewer/*
rm -rf ~/PornViewer-pv-linux
echo "Удалены сборки, бинарники и исходники приложения"

# Удаление данных приложения
rm -rf ~/.PornViewer
echo "Удалены данные приложения (~/.PornViewer)"

echo "🧹 Очищаем историю..."
history -c 2>/dev/null || true
rm -f ~/.bash_history ~/.zsh_history 2>/dev/null || true
unset JAVA_HOME M2_HOME PATH

echo "=== PornViewer wassuccessful Cleanupped ==="