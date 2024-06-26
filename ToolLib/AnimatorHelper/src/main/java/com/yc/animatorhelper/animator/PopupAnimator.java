package com.yc.animatorhelper.animator;

import android.graphics.Color;
import android.view.View;


/**
 * Description: 弹窗动画执行器
 */
public abstract class PopupAnimator {
    public static int animationDuration = 300;
    public static int shadowBgColor = Color.parseColor("#9F000000");
    public View targetView;
    // 内置的动画
    public AnimationType popupAnimation;

    public PopupAnimator(){

    }

    public PopupAnimator(View target){
        this(target, null);
    }

    public PopupAnimator(View target, AnimationType popupAnimation){
        this.targetView = target;
        this.popupAnimation = popupAnimation;
    }

    public abstract void initAnimator();
    public abstract void animateShow();
    public abstract void animateDismiss();
    public int getDuration(){
        return animationDuration;
    }
}
