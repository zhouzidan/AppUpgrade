# AppUpgrade

项目上的打包流程：gitLab -> Jenkins -> Pgyer

一款服务于开发人员、测试人员的App，主要用于解决日常需要安装App或升级App，都需要进入payer的网页下载版本、有的时候记不得下载网址要去翻找邮箱等繁琐的场景

App的主要步骤：
1. 通过扫码获取到Pgyer的ApiKey
2. 通过ApiKey获取到应用列表
3. 点击应用列表，选中目标应用，自动保存为关注的应用列表
4. 首页展示 关注的应用列表
5. 在首页上，点击关注列表，进入应用详情
6. 应用详情页面，展示当前应用的历史版本、最新版本的下载入口
7. 点击历史版本列表上的某一个，在外部浏览器上打开网页，已完成后续的查看、下载
8. 点击最新版本的下载按钮，App内部下载APK，引导安装




# 第三方依赖
1. 华为的扫码  com.huawei.hms:scan
2. 下载库 com.liulishuo.okdownload:okdownload
3. dialog库 com.afollestad.material-dialogs:core
