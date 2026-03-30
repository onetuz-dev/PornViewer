[Setup]
AppName=PornViewer
AppVersion=1.7.0
DefaultDirName={pf}\PornViewer
DefaultGroupName=PornViewer
UninstallDisplayIcon={app}\PornViewer.exe
Compression=lzma2
SolidCompression=yes
OutputDir=.
OutputBaseFilename=PornViewer-Setup-1.7.0
PrivilegesRequired=admin

[Files]
Source: "installer-windows\PornViewer\*"; DestDir: "{app}"; Flags: recursesubdirs

[Icons]
Name: "{group}\PornViewer"; Filename: "{app}\PornViewer.exe"
Name: "{commondesktop}\PornViewer"; Filename: "{app}\PornViewer.exe"

[Run]
Filename: "{app}\PornViewer.exe"; Description: "Запустить PornViewer"; Flags: postinstall nowait
