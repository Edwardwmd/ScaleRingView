package com.wmd.demo.scaleringview;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.zhy.autolayout.utils.AutoUtils;


/**
 * 辐射控制环形指示图
 * Created by 吴明德 on 2016/11/29.
 */
public class ScaleRingView extends View {
    //环扫圆环特殊线高
    private static final int[] SCALE_LINE_BASE_LEN_ARRAY = new int[]{
            AutoUtils.getPercentWidthSize(1),
            AutoUtils.getPercentWidthSize(3),
            AutoUtils.getPercentWidthSize(5),
            AutoUtils.getPercentWidthSize(7),
            AutoUtils.getPercentWidthSize(9)};
    // 控件宽
    private int width;
    // 控件高
    private int height;
    // 刻度盘半径
    private int dialRadius;
    // 圆弧半径
    private int arcRadius;
    //内部圆半径
    private int cirRadius;
    //内部圆颜色
    private int cirColor = Color.parseColor("#000000");
    // 刻度高
    private int scaleHeight = AutoUtils.getPercentWidthSize(17);

    //外刻度线高
    private int outsideScaleHeight = AutoUtils.getPercentWidthSize(13);

    // 刻度盘画笔
    private Paint dialPaint;
    // 圆弧画笔
    private Paint arcPaint;
    //最内部分的圆画笔
    private Paint cirPaint;
    // 标题画笔
    private Paint mRadiationUnitTextPaint;
    // 辐射显示画笔
    private Paint tempPaint;

    public void setRadiationUnitText(String mRadiationUnitText) {
        this.mRadiationUnitText = mRadiationUnitText;
        invalidate();
    }

    // 文本提示
    private String mRadiationUnitText ="";
    private String mOutsideTipsText = "";
    //辐射值字体大小
    private int mRadiationTextSize = UIUtils.sp2px(50);
    //辐射单位字体大小
    private int mUnitTextSize = UIUtils.sp2px(18);
    private int mOutsideTextSize = UIUtils.sp2px(12);
    // 扫射圆环辐射值（其实就是辐射值）
    private float mRadiationValue = 0.0f;
    //默认圆环的参数控制值（其实就是辐射值）
    private float mNoRadiationValue = 0.0f;
    // 最高辐射
    private float maxDetectRadiationValue = 0.0f;
    // 15格（每格3度，共45度）代表辐射值0.1vsm/h
    //每个刻度之间的角度
    private float anglePreScale = 3.0f;
    //刻度总数
    private int totalScaleCount = 120;
    //圆周角度
    private float angleOfCircumference = 360.0f;
    //辐射单位所在的矩形
    private Rect mUnitBound;
    //屏高度
    private int screenHeight;
    //当前刻度数量
    private int currentScaleLen = 0;
    //特殊长度的刻度数
    private int specialScaleLen;
    //正常长度刻度数
    private int namolScaleLen;
    //记录上一个传过来的辐射值
    private float preDetectRadiationValue = 0;
    //扫射圆环动画时间长度
    private long radiationScaleTime = 1000L;
    //是否是旋转整个圆环
    private int bigDialRadius;
    private Paint tipsPaint;
    private Rect mTipsBound;
    private int sizeWidth;
    private int sizeHeight;
    private double bVar;
    private double aVar;
    private int bigcirRadius;
    private Paint bigCirPaint;
    private RadialGradient radialGradient;
    public final static float STAGE_1_VALUE = 0.50f;
    public final static float STAGE_2_VALUE = 1.30f;
    private int[] preScaleColor = new int[]{Color.parseColor("#809BCD9B"), Color.parseColor("#80EECFA1"), Color.parseColor("#80EEB4B4")};
    private int[] scaleColor = new int[]{Color.argb(255, 119, 170, 0), Color.argb(255, 250, 186, 0), Color.argb(255, 251, 4, 0)};
    private int[] intSideColor = new int[]{Color.parseColor("#343D34"), Color.parseColor("#4E3D35"), Color.parseColor("#4B2C2E")};
    private int[] outsideColor = new int[]{0x366CD00, 0x5feba00, 0x6f10900, 0x066CD00, 0x1feba00, 0x1f10900};
    private int[] flagColor = new int[]{Color.parseColor("#85EEB422"), Color.parseColor("#85EE4000")};
    private int bgvColor = Color.TRANSPARENT;
    private int osColor = Color.TRANSPARENT;
    private ValueAnimator mAnimator;
    private int fromColor;
    private int toColor;
    private ValueAnimator outsideAnimator;

