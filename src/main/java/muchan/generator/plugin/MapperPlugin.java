package muchan.generator.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 自定义mapper插件
 * @author Demon Li
 * 2017-03-21
 */
public class MapperPlugin extends PluginAdapter {
	
	private String dateStr;
	
	private String author;
	
	private String baseMapper;
	
	public MapperPlugin(){
		this.dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());		
	}
	
	public boolean validate(List<String> warnings) {
		this.author = this.properties.getProperty("author");
		this.baseMapper = this.properties.getProperty("baseMapper");
		boolean valid = StringUtility.stringHasValue(author);
		if(!valid){
			author = "DemonLi";
		}
		valid = StringUtility.stringHasValue(baseMapper);
		if(!valid){
			baseMapper = "muchan.common.dao.base.BaseGeneratedMapper";
		}
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		addBaseMapper(interfaze, topLevelClass, introspectedTable);
		return true;
	}
	
	/**
	 * 给生成的mapper添加通用mapper(BaseMapper)
	 * @param interfaze
	 * @param topLevelClass
	 * @param introspectedTable
	 */
	protected void addBaseMapper(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable){
		String baseMapper = "";
		int begin = this.baseMapper.lastIndexOf(".");
		if(begin > -1){
			baseMapper = this.baseMapper.substring(begin + 1,this.baseMapper.length());
		} else {
			baseMapper = this.baseMapper;
		}
		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(baseMapper + "<"
				+ introspectedTable.getBaseRecordType() + ","
				+ introspectedTable.getExampleType() +">");
		FullyQualifiedJavaType imp = new FullyQualifiedJavaType(this.baseMapper);
		/**
		 * 添加 extends BaseMapper
		 */
		interfaze.addSuperInterface(fqjt);

		/**
		 * 添加import;
		 */
		interfaze.addImportedType(imp);
		/**
		 * 方法不需要
		 */
		interfaze.getMethods().clear();
		interfaze.getAnnotations().clear();
		addJavaDoc(interfaze,introspectedTable.getBaseRecordType());
	}
	
	/**
	 * 给生成的mapper加上注释
	 * @param interfaze
	 * @param name
	 */
	protected void addJavaDoc(Interface interfaze,String name){
		interfaze.addJavaDocLine("/**");
		StringBuilder sb = new StringBuilder();
		sb.append(" * 自动生成的 ");
		name = name.substring(name.lastIndexOf(".") + 1);
		sb.append(name);
		sb.append(" 数据存取接口");
		sb.append("\n * \n * ");
		sb.append("该类于 ");
		sb.append(this.dateStr);
		sb.append(" 生成,请勿手工修改!");
		sb.append("\n * \n * ");
		sb.append("@author " + this.author);
		interfaze.addJavaDocLine(sb.toString());
		interfaze.addJavaDocLine(" */");
	}
	
}














