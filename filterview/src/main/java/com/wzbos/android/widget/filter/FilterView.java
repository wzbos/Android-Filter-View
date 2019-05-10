package com.wzbos.android.widget.filter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mgzf.android.widget.filter.R;


public class FilterView extends LinearLayout {
    int expandedIcon = R.mipmap.wzbfilterview_ic_expanded;
    int collapseIcon = R.mipmap.wzbfilterview_ic_collapse;
    int selectedIconTintColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
    int unSelectedIconTintColor = ContextCompat.getColor(getContext(), R.color.textColor);
    int selectedTextColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
    int unSelectedTextColor = ContextCompat.getColor(getContext(), R.color.textColor);
    int expandedBackgroundColor = ContextCompat.getColor(getContext(), R.color.wzbfilterview_expanded_background);
    TextView txtName;
    ImageView ivIcon;
    FrameLayout expandedLayout;
    View expandedView;
    float expandedViewHeight;
    int expandedDuration = 300;
    float expandedViewTop;
    ViewGroup root;
    OnFilterViewVisibleChangeListener listener;
    boolean isSelected;
    boolean isExpanded;

    public FilterView(Context context) {
        this(context, null);
    }

    public FilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);

        txtName = new AppCompatCheckedTextView(context);
        txtName.setSingleLine();
        txtName.setClickable(false);
        txtName.setEllipsize(TextUtils.TruncateAt.END);

        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParam.leftMargin = dpTpPx(3);
        ivIcon = new ImageView(context);
        ivIcon.setLayoutParams(layoutParam);

        addView(txtName);
        addView(ivIcon);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FilterView);
            if (array.hasValue(R.styleable.FilterView_text)) {
                txtName.setText(array.getString(R.styleable.FilterView_text));
            }

            if (array.hasValue(R.styleable.FilterView_textSize)) {
                txtName.setTextSize(array.getDimension(R.styleable.FilterView_textSize, 14));
            }

            setSelected(array.getBoolean(R.styleable.FilterView_selected, false));

            selectedTextColor = array.getColor(R.styleable.FilterView_selectedTextColor, selectedTextColor);
            unSelectedTextColor = array.getColor(R.styleable.FilterView_unSelectedTextColor, unSelectedTextColor);
            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_checked};
            states[1] = new int[]{-android.R.attr.state_checked};
            int[] colors = new int[]{selectedTextColor, unSelectedTextColor};
            txtName.setTextColor(new ColorStateList(states, colors));

            expandedViewHeight = array.getDimension(R.styleable.FilterView_expandedViewHeight, expandedViewHeight);
            expandedViewTop = array.getDimension(R.styleable.FilterView_expandedDuration, expandedViewTop);
            expandedDuration = array.getInteger(R.styleable.FilterView_expandedDuration, expandedDuration);

            expandedIcon = array.getResourceId(R.styleable.FilterView_expandedIcon, expandedIcon);
            collapseIcon = array.getResourceId(R.styleable.FilterView_collapseIcon, collapseIcon);

            selectedIconTintColor = array.getColor(R.styleable.FilterView_selectedIconTintColor, selectedIconTintColor);
            unSelectedIconTintColor = array.getColor(R.styleable.FilterView_unSelectedIconTintColor, unSelectedIconTintColor);

            if (expandedIcon > 0 || collapseIcon > 0)
                setExpanded(false);

            array.recycle();
        }

        setGravity(Gravity.CENTER);
        setPadding(dpTpPx(2), 0, dpTpPx(2), dpTpPx(0));
        initViews();

        setOnClickListener(v -> {
            if (isExpanded) {
                close();
            } else {
                show();
            }
        });
    }


    private void initViews() {
        Activity activity = (Activity) getContext();
        root = activity.findViewById(android.R.id.content);
    }

    public void setSelected(boolean val) {
        isSelected = val;
        if (val) {
            txtName.setTextColor(selectedTextColor);
        } else {
            txtName.setTextColor(unSelectedTextColor);
        }
        if (ivIcon.getDrawable() != null)
            DrawableCompat.setTint(ivIcon.getDrawable(), val ? selectedIconTintColor : unSelectedIconTintColor);
    }

    public void setExpandedView(View dropDownView) {
        if (expandedLayout == null) {
            expandedLayout = new FrameLayout(getContext());
            expandedLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            expandedLayout.setBackgroundColor(expandedBackgroundColor);
            expandedLayout.setVisibility(GONE);
            expandedLayout.setOnClickListener(v -> close());
        } else {
            expandedLayout.removeAllViews();
        }
        this.expandedView = dropDownView;
        this.expandedView.setVisibility(VISIBLE);

        setContainerViewLayout();
        expandedLayout.addView(this.expandedView);
    }

    public void setExpandedViewTop(float top) {
        this.expandedViewTop = top;
    }

    private void setExpanded(boolean val) {
        isExpanded = val;
        if (val) {
            Drawable drawable = getResources().getDrawable(expandedIcon);
            DrawableCompat.setTint(drawable, selectedIconTintColor);
            ivIcon.setImageDrawable(drawable);
            txtName.setTextColor(selectedTextColor);
        } else {
            Drawable drawable = getResources().getDrawable(collapseIcon);
            DrawableCompat.setTint(drawable, isSelected ? selectedIconTintColor : unSelectedIconTintColor);
            ivIcon.setImageDrawable(drawable);
            txtName.setTextColor(isSelected ? selectedTextColor : unSelectedTextColor);
        }
    }

    public void setExpandedViewHeight(float height) {
        expandedViewHeight = height;
    }

    public void setExpandedViewHeightRatio(float ratio) {
        if (ratio > 0 && ratio < 1)
            expandedViewHeight = (int) (Utils.getScreenSize(getContext()).y * ratio);
    }


    public void setAnimationDuration(int animationDuration) {
        this.expandedDuration = animationDuration;
    }

    public void setExpandedIcon(int expandedIcon) {
        this.expandedIcon = expandedIcon;
    }

    public void setExpandedBackgroundColor(int expandedBackgroundColor) {
        this.expandedBackgroundColor = expandedBackgroundColor;
    }

    public void setCollapseIcon(int collapseIcon) {
        this.collapseIcon = collapseIcon;
    }

    public void setSelectedIconTintColor(int selectedIconTintColor) {
        this.selectedIconTintColor = selectedIconTintColor;
    }

    public void setUnSelectedIconTintColor(int unSelectedIconTintColor) {
        this.unSelectedIconTintColor = unSelectedIconTintColor;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
    }

    public void setUnSelectedTextColor(int unSelectedTextColor) {
        this.unSelectedTextColor = unSelectedTextColor;
    }


    private int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }

    private void setContainerViewLayout() {
        Activity activity = (Activity) getContext();
        ViewGroup root = activity.findViewById(android.R.id.content);
        View view = (View) getParent();
        int[] rootPoint = Utils.getLoc(root);
        int[] loc = Utils.getLoc(view);
        int height = view.getMeasuredHeight();
        int y = (int)(loc[1] - rootPoint[1] + height + expandedViewTop);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) expandedLayout.getLayoutParams();
        layoutParams.setMargins(0, y, 0, 0);
        expandedLayout.setLayoutParams(layoutParams);
    }

    private void show() {
        setExpanded(true);
        if (expandedLayout != null) {
            root.addView(expandedLayout);
            setContainerViewLayout();
            expandedLayout.setVisibility(VISIBLE);
        }
        if (expandedView != null) {
            expandedView.setVisibility(View.VISIBLE);
            animateToggle(expandedView, true);
        }
        if (listener != null) {
            listener.onShowFilterView(this);
        }


    }

    public void close() {
        setExpanded(false);
        if (expandedView != null) {
            expandedView.setVisibility(GONE);
            animateToggle(expandedView, false);
        }

        if (expandedLayout != null) {
            expandedLayout.setVisibility(GONE);
            root.removeView(expandedLayout);
        }
        if (listener != null) {
            listener.onCloseFilterView(this);
        }
    }


    public void animateToggle(final View targetView, boolean isShow) {
        targetView.measure(0, 0);
        float height = targetView.getMeasuredHeight();

        if (height > expandedViewHeight && expandedViewHeight > 0)
            height = expandedViewHeight;

        ValueAnimator valueAnimator = isShow ? ValueAnimator.ofFloat(0f, height) : ValueAnimator.ofFloat(height, 0f);
        valueAnimator.setDuration(expandedDuration);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float val = (float) valueAnimator1.getAnimatedValue();
            setViewHeight(targetView, val);
        });
        valueAnimator.start();
    }

    public static void setViewHeight(View view, float height) {
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) height;
        view.requestLayout();
    }


    public void setOnFilterViewVisibleChangeListener(OnFilterViewVisibleChangeListener listener) {
        this.listener = listener;
    }

    public interface OnFilterViewVisibleChangeListener {
        void onShowFilterView(FilterView filterView);

        void onCloseFilterView(FilterView filterView);
    }
}
