package com.winsion.component.user.config.constants;

/**
 * Created by 10295 on 2017/12/6 0006.
 * 正则表达式
 */

public interface UrlRegular {
    /**
     * 校验IP地址正则
     */
    String IP_REGULAR = "(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])";
    /**
     * 校验端口号正则
     */
    String PORT_REGULAR = "([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])";
}
