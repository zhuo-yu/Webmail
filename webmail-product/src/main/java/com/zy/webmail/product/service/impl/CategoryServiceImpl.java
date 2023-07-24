package com.zy.webmail.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zy.webmail.product.service.CategoryBrandRelationService;
import com.zy.webmail.product.vo.Catagory2Vo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.dao.CategoryDao;
import com.zy.webmail.product.entity.CategoryEntity;
import com.zy.webmail.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    private Map<String,Object> cache = new HashMap<>();


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

       /*2、组装成父子的数形结构
       * 2.1、 查出所有的一级分类
       * */
        List<CategoryEntity> LeveloneMenus = categoryEntities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map((menu)->{
            menu.setChildren(getChildren(menu,categoryEntities));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort()) ;
        }).collect(Collectors.toList());
        System.out.println("查询到的数据:"+LeveloneMenus);
        return LeveloneMenus;
    }

    @Override
    public void removeMenusByIds(List<Long> asList) {
        //TODO 1、检查当前删除的菜单
        baseMapper.deleteBatchIds(asList);
    }
    /*寻找catelogId全路径*/
    @Override
    public Long[] getcatelogpath(Long attrGroupId) {
        List<Long> paths=new ArrayList<>();
        List<Long> findparentpath = this.findparentpath(attrGroupId, paths);
        Collections.reverse(findparentpath);
        return findparentpath.toArray(new Long[findparentpath.size()]);
    }

    /*同步修改其他关联表*/
    @Transactional
    @Override
    @CacheEvict(value = "category",allEntries = true)
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updatecategory(category.getCatId(),category.getName());
        }
    }

    /*获取菜单一级数据*/
    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public List<CategoryEntity> getLevelOne() {
        long l = System.currentTimeMillis();
        List<CategoryEntity> list=baseMapper.getLevelOne();
        System.out.println("消耗时间:"+(System.currentTimeMillis()-l));
        return list;
    }

    //使用spring-cache
    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catagory2Vo>> getCatagoryJSON(){
        System.out.println("查询数据库...");
        //优化代码
        List<CategoryEntity> allData = baseMapper.selectList(null);


        //查出一级分类
//        List<CategoryEntity> levelOne = this.getLevelOne();
        List<CategoryEntity> levelOne = getParentCid(allData, 0L);
        Map<String, List<Catagory2Vo>> dataMap = levelOne.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

//            List<CategoryEntity> Level2 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId())); //拿到二级分类数据
            List<CategoryEntity> Level2 = getParentCid(allData, v.getCatId()); //拿到二级分类数据
            List<Catagory2Vo> k2List = null;
            if (Level2 != null) {
                k2List = Level2.stream().map(k2 -> {
                    Catagory2Vo catagory2Vo = new Catagory2Vo();
//                    List<CategoryEntity> Level3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", k2.getCatId())); //拿到三级分类数据
                    List<CategoryEntity> Level3 = getParentCid(allData, k2.getCatId());
                    List<Catagory2Vo.Catagory3Vo> k3List = null;
                    if (Level3 != null) {
                        //对Catagory3Vo进行赋值
                        k3List = Level3.stream().map(k3 -> {
                            Catagory2Vo.Catagory3Vo catagory3Vo = new Catagory2Vo.Catagory3Vo();
                            catagory3Vo.setCatalog2Id(k3.getParentCid().toString());
                            catagory3Vo.setId(k3.getCatId().toString());
                            catagory3Vo.setName(k3.getName());
                            return catagory3Vo;
                        }).collect(Collectors.toList());
                    }

                    //对catagory2Vo进行赋值
                    {
                        catagory2Vo.setCatalog1Id(k2.getParentCid().toString());
                        catagory2Vo.setCatalog3List(k3List);
                        catagory2Vo.setId(k2.getCatId().toString());
                        catagory2Vo.setName(k2.getName());
                    }
                    return catagory2Vo;
                }).collect(Collectors.toList());
            }
            return k2List;
        }));
        return dataMap;
    }

    /**
     * 缓存穿透: 一个一直在请求不存在值的请求,导致越过redis,频繁的去访问数据库;解决： 取到为null照样作为key存入redis
     * 缓存雪崩: 多个缓存值在同一个时间内失效导致在此期间大量请求直接访问数据库; 解决:给每个存入缓存的值都设一个随机时间过期值
     * 缓存击穿: 某个热点频繁访问的数据,当数据失效后,在失效期间突然大量访问直接访问到数据库; 解决:加锁,在分布式环境下最好加分布式锁
     * @return
     */
