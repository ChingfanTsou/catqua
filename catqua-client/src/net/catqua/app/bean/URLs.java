package net.catqua.app.bean;

import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import net.catqua.app.common.StringUtils;

/**
 * 接口URL实体类
 */
public class URLs implements Serializable {
	
	public final static String HOST = "www.oschina.net";//192.168.1.213  www.oschina.net
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";
	
	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
	private final static String Cat_URL_API_HOST = HTTP + "ijingo.info/catqua" + URL_SPLITTER;
	public final static String LOGIN_VALIDATE_HTTP = Cat_URL_API_HOST + "login.php";
	public final static String LOGIN_VALIDATE_HTTPS = Cat_URL_API_HOST + "login.php";
	public final static String NEWS_LIST = URL_API_HOST+"action/api/news_list";
	public final static String NEWS_DETAIL = URL_API_HOST+"action/api/news_detail";
	
	public final static String POST_LIST = Cat_URL_API_HOST+"post_list.php";
	public final static String POST_DETAIL = Cat_URL_API_HOST+"post_detail.php";
	public final static String POST_PUB = Cat_URL_API_HOST+"post_pub.php";
	
	public final static String TWEET_LIST = URL_API_HOST+"action/api/tweet_list";
	public final static String TWEET_DETAIL = URL_API_HOST+"action/api/tweet_detail";
	public final static String TWEET_PUB = URL_API_HOST+"action/api/tweet_pub";
	public final static String TWEET_DELETE = URL_API_HOST+"action/api/tweet_delete";
	public final static String ACTIVE_LIST = URL_API_HOST+"action/api/active_list";
	public final static String MESSAGE_LIST = URL_API_HOST+"action/api/message_list";
	public final static String MESSAGE_DELETE = URL_API_HOST+"action/api/message_delete";
	public final static String MESSAGE_PUB = URL_API_HOST+"action/api/message_pub";
	
	public final static String COMMENT_LIST = Cat_URL_API_HOST+"comment_list.php";
	public final static String COMMENT_PUB = Cat_URL_API_HOST+"comment_pub.php";
	public final static String COMMENT_REPLY = Cat_URL_API_HOST+"comment_reply.php";
	public final static String COMMENT_DELETE = Cat_URL_API_HOST+"comment_delete.php";
	
	public final static String SOFTWARECATALOG_LIST = URL_API_HOST+"action/api/softwarecatalog_list";
	public final static String SOFTWARETAG_LIST = URL_API_HOST+"action/api/softwaretag_list";
	public final static String SOFTWARE_LIST = URL_API_HOST+"action/api/software_list";
	public final static String SOFTWARE_DETAIL = URL_API_HOST+"action/api/software_detail";	
	public final static String USERBLOG_LIST = URL_API_HOST+"action/api/userblog_list";
	public final static String USERBLOG_DELETE = URL_API_HOST+"action/api/userblog_delete";
	public final static String BLOG_LIST = URL_API_HOST+"action/api/blog_list";
	public final static String BLOG_DETAIL = URL_API_HOST+"action/api/blog_detail";
	public final static String BLOGCOMMENT_LIST = URL_API_HOST+"action/api/blogcomment_list";
	public final static String BLOGCOMMENT_PUB = URL_API_HOST+"action/api/blogcomment_pub";
	public final static String BLOGCOMMENT_DELETE = URL_API_HOST+"action/api/blogcomment_delete";
	public final static String MY_INFORMATION = URL_API_HOST+"action/api/my_information";
	public final static String USER_INFORMATION = URL_API_HOST+"action/api/user_information";
	public final static String USER_UPDATERELATION = URL_API_HOST+"action/api/user_updaterelation";
	public final static String USER_NOTICE = URL_API_HOST+"action/api/user_notice";
	public final static String NOTICE_CLEAR = URL_API_HOST+"action/api/notice_clear";
	public final static String FRIENDS_LIST = URL_API_HOST+"action/api/friends_list";
	public final static String FAVORITE_LIST = URL_API_HOST+"action/api/favorite_list";
	public final static String FAVORITE_ADD = URL_API_HOST+"action/api/favorite_add";
	public final static String FAVORITE_DELETE = URL_API_HOST+"action/api/favorite_delete";
	
	public final static String SEARCH_LIST = Cat_URL_API_HOST+"search_list.php";
	
	public final static String PORTRAIT_UPDATE = URL_API_HOST+"action/api/portrait_update";
	public final static String UPDATE_VERSION = URL_API_HOST+"MobileAppVersion.xml";
	
	private final static String URL_HOST = "oschina.net";
	private final static String URL_WWW_HOST = "www."+URL_HOST;
	private final static String URL_MY_HOST = "my."+URL_HOST;
	
	private final static String URL_TYPE_NEWS = URL_WWW_HOST + URL_SPLITTER + "news" + URL_SPLITTER;
	private final static String URL_TYPE_SOFTWARE = URL_WWW_HOST + URL_SPLITTER + "p" + URL_SPLITTER;
	private final static String URL_TYPE_QUESTION = URL_WWW_HOST + URL_SPLITTER + "question" + URL_SPLITTER;
	private final static String URL_TYPE_BLOG = URL_SPLITTER + "blog" + URL_SPLITTER;
	private final static String URL_TYPE_TWEET = URL_SPLITTER + "tweet" + URL_SPLITTER;
	private final static String URL_TYPE_ZONE = URL_MY_HOST + URL_SPLITTER + "u" + URL_SPLITTER;
	private final static String URL_TYPE_QUESTION_TAG = URL_TYPE_QUESTION + "tag" + URL_SPLITTER;
	
	public final static int URL_OBJ_TYPE_OTHER = 0x000;
	public final static int URL_OBJ_TYPE_NEWS = 0x001;
	public final static int URL_OBJ_TYPE_SOFTWARE = 0x002;
	public final static int URL_OBJ_TYPE_QUESTION = 0x003;
	public final static int URL_OBJ_TYPE_ZONE = 0x004;
	public final static int URL_OBJ_TYPE_BLOG = 0x005;
	public final static int URL_OBJ_TYPE_TWEET = 0x006;
	public final static int URL_OBJ_TYPE_QUESTION_TAG = 0x007;
	
	private int objId;
	private String objKey = "";
	private int objType;
	
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getObjKey() {
		return objKey;
	}
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}
	
	/**
	 * 转化URL为URLs实体
	 * @param path
	 * @return 不能转化的链接返回null
	 */
	public final static URLs parseURL(String path) {
		if(StringUtils.isEmpty(path))return null;
		path = formatURL(path);
		URLs urls = null;
		String objId = "";
		try {
			URL url = new URL(path);
			urls = new URLs();
			objId = parseObjId(path, "q/");
			String[] _tmp = objId.split(URL_UNDERLINE);
			urls.setObjId(StringUtils.toInt(_tmp[1]));
			urls.setObjType(URL_OBJ_TYPE_QUESTION);

		} catch (Exception e) {
			e.printStackTrace();
			urls = null;
		}
		return urls;
	}

	/**
	 * 解析url获得objId
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_type){
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains(URL_SPLITTER)){
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		}else{
			objId = str;
		}
		return objId;
	}
	
	/**
	 * 解析url获得objKey
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type){
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains("?")){
			tmp = str.split("?");
			objKey = tmp[0];
		}else{
			objKey = str;
		}
		return objKey;
	}
	
	/**
	 * 对URL进行格式处理
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}	
}
