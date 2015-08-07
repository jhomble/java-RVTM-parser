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
Const wdFormatPDF = 17
Set objWord = CreateObject("Word.Application")
Set objDoc = objWord.Documents.Open(""&sSourceFile&"")
if InStr(sSourceFile, ".docx")=0 then
txtName = Replace(sSourceFile, ".doc", ".pdf")
else
txtName = Replace(sSourceFile, ".docx", ".pdf")
end if
objDoc.SaveAs txtName, wdFormatPDF
objDoc.Close
objWord.Quit