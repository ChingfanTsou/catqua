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

$query_rs = sprintf("SELECT * FROM `post` WHERE post.id=%s",$id);
$rs = mysql_query($query_rs, $dblink);
header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";  
if($row = mysql_fetch_assoc($rs)) {
	echo"
<catquq>	
  <post>
      <id>".$row['id']."</id>
      <title><![CDATA[".$row['title']."]]></title>
      <url><![CDATA[]]></url>
      <portrait>http://static.oschina.net/uploads/user/700/1400968_100.jpeg?t=1384483211000</portrait>
      <body>
		<![CDATA[".$row['body']."]]>
	  </body>
      <author><![CDATA[".$row['usrname']."]]></author>
      <authorid>".$row['uid']."</authorid>
      <answerCount>".$row['answerCount']."</answerCount>
      <viewCount>".$row["viewCount"]."</viewCount>
      <pubDate>".$row["pubDate"]."</pubDate>
	  <favorite>0</favorite>
	    </post>
<notice>
	<atmeCount>0</atmeCount>
	<msgCount>0</msgCount>
	<reviewCount>0</reviewCount>
	<newFansCount>0</newFansCount>
</notice>
</catquq>";
$update_query=sprintf("UPDATE post SET viewCount = viewCount+1 WHERE id = %s",$row['id']);
$rs = mysql_query($update_query, $dblink);
} else {
	echo "<catquq><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount>	<newFansCount>0</newFansCount></notice></catquq>";
}
?>
