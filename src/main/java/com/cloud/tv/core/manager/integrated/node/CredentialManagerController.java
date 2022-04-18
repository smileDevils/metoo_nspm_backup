package com.cloud.tv.core.manager.integrated.node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.tv.core.manager.admin.tools.ShiroUserHolder;
import com.cloud.tv.core.service.ICredentialService;
import com.cloud.tv.core.service.IGroupService;
import com.cloud.tv.core.service.ISysConfigService;
import com.cloud.tv.core.service.IUserService;
import com.cloud.tv.core.utils.NodeUtil;
import com.cloud.tv.core.utils.ResponseUtil;
import com.cloud.tv.dto.CredentialDto;
import com.cloud.tv.entity.Credential;
import com.cloud.tv.entity.Group;
import com.cloud.tv.entity.SysConfig;
import com.cloud.tv.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.nutz.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Api("凭据")
@RequestMapping({"/nspm/credential"})
@RestController
public class CredentialManagerController {
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private NodeUtil nodeUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private IGroupService groupService;

    public CredentialManagerController() {
    }

    @ApiOperation("列表")
    @RequestMapping({"/push/credential/getall"})
    public Object getall(@RequestBody(required = false) CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            if (dto.getBranchLevel() == null || dto.getBranchLevel().equals("")) {
                User currentUser = ShiroUserHolder.currentUser();
                User user = this.userService.findByUserName(currentUser.getUsername());
                dto.setBranchLevel(user.getGroupLevel());
            }

            url = url + "/push/credential/getall";
            Object result = this.nodeUtil.postBody(dto, url, token);
            JSONObject object = JSONObject.parseObject(result.toString());
            if (object.get("content") != null) {
                JSONObject content = JSONObject.parseObject(object.get("content").toString());
                if (content.get("list") != null) {
                    List list = new ArrayList();
                    JSONArray arrays = JSONArray.parseArray(content.get("list").toString());

                    JSONObject json;
                    for(Iterator var10 = arrays.iterator(); var10.hasNext(); list.add(json)) {
                        Object array = var10.next();
                        json = JSONObject.parseObject(array.toString());
                        if (json.get("branchLevel") != null) {
                            Group group = this.groupService.getObjByLevel(json.get("branchLevel").toString());
                            if (group != null) {
                                json.put("branchName", group.getBranchName());
                            } else {
                                json.put("branchName", "");
                            }
                        }
                    }

                    object.put("list", list);
                }
            }

            return ResponseUtil.ok(object);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("新增")
    @RequestMapping({"/push/credential/create"})
    public Object create(@RequestBody(required = false) CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/create";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("编辑")
    @RequestMapping({"/push/credential/get"})
    public Object get(@RequestBody(required = false) CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/get";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("编辑保存")
    @RequestMapping({"/push/credential/modify"})
    public Object modify(@RequestBody(required = false) CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/modify";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("删除")
    @RequestMapping({"/push/credential/delete"})
    public Object delete(@RequestBody(required = false) CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/delete";
            Object result = this.nodeUtil.postFormDataBody(dto, url, token);
            return ResponseUtil.ok(result);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("分配组")
    @RequestMapping({"/push/credential/batch-credential-update"})
    public Object batch_credential_update(@RequestBody(required = false) CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/batch-credential-update";
            Object result = this.nodeUtil.postBody(dto, url, token);
            return ResponseUtil.ok(result);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("导出")
    @GetMapping({"/push/credential/export-credential"})
    public Object export_credential(HttpServletResponse response, CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/export-credential";
            if (dto.getIsReload() != null || dto.getIsTrusted() != null) {
                if (dto.getIsReload() != null && !dto.getIsReload()) {
                    Map map = new HashMap();
                    map.put("isReload", dto.getIsReload());
                    return this.nodeUtil.download(map, url, token);
                }

                Object result = this.nodeUtil.getBody(dto, url, token);
                return ResponseUtil.ok(result);
            }
        }

        return ResponseUtil.error();
    }

    @ApiOperation("下载模板")
    @RequestMapping({"/push/credential/download-credential-template"})
    public Object download_credential_template() {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/download-credential-template";
            Object result = this.nodeUtil.postBody((Object)null, url, token);
            return ResponseUtil.ok(result);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("导出")
    @GetMapping({"/push/downloadFile/template"})
    public Object template(HttpServletResponse response, CredentialDto dto) {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/downloadFile/凭据批量生成模板.xls";
            return this.nodeUtil.download((Map)null, url, token);
        } else {
            return ResponseUtil.error();
        }
    }

    @ApiOperation("导入文件")
    @RequestMapping({"/push/credential/import-credential"})
    public Object upload(@RequestParam(value = "multipartFile",required = false) MultipartFile file, String encrypt) throws IOException {
        SysConfig sysConfig = this.sysConfigService.findSysConfigList();
        String url = sysConfig.getNspmUrl();
        String token = sysConfig.getNspmToken();
        if (url != null && token != null) {
            url = url + "/push/credential/import-credential";
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                public String getFilename() {
                    return file.getOriginalFilename();
                }

                public long contentLength() {
                    return file.getSize();
                }
            };
            MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap();
            multipartRequest.add("file", fileAsResource);
            multipartRequest.add("fileName", file.getName());
            multipartRequest.add("fileSize", file.getSize());
            multipartRequest.add("encrypt", encrypt);
            Object result = this.nodeUtil.post_form_data(multipartRequest, url, token);
            return ResponseUtil.ok(result);
        } else {
            return ResponseUtil.error();
        }
    }
}

