# Weibo for Android

轻量的新浪微博 Android 客户端，显示名是 **Water Walk**。包名 `org.lytsing.android.weibo`，当前版本 **1.3**（`versionCode` 11）。最低支持 Android 4.0（API 14）。

启动后若本地没有有效的 OAuth2 token，会走微博 SSO 授权，成功后进入好友时间线。没有广告。

![时间线](http://lytsing.org/images/weibo1.png) ![微博详情](http://lytsing.org/images/weibo2.png)

## 功能

已接到界面上的能力：

| 界面 | 行为 |
| --- | --- |
| 登录 | `SsoHandler` 做微博 SSO。token 和过期时间写在 `SharedPreferences`，由 `Session` 读写 |
| 时间线 | 打开和刷新走 Volley，请求 `https://api.weibo.com/2/statuses/friends_timeline.json`，每次 20 条。下拉刷新用 `since_id`，上拉加载用微博 SDK 的 `StatusesAPI.friendsTimeline` |
| 发微博 | 纯文字走 `StatusesAPI.update`。选了图走 `StatusesAPI.upload`。可以插表情，也可以附带当前位置 |
| 详情 | 看正文、转发原文、中图、评论列表。可以评论、转发、收藏 |
| 图片 | 中图点开后进入 `ImageViewerActivity`，再进全屏缩放 |
| 侧栏 | 列出 Home、Settings、Search、Help。目前只有 Settings 会打开设置页 |
| 设置 | 计算并清除 AndroidQuery 图片缓存，查看开源许可、版本号，跳到本仓库 |

侧栏里的 Search、Help 没有对应页面。`core/WeiboApiClient` 和 `StatusesService`、`SearchService` 是一套未接入界面的 Retrofit 1.7 声明，路径还是 Twitter 的 `/1.1/...`，`RestAdapter` 也没有设置 endpoint。界面实际发请求的是 Volley 和 `WeiboSDK`。

应用密钥、回调地址和授权 scope 写在 `app/src/main/java/org/lytsing/android/weibo/core/Configuration.java`。换自己的微博应用时改这里。

## 运行环境

- JDK：用 Android Studio 自带的 JBR。`build.sh` 把 `JAVA_HOME` 指到 `/Applications/Android Studio.app/Contents/jbr/Contents/Home`
- Android Gradle Plugin 7.4.1
- Gradle Wrapper **7.5**
- `app`：`compileSdkVersion` / `targetSdkVersion` 30，`minSdkVersion` 14，已启用 AndroidX
- `WeiboSDK`：库模块，`compileSdkVersion` 16，`minSdkVersion` 14

## 编译

```bash
./gradlew check
./gradlew clean build
```

`./build.sh` 就是上面两条，并先设置 `JAVA_HOME`。`check` 会跑 Checkstyle 8.31 和 PMD，失败不中断构建，报告在 `app/build/reports/`。

## 模块

```
app/          应用。界面、Volley 请求、模型
WeiboSDK/     新浪微博 SDK 源码，另依赖 libs/weibosdkcore.jar
config/quality/   Checkstyle 与 PMD 规则
```

`app` 的主要依赖：

| 依赖 | 用途 |
| --- | --- |
| `:WeiboSDK` | 登录、发微博、评论、转发、收藏、上拉加载 |
| Volley 1.2.0 | 时间线请求和图片 |
| Gson 2.8.8 | 解析时间线 JSON |
| Logger 2.2.0 | 日志 |
| MenuDrawer 3.0.6 | 侧栏 |
| AndroidX `legacy-support-v4` 1.0.0 | `FragmentActivity`、`NavUtils` |
| `app/libs/pullandloadmore-1.0.0.aar` | 下拉刷新、上拉加载 |
| `app/libs/android-query-full.0.26.7.jar` | 发微博页的图片和定位 |
| `app/libs/okhttp-2.0.0.jar` 及 urlconnection、okio | `OkHttpStack`，给 Volley 当传输 |
| `app/libs/retrofit-1.7.1.jar` | 未接入界面的 API 声明 |
| `app/libs/CWAC-SackOfViewsAdapter.jar` | 详情页把正文和评论拼进同一个列表 |

ActionBarSherlock 已经从工程里去掉，菜单使用系统 ActionBar。

## 代码结构

```
app/src/main/java/org/lytsing/android/weibo/
  WeiboApplication.java          Volley 队列、图片缓存、恢复登录态
  Consts.java
  ui/TimelineActivity.java       好友时间线，启动入口
  ui/AuthenticatedActivity.java  SSO 登录
  ui/ComposeActivity.java        发微博
  ui/StatusDetailActivity.java   详情、评论、转发、收藏
  ui/ImageViewerActivity.java
  ui/ImageZoomActivity.java
  ui/SettingsActivity.java
  ui/MenuFragment.java
  core/Configuration.java        App Key、回调、scope、API 根地址
  core/Session.java              token 持久化
  core/WeiboApiClient.java       未使用的 Retrofit 客户端
  core/services/                 Retrofit 接口，界面未调用
  toolbox/GsonRequest.java
  toolbox/OkHttpStack.java
```

## 代码风格

沿用 Android 代码风格：4 空格缩进，非 public 非 static 字段以 `m` 开头，左大括号不单独成行。看周围已有代码，保持一致即可。

提交以一个修复为一笔。若关闭 issue，在说明里写 `closes #12`。贡献代码使用 Apache License 2.0。

## 许可

Copyright (c) 2012-2017 [Lytsing Huang](http://lytsing.org)

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
