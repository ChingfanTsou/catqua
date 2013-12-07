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
$cid=@$_REQUEST['replyid'];
$content=@$_REQUEST['content'];
$flag = true;

if (strcmp(trim($uid),"")==0) $flag = false;
if (strcmp(trim($cid),"")==0) $flag = false;
if (strcmp(trim($content),"")==0) $flag = false;

if ($flag) {
	$query_cid = sprintf("SELECT * FROM `comment` WHERE id = %s", $cid);
	$res = mysql_query($query_cid, $dblink);
	if (mysql_num_rows($res) == 0) $flag = false;
}

header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";  
if (!isset($_COOKIE["uid"]))
	echo"<catquq><result><errorCode>0</errorCode><errorMessage>用户未登录</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
else if ($flag == false || $_COOKIE["uid"] != $uid) 
	echo "<catquq><result><errorCode>-1</errorCode><errorMessage>操作不被允许</errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
else {
$query_rs = sprintf("INSERT INTO `reply` (uid, usrname, cid, content) 
VALUES (%s, \"%s\", %s, \"%s\")",$uid,$_COOKIE["username"],$cid,$content);
$rs = mysql_query($query_rs, $dblink);
$query_rs = sprintf("SELECT * FROM `comment` WHERE id=%s",$cid);
$rs = mysql_query($query_rs, $dblink);
$row = mysql_fetch_assoc($rs);
	echo"<catquq><result><errorCode>1</errorCode><errorMessage><![CDATA[操作成功]]></errorMessage></result>";
	echo "<comment><id>".$row['id']."</id><portrait>http://static.oschina.net/uploads/user/700/1400968_100.jpeg?t=1384483211000</portrait><author><![CDATA[".$row['usrname']."]]></author><authorid>".$row['uid']."</authorid><content><![CDATA[".$row['content']."]]></content><pubDate>".$row['pubDate']."</pubDate><appclient>1</appclient><replies>";
	$query_reply = sprintf("SELECT * FROM `reply` WHERE cid=%s ORDER BY pubDate DESC",$row['id']);
	$res = mysql_query($query_reply, $dblink);
	while($reply = mysql_fetch_assoc($res)) 
		echo "<reply><rauthor><![CDATA[".$reply['usrname']."]]></rauthor><rpubDate>".$reply['pubDate']."</rpubDate><rcontent><![CDATA[".$reply['content']."]]></rcontent></reply>";
	echo " </replies></comment><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
}
?>
