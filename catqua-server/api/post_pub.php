<?php
mb_http_output('utf8');
$host_dblink = "76.163.252.227";
$database_dblink = "A929774_catqua";
$username_dblink = "A929774_root";
$password_dblink = "Jingowj1234";
$dblink = mysql_pconnect($host_dblink, $username_dblink, $password_dblink) or trigger_error(mysql_error(),E_USER_ERROR);
mysql_query("SET NAMES utf8",$dblink);
mysql_query("SET CHARACTER_SET_CLIENT=utf8",$dblink);
mysql_query("SET CHARACTER_SET_RESULTS=utf8",$dblink);
mysql_select_db($database_dblink, $dblink);

$uid=@$_REQUEST['uid'];
$title=@$_REQUEST['title'];
$catalog=@$_REQUEST['catalog'];
$content=@$_REQUEST['content'];
$flag = true;
if (strcmp(trim($uid),"")==0) $flag = false;
if (strcmp(trim($title),"")==0) $flag = false;
if (strcmp(trim($catalog),"")==0) $flag = false;
if (strcmp(trim($content),"")==0) $flag = false;
header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";  
if (!isset($_COOKIE["uid"]))
	echo"<catquq><result><errorCode>0</errorCode><errorMessage>用户未登录</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
else if ($flag == false || $_COOKIE["uid"] != $uid) 
	echo "<catquq><result><errorCode>-1</errorCode><errorMessage>操作不被允许</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
else {
$query_rs = sprintf("INSERT INTO `post` (uid, usrname, title, catalog, body) 
VALUES (%s, \"%s\", \"%s\", %s, \"%s\")",$uid,$_COOKIE["username"],$title,$catalog,$content);
$rs = mysql_query($query_rs, $dblink);
	echo"<catquq><result><errorCode>1</errorCode><errorMessage>操作成功</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
}
?>
