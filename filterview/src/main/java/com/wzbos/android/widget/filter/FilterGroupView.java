package com.wzbos.android.widget.filter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class FilterGroupView extends LinearLayout implements FilterView.OnFilterViewVisibleChangeListener {
    private OnFilterViewListener onFilterViewListener;

    public void setOnFilterViewListener(OnFilterViewListener listener) {
        this.onFilterViewListener = listener;
    }

    public FilterGroupView(Context context) {
        super(context);
    }

    public FilterGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof FilterView) {
            FilterView filterView = (FilterView) child;
            filterView.setOnFilterViewVisibleChangeListener(this);
        }
    }

    public FilterView getFilterView(int index) {
        if (index < getChildCount()) {
            View view = getChildAt(index);
            if (view instanceof FilterView) {
                return (FilterView) view;
            }
        }

        return null;
    }


    public void setExpandedViewTop(int top) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof FilterView) {
                ((FilterView) view).setExpandedViewTop(top);
            }
        }
    }

    public void close() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof FilterView) {
                ((FilterView) view).close();
            }
        }
    }

    @Override
    public void onShowFilterView(FilterView filterView) {
        if (onFilterViewListener != null)
            onFilterViewListener.onExpanded(this, filterView);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (filterView != view) {
                if (view instanceof FilterView) {
                    FilterView fv = (FilterView) view;
                    fv.close();
                }
            }
        }
    }

    @Override
    public void onCloseFilterView(FilterView filterView) {
        if (onFilterViewListener != null)
            onFilterViewListener.onClose(this, filterView);
    }

    public interface OnFilterViewListener {
        void onExpanded(FilterGroupView groupView, FilterView filterView);

        void onClose(FilterGroupView groupView, FilterView filterView);
    }
}
