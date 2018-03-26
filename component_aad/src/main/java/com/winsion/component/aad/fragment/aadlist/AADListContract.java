package com.winsion.component.aad.fragment.aadlist;

import com.winsion.component.aad.constants.AADType;
import com.winsion.component.aad.entity.AADEntity;
import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 到发列表页Contract
 */

class AADListContract {
    interface View extends BaseView {
        void getAADListDataSuccess(List<AADEntity> aadEntities);

        void getAADListDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getAADListData(@AADType int aadType);
    }
}
