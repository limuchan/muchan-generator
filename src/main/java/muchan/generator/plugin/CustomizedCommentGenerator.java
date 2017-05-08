package muchan.generator.plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * mybatis generator 自定义注释生成器
 * @author Demon Li
 * 2017-03-18
 */
public class CustomizedCommentGenerator implements CommentGenerator{
	
	private Properties properties;
	@SuppressWarnings("unused")
	private Properties systemPro;
	private boolean suppressDate;
	private boolean suppressAllComments;
	private String currentDateStr;
	
	public CustomizedCommentGenerator(){
		properties = new Properties();
		systemPro = System.getProperties();
		suppressDate = false;
		suppressAllComments = false;
		currentDateStr = (new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
	}
	
	
	public void addConfigurationProperties(Properties properties) {
		this.properties.putAll(properties);
		suppressDate = StringUtility.isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
		suppressAllComments = StringUtility.isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
            return;
        }
		
		String remarks = introspectedColumn.getRemarks();
		if(remarks == null || "".equals(remarks.trim())){
			field.addJavaDocLine("");
			return;
		}
        StringBuilder sb = new StringBuilder();
        
        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(remarks);
        field.addJavaDocLine(sb.toString());

//      addJavadocTag(field, false);

        field.addJavaDocLine(" */");
		
	}
	
	/**
	 * 给实体类的属性加上数据库表的字段注释
	 */
	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
            return;
        }
		String fieldName = field.getName();
		List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		List<String> fieldNames = new ArrayList<String>();
		for(IntrospectedColumn column : columns){
			fieldNames.add(column.getJavaProperty());
		}
		if(!fieldNames.contains(fieldName)){
			field.addJavaDocLine("");
			return;
		}
        StringBuilder sb = new StringBuilder();

        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
		
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
		//类注释
		return;
	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
		return;

	}

	public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
		if(suppressAllComments){
			return;
		}
		StringBuilder sb = new StringBuilder();
		innerEnum.addJavaDocLine("/**");
//      addJavadocTag(innerEnum, false);
		sb.append(" * ");
		sb.append(introspectedTable.getFullyQualifiedTable());
		innerEnum.addJavaDocLine(sb.toString());
		innerEnum.addJavaDocLine("*/");
	}

	public void addGetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if(suppressAllComments){
			return;
		}
		// TODO get方法加注释
		
	}

	public void addSetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if(suppressAllComments){
			return;
		}
		// TODO set方法加注释
		
	}

	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
		if(suppressAllComments){
			return;
		}
		// TODO 方法上加注释
	}

	public void addJavaFileComment(CompilationUnit compilationUnit) {
		// TODO 添加文档注释
		return;
		
	}

	public void addComment(XmlElement xmlElement) {
		// TODO 给xml映射文件添加注释
		return;
	}

	public void addRootComment(XmlElement rootElement) {
		// TODO 
		return;
	}
	
	protected String getDateString(){
		String result = null;
		if(!suppressDate){
			result = currentDateStr;
		}
		return result;
	}
	
	protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete){
		javaElement.addJavaDocLine(" *");
		StringBuilder sb = new StringBuilder();
		sb.append(" * ");
		sb.append(MergeConstants.NEW_ELEMENT_TAG);
		if(markAsDoNotDelete){
			sb.append("请不要删除");
		}
		String str = getDateString();
		if(str != null){
			sb.append(" ");
			sb.append(str);
		}
		javaElement.addJavaDocLine(sb.toString());
	}


	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// TODO Auto-generated method stub
		return;
	}
	
}



















