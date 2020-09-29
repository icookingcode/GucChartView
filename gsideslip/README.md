# 流体控件
## Maven
```
<dependency>
  <groupId>com.guc.gsideslip</groupId>
  <artifactId>gsideslip</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
## Gradle
```
implementation 'com.guc.gsideslip:gsideslip:1.0.0'
```
## SideSlipView
### xml引用
```
    <com.guc.gsideslip.SideSlipView
        android:id="@+id/sideSlipView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
### 使用
```
    sideSlipView.setImageResource(R.drawable.bg_guide_1)
    
    sideSlipView.setOnStateChangedListener(object : SideSlipView.OnStateChangedListener{
        override fun onStateChanged(state: Int) {
            if (state == STATE_EXPANDED) {
                Log.e(TAG,"展开")
            else if (state == STATE_MOVING) {
                Log.e(TAG,"滑动")
            } else {
                Log.e(TAG,"关闭")
            }
        }
    
    })
```
## SideSlipSurfaceView 用法同SideSlipView
## SideSlipGuideView  炫酷引导页
### xml引用
```
    <com.guc.gsideslip.SideSlipGuideView
        android:id="@+id/sideSlipGuideView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
### 使用
```
   sideSlipGuideView.addGuides(R.drawable.bg_guide_1,R.drawable.bg_guide_2,R.drawable.bg_guide_3)
   sideSlipGuideView.onStart = {
        Toast.makeText(this,"开启应用",Toast.LENGTH_LONG).show()  //点击最后一页进入应用
   }
```

#关于我
Name: Guchao
Email: happygc913@gmail.com / happygc@126.com
CSDN: [snow_lyGirl](https://blog.csdn.net/qq_31028313)
GitHub: [GuchaoGit](https://github.com/GuchaoGit?tab=repositories)
Gitee:[GuChaoGitee](https://gitee.com/guchaogitee/projects)
加入QQ群:128937635