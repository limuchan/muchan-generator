package muchan.generator.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

public class ModelPlugin extends PluginAdapter{
	
	private String dateStr;
	
	private String author;
	
	public ModelPlugin(){
		this.dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public boolean validate(List<String> warnings) {
		this.author = this.properties.getProperty("author");
		boolean valid = StringUtility.stringHasValue(this.author);
		if(!valid){
			this.author = "Demon Li";
		}
		return true;
	}

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		String name  = topLevelClass.getType().getFullyQualifiedName();
		addJavaDoc(topLevelClass, introspectedTable, name);
		return true;
	}
	
	protected void addJavaDoc(TopLevelClass topLevelClass, IntrospectedTable introspectedTable,String name){
		topLevelClass.addJavaDocLine("/**");
		StringBuilder sb = new StringBuilder();
		sb.append(" * 自动生成的 ");
		name = name.substring(name.lastIndexOf(".") + 1);
		sb.append(name);
		sb.append(" 实体类");
		sb.append("\n * \n * ");
		sb.append("该类于 ");
		sb.append(this.dateStr);
		sb.append(" 生成,请勿手工修改!");
		sb.append("\n * \n * ");
		sb.append("@author " + this.author);
		topLevelClass.addJavaDocLine(sb.toString());
		topLevelClass.addJavaDocLine(" */");
	}
	
}