    public ScaleRingView(Context context) {
        this(context, null);
    }

    public ScaleRingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleRingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ScaleRingView);
        if (attr == null) {
            return;
        }
        try {
            if (attr.hasValue(R.styleable.ScaleRingView_radiation_textsize)) {
                mRadiationTextSize = attr.getDimensionPixelSize(R.styleable.ScaleRingView_radiation_textsize, 0);
            }
            if (attr.hasValue(R.styleable.ScaleRingView_unit_textsize)) {
                mUnitTextSize = attr.getDimensionPixelSize(R.styleable.ScaleRingView_unit_textsize, 0);
            }
            if (attr.hasValue(R.styleable.ScaleRingView_outside_textsize)) {
                mOutsideTextSize = attr.getDimensionPixelSize(R.styleable.ScaleRingView_outside_textsize, 0);
            }
            if (attr.hasValue(R.styleable.ScaleRingView_bigDialRadius)) {
                bigDialRadius = attr.getDimensionPixelSize(R.styleable.ScaleRingView_bigDialRadius, 0);
            }
            if (attr.hasValue(R.styleable.ScaleRingView_cirRadius)) {
                cirRadius = attr.getDimensionPixelSize(R.styleable.ScaleRingView_cirRadius, 0);
            }
            if (attr.hasValue(R.styleable.ScaleRingView_dialRadius)) {
                dialRadius = attr.getDimensionPixelSize(R.styleable.ScaleRingView_dialRadius, 0);
            }

        } finally {
            attr.recycle();
        }


    }

    protected TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    private void init() {
        bigCirPaint = new Paint();
        bigCirPaint.setAntiAlias(true);
        bigCirPaint.setStyle(Paint.Style.FILL);


        dialPaint = new Paint();
        dialPaint.setAntiAlias(true);
        dialPaint.setStrokeWidth(AutoUtils.getPercentWidthSize(3));
        dialPaint.setStyle(Paint.Style.STROKE);

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(Color.parseColor("#FFFFFF"));
        arcPaint.setStrokeWidth(AutoUtils.getPercentWidthSize(3));
        arcPaint.setStyle(Paint.Style.STROKE);

        cirPaint = new Paint();
        cirPaint.setAntiAlias(true);
        cirPaint.setColor(cirColor);
        cirPaint.setStyle(Paint.Style.FILL);

        mRadiationUnitTextPaint = new Paint();
        mRadiationUnitTextPaint.setAntiAlias(true);
        mRadiationUnitTextPaint.setTextSize(AutoUtils.getPercentWidthSize(mUnitTextSize));
        mRadiationUnitTextPaint.setColor(Color.parseColor("#FFFFFF"));
        mRadiationUnitTextPaint.setStyle(Paint.Style.STROKE);
        mUnitBound = new Rect();

        tempPaint = new Paint();
        tempPaint.setAntiAlias(true);
        tempPaint.setTypeface(Typeface.DEFAULT_BOLD);
        tempPaint.setTextSize(AutoUtils.getPercentWidthSize(mRadiationTextSize));
        tempPaint.setColor(Color.parseColor("#FFFFFF"));
        tempPaint.setStyle(Paint.Style.STROKE);

        tipsPaint = new Paint();
        tipsPaint.setAntiAlias(true);
        mTipsBound = new Rect();
        tipsPaint.setTypeface(Typeface.DEFAULT_BOLD);
        tipsPaint.setTextSize(AutoUtils.getPercentWidthSize(mOutsideTextSize));
        tipsPaint.setColor(Color.parseColor("#FFFFFF"));
        tipsPaint.setStyle(Paint.Style.STROKE);

        specialScaleLen = SCALE_LINE_BASE_LEN_ARRAY.length;
        bVar = Math.floor(0.5d + (STAGE_1_VALUE * STAGE_1_VALUE / (STAGE_2_VALUE - STAGE_1_VALUE * 2)));
        aVar = Math.floor(0.5d + (80.0f / (Math.log(STAGE_2_VALUE / bVar + 1.05f))));
        maxDetectRadiationValue = (float) (bVar * (Math.exp(totalScaleCount / aVar) - 1));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(sizeWidth, sizeHeight);
        width = height = Math.min(sizeHeight, sizeWidth);
        screenHeight = Math.max(sizeHeight, sizeWidth);
        bigcirRadius = width / 2 - AutoUtils.getPercentWidthSize(20);
        // 刻度盘半径
        bigDialRadius = width / 2 - AutoUtils.getPercentWidthSize(60);

        dialRadius = width / 2 - AutoUtils.getPercentWidthSize(85);
        // 圆弧半径
        arcRadius = dialRadius - AutoUtils.getPercentWidthSize(22);
        //内部圆半径
        cirRadius = arcRadius - AutoUtils.getPercentWidthSize(2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOutsideCircle(canvas);
        drawCircle(canvas);
        drawScale(canvas);
        drawArc(canvas);
        drawText(canvas);
        drawRad(canvas);
        drawOutsideText(canvas);
        invalidate();
    }

    private void drawOutsideCircle(Canvas canvas) {
        canvas.save();
        internalBigCircleColorControl();
        if (width > screenHeight) {
            canvas.drawCircle(width / 2, screenHeight / 2, bigcirRadius, bigCirPaint);
        } else {
            canvas.drawCircle(screenHeight / 2, width / 2, bigcirRadius, bigCirPaint);
        }

    }

    private void internalBigCircleColorControl() {
        radialGradient = new RadialGradient(width / 2, screenHeight / 2, bigcirRadius, bgvColor, osColor, Shader.TileMode.MIRROR);
        bigCirPaint.setShader(radialGradient);
    }

    /**
     * 绘制内部圆，作为填充色用
     *
     * @param canvas 画布
     */
    private void drawCircle(Canvas canvas) {
        canvas.save();
        if (width > screenHeight) {
            canvas.drawCircle(width / 2, screenHeight / 2, cirRadius, cirPaint);
        } else {
            canvas.drawCircle(screenHeight / 2, width / 2, cirRadius, cirPaint);
        }
        internalCircleColorControl();

    }

    /**
     * 绘制刻度盘
     *
     * @param canvas 画布
     */
    private void drawScale(Canvas canvas) {
        canvas.save();

        namolScaleLen = currentScaleLen - specialScaleLen;
        canvas.translate(getWidth() / 2, getHeight() / 2);

        //1.1逆时针旋转-180度
        canvas.rotate(-angleOfCircumference / 2);
        //1.2正常情况下的圆环刻度，作为底色
        for (int i = 0; i < totalScaleCount; i++) {
            if (i == totalScaleCount / 3) {
                dialPaint.setColor(flagColor[0]);
                canvas.drawLine(0, -bigDialRadius, 0, -bigDialRadius + outsideScaleHeight, dialPaint);
            } else if (i == 2 * totalScaleCount / 3) {
                dialPaint.setColor(flagColor[1]);
                canvas.drawLine(0, -bigDialRadius, 0, -bigDialRadius + outsideScaleHeight, dialPaint);
            }

            canvas.rotate(anglePreScale);
        }
        dialPaint.setColor(Color.parseColor("#60FFFFFF"));

        for (int i = 0; i < totalScaleCount; i++) {
            normalRingColorControl(i);
            canvas.drawLine(0, -dialRadius, 0, -dialRadius + scaleHeight, dialPaint);
            canvas.rotate(anglePreScale);
        }

        //2.1参数变化时圆环会对应到当前所在的刻度
        canvas.rotate(-angleOfCircumference);
        ringSweepColorControl();
        if (currentScaleLen >= specialScaleLen) {
            for (int i = 0; i < namolScaleLen; i++) {
                canvas.drawLine(0, -dialRadius, 0, -dialRadius + scaleHeight, dialPaint);
                canvas.rotate(anglePreScale);
            }
            for (int i = 0; i < specialScaleLen; i++) {
                canvas.drawLine(0, -dialRadius + scaleHeight, 0, -dialRadius - SCALE_LINE_BASE_LEN_ARRAY[i], dialPaint);
                canvas.rotate(anglePreScale);
            }
        } else {
            for (int i = 0; i < currentScaleLen; i++) {
                canvas.drawLine(0, -dialRadius + scaleHeight, 0, currentScaleLen == 1 && mRadiationValue == 0 ? -dialRadius - SCALE_LINE_BASE_LEN_ARRAY[4] : -dialRadius - SCALE_LINE_BASE_LEN_ARRAY[i], dialPaint);
                canvas.rotate(anglePreScale);
            }
        }

        canvas.restore();
    }

    /**
     * 绘制刻度盘下的圆弧
     *
     * @param canvas 画布
     */
    private void drawArc(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        RectF rectF = new RectF(-arcRadius, -arcRadius, arcRadius, arcRadius);
        canvas.drawArc(rectF, 0, angleOfCircumference, false, arcPaint);
        canvas.restore();
    }

    /**
     * 绘制辐射单位
     *
     * @param canvas 画布
     */
    private void drawText(Canvas canvas) {
        canvas.save();
        //绘制文字
        mRadiationUnitTextPaint.setTextSize(AutoUtils.getPercentWidthSize(mUnitTextSize));
        radiationUnitTextColorControl();
        mRadiationUnitTextPaint.getTextBounds(mRadiationUnitText, 0, mRadiationUnitText.length(), mUnitBound);
        // 绘制辐射单位
        if (width > screenHeight)
            canvas.drawText(mRadiationUnitText, (width - mUnitBound.width()) / 2, 9 * bigDialRadius / 5, mRadiationUnitTextPaint);
        else
            canvas.drawText(mRadiationUnitText, (screenHeight - mUnitBound.width()) / 2, 9 * bigDialRadius / 5, mRadiationUnitTextPaint);
    }


    private void drawOutsideText(Canvas canvas) {
        canvas.save();
        canvas.rotate(angleOfCircumference);
        tipsPaint.setTextSize(AutoUtils.getPercentWidthSize(mOutsideTextSize));
        tipsPaint.getTextBounds(mOutsideTipsText, 0, mOutsideTipsText.length(), mTipsBound);
        for (int i = 0; i < totalScaleCount; i++) {
            if (i == 2 * totalScaleCount / 3) {
                mOutsideTipsText = "1.30";
                tipsPaint.setColor(flagColor[1]);
                canvas.drawText(
                        mOutsideTipsText,
                        screenHeight > width ? width / 2 + bigDialRadius - mTipsBound.width() : screenHeight / 2 + bigDialRadius - mTipsBound.width(),
                        screenHeight > width ? width / 2 + (bigDialRadius * (float) Math.sin(10)) : screenHeight / 2 + (bigDialRadius * (float) Math.sin(10)), tipsPaint);
            } else if (i == totalScaleCount / 3) {
                mOutsideTipsText = "0.50";
                tipsPaint.setColor(flagColor[0]);
                canvas.drawText(
                        mOutsideTipsText,
                        screenHeight > width ? width / 2 - bigDialRadius : screenHeight / 2 - bigDialRadius,
                        screenHeight > width ? width / 2 + (bigDialRadius * (float) Math.sin(10)) : screenHeight / 2 + (bigDialRadius * (float) Math.sin(10)), tipsPaint);
            }
        }

    }

    private void radiationUnitTextColorControl() {
        mRadiationUnitTextPaint.setColor(bgvColor);
    }


    /**
     * 绘制辐射
     *
     * @param canvas 画布
     */
    private void drawRad(Canvas canvas) {
        canvas.save();
        tempPaint.setTextSize(AutoUtils.getPercentWidthSize(mRadiationTextSize));
        radiationTextColorControl();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        float tempWidth = tempPaint.measureText(UIUtils.retainDecimall(mRadiationValue));
        float tempHeight = (tempPaint.ascent() + tempPaint.descent()) / 2;
        canvas.drawText(UIUtils.retainDecimall(mRadiationValue), -tempWidth / 2, -tempHeight, tempPaint);
        canvas.restore();
    }

    /**
     * 设置辐射
     *
     * @param currentRadiationValue 设置的辐射
     */
    public synchronized void setDetectRadiationValue(final float currentRadiationValue) {
        this.mNoRadiationValue = currentRadiationValue;

        ValueAnimator va = ValueAnimator.ofFloat(preDetectRadiationValue, currentRadiationValue);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mRadiationValue = (float) animation.getAnimatedValue();
                if (mRadiationValue == 0) {
                    currentScaleLen = (int) (Math.floor(0.5d + aVar * (Math.log(mRadiationValue / bVar + 1.015))));
                } else if (mRadiationValue > 0 && mRadiationValue <= maxDetectRadiationValue) {
                    currentScaleLen = (int) (Math.floor(0.5d + aVar * (Math.log(mRadiationValue / bVar + 1.055))));
                } else {
                    currentScaleLen = (int) (Math.floor(0.5d + aVar * (Math.log(maxDetectRadiationValue / bVar + 1))));
                }
            }
        });
        mAnimator = animator().asValueAnimator(new ColorChangeFAnimator.OnNewColorListener() {
            @Override
            public void onNewColor(@ColorInt int color) {
                bgvColor = color;
            }
        });
        outsideAnimator = outsideCircleAnimator().asValueAnimator(new ColorChangeFAnimator.OnNewColorListener() {
            @Override
            public void onNewColor(@ColorInt int color) {
                osColor = color;
            }
        });
        AnimatorSet as = new AnimatorSet();
        as.play(va).with(mAnimator).with(outsideAnimator);
        as.setInterpolator(new AccelerateDecelerateInterpolator());
        as.setDuration(radiationScaleTime);
        as.start();
        preDetectRadiationValue = currentRadiationValue;
        invalidate();
    }

    private ColorChangeFAnimator outsideCircleAnimator() {
        if (mNoRadiationValue - preDetectRadiationValue == 0) {
            if (preDetectRadiationValue >= 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                fromColor = outsideColor[0];
                toColor = outsideColor[3];
            } else if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                fromColor = outsideColor[1];
                toColor = outsideColor[4];
            } else {
                fromColor = outsideColor[2];
                toColor = outsideColor[5];
            }
        } else if (mNoRadiationValue - preDetectRadiationValue > 0) {
            if (mNoRadiationValue > 0 && mNoRadiationValue < STAGE_1_VALUE) {
                fromColor = outsideColor[0];
                toColor = outsideColor[3];
            } else if (mNoRadiationValue >= STAGE_1_VALUE && mNoRadiationValue < STAGE_2_VALUE) {
                if (preDetectRadiationValue > 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                    fromColor = outsideColor[0];
                    toColor = outsideColor[1];
                } else if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = outsideColor[1];
                    toColor = outsideColor[4];
                } else {
                    fromColor = outsideColor[0];
                    toColor = outsideColor[1];
                }
            } else {
                if (preDetectRadiationValue > 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                    fromColor = outsideColor[0];
                    toColor = outsideColor[2];
                } else if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = outsideColor[1];
                    toColor = outsideColor[2];
                } else {
                    fromColor = outsideColor[2];
                    toColor = outsideColor[5];
                }

            }
        } else {
            if (mNoRadiationValue > 0 && mNoRadiationValue < STAGE_1_VALUE) {
                if (preDetectRadiationValue >= 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                    fromColor = outsideColor[0];
                    toColor = outsideColor[3];
                } else if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = outsideColor[1];
                    toColor = outsideColor[0];
                } else {
                    fromColor = outsideColor[2];
                    toColor = outsideColor[0];
                }

            } else if (mNoRadiationValue >= STAGE_1_VALUE && mNoRadiationValue < STAGE_2_VALUE) {
                if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = outsideColor[1];
                    toColor = outsideColor[4];
                } else if (preDetectRadiationValue >= STAGE_2_VALUE) {
                    fromColor = outsideColor[2];
                    toColor = outsideColor[1];
                }
            } else {
                if (mNoRadiationValue == 0) {
                    fromColor = outsideColor[0];
                    toColor = outsideColor[3];
                } else {
                    fromColor = outsideColor[2];
                    toColor = outsideColor[5];
                }
            }
        }

        return ColorChangeFAnimator.argb(fromColor, toColor);
    }

    private ColorChangeFAnimator animator() {
        if (mNoRadiationValue - preDetectRadiationValue == 0) {
            if (mNoRadiationValue >= 0 && mNoRadiationValue < STAGE_1_VALUE
                    && preDetectRadiationValue >= 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                fromColor = scaleColor[0];
                toColor = scaleColor[0];
            } else if ((mNoRadiationValue >= STAGE_1_VALUE && mNoRadiationValue < STAGE_2_VALUE && preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE)) {
                fromColor = scaleColor[1];
                toColor = scaleColor[1];
            } else {
                fromColor = scaleColor[2];
                toColor = scaleColor[2];
            }
        } else if (mNoRadiationValue - preDetectRadiationValue > 0) {
            if (mNoRadiationValue >= 0 && mNoRadiationValue < STAGE_1_VALUE) {
                fromColor = scaleColor[0];
                toColor = scaleColor[0];
            } else if (mNoRadiationValue >= STAGE_1_VALUE && mNoRadiationValue < STAGE_2_VALUE) {
                if (preDetectRadiationValue > 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                    fromColor = scaleColor[0];
                    toColor = scaleColor[1];
                } else if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = scaleColor[1];
                    toColor = scaleColor[1];
                } else {
                    fromColor = scaleColor[0];
                    toColor = scaleColor[1];
                }
            } else {
                if (preDetectRadiationValue >= 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                    fromColor = scaleColor[0];
                    toColor = scaleColor[2];
                } else if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = scaleColor[1];
                    toColor = scaleColor[2];
                } else {
                    fromColor = scaleColor[2];
                    toColor = scaleColor[2];
                }
            }
        } else {

            if (mNoRadiationValue > 0 && mNoRadiationValue < STAGE_1_VALUE) {
                if (preDetectRadiationValue >= 0 && preDetectRadiationValue < STAGE_1_VALUE) {
                    fromColor = scaleColor[0];
                    toColor = scaleColor[0];
                } else if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = scaleColor[1];
                    toColor = scaleColor[0];
                } else {
                    fromColor = scaleColor[2];
                    toColor = scaleColor[0];
                }

            } else if (mNoRadiationValue >= STAGE_1_VALUE && mNoRadiationValue < STAGE_2_VALUE) {
                if (preDetectRadiationValue >= STAGE_1_VALUE && preDetectRadiationValue < STAGE_2_VALUE) {
                    fromColor = scaleColor[1];
                    toColor = scaleColor[1];
                } else if (preDetectRadiationValue >= STAGE_2_VALUE) {
                    fromColor = scaleColor[2];
                    toColor = scaleColor[1];
                }
            } else {
                if (mNoRadiationValue == 0) {
                    fromColor = scaleColor[0];
                    toColor = scaleColor[0];
                } else {
                    fromColor = scaleColor[2];
                    toColor = scaleColor[2];
                }
            }
        }
        return ColorChangeFAnimator.hsv(fromColor, toColor);
    }

    /**
     * 环扫圆环颜色控制
     */
    private void ringSweepColorControl() {
        dialPaint.setColor(bgvColor);
    }


    /**
     * 普通圆环颜色控制
     *
     * @param dialNumber 刻度数
     */
    private void normalRingColorControl(int dialNumber) {
        if (mNoRadiationValue == 0) {
            if (dialNumber >= 0 && dialNumber < totalScaleCount / 3) {
                dialPaint.setColor(preScaleColor[0]);
            } else if (dialNumber >= totalScaleCount / 3 && dialNumber < 2 * totalScaleCount / 3) {
                dialPaint.setColor(preScaleColor[1]);
            } else if (dialNumber >= 2 * totalScaleCount / 3) {
                dialPaint.setColor(preScaleColor[2]);
            }
        }
    }

    /**
     * 辐射值字体颜色控制
     */
    private void radiationTextColorControl() {
        tempPaint.setColor(bgvColor);
    }

    /**
     * 内部圆颜色控制
     */
    private void internalCircleColorControl() {
        if (mRadiationValue == 0) {
            cirPaint.setColor(intSideColor[0]);
        } else if (mRadiationValue > 0 && mRadiationValue < STAGE_1_VALUE) {
            cirPaint.setColor(intSideColor[0]);
        } else if (mRadiationValue >= STAGE_1_VALUE && mRadiationValue < STAGE_2_VALUE) {
            cirPaint.setColor(intSideColor[1]);
        } else
            cirPaint.setColor(intSideColor[2]);
    }

}
