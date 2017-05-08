package muchan.generator.plugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;
/**
 * mybatis generator 生成代码名称替换插件
 * @author Demon Li
 * 2017-03-18
 */
public class RenameExampleClassPlugin extends PluginAdapter{
	
	private String searchString;
	private String replaceString;
	private Pattern pattern;
	
	public boolean validate(List<String> warnings) {
		this.searchString = this.properties.getProperty("searchString");
		this.replaceString = this.properties.getProperty("replaceString");
		boolean valid = (StringUtility.stringHasValue(this.searchString)) && 
						(StringUtility.stringHasValue(this.replaceString));
		if(valid){
			this.pattern = Pattern.compile(this.searchString);
		} else {
			if(!StringUtility.stringHasValue(this.searchString)){
				warnings.add(Messages.getString("ValidationError.18","RenameExampleClassPlugin","serarchString"));
			}
			if(!StringUtility.stringHasValue(this.replaceString)){
				warnings.add(Messages.getString("ValidationError.18","RenameExampleClassPlugin","replaceString"));
			}
		}
		return valid;
	}

	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		String type = introspectedTable.getExampleType();
		Matcher matcher = this.pattern.matcher(type);
		type = matcher.replaceAll(this.replaceString);
		
		introspectedTable.setExampleType(type);
		
		introspectedTable.setCountByExampleStatementId("countBy" + this.replaceString);
		introspectedTable.setDeleteByExampleStatementId("deleteBy" + this.replaceString);
		introspectedTable.setSelectByExampleStatementId("selectBy" + this.replaceString);
		introspectedTable.setSelectByExampleWithBLOBsStatementId("selectBy" + this.replaceString + "WithBLOBs");
		introspectedTable.setUpdateByExampleStatementId("updateBy" + this.replaceString);
		introspectedTable.setUpdateByExampleSelectiveStatementId("updateBy" + this.replaceString + "Selective");
		introspectedTable.setUpdateByExampleWithBLOBsStatementId("updateBy" + this.replaceString + "WithBLOBs");
		introspectedTable.setExampleWhereClauseId(this.replaceString + "_Where_Clause");
		introspectedTable.setMyBatis3UpdateByExampleWhereClauseId("Update_By_" + this.replaceString + "_Where_Clause");
		
	}
	
	
	
	
}















