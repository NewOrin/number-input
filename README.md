## InputNumberView  数字输入框

### 简介

自定义View组合控件，完成验证码输入框or密码输入框效果。该控件使用FrameLayout作为父布局，定义了透明背景的EditText用于接收用户输入的内容，显示的数字采用自定义的TextView。整个控件提供多个自定义属性如字体，边框等自定义

### 示例

![示例图片](https://github.com/NewOrin/number-input/blob/master/sample/screenshot01.png)

### 自定义属性

| name              | 说明                  | format    | 默认值     |
| ----------------- | ------------------- | --------- | ------- |
| niv_text_size_sp  | 输入框字体大小             | integer   | 16      |
| niv_text_color    | 输入框字体颜色             | color     | #333333 |
| niv_text_divider  | 输入框间隔               | dimension | 5       |
| niv_text_width    | 输入框宽度(width=height) | dimension | 40      |
| niv_border_width  | 输入框边框宽度             | dimension | 2       |
| niv_border_color  | 输入边框颜色              | color     | #333333 |
| niv_border_radius | 输入框圆角角度             | dimension | 4       |
| niv_is_fill       | 是否填充输入框             | boolean   | false   |
| niv_count         | 输入框个数(最大为10)        | integer   | 6       |
| niv_is_pw_mode    | 输入数字是否用点代替          | boolean   | false   |

### 可用方法

| method_name                              | description | return type |
| ---------------------------------------- | ----------- | ----------- |
| setInputCompleteListener(inputCompleteListener: InputCompleteListener) | 输入完成时监听     | Unit        |

### 使用示例

- 上示例图中xml代码如下

  ```xml
  <LinearLayout
      xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:gravity="center"
      android:orientation="vertical"
      android:paddingBottom="20dp"
      android:paddingTop="20dp"
      tools:context="com.neworin.sample.MainActivity">

      <com.neworin.numberinputview.widget.NumberInputView
          android:id="@+id/sample_niv1"
          android:layout_width="match_parent"
          android:layout_height="0dp"
          android:layout_weight="1"
          app:niv_count="4"
          app:niv_is_fill="false"
          app:niv_text_size_sp="18"/>

      <com.neworin.numberinputview.widget.NumberInputView
          android:id="@+id/sample_niv2"
          android:layout_width="match_parent"
          android:layout_height="0dp"
          android:layout_weight="1"
          app:niv_border_color="@color/colorPrimaryDark"
          app:niv_border_radius="0dp"
          app:niv_count="5"
          app:niv_text_color="@color/color_red"/>

      <com.neworin.numberinputview.widget.NumberInputView
          android:id="@+id/sample_niv3"
          android:layout_width="match_parent"
          android:layout_height="0dp"
          android:layout_weight="1"
          app:niv_border_color="@color/input_layout_bg"
          app:niv_border_radius="0dp"
          app:niv_count="6"
          app:niv_is_fill="true"
          app:niv_text_size_sp="20"/>

      <com.neworin.numberinputview.widget.NumberInputView
          android:id="@+id/sample_niv4"
          android:layout_width="match_parent"
          android:layout_height="0dp"
          android:layout_weight="1"
          app:niv_count="6"
          app:niv_is_pw_mode="true"
          app:niv_text_color="@color/colorPrimary"
          app:niv_text_size_sp="18"/>

  </LinearLayout>
  ```

  ​

### 实现步骤

- 该控件使用Kotlin语言开发，由四个控件组合而成，即BorderTextView，LinearLayout，EditText和FrameLayout。

1. BorderTextView
   控件的自定义BorderTextView继承自AppCompatTextView，在原有的TextView属性下，有两个特效，第一是实现了TextView绘制背景边框，该边框可FILL也可STROKE，边框颜色、粗细均可自定义。第二是在TextView的中心，以1/2 * TextSize的半径绘制了一个圆，将输入的文字覆盖，从而实现了输入密码需要将内容以"●"显示的功能。其实也可以用通过设置TextView的transformationMethod属性来将TextView显示成"●"，但考虑到两个问题，其一是圆点的颜色不能变化，其二是圆点的大小不好改变。BorderTextView中用Canvas.drawaCircle()方法，圆点的颜色、大小和TextView的属性保持一致，Everthing under control的感觉有木有。

   - BorderTextView的代码可见BorderTextView.kt文件

   - 定义一个存放BorderTextView的集合，用于显示一个或多个输入框

     ```kotlin
      /**
          * 初始化TextView
          */
         private fun initTextView() {
             mTextViews = mutableListOf()
             for (i in 1..mTextViewCounts) {
                 val tv = BorderTextView(context)
                 tv.let {
                     it.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize.toFloat())
                     it.setTextColor(mTextColor)
                     it.gravity = Gravity.CENTER
                     it.setIsFill(mIsFill)
                     it.setStrokeColor(mBorderColor)
                     it.setIsPasswordMode(mIsPwMode)
                     if (mIsFill) {
                         it.setStrokeWidth(0)
                     } else {
                         it.setStrokeWidth(mBorderWidth)
                     }
                     it.setCornerRadius(mBorderRadius)
                 }
                 mTextViews.add(tv)
             }
         }
     ```

2. LinearLayout

   - LinearLayout用于存放多个输入框，orientation设置为LinearLayout.HORIZONTAL即可

3. EditText

   - EditText的背景设置成透明，再设置 isCursorVisible = false、inputType = InputType.TYPE_CLASS_NUMBER 属性。同时，用户输入的时候需要拦截输入内容，再将内容显示到BorderTextView上。监听用户键盘的KeyEvent.KEYCODE_DEL 事件用户处理输入内容的删除

     ```kotlin
         /**
          * 设置监听
          */
         private fun setListener() {
             mEditText.addTextChangedListener(object : TextWatcher {
                 override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                 }

                 override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 }

                 override fun afterTextChanged(s: Editable?) {
                     if (s?.isNotEmpty() == true) {
                         mEditText.setText("")
                         if (mInputSb.length >= mTextViewCounts) {
                             mInputCompleteListener?.inputComplete(mInputSb.toString())
                             return
                         }
                         if (mInputSb.length < mTextViewCounts) {
                             mInputSb.append(s.toString())
                             mInputSb.forEachIndexed { index, c ->
                                 mTextViews[index].text = c.toString()
                             }
                             if (mInputSb.length == mTextViewCounts) {
                                 mInputCompleteListener?.inputComplete(mInputSb.toString())
                             }
                         }
                     }
                 }
             })
             mEditText.setOnKeyListener { v, keyCode, event ->
                 if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                     if (mInputSb.isNotEmpty()) {
                         mInputSb.delete(mInputSb.length - 1, mInputSb.length)
                         mTextViews[mInputSb.length].text = ""
                         return@setOnKeyListener true
                     }
                 }
                 return@setOnKeyListener false
             }
         }

     ```

4.  FrameLayout

   - FrameLayout就简单了，将上面的LinearLayout和EditText放一起即可

### That's all，如有问题，不吝赐教~

MIT License

Copyright (c) 2018 NewOrin Zhang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.