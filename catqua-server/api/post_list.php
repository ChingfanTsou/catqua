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

$catalog=@$_REQUEST['catalog'];
$pageIndex=@$_REQUEST['pageIndex'];
$pageSize=@$_REQUEST['pageSize'];

if (strcmp(trim($catalog),"")==0) $catalog = 0;
if (strcmp(trim($pageIndex),"")==0) $pageIndex = 0;
if (strcmp(trim($pageSize),"")==0) $pageSize = 20;

$startPos = $pageIndex*$pageSize;
if ($catalog != 0)
	$query_rs = sprintf("SELECT * FROM `post` WHERE post.catalog=%s ORDER BY post.pubDate DESC LIMIT %s, %s",$catalog,$startPos,$pageSize);
else
	$query_rs = sprintf("SELECT * FROM `post` ORDER BY post.pubDate DESC LIMIT %s, %s",$startPos,$pageSize);
$rs = mysql_query($query_rs, $dblink);
header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
$row_num=mysql_num_rows($rs);
if ($row_num) {
	echo "<catquq><postCount>0</postCount> <!-- 总帖子数 --><pagesize>".$row_num."</pagesize><posts>";
	while($row = mysql_fetch_assoc($rs)) {
		echo "<post><id>".$row['id']."</id>
    	  <portrait>http://static.oschina.net/uploads/user/700/1400968_100.jpeg?t=1384483211000</portrait>
    	  <author><![CDATA[".$row['usrname']."]]></author>
		  <authorid>".$row['uid']."</authorid>
    	  <title><![CDATA[".$row['title']."]]></title>
    	  <answerCount>".$row['answerCount']."</answerCount>
    	  <viewCount>".$row['viewCount']."</viewCount>
          <pubDate>".$row['pubDate']."</pubDate>
          <answer>
			<name><![CDATA[Jingo]]></name>
            <time>1993-10-26 00:00:00</time>
		  </answer>
          </post>";
	}
	echo "</posts><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
	
} else {
	echo
	"<catquq><postCount>0</postCount> <!-- 总帖子数 --><pagesize>0</pagesize><posts></posts><notice>	<atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
}
?>
