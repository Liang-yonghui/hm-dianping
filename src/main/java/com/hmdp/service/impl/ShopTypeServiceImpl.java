package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public List<ShopType> queryTypeList() {
        //1. 从redis中查询
        String cacheList = stringRedisTemplate.opsForValue().get("cache:shiop:list");
        //2. 若存在，直接返回
        if (StrUtil.isNotBlank(cacheList)){
            List<ShopType> typeList= (List<ShopType>) JSONUtil.toBean(cacheList, List.class)
                    .stream().map(o -> JSONUtil.toBean((String) o, ShopType.class)).collect(Collectors.toList());
            return typeList;
        }
        //3. 若不存在，则直接去数据库查询
        List<ShopType> list = list();
        //4. 将查询到的数据写入redis
        stringRedisTemplate.opsForValue().set("cache:shiop:list", JSONUtil.toJsonStr(list));
        return list;
    }
}
