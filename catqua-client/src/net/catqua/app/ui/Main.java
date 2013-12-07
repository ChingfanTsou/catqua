package net.catqua.app.ui;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.catqua.app.bean.Notice;
import net.catqua.app.AppContext;
import net.catqua.app.AppException;
import net.catqua.app.R;
import net.catqua.app.adapter.ListViewQuestionAdapter;
import net.catqua.app.bean.Post;
import net.catqua.app.bean.PostList;
import net.catqua.app.common.StringUtils;
import net.catqua.app.common.UIHelper;
import net.catqua.app.widget.BadgeView;
import net.catqua.app.widget.NewDataToast;
import net.catqua.app.widget.PullToRefreshListView;
import net.catqua.app.widget.ScrollLayout;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

public class Main extends BaseActivity {

	public static final int QUICKACTION_LOGIN_OR_LOGOUT = 0;
	public static final int QUICKACTION_SEARCH = 1;
	public static final int QUICKACTION_EXIT = 2;

	private ScrollLayout mScrollLayout;
	private RadioButton[] mButtons;
	private String[] mHeadTitles;
	private int mViewCount;
	private int mCurSel;

	private ImageView mHeadLogo;
	private TextView mHeadTitle;
	private ProgressBar mHeadProgress;
	private ImageButton mHeadPub_post;

	private int curQuestionCatalog = PostList.CATALOG_ASK;
	private PullToRefreshListView lvQuestion;
	private ListViewQuestionAdapter lvQuestionAdapter;
	private List<Post> lvQuestionData = new ArrayList<Post>();
	private Handler lvQuestionHandler;
	private int lvQuestionSumData;

	private RadioButton fbQuestion;
	private ImageView fbSetting;

	private Button framebtn_Question_ask;
	private Button framebtn_Question_share;
	private Button framebtn_Question_other;
	private Button framebtn_Question_job;
	private Button framebtn_Question_site;

	private View lvQuestion_footer;

	private TextView lvQuestion_foot_more;

	private ProgressBar lvQuestion_foot_progress;


	public static BadgeView bv_active;
	public static BadgeView bv_message;
	public static BadgeView bv_atme;
	public static BadgeView bv_review;

	private QuickActionWidget mGrid;// 快捷栏控件

	private boolean isClearNotice = false;
	private int curClearNoticeType = 0;

	private AppContext appContext;// 全局Context

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		appContext = (AppContext) getApplication();
		// 网络连接判断
		if (!appContext.isNetworkConnected())
			UIHelper.ToastMessage(this, R.string.network_not_connected);
		// 初始化登录
		appContext.initLoginInfo();

