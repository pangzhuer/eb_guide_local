<!DOCTYPE html>
<html>
<head>
<meta charset='utf-8'>
<title>文件上传</title>
</head>

<body>
	<div>
		<form action="./uploadDO" method="post" enctype="multipart/form-data">
			<label for="fileDec" >文件描述</label>
			<input name="fileDec" type="text" />
			<br/>
			<label for="vFile" >选择文件</label>
			<input name="vFile" type="file" />
			<input type="submit" value="submit" />
		</form>
	</div>
</body>
</html>