package com.zy.webmail.product.service.impl;

import com.zy.webmail.product.service.CategoryBrandRelationService;
import com.zy.webmail.product.vo.Catagory2Vo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updatecategory(category.getCatId(),category.getName());
        }
    }

    /*获取菜单一级数据*/
    @Override
    public List<CategoryEntity> getLevelOne() {
        List<CategoryEntity> list=baseMapper.getLevelOne();
        return list;
    }

    /*获得所有分类数据*/
    @Override
    public Map<String, List<Catagory2Vo>> getCatagoryJSON() {
        //查出一级分类
        List<CategoryEntity> levelOne = this.getLevelOne();
        Map<String, List<Catagory2Vo>> AllLevel = levelOne.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            List<CategoryEntity> Level2 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId())); //拿到二级分类数据
            List<Catagory2Vo> k2List=null;
            if(Level2!=null){
                k2List = Level2.stream().map(k2 -> {
                    Catagory2Vo catagory2Vo = new Catagory2Vo();
                    List<CategoryEntity> Level3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", k2.getCatId())); //拿到三级分类数据
                    List<Catagory2Vo.Catagory3Vo> k3List=null;
                    if(Level3!=null){
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

        return AllLevel;
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