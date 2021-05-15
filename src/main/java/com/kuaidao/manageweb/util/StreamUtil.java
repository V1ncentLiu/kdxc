package com.kuaidao.manageweb.util;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 时候需要将基础转换补全
 */
public class StreamUtil {

    /**
     * 分组计数（返回整数类型）
     */
    public static <T,K>  Map<K,Integer> groupByWithCount(List<T> list,Function<T, K> keyMapper){
        return list.stream().collect( Collectors.groupingBy(keyMapper,Collectors.reducing(0, e -> 1,Integer::sum)));
    }

    /**
     *  分组计数（返回Long类型）
     */
    public static <T,K>  Map<K,Long> groupByWithLongCount(List<T> list,Function<T, K> keyMapper){
        return list.stream().collect( Collectors.groupingBy(keyMapper,Collectors.counting()));
    }

    /**
     *  分组
     */
    public static <T,K>  Map<K,List<T>> groupBy(List<T> list,Function<T, K> keyMapper){
        return list.stream().collect( Collectors.groupingBy(keyMapper));
    }


    /**
     *  转换map
     */
    public static <T,K,V>  Map<K,V> toMap(List<T> list,Function<T, K> keyMapper, Function<T, V> valueMapper){
        return list.stream().collect( Collectors.toMap(keyMapper, valueMapper, (m, n) -> m));
    }

    /**
     *  转化List
     */
    public static <T,K> List<K> toList(List<T> list,Function<T, K> valMapper){
        return list.stream().map(valMapper).collect(Collectors.toList());
    }

}
