Option Explicit

Dim shell, fso, baseDir, javaExe, javaOpts, classPath, mainClass, cmd, exitCode

Set shell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")

baseDir = fso.GetParentFolderName(WScript.ScriptFullName)
shell.CurrentDirectory = baseDir

javaExe = "javaw"
javaOpts = "--add-opens=java.xml/com.sun.org.apache.xerces.internal.parsers=ALL-UNNAMED"
classPath = "config;..\libs\*"
mainClass = "l2mv.loginserver.panel.AuthGuiLauncher"

Do
	cmd = """" & javaExe & """ " & javaOpts & " -Xmx128m -cp """ & classPath & """ " & mainClass
	exitCode = shell.Run(cmd, 0, True)

	If exitCode = 2 Then
		WScript.Sleep 1500
	ElseIf exitCode = 1 Then
		MsgBox "O Auth Server foi encerrado por erro. Verifique o painel ou a pasta de logs.", vbCritical, "L2JPremium Auth"
		Exit Do
	Else
		Exit Do
	End If
Loop
