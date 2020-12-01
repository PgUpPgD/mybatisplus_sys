package com.feri.mybatisplus_sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feri.mybatisplus_sys.entity.Work;
import com.feri.mybatisplus_sys.service.WorkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 *@Author feri
 *@Date Created in 2019/3/21 14:41
 */
@RestController
@Api(value = "记录工作",tags = "工作相关方法")
public class WorkController {
    @Autowired
    private WorkService workService;

    //新增
    @ApiOperation(value = "新增工作")
    @PostMapping("work/add.do")
    public String save(Work work){
        Work works =  new Work("小华", "新华小学", 500, new Date());
        return workService.save(works)==true?"新增成功":"新增失败";
    }
    @PostMapping("work/add1.do")
    public String save1(){
        Collection<Work> list = new ArrayList<>();
        list.add(new Work("小李", "新华小学", 500, new Date()));
        list.add(new Work("小名", "新华小学", 500, new Date()));
        workService.saveBatch(list);
        return "ok";
    }

    @PostMapping("work/saveOrUpdate.do")
    public String saveOrUpdate(){
        Collection<Work> list = new ArrayList<>();
        list.add(new Work("小李", "新华小学", 500, new Date()));
        list.add(new Work(3,"小明", "新华小学", 500, new Date()));
        workService.saveOrUpdateBatch(list);
        return "ok";
    }
    @PostMapping("work/saveOrUpdate1.do")
    public String saveOrUpdate1(){
        Work work = new Work("小李", "新华小学", 500, new Date());
        Work work1 = new Work(3, "小明", "新华小学", 500, new Date());
        workService.saveOrUpdate(work);
        return "ok";
    }


    //修改
    @ApiOperation(value = "修改工作")
    @PutMapping("work/updatemoney.do")
    public String update2(int id,int money){
        return workService.updateMoney(id,money)>0?"修改成功":"修改失败";
    }
    //修改
    @ApiOperation(value = "修改工作")
    @PutMapping("work/update.do")
    public String update(Work work){
        return workService.updateById(work)?"修改成功":"修改失败";
    }

    @RequestMapping("work/update1.do")
    public String update1(){
        Work work = new Work();
        work.setMoney(1000);
        return workService.update(work,Wrappers.<Work>query().lambda().eq(Work::getName,"toms"))
                ?"修改成功":"修改失败";
    }

    //删除
    @ApiOperation(value = "删除工作")
    @DeleteMapping("work/delete.do")
    public String del(int id){
        return workService.removeById(id)?"删除成功":"删除失败";
    }
    @RequestMapping("work/delete1.do")
    public String del1(){
        Map<String, Object> map = new HashMap<>();
        map.put("name","小李");
        return workService.removeByMap(map)?"删除成功":"删除失败";
    }
    @RequestMapping("work/delete2.do")
    public String del2(){
        return workService.remove(Wrappers.<Work>query().lambda().eq(Work::getName,"小明")
        )?"删除成功":"删除失败";
    }

    //查询单个
    @ApiOperation(value = "查询详情")
    @GetMapping("work/one.do")
    public Work one(int id){
        return workService.getById(id);
    }
    //查询全部
    @ApiOperation(value = "查询全部")
    @GetMapping("work/all.do")
    public List<Work> queryAll(){
        return workService.list();
    }

    @RequestMapping("work/get.do")
    public List<Work> get(){
        Map map = new HashMap<String, Object>();
        map.put("name","小李");
        return (List<Work>) workService.listByMap(map);
    }

    //分页查询
    @ApiOperation(value = "分页查询")
    @GetMapping("work/page.do")
    public List<Work> page(int page,int limit){ //.getRecords()
        Page<Work> page1=new Page<>(page,limit);
        IPage<Work> page2 = workService.page(page1);
        return null;
    }

    @RequestMapping("work/count.do")
    public int count(){
        return workService.count();
    }

    @RequestMapping("work/update3.do")
    public Boolean update3(){
        Work work = new Work("小李", "新华小学", 500, new Date());
        //boolean b = workService.update().eq("name", "十大").remove();
        boolean b = workService.lambdaUpdate().eq(Work::getName, "达到").update(work);
        return b;
    }

    @RequestMapping("work/like.do")
    public String like(){
        List<Work> list = workService.list(Wrappers.<Work>query().like("name", "是"));
        System.out.println(list.get(0).getCreatetime());
        return "ok";
    }

    @RequestMapping("work/notInSql.do")
    public String notInSql(){
        List<Work> list = workService.list(Wrappers.<Work>query().notInSql("id", "1,2"));
        System.out.println(list);
        return "ok";
    }

    @RequestMapping("work/orderByDesc.do")
    public String orderByDesc(){
        List<Work> list = workService.list(Wrappers.<Work>query().orderByDesc("id"));
        System.out.println(list);
        return "ok";
    }

    @RequestMapping("work/and.do")
    public String and(){
        List<Work> list = workService.list(Wrappers.<Work>query().and(i -> i.eq("money",500)
                .ne("name","小是")));
        System.out.println(list);
        return "ok";
    }

    @RequestMapping("work/select.do")
    public String select(){
        List<Work> list = workService.list(Wrappers.<Work>query().select("id","name","money"));
        //Work work = new Work("小李", "新华小学", 500, new Date());
        //Class<Work> works = (Class<Work>) work.getClass();
        //List<Work> list = workService.list(Wrappers.<Work>query().select(works,i -> i.getProperty().startsWith("小李")));
        System.out.println(list);
        return "ok";
    }



}
