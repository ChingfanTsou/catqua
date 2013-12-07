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
$uid=@$_REQUEST['authorid'];
$cid=@$_REQUEST['replyid'];
$flag = true;
if (strcmp(trim($uid),"")==0) $flag = false;
if (strcmp(trim($cid),"")==0) $flag = false;
if ($flag) {
	$query_rid = sprintf("SELECT * FROM `comment` WHERE id = %s", $cid);
	$res = mysql_query($query_rid, $dblink);
	if (mysql_num_rows($res) == 0) $flag = false;
}
header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";  
if (!isset($_COOKIE["uid"]))
	echo"<catquq><result><errorCode>0</errorCode><errorMessage>用户未登录</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
else if ($flag == false || $_COOKIE["uid"] != $uid) 
	echo "<catquq><result><errorCode>-1</errorCode><errorMessage>操作不被允许</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
else {
	$query_del = sprintf("DELETE FROM `comment` WHERE id=%s", $cid);
	if (mysql_query($query_del, $dblink)) {
		$del_rep = sprintf("DELETE FROM `reply` WHERE cid=%s", $cid);
		echo"<catquq><result><errorCode>1</errorCode><errorMessage><![CDATA[操作成功]]></errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
	}
	else
		echo"<catquq><result><errorCode>-1</errorCode><errorMessage>其他错误</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
}
?>
