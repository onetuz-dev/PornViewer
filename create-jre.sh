rm -rf ./pv-jre
jlink --add-modules java.base,jdk.crypto.ec,jdk.httpserver,java.desktop,java.logging,java.net.http,java.sql,jdk.unsupported --strip-debug --no-header-files --no-man-pages --compress=zip-9 --output ./pv-jre
rm -rf ./pv-jre/legal
clear
du -sh ./pv-jre
./pv-jre/bin/java -jar PornViewer.jar