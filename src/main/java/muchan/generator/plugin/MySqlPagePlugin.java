package muchan.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;
/**
 * mysql数据库mybatis generator 分页插件
 * @author Demon Li
 * 2017-03-17
 */
public final class MySqlPagePlugin extends PluginAdapter{
	
	private String page;
	
	public boolean validate(List<String> warnings) {
		this.page = this.properties.getProperty("page");
		boolean valid = StringUtility.stringHasValue(this.page);
		if(!valid){
			this.page = "muchan.mybatis.Page";
		}
		return valid;
	}

	
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,IntrospectedTable introspectedTable){
		addPage(topLevelClass,introspectedTable,"page");
		return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
	}
	
	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		XmlElement page = new XmlElement("if");
		page.addAttribute(new Attribute("test" , "page != null"));
		page.addElement(new TextElement("limit #{page.begin} , #{page.length}"));
		element.addElement(page);
		
		return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
	}
	
	

	@Override
	public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		XmlElement page = new XmlElement("if");
		page.addAttribute(new Attribute("test" , "page != null"));
		page.addElement(new TextElement("limit #{page.begin} , #{page.length}"));
		element.addElement(page);
		return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element, introspectedTable);
	}

	private void addPage(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
		topLevelClass.addImportedType(new FullyQualifiedJavaType(this.page));
		CommentGenerator commentGenerator = this.context.getCommentGenerator();
		Field field = new Field();
		
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(new FullyQualifiedJavaType(this.page));
		field.setName(name);
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		char c = name.charAt(0);
		String camel = Character.toUpperCase(c) + name.substring(1);
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("set" + camel);
		method.addParameter(new Parameter(new FullyQualifiedJavaType(this.page), name));
		method.addBodyLine("this." + name + " = " + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType(this.page));
		method.setName("get" + camel);
		method.addBodyLine("return " + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		
	}

	
}















