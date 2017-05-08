package muchan.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 给生成的类加上序列化接口以及序列号
 * @author Demon Li
 * 2017-03-18
 */
public class SerializablePlugin extends PluginAdapter{
	
	private FullyQualifiedJavaType serializable;
	
	public SerializablePlugin(){
		this.serializable = new FullyQualifiedJavaType("java.io.Serializable");
	}
	
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}
	
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	@Override
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	@Override
	public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	protected void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		topLevelClass.addImportedType(this.serializable);
		topLevelClass.addSuperInterface(this.serializable);
		
		Field field = new Field();
		field.setFinal(true);
		field.setInitializationString("1L");
		field.setName("serialVersionUID");
		field.setStatic(true);
		field.setType(new FullyQualifiedJavaType("long"));
		field.setVisibility(JavaVisibility.PRIVATE);
		this.context.getCommentGenerator().addFieldComment(field, introspectedTable);
		
		topLevelClass.addField(field);
	}
	
}












