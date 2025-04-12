package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {
        //1. 从redis中查询缓存
        String shop = stringRedisTemplate.opsForValue().get("cache:shop:"+id);
        //2. 判断是否存在
        if (StrUtil.isNotBlank(shop)){
            //3. 存在，返回
            Shop bean = JSONUtil.toBean(shop, Shop.class);
            return Result.ok(bean);
        }
        //4. 不存在，根据id查询数据库
        Shop shop1 = getById(id);
        if (shop1 == null) return Result.fail("商品不存在");
        //5. 存在，写入redis
        stringRedisTemplate.opsForValue().set("cache:shiop:"+id, shop1.toString());
        return Result.ok(shop1);
    }
}
