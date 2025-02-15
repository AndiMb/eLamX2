[Setup]
AppId=eLamX
AppName=eLamX
AppVersion=$ELAMXVERSION$
;VersionInfoVersion=$ELAMXVERSION$.0.0

; TO-DO ${APP_URLS}
LicenseFile=license.txt

DefaultDirName="{autopf}\eLamX"
DisableProgramGroupPage=yes
OutputBaseFilename="eLamX.$ELAMXVERSION$"
SetupIconFile="elamx\etc\elamx.ico"
Compression=lzma
SolidCompression=yes
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
WizardStyle=modern

[Languages]
Name: "en"; MessagesFile: "compiler:Default.isl"
Name: "de"; MessagesFile: "compiler:Languages\German.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: checkedonce

[InstallDelete]
Type: filesandordirs; Name: "{app}\bin"
Type: filesandordirs; Name: "{app}\elamx"
Type: filesandordirs; Name: "{app}\etc"
Type: filesandordirs; Name: "{app}\jre"
Type: filesandordirs; Name: "{app}\platform"
Type: filesandordirs; Name: "{app}\View3DSuite"


[Files]
Source: "elamx\bin\*"; DestDir: "{app}\bin"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "elamx\elamx\*"; DestDir: "{app}\elamx"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "elamx\etc\*"; DestDir: "{app}\etc"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "elamx\jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "elamx\platform\*"; DestDir: "{app}\platform"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "elamx\View3DSuite\*"; DestDir: "{app}\View3DSuite"; Flags: ignoreversion recursesubdirs createallsubdirs


[Icons]
Name: "{commonprograms}\eLamX"; Filename: "{app}\bin\elamx64.exe"; Parameters: "--jdkhome ""{app}\jre"""; IconFilename: "{app}\etc\elamx.ico";
Name: "{commondesktop}\eLamX"; Filename: "{app}\bin\elamx64.exe"; Parameters: "--jdkhome ""{app}\jre"""; IconFilename: "{app}\etc\elamx.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\bin\elamx64.exe"; Parameters: "--jdkhome ""{app}\jre"""; Description: "{cm:LaunchProgram,eLamX}"; Flags: nowait postinstall skipifsilent
