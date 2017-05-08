package muchan.generator.gen;

import static org.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.XmlFileMergerJaxp;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 自定义的Generator
 * <p>
 * 1.生成AO应用对象
 * </p>
 * <p>
 * 2.Mapper接口自定义继承BaseGeneratedMapper通用mapper
 * </p>
 * <p>
 * 3.去除Mapper.xml文件的续写特性
 * </p>
 * @author DemonLi
 *
 */
public class MyGenerator extends MyBatisGenerator {
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private Configuration configuration;

	private ShellCallback shellCallback;

	/** The generated java files. */
	private List<GeneratedJavaFile> generatedJavaFiles;

	/** The generated xml files. */
	private List<GeneratedXmlFile> generatedXmlFiles;

	/** The warnings. */
	private List<String> warnings;

	/** The projects. */
	private Set<String> projects;
	
	@SuppressWarnings("deprecation")
	private static final freemarker.template.Configuration TEMPLATE_CFG = new freemarker.template.Configuration();
	
	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/** Ther AO package */
	private String appObjectPackage;
	
	/** The javaFiles targetProject */
	private String targetProject;
	
	/** 项目根路径 */
	private String rootProjectPath = getProjectRootPath();
	
	/** 实体类包名(全名) */
	private String entityPackage;
	
	/** DAO接口包名(全名) */
	private String mapperPackage;
	
	/** XMl文件路径 */
	private String mapperXmlPath;
	
	static {
		TEMPLATE_CFG.setOutputEncoding("UTF-8");
	}
	
	/**
	 * 需要生成AO应用对象的构造器
	 * @param appObjectPackage AO应用对象所在的包名
	 */
	public MyGenerator(Configuration configuration, ShellCallback shellCallback, List<String> warnings,String appObjectPackage)
			throws InvalidConfigurationException {
		super(configuration,shellCallback,warnings);
		if (configuration == null) {
            throw new IllegalArgumentException(getString("RuntimeError.2")); 
        } else {
            this.configuration = configuration;
        }

        if (shellCallback == null) {
            this.shellCallback = new DefaultShellCallback(false);
        } else {
            this.shellCallback = shellCallback;
        }

        if (warnings == null) {
            this.warnings = new ArrayList<String>();
        } else {
            this.warnings = warnings;
        }
        this.generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
        this.generatedXmlFiles = new ArrayList<GeneratedXmlFile>();
        this.projects = new HashSet<String>();

        this.configuration.validate();
        
        this.appObjectPackage = appObjectPackage;
	}

	@Override
	public void generate(ProgressCallback callback) throws SQLException, IOException, InterruptedException {
		this.generate(callback, null, null, true);
	}

	@Override
	public void generate(ProgressCallback callback, Set<String> contextIds)
			throws SQLException, IOException, InterruptedException {
		this.generate(callback, contextIds, null, true);
	}

