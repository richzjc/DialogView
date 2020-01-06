package com.richzjc.dialog_view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogViewLayout extends FrameLayout implements IAnimView {

    private View dim;
    private View contentView;
    private boolean isAnimation;
    private OnVisibleChange onVisibleChange;

    public void setOnVisibleChange(OnVisibleChange onVisibleChange){
        this.onVisibleChange = onVisibleChange;
    }
    public DialogViewLayout(@NonNull Context context) {
        this(context, null);
    }

    public DialogViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initDimView() {
        if (dim == null) {
            dim = new View(getContext());
            dim.setClickable(false);
            dim.setBackgroundColor(Color.parseColor("#000000"));
            dim.setAlpha(0);
            dim.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    endAnim();
                }
            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(dim, 0, params);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        initDimView();
        if (getChildCount() >= 2) {
            throw new IllegalStateException("child count can not larger than two");
        } else {
            if (child != dim) {
                contentView = child;
            }
            super.addView(child, index, params);
        }
    }

    public void startAnim() {
        if (getVisibility() == View.GONE && !isAnimation) {
            AnimatorSet set = new AnimatorSet();
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(View.VISIBLE);
                    isAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimation = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isAnimation = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(dim, "alpha", 0f, 0.5f);
            set.setInterpolator(new LinearInterpolator());
            set.setDuration(300);
            ObjectAnimator alpha1Animation = ObjectAnimator.ofFloat(contentView, "alpha", 0, 1f);
            ObjectAnimator translateAnimation;
            if (((LayoutParams) contentView.getLayoutParams()).gravity == Gravity.BOTTOM) {
                translateAnimation = ObjectAnimator.ofFloat(contentView, "translationY", contentView.getHeight(), 0);
            } else {
                translateAnimation = ObjectAnimator.ofFloat(contentView, "translationY", -contentView.getHeight(), 0f);
            }
            set.playTogether(alphaAnimation, alpha1Animation, translateAnimation);
            set.start();
        }
    }

    public void endAnim() {
        if (getVisibility() == View.VISIBLE && !isAnimation) {
            AnimatorSet set = new AnimatorSet();
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.GONE);
                    isAnimation = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isAnimation = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(dim, "alpha", 0.5f, 0f);
            ObjectAnimator alpha1Animation = ObjectAnimator.ofFloat(contentView, "alpha", 1, 0);
            ObjectAnimator translateAnimation;
            if (((LayoutParams) contentView.getLayoutParams()).gravity == Gravity.BOTTOM) {
                translateAnimation = ObjectAnimator.ofFloat(contentView, "translationY", 0, contentView.getHeight());
            } else {
                translateAnimation= ObjectAnimator.ofFloat(contentView, "translationY", 0, -contentView.getHeight());
            }

            set.setInterpolator(new LinearInterpolator());
            set.setDuration(300);
            set.playTogether(alphaAnimation, alpha1Animation, translateAnimation);
            set.start();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(onVisibleChange != null)
            onVisibleChange.onVisibleChange(visibility);
    }

    public interface OnVisibleChange{
        void onVisibleChange(int visiblity);
    }
}
