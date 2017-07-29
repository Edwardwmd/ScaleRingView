package com.wmd.demo.scaleringview;

import android.content.Context;
import android.util.TypedValue;

import java.text.DecimalFormat;

/**
 * 时间：2017/07/28/22：03
 * 作者：吴明德
 * 邮箱：1732141816@qq.com
 * 作用：xxxx
 * 声明：版权归作者所有
 */

public  class UIUtils {

    public static Context getContext(){
        return RingAppcation.getAppcation();
    }
    //5.dp转换px
    public static int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    //6.px转换dp
    public static int px2dp(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    //7.sp转px
    public static int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getContext().getResources().getDisplayMetrics());
    }
    /**
     * 字符串转换
     *
     * @param value double型保留2位小数
     * @return
     */
    public static String retainDecimal(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return UIUtils.replaceString(decimalFormat.format(value));
    }

    /**
     * 字符串转换
     *
     * @param value 浮点型保留2位小数
     * @return
     */
    public static String retainDecimall(double value) {

        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return UIUtils.replaceString(decimalFormat.format(value));
    }

    public static String replaceString(String oldString) {
        if (oldString.indexOf(",") != -1) {
            return oldString.replace(",", ".");
        } else {
            return oldString;
        }
    }

}