	@Override
	public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames)
			throws SQLException, IOException, InterruptedException {
		this.generate(callback, contextIds, fullyQualifiedTableNames, true);
	}

	@Override
	public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames,
			boolean writeFiles) throws SQLException, IOException, InterruptedException {
		if (callback == null) {
			callback = new NullProgressCallback();
		}

		this.generatedJavaFiles.clear();
		this.generatedXmlFiles.clear();
		ObjectFactory.reset();
		RootClassInfo.reset();

		List<Context> contextsToRun;
		if (contextIds == null || contextIds.size() == 0) {
			contextsToRun = this.configuration.getContexts();
		} else {
			contextsToRun = new ArrayList<Context>();
			for (Context context : this.configuration.getContexts()) {
				if (contextIds.contains(context.getId())) {
					contextsToRun.add(context);
				}
			}
		}

		if (this.configuration.getClassPathEntries().size() > 0) {
			ClassLoader classLoader = getCustomClassloader(configuration.getClassPathEntries());
			ObjectFactory.addExternalClassLoader(classLoader);
		}

		int totalSteps = 0;
		for (@SuppressWarnings("unused") Context context : contextsToRun) {
			callback.introspectionStarted(totalSteps);
		}

		for (Context context : contextsToRun) {
			context.introspectTables(callback, this.warnings, fullyQualifiedTableNames);
		}

		totalSteps = 0;
		for (Context context : contextsToRun) {
			totalSteps += context.getGenerationSteps();
		}

		callback.generationStarted(totalSteps);

		for (Context context : contextsToRun) {
			context.generateFiles(callback, generatedJavaFiles, generatedXmlFiles, warnings);
		}
		
		// now save the files
		if (writeFiles) {
			callback.saveStarted(generatedXmlFiles.size() + generatedJavaFiles.size());

			for (GeneratedXmlFile gxf : generatedXmlFiles) {
				projects.add(gxf.getTargetProject());
				writeGeneratedXmlFile(gxf, callback);
			}

			for (GeneratedJavaFile gjf : generatedJavaFiles) {
				projects.add(gjf.getTargetProject());
				writeGeneratedJavaFile(gjf, callback);
			}

			for (String project : projects) {
				shellCallback.refreshProject(project);
			}
		}
		
		setTargetProject();
		setEntityPackage();
		setMapperPackage();
		setMapperXmlPath();

		Set<String> entityNames = getEntityNames();
		
		
		try {
			this.generateAOs(entityNames);
			this.generateMapperXmls(entityNames);
			this.generateMappers(entityNames);
		} catch (Exception e) {
			throw new RuntimeException("生成文件对象出错!",e);
		}

		callback.done();

	}

	private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback)
			throws InterruptedException, IOException {
		File targetFile;
		String source;
		try {
			File directory = shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
			targetFile = new File(directory, gjf.getFileName());
			if (targetFile.exists()) {
				if (shellCallback.isMergeSupported()) {
					source = shellCallback.mergeJavaFile(gjf.getFormattedContent(), targetFile.getAbsolutePath(),
							MergeConstants.OLD_ELEMENT_TAGS, gjf.getFileEncoding());
				} else if (shellCallback.isOverwriteEnabled()) {
					source = gjf.getFormattedContent();
					warnings.add(getString("Warning.11", //$NON-NLS-1$
							targetFile.getAbsolutePath()));
				} else {
					source = gjf.getFormattedContent();
					targetFile = getUniqueFileName(directory, gjf.getFileName());
					warnings.add(getString("Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
				}
			} else {
				source = gjf.getFormattedContent();
			}

			callback.checkCancel();
			callback.startTask(getString("Progress.15", targetFile.getName())); //$NON-NLS-1$
			writeFile(targetFile, source, gjf.getFileEncoding());
		} catch (ShellException e) {
			warnings.add(e.getMessage());
		}
	}

	private File getUniqueFileName(File directory, String fileName) {
		File answer = null;

		// try up to 1000 times to generate a unique file name
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < 1000; i++) {
			sb.setLength(0);
			sb.append(fileName);
			sb.append('.');
			sb.append(i);

			File testFile = new File(directory, sb.toString());
			if (!testFile.exists()) {
				answer = testFile;
				break;
			}
		}

		if (answer == null) {
			throw new RuntimeException(getString("RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
		}

		return answer;
	}

	private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback)
			throws InterruptedException, IOException {
		File targetFile;
		String source;
		try {
			File directory = shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
			targetFile = new File(directory, gxf.getFileName());
			if (targetFile.exists()) {
				if (gxf.isMergeable()) {
					source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
				} else if (shellCallback.isOverwriteEnabled()) {
					source = gxf.getFormattedContent();
					warnings.add(getString("Warning.11", //$NON-NLS-1$
							targetFile.getAbsolutePath()));
				} else {
					source = gxf.getFormattedContent();
					targetFile = getUniqueFileName(directory, gxf.getFileName());
					warnings.add(getString("Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
				}
			} else {
				source = gxf.getFormattedContent();
			}

			callback.checkCancel();
			callback.startTask(getString("Progress.15", targetFile.getName())); //$NON-NLS-1$
			writeFile(targetFile, source, "UTF-8"); //$NON-NLS-1$
		} catch (ShellException e) {
			warnings.add(e.getMessage());
		}
	}

	private void writeFile(File file, String content, String fileEncoding) throws IOException {
		FileOutputStream fos = new FileOutputStream(file, false);
		OutputStreamWriter osw;
		if (fileEncoding == null) {
			osw = new OutputStreamWriter(fos);
		} else {
			osw = new OutputStreamWriter(fos, fileEncoding);
		}

		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(content);
		bw.close();
	}
	
	/**
	 * 获取所有实体类的名字
	 * @return 实体类的Set集合
	 */
	private Set<String> getEntityNames(){
		Set<String> set = new HashSet<String>();
		if(this.generatedJavaFiles != null && !this.generatedJavaFiles.isEmpty()){
			for(GeneratedJavaFile file : this.generatedJavaFiles){
				String fileName = file.getFileName();
				//找到实体类文件
				if(!fileName.startsWith(".") && !fileName.endsWith("Mapper.java") 
						&& !fileName.endsWith("Dao.java")
						&& !fileName.endsWith("Criteria.java")
						&& !fileName.endsWith("Example.java")){
					String name = fileName.replace(".java", "");
					set.add(name);
				}
			}
		}
		return set;
	}
	
	/**
	 * 生成AO对象
	 * @param entityNames 实体类Set集合
	 * @throws IOException
	 * @throws TemplateException
	 */
	private void generateAOs(Set<String> entityNames) throws IOException,TemplateException{
		//获取类路径
		URL resource = MyGenerator.class.getResource("");
		//获取模板文件夹路劲
		String path = resource.toString().substring(resource.toString().indexOf("/", 1));
		System.out.println(path);
		String templatePath = URLDecoder.decode(path,"UTF-8");
		TEMPLATE_CFG.setDefaultEncoding("UTF-8");
		TEMPLATE_CFG.setDirectoryForTemplateLoading(new File(templatePath));
		
		//获取AO模板文件
		Template template = TEMPLATE_CFG.getTemplate("ao.ftl");
		template.setOutputEncoding("UTF-8");
		//是否生成AO应用对象
		if("".equals(this.appObjectPackage) || this.appObjectPackage == null){
			return;
		}
		String appObjectPackagePath = this.rootProjectPath + "/"  + this.targetProject  + "/" + this.appObjectPackage.replaceAll("\\.", "/");
		
		File appObjectpackageFile = new File(appObjectPackagePath);
		if(!appObjectpackageFile.exists()){
			appObjectpackageFile.mkdirs();
		}
		
		for(String entityName : entityNames){
			File target = new File(appObjectPackagePath + File.separator + entityName + "AO.java");
			if(target.exists()){
				System.out.println("应用对象[" + target.getName().replace(".java", "") + "已经存在,不重复生成!");
				return;
			}
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(target),"UTF-8"));
			Map<String,String> dataModel = new HashMap<String,String>();
			Date date = new Date();
			dataModel.put("appObjectPackage", this.appObjectPackage);
			dataModel.put("dateTime", this.dateFormat.format(date));
			dataModel.put("date",new SimpleDateFormat("yyyy-MM-dd").format(date));

			dataModel.put("entityPackage", this.entityPackage);
			dataModel.put("entityName", entityName);
			template.process(dataModel, out);
			out.flush();
			out.close();
			System.out.println("生成应用对象[" + this.appObjectPackage + '.' + entityName + "AO" + "]");
		}	
	}
	
	/**
	 * 生成Mapper接口
	 * @param entityNames 实体类Set集合
	 * @throws TemplateException
	 * @throws IOException
	 */
	private void generateMappers(Set<String> entityNames) throws TemplateException, IOException {
		Template template = TEMPLATE_CFG.getTemplate("mapper.ftl");
		template.setOutputEncoding("UTF-8");
		
		String javaMapperPath = this.mapperPackage;
		String mapperPath = this.rootProjectPath + File.separator + this.targetProject + File.separator
				+ this.mapperPackage.replaceAll("\\.", "/");
		String mapperXmlPath = this.rootProjectPath + File.separator + this.mapperXmlPath;
		
		for(String entityName : entityNames){
			File old = new File(mapperPath + File.separator + entityName + "Mapper.java");
			if(old.exists()){
				File oldMapperXML = new File(mapperXmlPath + File.separator + entityName + "GeneratedMapper.xml");
				InputStream oldInputStream = new FileInputStream(oldMapperXML);
				String content = IOUtils.toString(oldInputStream, "UTF-8");
				IOUtils.closeQuietly(oldInputStream);

				File target = new File(mapperPath + File.separator + entityName + "GeneratedMapper.java");
				
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), "UTF-8"));
				Map<String, String> dataModel = new HashMap<String, String>();
				Date date = new Date();
				
				dataModel.put("appObjectPackage", this.appObjectPackage);
				dataModel.put("entityPackage", this.entityPackage);
				dataModel.put("mapperPackage", javaMapperPath);
				dataModel.put("dateTime", this.dateFormat.format(date));
				dataModel.put("date",new SimpleDateFormat("yyyy-MM-dd").format(date));
				
				String temp = entityName.concat("WithBLOBs");
				String entityNameWithBLOBsAO = entityName;
				String baseGeneratedMapper = "BaseGeneratedMapper";
				
				if(content.contains("WithBLOBs")){
					baseGeneratedMapper = "BaseWithBLOBsGeneratedMapper";
				}
				if(entityNames.contains(temp)){
					entityNameWithBLOBsAO = temp;
					baseGeneratedMapper = "BaseWithBLOBsGeneratedMapper";
				}
				
				dataModel.put("baseGeneratedMapper", baseGeneratedMapper);
				dataModel.put("entityNameWithBLOBsAO", entityNameWithBLOBsAO);
				dataModel.put("entityName", entityName);
				template.process(dataModel, out);
				
				out.flush();
				out.close();
				
				old.delete();
				
				System.out.println("生成DAO[" + this.appObjectPackage + '.' + entityName + "GeneratedMapper" + "]");
				
			}
		}	
	}
	
	/**
	 * 生成mapper.xml文件
	 * @param entityNames 实体类Set集合
	 * @throws IOException
	 * @throws TemplateException
	 */
	private void generateMapperXmls(Set<String> entityNames) throws IOException, TemplateException {
		String mapperXmlPath = this.mapperXmlPath.replaceAll("\\.", "/");
		for(String entityName : entityNames){
			File old = new File(mapperXmlPath + File.separator + entityName + "Mapper.xml");
			if(old.exists()){
				File target = new File(mapperXmlPath + File.separator + entityName + "GeneratedMapper.xml");
				File target2 = new File(mapperXmlPath + File.separator + entityName + "GeneratedMapper.xml");
				Date date = new Date();
				StringBuilder contentBuilder = new StringBuilder();
				
				InputStream oldInputStream = new FileInputStream(old);
				String content = IOUtils.toString(oldInputStream, "UTF-8");
				IOUtils.closeQuietly(oldInputStream);
				
				contentBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(LINE_SEPARATOR)
				.append("<!--").append(LINE_SEPARATOR).append(LINE_SEPARATOR).append("    ")
				.append("Description: ").append("A generated data access implementation of entity ")
				.append(entityName).append(".").append(LINE_SEPARATOR).append("    ")
				.append("             Generated at ").append(this.dateFormat.format(date))
				.append(", do NOT modify!").append(LINE_SEPARATOR).append("    ")
				.append("Author: Demon Li ").append(LINE_SEPARATOR)
				.append("    ").append("Version: 1.0.0.0, ")
				.append(new SimpleDateFormat("yyyy-MM-dd").format(date))
				.append(LINE_SEPARATOR).append(LINE_SEPARATOR).append("-->");
				content = content.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", "");
				content = content.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
				
				content = content
						.replace("\"" + this.entityPackage + '.' + entityName + "\"",
								"\"" + this.appObjectPackage + '.' + entityName + "AO" + "\"")
						.replace("\"" + this.entityPackage + '.' + entityName.concat("WithBLOBs") + "\"",
								"\"" + this.appObjectPackage + '.' + entityName.concat("WithBLOBs") + "AO" + "\"")
						.replace("\"" + this.mapperPackage + '.' + entityName + "Mapper" + "\"",
								"\"" + this.mapperPackage + '.' + entityName + "GeneratedMapper" + "\"");
				
				contentBuilder.append(content);
				
				FileWriter writer = new FileWriter(target);
				IOUtils.write(contentBuilder.toString(), writer);
				IOUtils.closeQuietly(writer);
				
				FileWriter writer2 = new FileWriter(target2);
				IOUtils.write(contentBuilder.toString(), writer2);
				IOUtils.closeQuietly(writer2);
				
				old.delete();
			}
		}
	}
	
	@Override
	public List<GeneratedJavaFile> getGeneratedJavaFiles() {
		return this.generatedJavaFiles;
	}

	@Override
	public List<GeneratedXmlFile> getGeneratedXmlFiles() {
		return this.generatedXmlFiles;
	}
	
	private String getProjectRootPath(){
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

	public void setTargetProject() {
		if(this.generatedJavaFiles != null && !this.generatedJavaFiles.isEmpty()){
			this.targetProject = this.generatedJavaFiles.get(0).getTargetProject();
		}
	}
	
	public void setEntityPackage(){
		if(this.generatedJavaFiles != null && !this.generatedJavaFiles.isEmpty()){
			for(GeneratedJavaFile file : this.generatedJavaFiles){
				String fileName = file.getFileName();
				if(!fileName.startsWith(".") && !fileName.endsWith("Mapper.java") 
						&& !fileName.endsWith("Criteria.java")
						&& !fileName.endsWith("Example.java")){
					this.entityPackage = file.getTargetPackage();
					return;
				}
			}
		}
	}
	
	public void setMapperPackage(){
		if(this.generatedJavaFiles != null && !this.generatedJavaFiles.isEmpty()){
			for(GeneratedJavaFile file : this.generatedJavaFiles){
				String fileName = file.getFileName();
				if(!fileName.startsWith(".") && (fileName.endsWith("Mapper.java")
						 || fileName.endsWith("Dao.java"))){
					this.mapperPackage = file.getTargetPackage();
					return;
				}
			}
		}
	}
	
	public void setMapperXmlPath(){
		if(this.generatedXmlFiles != null && this.generatedXmlFiles.size() > 0){
			GeneratedXmlFile file = this.generatedXmlFiles.get(0);
			this.mapperXmlPath = file.getTargetProject() + "/" + file.getTargetPackage();
		}
	}
	
}




