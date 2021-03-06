package com.vfuchong.sptsm.gateway.routedefine;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.vfuchong.sptsm.gateway.common.GatewayConstans;
import com.vfuchong.sptsm.gateway.routedefine.predicaterule.PredicateGroup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 * 动态路由规则配置
 *
 * @author Francis.zz
 * @date 2020-03-26 17:46
 * ************************************
 */
@NacosConfigurationProperties(prefix = "route", dataId = GatewayConstans.DATA_ID_ROUTE, groupId = GatewayConstans.GROUP_GATEWAY, type = ConfigType.YAML, autoRefreshed = true)
@Configuration
@Data
@Slf4j
public class RouteRuleProp {
    /**
     * NacosConfigurationProperties 属性绑定的实现查看 {@link com.alibaba.nacos.spring.context.properties.config.NacosConfigurationPropertiesBinder#bind(Object, String, NacosConfigurationProperties)}
     */
    private List<RouteRule> rules;
    
    private PredicateGroup commonPredicate;
    
    public Flux<Route> getRoute(RouteLocatorBuilder.Builder builder) {
        try {
            this.rules.forEach(routeRule -> routeRule.getRoute(builder, this.commonPredicate));
        } catch (Exception e) {
            log.error("路由规则配置失败", e);
            return Flux.empty();
        }
        return builder.build().getRoutes()
                .doOnNext(route -> log.info("注册路由id:" + route.getId()));
    }
    
    public boolean validate(RouteLocatorBuilder.Builder builder) {
        if(rules == null) {
            log.warn("not found RouteRule config");
            return false;
        }
        try {
            this.rules.forEach(routeRule -> routeRule.getRoute(builder, this.commonPredicate));
        } catch (Exception e) {
            log.error("路由规则配置失败", e);
            return false;
        }
        return true;
    }
}
