package com.wzbos.android.widget.filter;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

class Utils {

    static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    static int[] getLoc(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }
}
