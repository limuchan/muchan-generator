package ${appObjectPackage};

import java.io.Serializable;
import ${entityPackage}.${entityName};

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
/**
 * 应用对象 - ${entityName}.
 * <p>
 * 该类于 ${dateTime} 首次生成，后由开发手工维护。
 * </p>
 * @author DemonLi
 * @version 1.0.0, ${date}
 */
public final class ${entityName}AO extends ${entityName} implements Serializable {

    /**
     * 默认的序列化 id.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
