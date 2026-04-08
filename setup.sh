#!/bin/bash
# setup.sh — автоматическая установка Java, Maven и сборка PornViewer

set -e

echo "=== PornViewer Setup for Linux ==="

# Создание временных папок
mkdir -p pv_temp/java
mkdir -p pv_temp/maven

cd pv_temp

# 1. Java
echo "📥 Скачиваем Java 21..."
curl -L -o jdk-21.jdk.tar.gz https://download.oracle.com/java/21/archive/jdk-21.0.2_linux-x64_bin.tar.gz

echo "📦 Распаковываем Java..."
tar -xzf jdk-21.jdk.tar.gz -C ./java
export JAVA_HOME=~/PornViewer-pv-linux/pv_temp/java/jdk-21.0.2
export PATH=$JAVA_HOME/bin:$PATH

# 2. Maven
echo "📥 Скачиваем Maven..."
curl -L -o maven-3.9.14-bin.tar.gz https://dlcdn.apache.org/maven/maven-3/3.9.14/binaries/apache-maven-3.9.14-bin.tar.gz

echo "📦 Распаковываем Maven..."
tar -xzf maven-3.9.14-bin.tar.gz -C ./maven
export M2_HOME=~/PornViewer-pv-linux/pv_temp/maven/apache-maven-3.9.14
export PATH=$M2_HOME/bin:$PATH

echo "=== PornViewer was setupped with dependencies ==="