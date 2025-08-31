package com.jy.medusa.gaze.utils;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;

public class MedusaPkGeneratedUtils {

    /**
     * @return 返回值类型
     */
    public static Long genSnowflake(){
        return Long.valueOf(IdUtil.getSnowflake(getWorkId(), 1L).nextIdStr());
    }

    /**
     * @return 返回值类型
     */
    public static String genUUID(){
        return UUID.fastUUID().toString().replace("-", "");
    }

    /**
     * workId使用IP生成
     * @return 返回值类型
     */
    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums = sums + b;
            }
            return (long) (sums % 32);
        }
        catch (Exception e) {
            // 失败就随机
            return RandomUtils.nextLong(0, 31);
        }
    }
}
