package muchan.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * toString方法插件
 * 
 * @author DemonLi
 *
 */
public class ToStringPlugin extends PluginAdapter {
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		generateToString(introspectedTable, topLevelClass);
		return true;
	}

	@Override
	public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		generateToString(introspectedTable, topLevelClass);
		return true;
	}

	@Override
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		generateToString(introspectedTable, topLevelClass);
		return true;
	}

	private void generateToString(IntrospectedTable introspectedTable, TopLevelClass topLevelClass) {
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getStringInstance());
		method.setName("toString");
		if (introspectedTable.isJava5Targeted()) {
			method.addAnnotation("@Override");
		}
		topLevelClass.addImportedType(new FullyQualifiedJavaType("org.apache.commons.lang3.builder.ToStringBuilder"));
		topLevelClass.addImportedType(new FullyQualifiedJavaType("org.apache.commons.lang3.builder.ToStringStyle"));
		
		context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
		
		method.addBodyLine("return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);");
		topLevelClass.addMethod(method);
	}

}
