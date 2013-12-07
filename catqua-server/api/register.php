<?php
mb_http_output('utf8');
$host_dblink = "76.163.252.227:3306";
$database_dblink = "A929774_catqua";
$username_dblink = "A929774_root";
$password_dblink = "Jingowj1234";
$dblink = mysql_pconnect($host_dblink, $username_dblink, $password_dblink) or trigger_error(mysql_error(),E_USER_ERROR);
mysql_query("SET NAMES utf8",$dblink);
mysql_query("SET CHARACTER_SET_CLIENT=utf8",$dblink);
mysql_query("SET CHARACTER_SET_RESULTS=utf8",$dblink);
mysql_select_db($database_dblink, $dblink);

$username=@$_REQUEST['username'];
$pwd=@$_REQUEST['pwd'];

$query_rs = sprintf("SELECT * FROM `user` WHERE user.name='%s'",$username);
$rs = mysql_query($query_rs, $dblink);
$flag = mysql_fetch_row($rs);
header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
if ($flag) {
	echo "<catquq><result><errorCode>0</errorCode><errorMessage><![CDATA[用户名已存在]]></errorMessage></result><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
} else {
	$query_insert = sprintf("INSERT INTO `user`(name, pwd) VALUES('%s','%s')",$username,$pwd);
	mysql_query($query_insert, $dblink);
	$query = sprintf("SELECT * FROM `user` WHERE name='%s'",$username);
	$rs = mysql_query($query, $dblink);
	$row = mysql_fetch_assoc($rs);
	setcookie("uid", $row['id'], time()+3600);
        setcookie("username", $row['name'], time()+3600);
	echo"<catquq>
  <result>
    <errorCode>1</errorCode>
    <errorMessage><![CDATA[登录成功]]></errorMessage>
  </result>
    <user>
    <uid>".$row['id']."</uid>
    <location><![CDATA[黑龙江 哈尔滨]]></location>
    <name><![CDATA[".$row['name']."]]></name>
    <followers>0</followers>
    <fans>0</fans>
    <score>0</score>
    <portrait>http://static.oschina.net/uploads/user/700/1400968_100.jpeg?t=1384483211000</portrait>
  </user>
  <notice>
	<atmeCount>0</atmeCount>
	<msgCount>0</msgCount>
	<reviewCount>0</reviewCount>
	<newFansCount>0</newFansCount>
</notice>
</catquq>";
}

?>
