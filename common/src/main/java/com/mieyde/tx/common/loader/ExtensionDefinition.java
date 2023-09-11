package com.mieyde.tx.common.loader;

import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 13:22
 */
@Getter
@AllArgsConstructor
public class ExtensionDefinition<T> {

    private final String name;
    private final Class<T> serviceClass;
    private final Integer order;
    private final Scope scope;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (StringUtils.isBlank(name) ? 0 : name.hashCode());
        result = prime * result + (ObjectUtils.isNull(serviceClass) ? 0 : serviceClass.hashCode());
        result = prime * result + (ObjectUtils.isNull(order) ? 0 : order.hashCode());
        result = prime * result + (ObjectUtils.isNull(scope) ? 0 : scope.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (ObjectUtils.equals(this,obj)){
            return true;
        }
        if (ObjectUtils.isNull(obj) || !ObjectUtils.equals(this.getClass(),obj.getClass()) || !(obj instanceof ExtensionDefinition)){
            return false;
        }
        ExtensionDefinition<?> other = (ExtensionDefinition<?>) obj;
        if (!StringUtils.equals(this.name,other.getName())
                || !ObjectUtils.equals(this.serviceClass,other.getServiceClass())
                || !ObjectUtils.equals(this.order,other.getOrder())
                || !ObjectUtils.equals(this.scope,other.getScope())
        ){
            return false;
        }
        return true;
    }
}
