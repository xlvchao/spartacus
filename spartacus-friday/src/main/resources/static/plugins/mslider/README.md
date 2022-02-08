# 侧滑弹层插件--mSlider.js
#### 用法：
1. 引入mSlider.js
```javascript
<script src="mSlider.js"></script>
```

2. new一个实例：
```javascript
var demo = new mSlider({dom: "#xxxx"})
```

3. 绑定方法:
```javascript
demo.open()
```

#### 参数：
<table>
<tbody>
<tr>
<td>属性</td>
<td>说明</td>
<td>举例</td>
</tr>
<tr>
<td>dom</td>
<td>容器节点(必填)</td>
<td>如："#xxx" 、 ".xxx"</td>
</tr>
<tr>
<td>distance</td>
<td>弹层宽度(选填，默认"60%")</td>
<td>支持：px|%|auto</td>
</tr>
<tr>
<td>time</td>
<td>自动关闭时间(选填，默认不关闭)</td>
<td>单位毫秒，如"2000"</td>
</tr>
<tr>
<td>direction</td>
<td>弹出方向(选填，默认"left")</td>
<td>"left":从左弹出，"right":从右弹出，"top":从上弹出，"bottom":从下弹出</td>
</tr>
<tr>
<td>maskClose</td>
<td>点击遮罩关闭弹层(选填，默认true)</td>
<td></td>
</tr>
<tr>
<td>callback</td>
<td>关闭时回调函数</td>
<td></td>
</tr>
</tbody>
</table>

#### 方法：
<table>
<tbody>
<tr>
<td>方法名</td>
<td>说明</td>
<td>举例</td>
</tr>
<tr>
<td>.open()</td>
<td>打开弹窗</td>
<td>如：xxx.open()</td>
</tr>
<tr>
<td>.close()</td>
<td>关闭弹窗</td>
<td>如：xxx.close()</td>
</tr>
</tbody>
</table>

#### 演示地址: [demo](http://denghao.me/demo/2016/mslider.html) 
#### 项目源码: [gitHub](https://github.com/denghao123/mSlider.js) 