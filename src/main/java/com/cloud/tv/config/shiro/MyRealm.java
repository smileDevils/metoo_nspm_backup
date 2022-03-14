package com.cloud.tv.config.shiro;

import com.cloud.tv.core.shiro.salt.MyByteSource;
import com.cloud.tv.core.shiro.tools.ApplicationContextUtils;
import com.cloud.tv.entity.Res;
import com.cloud.tv.entity.Role;
import com.cloud.tv.entity.User;
import com.cloud.tv.core.service.IRegisterService;
import com.cloud.tv.core.service.IResService;
import com.cloud.tv.core.service.IRoleService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 *     Title: MyRealm.java
 * </p>
 *
 * <p>
 *     Description: 自定义Realm
 *     同时开启身份验证和权限验证，需要继承AuthorizingRealm
 * </p>
 *
 *         for(Role role : roles){
 *                     System.out.println("角色：" + role.getType());
 *                     simpleAuthorizationInfo.addRole(role.getType());
 *                     simpleAuthorizationInfo.addStringPermission("BUYER:*:*");
 *                 }
 * <p>
 *     authen: hkk
 * </p>MultiRealmAuthenticator
 */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IResService resService;

    /**
     * 限定这个 Realm 只处理 UsernamePasswordToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;}


    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        IRegisterService registerService = (IRegisterService) ApplicationContextUtils.getBean("registerService");

        User user = registerService.findByUsername(username);
        if(!ObjectUtils.isEmpty(user)){
            if(username.equals(user.getUsername())){
              /* Collection sessions = sess
                    if(username.equals(loginUsername)){  //这里的username也就是当前登录的username
                        session.setTimeout(0);  //这里就把session清除，ionDAO.getActiveSessions();
                for(Session scession: sessions){
                    String loginUsername = String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY));//获得session中已经登录用户的名字

                    }
                }*/
                /**
                 * 将获取到的用户信息封装成AuthticationInfo对象返回，此处封装成SimpleAuthticationInfo对象
                 * 参数一：认证的实体信息，可以时从数据库中查询得到的实体类或用户名
                 * 参数二：查询获得的登陆密码
                 * 参数三：盐值
                 * 参数四：当前Realm对象的名称，直接调用父类的getName()方法即可
                 */
                return new SimpleAuthenticationInfo(user, user.getPassword(),  new MyByteSource(user.getSalt()), this.getName());
            }
        }
        return null;
    }
    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取身份信息
        User user = (User) principalCollection.getPrimaryPrincipal();
        // 根据身份信息获取角色信息，资源信息

       //User user = registerService.findByUsername(primaryPrincipal);
        // User user = registerService.findRolesByUserName(primaryPrincipal.getUsername());
        List<Role> roles = this.roleService.findRoleByUserId(user.getId());//user.getRoles();
        if(!CollectionUtils.isEmpty(roles)){
            if(user != null){
                String username = user.getUsername();
                SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

               /* roles.forEach(role->{
                     simpleAuthorizationInfo.addRole(role.getRoleCode());
                   // simpleAuthorizationInfo.addRole(role.getType());
                    //System.out.println(role.getName());
                    //simpleAuthorizationInfo.addStringPermission("BUYER:*:*");
                   // simpleAuthorizationInfo.addStringPermission(role.getType() + ":*:*");
                   // simpleAuthorizationInfo.addStringPermission(role.getType() + ":*");
                    List<Res> reses = resService.findResByRoleId(role.getId());
                    if(!CollectionUtils.isEmpty(reses)){
                        reses.forEach(res -> {
                            simpleAuthorizationInfo.addStringPermission(res.getValue());
                        });
                    }
                });*/

                for(Role role : roles){
                    simpleAuthorizationInfo.addRole(role.getRoleCode());
                    // simpleAuthorizationInfo.addRole(role.getType());
                    //System.out.println(role.getName());
                    //simpleAuthorizationInfo.addStringPermission("BUYER:*:*");
                    // simpleAuthorizationInfo.addStringPermission(role.getType() + ":*:*");
                    // simpleAuthorizationInfo.addStringPermission(role.getType() + ":*");
                    List<Res> permissions = resService.findResByRoleId(role.getId());
                    if(!CollectionUtils.isEmpty(permissions)){
                        permissions.forEach(permission -> {
                            System.out.println("permission: " + permission.getValue());
                            simpleAuthorizationInfo.addStringPermission(permission.getValue());
                        });
                    }
                }
           /* if(primaryPrincipal.equals(username)){
                SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                // simpleAuthorizationInfo.setRoles("buyer");
                if(user.getUserRole().equals("BUYER")){
                    simpleAuthorizationInfo.addRole("BUYER");
                    simpleAuthorizationInfo.addStringPermission("BUYER:*:*");
                }else if(user.getUserRole().equals("SELLER")){
                    simpleAuthorizationInfo.addRole("SELLER");
                    simpleAuthorizationInfo.addStringPermission("SELLER:*:*");
                }else if(user.getUserRole().equals("ADMIN")){
                    simpleAuthorizationInfo.addRole("ADMIN");
                    simpleAuthorizationInfo.addStringPermission("ADMIN:*:*");
                }*/
                return simpleAuthorizationInfo;
            }
        }
        return null;
    }

}
