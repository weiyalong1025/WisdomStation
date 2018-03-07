package com.winsion.component.basic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winsion.component.basic.R;

/**
 * Created by 10295 on 2018/3/5.
 * 现场中的模块
 */

public class ModuleView extends RelativeLayout {

    private ImageView ivModuleRedDot;

    public ModuleView(Context context) {
        this(context, null);
    }

    public ModuleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModuleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.basic_view_module, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ModuleView);
        Drawable moduleIcon = typedArray.getDrawable(R.styleable.ModuleView_moduleIcon);
        String moduleName = typedArray.getString(R.styleable.ModuleView_moduleName);
        boolean showRedDot = typedArray.getBoolean(R.styleable.ModuleView_showRedDot, false);
        typedArray.recycle();

        ImageView ivModuleIcon = findViewById(R.id.iv_module_icon);
        TextView tvModuleName = findViewById(R.id.tv_module_name);
        ivModuleRedDot = findViewById(R.id.iv_module_red_dot);
        ivModuleIcon.setImageDrawable(moduleIcon);
        tvModuleName.setText(moduleName);
        ivModuleRedDot.setVisibility(showRedDot ? VISIBLE : GONE);
    }

    public void showRedDot() {
        ivModuleRedDot.setVisibility(VISIBLE);
    }

    public void hideRedDot() {
        ivModuleRedDot.setVisibility(GONE);
    }
}
