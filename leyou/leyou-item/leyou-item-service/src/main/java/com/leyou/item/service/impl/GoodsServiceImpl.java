package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lennon
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;


    @Override
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        // 搜索条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("title","%"+key+"%");
        // 上架条件
        criteria.andEqualTo("saleable",saleable);
        // 设置分页
        PageHelper.startPage(page,rows);

        // 执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        // Spu集合转换为SpuBo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            // cope属性
            BeanUtils.copyProperties(spu, spuBo);
            // 查询bname
            Brand brand = brandMapper.selectByPrimaryKey(spuBo.getBrandId());
            spuBo.setBname(brand.getName());
            // 查询cname
            List<String> names =  categoryService.queryCategoryByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            spuBo.setCname(StringUtils.join(names,"-"));
            // 该集合转换方式必须有返回值
            return spuBo;
        }).collect(Collectors.toList());
        // SpuBo转化为PageResult输出
        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }

    @Override
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        // 新增spu表   （注意增加表顺序）
        // 接收的spuBo对象里没有spuBO表里对应的字段则需要设置(设置默认字段)主键默认为插入null
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);
        // 新增 spuDetail表
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);
        addSkuAndStock(spuBo);
        sendMsg("insert",spuBo.getId());
    }

    private void addSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku表
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);
            // 新增stock表
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
        sendMsg("insert", spuBo.getId());
    }

    @Override
    public SpuDetail querySpuDetailSpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    @Override
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        skus.forEach(t -> {
            Stock stock = stockMapper.selectByPrimaryKey(t.getId());
            t.setStock(stock.getStock());
        });
        return skus;
    }

    @Override
    public void updateGoods(SpuBo spuBo) {
        /**
         * 由于sku表的更新可能包括删除，更改，新增三种情况为了少做判断可先删除再新增来实现更新
         * spu可根据传来的spuId 直接更新 只包含更该一种情况
         */
        // 根据SpuId查看需要更新的sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skus = skuMapper.select(sku);
        skus.forEach(t ->{
        // 删除以前的库存
            stockMapper.deleteByPrimaryKey(t.getId());
        // 删除以前的sku
            Sku sku1 = new Sku();
            sku1.setSpuId(spuBo.getId());
            skuMapper.delete(sku1);

        } );
        // 新增sku和库存
        addSkuAndStock(spuBo);
        // 更新spu和详情
        spuMapper.updateByPrimaryKeySelective(spuBo);
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        sendMsg("update",spuBo.getId());
    }

    @Override
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    @Override
    public Sku querySkuBySkuId(Long skuId) {
        return this.skuMapper.selectByPrimaryKey(skuId);
    }

    // 消息生产方
    private void sendMsg(String type, Long id){
        try {
            amqpTemplate.convertAndSend("item"+type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}
