package com.zy.webmail.product.controller;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.StringUtils;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.zy.webmail.product.entity.CategoryEntity;
import com.zy.webmail.product.service.CategoryService;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.R;



/**
 * 商品三级分类
 *
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 12:58:00
 */
@RestController
@RequestMapping("/product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 查出所有分类以及子分类、以树形结构组装起来
     */
    @RequestMapping(value = "/list/tree")
    public R list(@RequestParam Map<String, Object> params){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree(); //查出所有的分类
        return R.ok().put("data", categoryEntities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateDetail(category);  //需要同步修改其他关联表

        return R.ok();
    }

    /**
     * 删除,使用逻辑删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        //检查当前删除的菜单是否被别的地方引用
//		categoryService.removeByIds(Arrays.asList(catIds));\
        System.out.println(catIds);
        categoryService.removeMenusByIds(Arrays.asList(catIds));
        return R.ok();
    }

    @RequestMapping("/hello")
    public String sayHello(){
        //名字一样获取的就是统一把锁
        RLock lock01 = redissonClient.getLock("lock01");
        lock01.lock();//阻塞式等待,默认30s
        //锁自动续期,看门狗模式,只要业务没执行完,就会给锁续时间,知道业务执行完或者业务出现问题
        try {
            System.out.println("获取锁成功..."+Thread.currentThread().getId());
            Thread.sleep(15000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("解锁成功..."+Thread.currentThread().getId());
            lock01.unlock();
        }
        return "hello";
    }

    //redisson 写锁
    @GetMapping("/writeLock")
    public String writeLock(){
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.writeLock();
        String s = null;
        try {
            rLock.lock();
            System.out.println("抢占到写锁:"+Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("uuid",s);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("释放写锁:"+Thread.currentThread().getId());
        }
        return s;
    }

    //redisson 读锁
    @GetMapping("/readLock")
    public String readLock(){
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock();
        String result = null;
        try {
            rLock.lock();
            System.out.println("抢占到读锁:"+Thread.currentThread().getId());
            result = redisTemplate.opsForValue().get("uuid");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("释放读锁:"+Thread.currentThread().getId());
        }
        return result;
    }

    /**
     * redisson 信号量 停车场景 车位占满则无法占位,必须等释放才行
     */
    //停车占位
    @GetMapping("/park")
    public String park(){
        //初始化车位
        initCarNumber();
        RSemaphore semaphore = redissonClient.getSemaphore("car");
        //占位 车位-1
        boolean b = semaphore.tryAcquire();
        String carNum = redisTemplate.opsForValue().get("car");
        System.out.println("还剩多少停车位?"+carNum);
        return "停车占位成功";
    }

    //离开释放
    @GetMapping("/leave")
    public String leave(){
        RSemaphore semaphore = redissonClient.getSemaphore("car");
        //离开 车位+1
        semaphore.release();
        return "释放车位成功";
    }

    private void initCarNumber(){
        String car = redisTemplate.opsForValue().get("car");
        if (StringUtils.isEmpty(car)) {
            redisTemplate.opsForValue().set("car", String.valueOf(3));
            System.out.println("车位初始化成功");
        }
    }


}
