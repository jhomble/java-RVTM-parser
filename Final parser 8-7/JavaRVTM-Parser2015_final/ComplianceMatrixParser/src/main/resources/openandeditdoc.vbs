Set args = WScript.Arguments
if args.count = 0 then
  wscript.echo "Missing argument"
  wscript.quit
end if
Set fso = CreateObject("Scripting.FileSystemObject")
filename=""
For I = 0 to args.Count - 1
if I=0 then
   filename=filename&args(I)
else
	filename=filename&" "&args(I)
end if
Next
sSourceFile = fso.GetAbsolutePathName(filename)
Set objWord = CreateObject("Word.Application")
objWord.Visible= TRUE
Set objDoc = objWord.Documents.Open(""&sSourceFile&"")
objDoc.CheckSpelling