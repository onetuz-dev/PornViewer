#!/bin/bash
echo "🔨 Building Windows installer for PornViewer..."

echo "📦 Creating Windows installer..."
wine "C:/Program Files/Java/jdk-21/bin/jpackage.exe" \
              --type exe \
              --input input-pv/ \
              --name PornViewer \
              --main-jar PornViewer.jar \
              --main-class com.plovdev.pornviewer.Launcher \
              --dest installer/ \
              --resource-dir resources-pv/ \
              --license-file files/LICENSE.txt \
              --icon files/pv-logo.ico \
              --app-version "1.7.0" \
              --vendor "PlovDev" \
              --copyright "© 2026 PlovDev. Все права защищены." \
              --description "Безопасный и анонимный просмотр видео 18+ с шифрованием и поддержкой плагинов" \
              --java-options "-Xmx2G" \
              --java-options "-Dfile.encoding=UTF-8" \
              --file-associations files/video-assoc.properties \
              --win-dir-chooser \
              --win-shortcut \
              --win-menu \
              --verbose \
              --temp build_temp

if [ $? -eq 0 ]; then
    echo "✅ Done! Installer in installer-windows/"
    ls -la installer-windows/
else
    echo "❌ Build failed!"
fi
