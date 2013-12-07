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

$pageIndex=@$_REQUEST['pageIndex'];
$pageSize=@$_REQUEST['pageSize'];
$content=@$_REQUEST['content'];
$catalog=@$_REQUEST['catalog'];

if (strcmp(trim($pageIndex),"")==0) $pageIndex = 0;
if (strcmp(trim($pageSize),"")==0) $pageSize = 20;
$flag = (strcmp(trim($content),"")==0);
$out = false;
$type = 0;
if (strcmp(trim($catalog),"post")==0) $type = 1;
header("Content-type: text/xml");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";  
if (!$flag) {
	$startPos = $pageIndex*$pageSize;
	if ($type == 1)
		$query_rs = "SELECT * FROM `post` WHERE title LIKE BINARY "."'%".$content."%'"." ORDER BY pubDate DESC LIMIT ".$startPos.','.$pageSize;
	else
		$query_rs = "SELECT * FROM `post` WHERE usrname = "."'".$content."'"." ORDER BY pubDate DESC LIMIT ".$startPos.','.$pageSize;
	$rs = mysql_query($query_rs, $dblink);
	$row_num=mysql_num_rows($rs);
	if ($row_num) {
		echo "<catquq><pagesize>".$row_num."</pagesize><results>";
		while($row = mysql_fetch_assoc($rs)) {
			echo "<result><objid>".$row['id']."</objid><type>post</type> <title><![CDATA[".$row['title']."]]></title><url><![CDATA[http://q/0_".$row['id']."]]></url><pubDate>".$row['pubDate']."</pubDate>
		  <author><![CDATA[".$row['usrname']."]]></author>
		</result>";
		}
		echo "</results><notice><atmeCount>0</atmeCount><msgCount>0</msgCount><reviewCount>0</reviewCount>	<newFansCount>0</newFansCount></notice></catquq>";
		$out = true;
	}
}
if (!$out)
	echo"<catquq><pagesize> </pagesize><results></results><notice><atmeCount>0</atmeCount><msgCount>0</msgCount>	<reviewCount>0</reviewCount><newFansCount>0</newFansCount></notice></catquq>";
	
?>
