package net.catqua.app.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.catqua.app.AppContext;
import net.catqua.app.AppException;
import net.catqua.app.bean.CommentList;
import net.catqua.app.bean.Notice;
import net.catqua.app.bean.Post;
import net.catqua.app.bean.PostList;
import net.catqua.app.bean.Result;
import net.catqua.app.bean.SearchList;
import net.catqua.app.bean.URLs;
import net.catqua.app.bean.User;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * API客户端接口：用于访问网络数据
 */
public class ApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}
	
	private static String getCookie(AppContext appContext) {
		if(appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}
	
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+appContext.getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	
	private static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	
	
	private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url, String cookie, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}
	
	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if(url.indexOf("?")<0)
			url.append('?');

		for(String name : params.keySet()){
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			//不做URLEncoder处理
			//url.append(URLEncoder.encode(String.valueOf(params.get(name)), UTF_8));
		}

		return url.toString().replace("?&", "?");
	}
	
	/**
	 * get请求URL
	 * @param url
	 * @throws AppException 
	 */
	private static InputStream http_get(AppContext appContext, String url) throws AppException {	
		//System.out.println("get_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);			
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();
				//System.out.println("XMLDATA=====>"+responseBody);
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		if(responseBody.contains("result") && responseBody.contains("errorCode") && appContext.containsProperty("user.uid")){
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody.getBytes()));	
				if(res.getErrorCode() == 0){
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return new ByteArrayInputStream(responseBody.getBytes());
	}
	
	/**
	 * 公用post方法
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static InputStream _post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException {
		//System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		
		//post表单参数处理
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null)
        for(String name : params.keySet()){
        	parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
        	//System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
        }
        if(files != null)
        for(String file : files.keySet()){
        	try {
				parts[i++] = new FilePart(file, files.get(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	//System.out.println("post_key_file==> "+file);
        }
		
		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);	        
		        httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		        if(statusCode != HttpStatus.SC_OK) 
		        {
		        	throw AppException.http(statusCode);
		        }
		        else if(statusCode == HttpStatus.SC_OK) 
		        {
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //保存cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setProperty("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}
		        }
		     	responseBody = httpPost.getResponseBodyAsString();
		        //System.out.println("XMLDATA=====>"+responseBody);
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
        
        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		if(responseBody.contains("result") && responseBody.contains("errorCode") && appContext.containsProperty("user.uid")){
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody.getBytes()));	
				if(res.getErrorCode() == 0){
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
        return new ByteArrayInputStream(responseBody.getBytes());
	}
	
	/**
	 * post请求URL
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException 
	 * @throws IOException 
	 * @throws  
	 */
	private static Result http_post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException, IOException {
        return Result.parse(_post(appContext, url, params, files));  
	}	
	
	/**
	 * 获取网络图片
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		//System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
		        InputStream inStream = httpGet.getResponseBodyAsStream();
		        bitmap = BitmapFactory.decodeStream(inStream);
		        inStream.close();
		        break;
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		return bitmap;
	}
	
	/**
	 * 登录， 自动处理cookie
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, String username, String pwd) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", username);
		params.put("pwd", pwd);
		params.put("keep_login", 1);
				
		String loginurl = URLs.LOGIN_VALIDATE_HTTP;
		if(appContext.isHttpsLogin()){
			loginurl = URLs.LOGIN_VALIDATE_HTTPS;
		}
		
		try{
			return User.parse(_post(appContext, loginurl, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	/**
	 * 注册并登录， 自动处理cookie
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User reg(AppContext appContext, String username, String pwd) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", username);
		params.put("pwd", pwd);
				
		String regurl = URLs.REGISTER;
		
		try{
			return User.parse(_post(appContext, regurl, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	/**
	 * 更新用户头像
	 * @param appContext
	 * @param uid 当前用户uid
	 * @param portrait 新上传的头像
	 * @return
	 * @throws AppException
	 */
	public static Result updatePortrait(AppContext appContext, int uid, File portrait) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		
		Map<String, File> files = new HashMap<String, File>();
		files.put("portrait", portrait);
				
		try{
			return http_post(appContext, URLs.PORTRAIT_UPDATE, params, files);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 更新用户之间关系（加关注、取消关注）
	 * @param uid 自己的uid
	 * @param hisuid 对方用户的uid
	 * @param newrelation 0:取消对他的关注 1:关注他
	 * @return
	 * @throws AppException
	 */
	public static Result updateRelation(AppContext appContext, int uid, int hisuid, int newrelation) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("hisuid", hisuid);
		params.put("newrelation", newrelation);
				
		try{
			return Result.parse(_post(appContext, URLs.USER_UPDATERELATION, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取用户通知信息
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static Notice getUserNotice(AppContext appContext, int uid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
				
		try{
			return Notice.parse(_post(appContext, URLs.USER_NOTICE, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 清空通知消息
	 * @param uid
	 * @param type 1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
	 * @return
	 * @throws AppException
	 */
	public static Result noticeClear(AppContext appContext, int uid, int type) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("type", type);
				
		try{
			return Result.parse(_post(appContext, URLs.NOTICE_CLEAR, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取帖子列表
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static PostList getPostList(AppContext appContext, final int catalog, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.POST_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return PostList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取帖子的详情
	 * @param url
	 * @param post_id
	 * @return
	 * @throws AppException
	 */
	public static Post getPostDetail(AppContext appContext, final int post_id) throws AppException {
		String newUrl = _MakeURL(URLs.POST_DETAIL, new HashMap<String, Object>(){{
			put("id", post_id);
		}});
		try{
			return Post.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 发帖子
	 * @param post （uid、title、catalog、content、isNoticeMe）
	 * @return
	 * @throws AppException
	 */
	public static Result pubPost(AppContext appContext, Post post) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", post.getAuthorId());
		params.put("title", post.getTitle());
		params.put("catalog", post.getCatalog());
		params.put("content", post.getBody());
		params.put("isNoticeMe", post.getIsNoticeMe());				
		
		try{
			return http_post(appContext, URLs.POST_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取评论列表
	 * @param catalog 1新闻  2帖子  3动弹  4动态
	 * @param id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static CommentList getCommentList(AppContext appContext, final int catalog, final int id, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.COMMENT_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("id", id);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return CommentList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 发表评论
	 * @param catalog 1新闻  2帖子  3动弹  4动态
	 * @param id 某条新闻，帖子，动弹的id
	 * @param uid 用户uid
	 * @param content 发表评论的内容
	 * @param isPostToMyZone 是否转发到我的空间  0不转发  1转发
	 * @return
	 * @throws AppException
	 */
	public static Result pubComment(AppContext appContext, int catalog, int id, int uid, String content, int isPostToMyZone) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("isPostToMyZone", isPostToMyZone);
		
		try{
			return http_post(appContext, URLs.COMMENT_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}

	/**
	 * 
	 * @param id 表示被评论的某条新闻，帖子，动弹的id 或者某条消息的 friendid 
	 * @param catalog 表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态
	 * @param replyid 表示被回复的单个评论id
	 * @param authorid 表示该评论的原始作者id
	 * @param uid 用户uid 一般都是当前登录用户uid
	 * @param content 发表评论的内容
	 * @return
	 * @throws AppException
	 */
	public static Result replyComment(AppContext appContext, int id, int catalog, int replyid, int authorid, int uid, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("replyid", replyid);
		params.put("authorid", authorid);
		
		try{
			return http_post(appContext, URLs.COMMENT_REPLY, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 删除评论
	 * @param id 表示被评论对应的某条新闻,帖子,动弹的id 或者某条消息的 friendid
	 * @param catalog 表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态&留言
	 * @param replyid 表示被回复的单个评论id
	 * @param authorid 表示该评论的原始作者id
	 * @return
	 * @throws AppException
	 */
	public static Result delComment(AppContext appContext, int id, int catalog, int replyid, int authorid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("catalog", catalog);
		params.put("replyid", replyid);
		params.put("authorid", authorid);

		try{
			return http_post(appContext, URLs.COMMENT_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取搜索列表
	 * @param catalog 全部:all 新闻:news  问答:post 软件:software 博客:blog 代码:code
	 * @param content 搜索的内容
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SearchList getSearchList(AppContext appContext, String catalog, String content, int pageIndex, int pageSize) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("content", content);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		try{
			return SearchList.parse(_post(appContext, URLs.SEARCH_LIST, params, null));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
}
