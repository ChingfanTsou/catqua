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

$id=@$_REQUEST['id'];
$pageIndex=@$_REQUEST['pageIndex'];
$pageSize=@$_REQUEST['pageSize'];

if (strcmp(trim($pageIndex),"")==0) $pageIndex = 0;
if (strcmp(trim($pageSize),"")==0) $pageSize = 20;

$startPos = $pageIndex*$pageSize;
$query_num = sprintf("SELECT * FROM `comment` WHERE pid=%s",$id);
$res = mysql_query($query_num, $dblink);
$allCount = mysql_num_rows($res);
$query_rs = sprintf("SELECT * FROM `comment` WHERE pid=%s ORDER BY pubDate DESC LIMIT %s, %s",$id,$startPos,$pageSize);
$rs = mysql_query($query_rs, $dblink);
header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
$row_num=mysql_num_rows($rs);
echo "<catquq>
<allCount>".$allCount."</allCount>
<pagesize>".$row_num."</pagesize><comments>";
while($row = mysql_fetch_assoc($rs)) {
	echo "<comment><id>".$row['id']."</id><portrait>http://static.oschina.net/uploads/user/700/1400968_100.jpeg?t=1384483211000</portrait><author><![CDATA[".$row['usrname']."]]></author><authorid>".$row['uid']."</authorid><content><![CDATA[".$row['content']."]]></content><pubDate>".$row['pubDate']."</pubDate><appclient>1</appclient><replies>";
	$query_reply = sprintf("SELECT * FROM `reply` WHERE cid=%s ORDER BY pubDate DESC",$row['id']);
	$res = mysql_query($query_reply, $dblink);
	while($reply = mysql_fetch_assoc($res)) 
		echo "<reply><rauthor><![CDATA[".$reply['usrname']."]]></rauthor><rpubDate>".$reply['pubDate']."</rpubDate><rcontent><![CDATA[".$reply['content']."]]></rcontent></reply>";
	echo " </replies><refers> </refers></comment>";
}
echo "</comments><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount>	<newFansCount>0</newFansCount></notice></catquq>";
?>
