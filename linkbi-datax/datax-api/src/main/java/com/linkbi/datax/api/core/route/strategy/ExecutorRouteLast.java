package com.linkbi.datax.api.core.route.strategy;

import com.linkbi.datatx.core.biz.model.ReturnT;
import com.linkbi.datatx.core.biz.model.TriggerParam;
import com.linkbi.datax.api.core.route.ExecutorRouter;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLast extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return new ReturnT<String>(addressList.get(addressList.size()-1));
    }

}
