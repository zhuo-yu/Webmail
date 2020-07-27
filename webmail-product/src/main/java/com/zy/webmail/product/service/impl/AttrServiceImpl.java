package com.zy.webmail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zy.common.constant.productconstant;
import com.zy.webmail.product.dao.*;
import com.zy.webmail.product.entity.*;
import com.zy.webmail.product.service.AttrAttrgroupRelationService;
import com.zy.webmail.product.service.ProductAttrValueService;
import com.zy.webmail.product.vo.AttrGroupRelationVo;
import com.zy.webmail.product.vo.AttrRespVo;
import com.zy.webmail.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.common.utils.PageUtils;
import com.zy.common.utils.Query;

import com.zy.webmail.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    CategoryDao categoryDao;

    @Resource
    AttrGroupDao attrGroupDao;

    @Resource
    AttrDao attrDao;

    @Resource
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    ProductAttrValueService productAttrValueService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);  //Spring属性赋值工具包，能够将一个对象的属性赋值给另一个对象 TODO 属性赋值包
        this.save(attrEntity); //将Attr数据写入attr数据表中
        if(attr.getAttrType()== productconstant.AttrEnum.ATTR_TYPE_SALE.getCode() && attr.getAttrGroupId()!=null){  //如果是基本属性且关联的分组不为空，才添加关联数据
            //保存关联关系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId()); //赋值相应的数据
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity); //赋值关联关系数据
        }
    }

    @Override
    public PageUtils querybaseAttrPage(Map<String, Object> params, Long catelogId, String attrtype) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type",attrtype.equalsIgnoreCase("base")?productconstant.AttrEnum.ATTR_TYPE_SALE.getCode():productconstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(catelogId!=0){
            wrapper.eq("catelog_id",catelogId);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        /*由于网页展示的数据多了groupName和catalogName，所以需要添加个Vo类添加这两个属性*/

        List<AttrRespVo> respVos = records.stream().map((attr) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attr, attrRespVo); //将attrentity赋值给attrrespvo
            if("base".equals(attrtype)){  //如果显示的是基本属性，否则销售属性不用设置分组的名字
                /*此处设置分组的名字*/
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
                if(attrAttrgroupRelationEntity!=null && attrAttrgroupRelationEntity.getAttrGroupId()!=null){
                    Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                    String name = attrGroupEntity.getAttrGroupName();
                    attrRespVo.setGroupName(name);
                }
            }
            /*此处设置分类的名字*/
            CategoryEntity categoryEntity = categoryDao.selectById(attr.getCatelogId());
            if(categoryEntity!=null){
                attrRespVo.setCatelogName(categoryEntity.getName());  //添加分类名字
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    /*获取属性*/
    @Override
    public AttrRespVo getDetail(Long attrId) {
        /*获取全路径代码*/
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity byId = this.getById(attrId);
        BeanUtils.copyProperties(byId,attrRespVo); //将原属性赋值给vo对象
        List<Long> list=new ArrayList<>();
        CategoryEntity categoryEntity = categoryDao.selectById(attrRespVo.getCatelogId());//获得对应的category对象
        List<Long> allcategoryid=this.getparentid(categoryEntity.getCatId(),list);  //返回全路径
        Collections.reverse(allcategoryid);
        Long[] catelogPath= allcategoryid.toArray(new Long[allcategoryid.size()]);
        attrRespVo.setCatelogPath(catelogPath); //设置全路径属性
        if(byId.getAttrType()==productconstant.AttrEnum.ATTR_TYPE_SALE.getCode()){   //如果是基本属性
            /*获取分组id代码*/
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(attrAttrgroupRelationEntity!=null){
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if(attrGroupEntity!=null){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());  //设置分组名
                }
            }
        }
        CategoryEntity category = categoryDao.selectById(attrRespVo.getCatelogId());
        if(category!=null){
            attrRespVo.setCatelogName(category.getName());
        }
        return attrRespVo;
    }

    @Override
    public void updateAttr(AttrRespVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity); //将基本信息更改

        if(attrEntity.getAttrType()==productconstant.AttrEnum.ATTR_TYPE_SALE.getCode()){  //如果是基本属性，才需要修改分组关联
            //修改分组关联
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            Integer Count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if(Count>0){
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId())); //更新该表
            }else{
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }

    }
    /*
    * 根据分组id找到关联表的所有基本属性
    * */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrEntity> list=new ArrayList<>();
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> collect = attrAttrgroupRelationEntityList.stream().map((attrAttrgroupRelationEntity) -> {
            return attrAttrgroupRelationEntity.getAttrId();
        }).collect(Collectors.toList());
        if(collect==null || collect.size()==0){
            return null;
        }
        list= this.listByIds(collect);
        return (List<AttrEntity>) list;
    }

    /*移除属性与分组的关联关系*/
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> collect = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item,attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());   //返回AttrAttrgroupRelationEntity集合为了删除AttrAttrgroupRelationEntity的数据
        attrAttrgroupRelationDao.deleteBatchRelation(collect); //自定义删除或批量删除方法
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1、当前分组只能关联自己所属分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2、当前分组只能关联别的分组没有引用的属性
        //2.1、当前分类下的其他分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //2.2、这些分组关联的属性
        List<Long> collect = attrGroupEntities.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.3、从当前分类的所有属性中移除这些属性
        List<AttrAttrgroupRelationEntity> groupId = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> collected = groupId.stream().map((item) -> {
            return item.getAttrId();  //返回已经关联的属性id
        }).collect(Collectors.toList());
        QueryWrapper<AttrEntity>  wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",productconstant.AttrEnum.ATTR_TYPE_SALE.getCode()); //只需要查出基本属性
        if(collected!=null && collected.size()>0){    //如果已关联的属性存在，则拼装返回还没有关联的属性
            wrapper.notIn("attr_id", collected);
        }
        if(!StringUtils.isEmpty((String) params.get("key"))){   //有查询索引
            wrapper.and((obj)->{
                obj.eq("attr_id",obj).or().like("attr_name",obj);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    /*获取spu规格*/
    @Override
    public List<ProductAttrValueEntity> listforspu(Long spuId) {
        List<ProductAttrValueEntity> spu_id = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return spu_id;
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }

    /*通过递归返回全路径*/
    private List<Long> getparentid(Long id,List<Long> list) {
        CategoryEntity categoryEntity = categoryDao.selectById(id);
        list.add(id);
        if(categoryEntity.getParentCid()!=0){   //如果有父id，说明需要继续递归
            this.getparentid(categoryEntity.getParentCid(),list);
        }
        return list;
    }

}