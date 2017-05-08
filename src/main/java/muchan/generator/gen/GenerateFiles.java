package muchan.generator.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import muchan.generator.gen.MyGenerator;

/**
 * 利用MyGenerator生成实体类,AO,Mapper,Mapper.xml文件
 * @author DemonLi
 *
 */
public class GenerateFiles {
	
	/**
	 * 生成文件
	 * @param path genratorConfig.xml文件所在路径,从根路径开始写
	 * @param appObjectPackage AO包名(全名)
	 * @throws Exception
	 */
	public static void generate(String path,String appObjectPackage){
		path = getProjectRootPath() + File.separator + path;
		File file = new File(path);
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		ConfigurationParser cp = new ConfigurationParser(warnings);
		try {
			Configuration config = cp.parseConfiguration(file);
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			MyGenerator generator = new MyGenerator(config, callback, warnings,appObjectPackage);
			//生成文件
			generator.generate(null);
		} catch (Exception e) {
			System.out.println("生成文件出错!");
			e.printStackTrace();
		}
	}
	
	private static String getProjectRootPath(){
		File directory = new File("");// 参数为空
        String rootPath = null;
		try {
			rootPath = directory.getCanonicalPath();
			rootPath = rootPath.replaceAll("\\\\", "/");
		} catch (IOException e) {
			System.out.println("项目根路径获取失败!");
			e.printStackTrace();
		}
        return rootPath;
	}
	
}