		this.initHeadView();
		this.initFootBar();
		this.initPageScroll();
		this.initFrameButton();
		this.initQuickActionGrid();
		this.initFrameListView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mViewCount == 0)
			mViewCount = 1;
		/*if (mCurSel == 0 && !fbQuestion.isChecked()) {
			fbQuestion.setChecked(true);
		}*/
		mScrollLayout.setIsScroll(appContext.isScroll());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	/**
	 * 初始化快捷栏
	 */
	private void initQuickActionGrid() {
		mGrid = new QuickActionGrid(this);
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_login,
				R.string.main_menu_login));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_search,
				R.string.main_menu_search));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_exit,
				R.string.main_menu_exit));

		mGrid.setOnQuickActionClickListener(mActionListener);
	}

	/**
	 * 快捷栏item点击事件
	 */
	private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			switch (position) {
			case QUICKACTION_LOGIN_OR_LOGOUT:// 用户登录-注销
				UIHelper.loginOrLogout(Main.this);
				break;
			case QUICKACTION_SEARCH:// 搜索
				UIHelper.showSearch(Main.this);
				break;
			case QUICKACTION_EXIT:// 退出
				UIHelper.Exit(Main.this);
				break;
			}
		}
	};

	/**
	 * 初始化所有ListView
	 */
	private void initFrameListView() {
		// 初始化listview控件
		this.initQuestionListView();
		// 加载listview数据
		this.initFrameListViewData();
	}

	/**
	 * 初始化所有ListView数据
	 */
	private void initFrameListViewData() {
		// 初始化Handler
		
		lvQuestionHandler = this.getLvHandler(lvQuestion, lvQuestionAdapter,
				lvQuestion_foot_more, lvQuestion_foot_progress,
				AppContext.PAGE_SIZE);

		// 加载问答数据
		if (lvQuestionData.isEmpty()) {
			loadLvQuestionData(curQuestionCatalog, 0, lvQuestionHandler,
					UIHelper.LISTVIEW_ACTION_INIT);
		}
	}

	/**
	 * 初始化帖子列表
	 */
	private void initQuestionListView() {
		lvQuestionAdapter = new ListViewQuestionAdapter(this, lvQuestionData,
				R.layout.question_listitem);
		lvQuestion_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvQuestion_foot_more = (TextView) lvQuestion_footer
				.findViewById(R.id.listview_foot_more);
		lvQuestion_foot_progress = (ProgressBar) lvQuestion_footer
				.findViewById(R.id.listview_foot_progress);
		lvQuestion = (PullToRefreshListView) findViewById(R.id.frame_listview_question);
		lvQuestion.addFooterView(lvQuestion_footer);// 添加底部视图 必须在setAdapter前
		lvQuestion.setAdapter(lvQuestionAdapter);
		lvQuestion
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 点击头部、底部栏无效
						if (position == 0 || view == lvQuestion_footer)
							return;

						Post post = null;
						// 判断是否是TextView
						if (view instanceof TextView) {
							post = (Post) view.getTag();
						} else {
							TextView tv = (TextView) view
									.findViewById(R.id.question_listitem_title);
							post = (Post) tv.getTag();
						}
						if (post == null)
							return;

						// 跳转到问答详情
						UIHelper.showQuestionDetail(view.getContext(),
								post.getId());
					}
				});
		lvQuestion.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvQuestion.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvQuestionData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvQuestion_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lvQuestion.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvQuestion.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvQuestion_foot_more.setText(R.string.load_ing);
					lvQuestion_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvQuestionSumData / AppContext.PAGE_SIZE;
					loadLvQuestionData(curQuestionCatalog, pageIndex,lvQuestionHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
							
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvQuestion.onScroll(view, firstVisibleItem, visibleItemCount,totalItemCount);
						
			}
		});
		lvQuestion
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadLvQuestionData(curQuestionCatalog, 0,lvQuestionHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
								
					}
				});
	}


	/**
	 * 初始化头部视图
	 */
	private void initHeadView() {
		mHeadLogo = (ImageView) findViewById(R.id.main_head_logo);
		mHeadTitle = (TextView) findViewById(R.id.main_head_title);
		mHeadProgress = (ProgressBar) findViewById(R.id.main_head_progress);
		mHeadPub_post = (ImageButton) findViewById(R.id.main_head_pub_post);

		mHeadPub_post.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showQuestionPub(v.getContext());
			}
		});
	}

	/**
	 * 初始化底部栏
	 */
	private void initFootBar() {

		fbQuestion = (RadioButton) findViewById(R.id.main_footbar_question);
		fbSetting = (ImageView) findViewById(R.id.main_footbar_setting);
		fbSetting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 展示快捷栏&判断是否登录&是否加载文章图片
				UIHelper.showSettingLoginOrLogout(Main.this,
						mGrid.getQuickAction(0));
				mGrid.show(v);
			}
		});
	}


	/**
	 * 初始化水平滚动翻页
	 */
	private void initPageScroll() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linearlayout_footer);
		mHeadTitles = getResources().getStringArray(R.array.head_titles);
		mViewCount = mScrollLayout.getChildCount();
		mButtons = new RadioButton[mViewCount];

		for (int i = 0; i < mViewCount; i++) {
			mButtons[i] = (RadioButton) linearLayout.getChildAt(i * 2);
			mButtons[i].setTag(i);
			mButtons[i].setChecked(false);
			mButtons[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					int pos = (Integer) (v.getTag());
					// 点击当前项刷新
					if (mCurSel == pos) {
							lvQuestion.clickRefresh();
					}
					mScrollLayout.snapToScreen(pos);
				}
			});
		}

		// 设置第一显示屏
		mCurSel = 0;
		//mButtons[mCurSel].setChecked(true);

		mScrollLayout
				.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
					public void OnViewChange(int viewIndex) {
						// 切换列表视图-如果列表数据为空：加载数据
							if (lvQuestionData.isEmpty()) {
								loadLvQuestionData(curQuestionCatalog, 0,
										lvQuestionHandler,
										UIHelper.LISTVIEW_ACTION_INIT);
							}
						setCurPoint(viewIndex);
					}
				});
	}

	/**
	 * 设置底部栏当前焦点
	 * 
	 * @param index
	 */
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index)
			return;
		mButtons[mCurSel].setChecked(false);
		mButtons[index].setChecked(true);
		mHeadTitle.setText(mHeadTitles[index]);
		mCurSel = index;
		mHeadPub_post.setVisibility(View.GONE);
		// 头部logo、发帖、发动弹按钮显示
		mHeadLogo.setImageResource(R.drawable.frame_logo_post);
		mHeadPub_post.setVisibility(View.VISIBLE);
	
	}

	/**
	 * 初始化各个主页的按钮(资讯、问答、动弹、动态、留言)
	 */
	private void initFrameButton() {
		// 初始化按钮控件
		framebtn_Question_ask = (Button) findViewById(R.id.frame_btn_question_ask);
		framebtn_Question_share = (Button) findViewById(R.id.frame_btn_question_share);
		framebtn_Question_other = (Button) findViewById(R.id.frame_btn_question_other);
		framebtn_Question_job = (Button) findViewById(R.id.frame_btn_question_job);
		framebtn_Question_site = (Button) findViewById(R.id.frame_btn_question_site);
		framebtn_Question_ask.setEnabled(false);
		framebtn_Question_ask.setOnClickListener(frameQuestionBtnClick(
				framebtn_Question_ask, PostList.CATALOG_ASK));
		framebtn_Question_share.setOnClickListener(frameQuestionBtnClick(
				framebtn_Question_share, PostList.CATALOG_SHARE));
		framebtn_Question_other.setOnClickListener(frameQuestionBtnClick(
				framebtn_Question_other, PostList.CATALOG_OTHER));
		framebtn_Question_job.setOnClickListener(frameQuestionBtnClick(
				framebtn_Question_job, PostList.CATALOG_JOB));
		framebtn_Question_site.setOnClickListener(frameQuestionBtnClick(
				framebtn_Question_site, PostList.CATALOG_SITE));
		
	}

	private View.OnClickListener frameQuestionBtnClick(final Button btn,
			final int catalog) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				if (btn == framebtn_Question_ask)
					framebtn_Question_ask.setEnabled(false);
				else
					framebtn_Question_ask.setEnabled(true);
				if (btn == framebtn_Question_share)
					framebtn_Question_share.setEnabled(false);
				else
					framebtn_Question_share.setEnabled(true);
				if (btn == framebtn_Question_other)
					framebtn_Question_other.setEnabled(false);
				else
					framebtn_Question_other.setEnabled(true);
				if (btn == framebtn_Question_job)
					framebtn_Question_job.setEnabled(false);
				else
					framebtn_Question_job.setEnabled(true);
				if (btn == framebtn_Question_site)
					framebtn_Question_site.setEnabled(false);
				else
					framebtn_Question_site.setEnabled(true);

				curQuestionCatalog = catalog;
				loadLvQuestionData(curQuestionCatalog, 0, lvQuestionHandler,
						UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
			}
		};
	}

	
	/**
	 * 获取listview的初始化Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @return
	 */
	private Handler getLvHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress, final int pageSize) {
		return new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					// listview数据处理
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2,
							msg.arg1);

					if (msg.what < pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(Main.this);
				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				mHeadProgress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					lv.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
	}

	/**
	 * listview数据处理
	 * 
	 * @param what
	 *            数量
	 * @param obj
	 *            数据
	 * @param objtype
	 *            数据类型
	 * @param actiontype
	 *            操作类型
	 * @return notice 通知信息
	 */
	private Notice handleLvData(int what, Object obj, int objtype,
			int actiontype) {
		Notice notice = null;
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			PostList plist = (PostList) obj;
			notice = plist.getNotice();
			lvQuestionSumData = what;
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
				if (lvQuestionData.size() > 0) {
					for (Post post1 : plist.getPostlist()) {
						boolean b = false;
						for (Post post2 : lvQuestionData) {
							if (post1.getId() == post2.getId()) {
								b = true;
								break;
							}
						}
						if (!b)
							newdata++;
					}
				} else {
					newdata = what;
				}
			}
			lvQuestionData.clear();// 先清除原有数据
			lvQuestionData.addAll(plist.getPostlist());
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
				// 提示新加载数据
				if (newdata > 0) {
					NewDataToast
							.makeText(
									this,
									getString(R.string.new_data_toast_message,
											newdata), appContext.isAppSound())
							.show();
				} else {
					NewDataToast.makeText(this,
							getString(R.string.new_data_toast_none), false)
							.show();
				}
			}
		}
		return notice;
	}

	/**
	 * 线程加载帖子数据
	 * 
	 * @param catalog
	 *            分类
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	private void loadLvQuestionData(final int catalog, final int pageIndex,
			final Handler handler, final int action) {
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					PostList list = appContext.getPostList(catalog, pageIndex,
							isRefresh);
					msg.what = list.getPageSize();
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_POST;
				if (curQuestionCatalog == catalog)
					handler.sendMessage(msg);
			}
		}.start();
	}
	/**
	 * 菜单被显示之前的事件
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		UIHelper.showMenuLoginOrLogout(this, menu);
		return true;
	}


	/**
	 * 监听返回--是否退出程序
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 是否退出应用
			UIHelper.Exit(this);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 展示快捷栏&判断是否登录
			UIHelper.showSettingLoginOrLogout(Main.this,
					mGrid.getQuickAction(0));
			mGrid.show(fbSetting, true);
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			// 展示搜索页
			UIHelper.showSearch(Main.this);
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}
}
