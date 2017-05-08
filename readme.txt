1.配置文件请看generatorConfig.xml



2.生成用法

import muchan.generator.gen.GenerateFiles;

public class Generate {
	
	public static void main(String[] args){
		//generatorConfig.xml文件路径
		String path = "src/main/resources/generatorConfig.xml";
		//AO对象包名(全名)
		String appObjectPackage = "test.appobject";
		
		GenerateFiles.generate(path, appObjectPackage);
		
	}
	
}