//    @Override
    public Map<String, List<Catagory2Vo>> getCatagoryJSON2(){
        Map<String, List<Catagory2Vo>> dataMap = new HashMap<>();
        String catagoryList = redisTemplate.opsForValue().get("catagoryList");
        if (StringUtils.isEmpty(catagoryList)){
            System.out.println("缓存没命中,查询数据库中...");
            dataMap = getCatagoryJSONFromDbByRedissonLock();
            //解决缓存雪崩
//            redisTemplate.opsForValue().set("catagoryList", JSON.toJSONString(dataMap), (long) (5*Math.random()*100), TimeUnit.MINUTES);
            return dataMap;
        }
        System.out.println("缓存命中,直接返回");
        dataMap = JSON.parseObject(catagoryList,new TypeReference<Map<String, List<Catagory2Vo>>>(){});
        return dataMap;
    }

    //使用Redisson分布式锁
    /*获得所有分类数据*/
    public Map<String, List<Catagory2Vo>> getCatagoryJSONFromDbByRedissonLock() {
        /**
         * 使用Redisson解决原子性问题
         */
        //分布式锁根据业务考虑粒度性,通常越细分越好
        RLock lock = redisson.getLock("catagoryJSON-lock");
        lock.lock();
        Map<String, List<Catagory2Vo>> dataList;
        try {
            dataList = getDataList();
        }finally {
            lock.unlock();
        }
        return dataList;
    }

    //分布式锁
    /*获得所有分类数据*/
    public Map<String, List<Catagory2Vo>> getCatagoryJSONFromDbByRedisLock() {
        /**
         * 分布式锁的原理:使用redis的setnx原子性指令,去区分是否占到锁,若设置成功则占到锁,反之则需要通过自旋等待锁的释放再次抢占锁
         * 同时需要注意的是,在加锁和解锁两个步骤都需要保证是一个原子性的操作
         * 加锁：设置锁,然后设置过期时间
         * 可以使用redis的set[nx,ex]同时设置锁和时间
         * 解锁: 判断唯一锁以及删除锁
         * 使用Lua脚本保证原子性
         */
        String uuid = UUID.randomUUID().toString();
        //加锁 设置锁和过期时间,以免发生删锁逻辑出错导致锁一直存在而发生死锁的情况
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (lock){
            System.out.println("获取锁成功...");
            Map<String, List<Catagory2Vo>> dataList;
            try {
                dataList = getDataList();
            }finally {
                String lua = "if redis.call('get',KEYS[1]) == ARGV[1] then  return redis.call('del',KEYS[1])  else return 0 end";
                Long result = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class), Arrays.asList("lock"), uuid);
            }

            /*
            if (uuid.equals(redisTemplate.opsForValue().get("lock"))){
                redisTemplate.delete("lock");
            }
            * 以上代码有个很大的问题,就是无法保证是一个原子性的操作,所以需要改善为Lua脚本执行操作
            */
            return dataList;
        }else {
            System.out.println("获取锁失败,正在等待...");
            try {
                Thread.sleep(200);
            }catch (Exception e){
                e.printStackTrace();
            }
            return getCatagoryJSONFromDbByRedisLock();
        }
    }

    private Map<String, List<Catagory2Vo>> getDataList() {
        String catagoryList = redisTemplate.opsForValue().get("catagoryList");
        if (StringUtils.isNotEmpty(catagoryList)) {
            Map<String, List<Catagory2Vo>> result = JSON.parseObject(catagoryList, new TypeReference<Map<String, List<Catagory2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询数据库...");
        //优化代码
        List<CategoryEntity> allData = baseMapper.selectList(null);


        //查出一级分类
//        List<CategoryEntity> levelOne = this.getLevelOne();
        List<CategoryEntity> levelOne = getParentCid(allData, 0L);
        Map<String, List<Catagory2Vo>> dataMap = levelOne.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

//            List<CategoryEntity> Level2 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId())); //拿到二级分类数据
            List<CategoryEntity> Level2 = getParentCid(allData, v.getCatId()); //拿到二级分类数据
            List<Catagory2Vo> k2List = null;
            if (Level2 != null) {
                k2List = Level2.stream().map(k2 -> {
                    Catagory2Vo catagory2Vo = new Catagory2Vo();
//                    List<CategoryEntity> Level3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", k2.getCatId())); //拿到三级分类数据
                    List<CategoryEntity> Level3 = getParentCid(allData, k2.getCatId());
                    List<Catagory2Vo.Catagory3Vo> k3List = null;
                    if (Level3 != null) {
                        //对Catagory3Vo进行赋值
                        k3List = Level3.stream().map(k3 -> {
                            Catagory2Vo.Catagory3Vo catagory3Vo = new Catagory2Vo.Catagory3Vo();
                            catagory3Vo.setCatalog2Id(k3.getParentCid().toString());
                            catagory3Vo.setId(k3.getCatId().toString());
                            catagory3Vo.setName(k3.getName());
                            return catagory3Vo;
                        }).collect(Collectors.toList());
                    }

                    //对catagory2Vo进行赋值
                    {
                        catagory2Vo.setCatalog1Id(k2.getParentCid().toString());
                        catagory2Vo.setCatalog3List(k3List);
                        catagory2Vo.setId(k2.getCatId().toString());
                        catagory2Vo.setName(k2.getName());
                    }
                    return catagory2Vo;
                }).collect(Collectors.toList());
            }
            return k2List;
        }));
        //解决缓存雪崩
        redisTemplate.opsForValue().set("catagoryList", JSON.toJSONString(dataMap), (long) (5 * Math.random() * 100), TimeUnit.MINUTES);
        return dataMap;
    }

    //本地锁
    /*获得所有分类数据*/
    public Map<String, List<Catagory2Vo>> getCatagoryJSONFromDbByLocalLock() {
        //本地锁
        synchronized (this){
            return getDataList();
        }
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> allData,Long catId) {
        List<CategoryEntity> collect = allData.stream().filter(res -> res.getParentCid().equals(catId)).collect(Collectors.toList());
        return collect;
    }


    /*使用递归将全路径找出来*/
    private List<Long> findparentpath(Long attrGroupId,List<Long> paths){
        CategoryEntity byId = this.getById(attrGroupId);
        paths.add(attrGroupId);  //将当前id存入
        if(byId.getParentCid()!=0){   //如果当前id有父id
            findparentpath(byId.getParentCid(),paths);
        }
        return paths;
    }


    /*递归查找所有菜单的子菜单*/
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map((categoryEntity)->{
            //找到子菜单
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            //菜单的排序
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort()) ;
        }).collect(Collectors.toList());
        return children;
    }

}