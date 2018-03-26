package com.winsion.component.aad.adapter;

import android.content.Context;

import com.winsion.component.aad.R;
import com.winsion.component.aad.entity.AADEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 到发列表Adapter
 */

public class AADListAdapter extends CommonAdapter<AADEntity> {

    public AADListAdapter(Context context, List<AADEntity> data) {
        super(context, R.layout.aad_item_aad_list, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, AADEntity bean, int position) {

    }
}
