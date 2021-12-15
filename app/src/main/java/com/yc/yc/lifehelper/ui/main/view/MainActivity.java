package com.yc.yc.lifehelper.ui.main.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.ns.yc.lifehelper.R;
import com.yc.toolutils.activity.ActivityManager;
import com.yc.widget.viewPager.NoSlidingViewPager;
import com.yc.yc.lifehelper.ui.data.view.fragment.DataFragment;
import com.yc.yc.lifehelper.ui.home.view.fragment.HomeFragment;
import com.yc.yc.lifehelper.ui.main.contract.MainContract;
import com.yc.yc.lifehelper.ui.main.presenter.MainPresenter;
import com.pedaily.yc.ycdialoglib.toast.ToastUtils;
import com.yc.configlayer.arounter.ARouterUtils;
import com.yc.configlayer.arounter.RouterConfig;
import com.yc.configlayer.constant.Constant;
import com.yc.imageserver.utils.GlideImageUtils;
import com.yc.yc.lifehelper.ui.me.view.fragment.MeFragment;
import com.yc.zxingserver.demo.EasyCaptureActivity;
import com.yc.zxingserver.scan.Intents;
import com.ycbjie.library.base.adapter.BasePagerAdapter;
import com.ycbjie.library.base.mvp.BaseActivity;
import com.ycbjie.library.listener.PerfectClickListener;
import com.ycbjie.library.web.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/03/22
 *     desc  : Main主页面
 *     revise:
 * </pre>
 */
