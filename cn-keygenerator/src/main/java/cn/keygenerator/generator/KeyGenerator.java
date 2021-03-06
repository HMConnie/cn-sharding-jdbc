package cn.keygenerator.generator;

import cn.keygenerator.redis.RedisSequenceGenerator;
import cn.keygenerator.utils.DbAndTableEnum;
import cn.keygenerator.utils.ShardingConstant;
import cn.keygenerator.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rere
 * @version 1.0
 * @date 2019/3/25 9:17
 * @className KeyGenerator
 * @desc 自定义分布式主键生成器
 */
@Component
public class KeyGenerator {

    /**
     * 默认集群机器总数
     */
    private static final int DEFAULT_HOST_NUM = 64;


    @Autowired
    private RedisSequenceGenerator sequenceGenerator;



    /**
     * 根据路由id生成内部系统主键id，
     * 路由id可以是内部其他系统主键id，也可以是外部第三方用户id
     *
     * @param targetEnum     待生成主键的目标表规则配置
     * @param relatedRouteId 路由id或外部第三方用户id
     * @return
     */
    public String generateKey(DbAndTableEnum targetEnum, String relatedRouteId) {

        if (StringUtils.isBlank(relatedRouteId)) {
            throw new IllegalArgumentException("路由id参数为空");
        }

        StringBuilder key = new StringBuilder();
        /** 1.id业务前缀*/
        String idPrefix = targetEnum.getCharsPrefix();

        Map<String, String> indexmap = getDbIndexAndTbIndexMap(targetEnum, relatedRouteId);


        /** 2.id数据库索引位*/
        String dbIndex = indexmap.get("dbIndex");
        /** 3.id表索引位*/
        String tbIndex = indexmap.get("tbIndex");
        /** 4.id规则版本位*/
        String idVersion = targetEnum.getIdVersion();
        /** 5.id时间戳位*/
        String timeString = getFormatDate();
        /** 6.id分布式机器位 2位*/
        String distributedIndex = getDistributedId(2);
        /** 7.随机数位*/
        String sequenceId = sequenceGenerator.getNextVal(targetEnum, Integer.parseInt(dbIndex), Integer.parseInt(tbIndex));
        /** 库表索引靠前*/
        return key.append(idPrefix)
                .append(dbIndex)
                .append(tbIndex)
                .append(idVersion)
                .append(timeString)
                .append(distributedIndex)
                .append(sequenceId).toString();
    }

    private String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 根据已知路由id取出库表索引，外部id和内部id均 进行ASCII转换后再对库表数量取模
     *
     * @param targetEnum     待生成主键的目标表规则配置
     * @param relatedRouteId 路由id
     *                       取模求表 取商求库
     * @return
     */
    private Map<String, String> getDbIndexAndTbIndexMap(DbAndTableEnum targetEnum, String relatedRouteId) {
        Map<String, String> map = new HashMap<>();
        /** 获取库索引*/
        String preDbIndex = String.valueOf(StringUtil.getDbIndexByMod(relatedRouteId, targetEnum.getDbCount(), targetEnum.getTbCount()));
        String dbIndex = StringUtil.fillZero(preDbIndex, ShardingConstant.DB_SUFFIX_LENGTH);
        /** 获取表索引*/
        String preTbIndex = String
                .valueOf(StringUtil.getTbIndexByMod(relatedRouteId, targetEnum.getDbCount(), targetEnum.getTbCount()));
        String tbIndex = StringUtil
                .fillZero(preTbIndex, ShardingConstant.TABLE_SUFFIX_LENGTH);
        map.put("dbIndex", dbIndex);
        map.put("tbIndex", tbIndex);
        return map;
    }

    /**
     * 生成id分布式机器位
     *
     * @return 分布式机器id
     * length与hostCount位数相同
     */
    private String getDistributedId(int length, int hostCount) {
        return StringUtil
                .fillZero(String.valueOf(getIdFromHostName() % hostCount), length);
    }

    /**
     * 生成id分布式机器位
     *
     * @return 支持最多64个分布式机器并存
     */
    private String getDistributedId(int length) {
        return getDistributedId(length, DEFAULT_HOST_NUM);
    }

    /**
     * 适配分布式环境，根据主机名生成id
     * 分布式环境下，如：Kubernates云环境下，集群内docker容器名是唯一的
     * 通过 @See org.apache.commons.lang3.SystemUtils.getHostName()获取主机名
     *
     * @return
     */
    private Long getIdFromHostName() {
        //unicode code point

        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int[] ints = StringUtils.toCodePoints(hostname);
        int sums = 0;
        for (int i : ints) {
            sums += i;
        }
        return (long) (sums);
    }

}
