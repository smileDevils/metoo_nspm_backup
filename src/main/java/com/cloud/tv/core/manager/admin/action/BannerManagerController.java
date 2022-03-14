package com.cloud.tv.core.manager.admin.action;

import com.cloud.tv.core.service.IBannerService;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.core.utils.query.PageInfo;
import com.cloud.tv.dto.BannerDto;
import com.cloud.tv.entity.Banner;
import com.cloud.tv.entity.RoomProgram;
import com.cloud.tv.entity.SysConfig;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.Map;


@Api("广告管理")
@RequestMapping("/admin/banner")
@RestController
public class BannerManagerController {

    @Autowired
    private ISysConfigService configService;
    @Autowired
    private IBannerService bannerService;

    @RequiresPermissions("LK:SYSCONFIG:MANAGER")
    @ApiOperation("轮播图列表")
    @RequestMapping("/carousel/list")
    public Object carousel(@RequestBody(required = false) BannerDto req){
        Page<Banner> page = this.bannerService.query(req);
        if(page.getResult().size() > 0){
            return ResponseUtil.ok(new PageInfo<RoomProgram>(page));
        }
        return ResponseUtil.ok();
    }

//    @RequiresPermissions("ADMIN:CAROUSEL:UPDATE")
    @ApiOperation("轮播图更新")
    @RequestMapping("/update")
    public Object update(@RequestBody BannerDto req){
        if(req != null){
            Banner banner = this.bannerService.findObjById(req.getId());
            if(banner != null){
                Map map = new HashMap();
                map.put("obj", banner);
                SysConfig configs = this.configService.findSysConfigList();
                map.put("domain", configs.getDomain());
                return ResponseUtil.ok(map);
            }
        }
        return ResponseUtil.badArgument("未找到指定资源");
    }

//    @RequiresPermissions("ADMIN:CAROUSEL:SAVE")
    @ApiOperation("轮播图保存")
    @RequestMapping("/save")
    public Object save(@RequestBody BannerDto dto){
        if(dto != null){
           /* if(dto.getTitle() != null && !dto.getTitle().equals("")){
                this.bannerService.save(dto);
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.badArgument("请输入标题");
            }*/
            this.bannerService.save(dto);
            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument();
    }

//    @RequiresPermissions("ADMIN:CAROUSEL:DELETE")
    @ApiOperation("轮播图删除")
    @RequestMapping("/delete")
    public Object delete(@RequestBody BannerDto dto){
        if(dto != null ){
            Banner banner = this.bannerService.findObjById(dto.getId());
            if(banner != null ){
                if(this.bannerService.delete(banner.getId())){
                    return ResponseUtil.ok();
                }
                return ResponseUtil.error();
            }
        }
        return ResponseUtil.badArgument();
    }
}
