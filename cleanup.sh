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

echo "=== PornViewer wassuccessful Cleanupped ==="