public class MainActivity extends BaseActivity<MainPresenter> implements View.OnClickListener
        , MainContract.View {

    private DrawerLayout mDrawerLayout;
    private NoSlidingViewPager mVpHome;
    private CommonTabLayout mCtlTable;
    private NavigationView mNavView;
    private TextView mSetting;
    private TextView mQuit;
    private Toolbar mToolbar;
    private FrameLayout mFlTitleMenu;
    private TextView mTvTitle;

    private long time;
    public static final int HOME = 0;
    public static final int FIND = 1;
    public static final int DATA = 2;
    public static final int USER = 3;
    private MainContract.Presenter presenter = new MainPresenter(this);
    private int selectIndex;

    @IntDef({HOME, FIND, DATA, USER})
    private @interface PageIndex {}
    public static final int REQUEST_CODE_SCAN = 0X01;

    /**
     * 跳转首页* @param context     上下文
     * @param selectIndex 添加注解限制输入值
     */
    public static void startActivity(Context context, @PageIndex int selectIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("selectIndex", selectIndex);
        context.startActivity(intent);
    }

    /**
     * 处理onNewIntent()，以通知碎片管理器 状态未保存。
     * 如果您正在处理新的意图，并且可能是 对碎片状态进行更改时，要确保调用先到这里。
     * 否则，如果你的状态保存，但活动未停止，则可以获得 onNewIntent()调用，发生在onResume()之前，
     * 并试图 此时执行片段操作将引发IllegalStateException。 因为碎片管理器认为状态仍然保存。
     *
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            selectIndex = intent.getIntExtra("selectIndex", HOME);
            mVpHome.setCurrentItem(selectIndex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            selectIndex = savedInstanceState.getInt("selectIndex",0);
            mVpHome.setCurrentItem(selectIndex);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState!=null){
            outState.putInt("selectIndex",selectIndex);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting_app, menu);
        menu.add(4, 4, 4, "开发作者介绍");
        menu.add(5, 5, 5,"分享此软件");
        menu.add(6, 6, 6,"开源项目介绍");
        menu.add(7, 7, 7,"我的掘金");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting:
                ToastUtils.showRoundRectToast("设置");
                break;
            case R.id.item_message:
                ToastUtils.showRoundRectToast("消息");
                break;
            case R.id.item_capture:
                String[] perms = {Manifest.permission.CAMERA};
                if (EasyPermissions.hasPermissions(this, perms)) {//有权限
                    Intent intent = new Intent(this, EasyCaptureActivity.class);
                    this.startActivityForResult(intent,REQUEST_CODE_SCAN);
                }
                break;
            case 4:
                WebViewActivity.lunch(this,Constant.GITHUB,"我的GitHub");
                break;
            case 5:
                WebViewActivity.lunch(this,Constant.LIFE_HELPER,"开源项目介绍");
                break;
            case 6:
                WebViewActivity.lunch(this,Constant.ZHI_HU,"我的知乎");
                break;
            case 7:
                WebViewActivity.lunch(this,Constant.JUE_JIN,"我的掘金");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            if (requestCode == REQUEST_CODE_SCAN) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                if (result.contains("http") || result.contains("https")) {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("url", result);
                    startActivity(intent);
                } else {
                    ToastUtils.showRoundRectToast(result);
                }
            }
        }
    }

    @Override
    public void initView() {
        initFindViewID();
        initDrawerLayoutStatus();
        initBar();
        initTabLayout();
        initViewPager();
        initNav();
    }

    private void initFindViewID() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mVpHome = findViewById(R.id.vp_home);
        mCtlTable = findViewById(R.id.ctl_table);
        mNavView = findViewById(R.id.nav_view);
        mSetting =findViewById(R.id.setting);
        mQuit = findViewById(R.id.quit);
        mToolbar = findViewById(R.id.toolbar);
        mFlTitleMenu = findViewById(R.id.fl_title_menu);
        mTvTitle = findViewById(R.id.tv_title);
    }


    @Override
    public void initListener() {
        mFlTitleMenu.setOnClickListener(MainActivity.this);
        mNavView.setOnClickListener(MainActivity.this);
        mSetting.setOnClickListener(listener);
        mQuit.setOnClickListener(listener);
    }


    @Override
    public void initData() {
        presenter.getUpdate();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_title_menu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    }


    /**
     * 初始化侧滑菜单的状态栏
     */
    private void initDrawerLayoutStatus() {
        //为DrawerLayout 布局设置状态栏变色，也就是加上透明度
        //YCAppBar.setStatusBarLightMode(this, R.color.colorTheme);
        //YCAppBar.setStatusBarLightMode(this, R.color.colorTheme);
    }


    /**
     * 初始化ActionBar按钮
     */
    private void initBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }


    /**
     * 初始化底部导航栏数据
     */
    private void initTabLayout() {
        ArrayList<CustomTabEntity> mTabEntities = presenter.getTabEntity();
        mCtlTable.setTabData(mTabEntities);
        mTvTitle.setText("新闻首页");
        mCtlTable.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                selectIndex = position;
                mVpHome.setCurrentItem(selectIndex);
                switch (position) {
                    case 0:
                        mTvTitle.setVisibility(View.VISIBLE);
                        mTvTitle.setText("新闻首页");
                        break;
                    case 1:
                        mTvTitle.setVisibility(View.VISIBLE);
                        mTvTitle.setText("数据中心");
                        break;
                    case 2:
                        mTvTitle.setVisibility(View.VISIBLE);
                        mTvTitle.setText("生活应用");
                        mCtlTable.showMsg(2, 0);
                        break;
                    case 3:
                        mTvTitle.setVisibility(View.VISIBLE);
                        mTvTitle.setText("更多内容");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }


    /**
     * 初始化ViewPager数据
     */
    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new DataFragment());
        fragments.add(new MeFragment());
        BasePagerAdapter adapter = new BasePagerAdapter(getSupportFragmentManager(), fragments);
        mVpHome.setAdapter(adapter);
        mVpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position >= 0) {
                    mCtlTable.setCurrentTab(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mVpHome.setOffscreenPageLimit(4);
        mVpHome.setCurrentItem(0);
    }

    /**
     * 初始化侧滑菜单
     */
    private void initNav() {
        View view = mNavView.inflateHeaderView(R.layout.nav_header_main);
        ImageView ivAvatar = view.findViewById(R.id.iv_avatar);
        TextView tvUsername = view.findViewById(R.id.tv_username);
        LinearLayout llNavHomepage = view.findViewById(R.id.ll_nav_homepage);
        LinearLayout llNavScanDownload = view.findViewById(R.id.ll_nav_scan_download);
        LinearLayout llNavFeedback = view.findViewById(R.id.ll_nav_feedback);
        LinearLayout llNavAbout = view.findViewById(R.id.ll_nav_about);
        LinearLayout llNavLogin = view.findViewById(R.id.ll_nav_login);
        LinearLayout llNavVideo = view.findViewById(R.id.ll_nav_video);
        GlideImageUtils.loadImageRound(this, R.drawable.ic_person_logo, ivAvatar);
        tvUsername.setText("杨充");
        ivAvatar.setOnClickListener(listener);
        llNavHomepage.setOnClickListener(listener);
        llNavScanDownload.setOnClickListener(listener);
        llNavFeedback.setOnClickListener(listener);
        llNavAbout.setOnClickListener(listener);
        llNavLogin.setOnClickListener(listener);
        llNavVideo.setOnClickListener(listener);
    }


    /**
     * 自定义菜单点击事件
     */
    private PerfectClickListener listener = new PerfectClickListener() {
        @Override
        protected void onNoDoubleClick(final View v) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerLayout.postDelayed(() -> {
                switch (v.getId()) {
                    case R.id.iv_avatar:

                        break;
                    // 主页
                    case R.id.ll_nav_homepage:
                        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", "");
                        startActivity(intent);
                        break;
                    //扫码下载
                    case R.id.ll_nav_scan_download:

                        break;
                    // 问题反馈
                    case R.id.ll_nav_feedback:
                        ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_FEEDBACK);
                        break;
                    // 关于
                    case R.id.ll_nav_about:
                        ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_ABOUT_ME);
                        break;
                    // 个人
                    case R.id.ll_nav_login:
                        break;
                    case R.id.ll_nav_video:
                        ToastUtils.showRoundRectToast( "后期接入讯飞语音");
                        break;
                    case R.id.setting:
                        ARouterUtils.navigation(RouterConfig.App.ACTIVITY_APP_SETTING_ACTIVITY);
                        break;
                    case R.id.quit:
                        ActivityManager.getInstance().appExist();
                        break;
                    default:
                        break;
                }
            }, 0);
        }
    };

    /**
     * 是当某个按键被按下是触发。所以也有人在点击返回键的时候去执行该方法来做判断
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.e("触摸监听", "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout!=null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                //双击返回桌面
                if ((System.currentTimeMillis() - time > 1000)) {
                    ToastUtils.showRoundRectToast("再按一次返回桌面");
                    time = System.currentTimeMillis();
                } else {
                    moveTaskToBack(true);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}