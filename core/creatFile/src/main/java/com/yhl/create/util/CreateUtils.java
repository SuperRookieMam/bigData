package com.yhl.create.util;


import com.yhl.create.componet.annotation.Description;
import org.springframework.util.ObjectUtils;

import javax.persistence.Id;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/****
 * 字符串工具类
 *
 * @author Administrator
 */
public abstract class CreateUtils {
	// 根据包名加载包下面的所有class
	public static Map<String,Object>  getClassByFile(File file) throws ClassNotFoundException {
			String classPath = CreateUtils.class.getClassLoader().getResource("").getPath().substring(1);
			Map<String,Object> map =new HashMap<>();
			if (file.isDirectory()){
				map.put("isDirectory",true);
				List<Map<String,Object>> list =new ArrayList<>();
				map.put("fileList",list);
				File[] files =	file.listFiles();
				for (int i = 0; i < files.length; i++) {
					list.add(getClassByFile(files[i]));
				}
			}else {
				map.put("isDirectory",false);
				if (file.getName().endsWith(".class")){
					String className = file.getPath();
					className =className.replace("\\","/")
							.replace(classPath,"")
							.replace(".class","")
							.replaceAll("/",".");
					Class<?> clazz =Class.forName(className);
					map.put("class",clazz);
				}
			}
			return map;
		}
	//获取模板文件string
	public static Map<String,BufferedReader>  gettemplateFile(String templatePath) throws FileNotFoundException {
		File file = new File(templatePath);
		File[] files =file.listFiles();
		Map<String,BufferedReader> map =new HashMap<>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".template")){
				FileReader fileReader =new FileReader(files[i]);
				BufferedReader bufferedReader =new BufferedReader(fileReader);
				String fileName =files[i].getName();
				fileName =fileName.substring(fileName.indexOf("\\")+1,fileName.indexOf("."));
				map.put(fileName,bufferedReader);
			}
		}
		return map;
	}
	public static void createFileByMap(Map<String,Object> map,String createPath,String templatePath) {
		File file =new File(createPath);
		if (!file.exists()){
			file.mkdir();
		}
		if (!((Boolean) map.get("isDirectory"))) {
			Class<?> clazz =(Class)	map.get("class");
			try {
				createFileByClass(clazz,templatePath,createPath);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("根据class创建实体失败");
			}
		}else {
			List<Map<String,Object>> list =(List<Map<String,Object>>) map.get("fileList");
			list.forEach(ele -> {
				createFileByMap(ele,createPath,templatePath);
			});
		}
	}


	//根据模板和类生成文件
	public static void createFileByClass(Class<?> clazz,String templatePath,String createPath) throws IOException {
		 List<Field> list =	MyClassUtil.getAllFields(clazz);
		 List<Map<String,BufferedWriter>> list1 =new ArrayList<>();
		 File daoFile =new File(createPath+"/dao/"+clazz.getSimpleName()+"Dao.java");
		 if (!daoFile.exists()){
		 	File file =daoFile.getParentFile();
		 	if (!file.exists()){
		 		file.mkdir();
			}
		 }
		 if (daoFile.createNewFile()){
		 	FileWriter fileWriter =new FileWriter(daoFile);
		 	BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);
		 	Map<String,BufferedWriter> bufferedWriterMap =new HashMap<>();
			 bufferedWriterMap.put("DaoTemplate",bufferedWriter);
			 list1.add(bufferedWriterMap);
		 }

		 File serviceFile =new File(createPath+"/service/"+clazz.getSimpleName()+"Service.java");
		if (!serviceFile.exists()){
			File file =serviceFile.getParentFile();
			if (!file.exists()){
				file.mkdir();
			}
		}
		 if (serviceFile.createNewFile()){
			FileWriter fileWriter =new FileWriter(serviceFile);
			BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);
			Map<String,BufferedWriter> bufferedWriterMap =new HashMap<>();
			bufferedWriterMap.put("ServiceTemplate",bufferedWriter);
			list1.add(bufferedWriterMap);
		}
		 File serviceImplFile =new File(createPath+"/service/impl/"+clazz.getSimpleName()+"ServiceImpl.java");
		if (!serviceImplFile.exists()){
			File file =serviceImplFile.getParentFile();
			if (!file.exists()){
				file.mkdir();
			}
		}
		 if (serviceImplFile.createNewFile()){
			FileWriter fileWriter =new FileWriter(serviceImplFile);
			BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);
			Map<String,BufferedWriter> bufferedWriterMap =new HashMap<>();
			bufferedWriterMap.put("ServiceImplTemplate",bufferedWriter);
			list1.add(bufferedWriterMap);
		}
		 File  controllerFile =new File(createPath+"/controller/"+clazz.getSimpleName()+"Controller.java");
		if (!controllerFile.exists()){
			File file =controllerFile.getParentFile();
			if (!file.exists()){
				file.mkdir();
			}
		}
		 if (controllerFile.createNewFile()){
			FileWriter fileWriter =new FileWriter(controllerFile);
			BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);
			Map<String,BufferedWriter> bufferedWriterMap =new HashMap<>();
			bufferedWriterMap.put("ControllerTemplate",bufferedWriter);
			list1.add(bufferedWriterMap);
		}
		 String classname =clazz.getName();
		 classname =classname.substring(0,classname.lastIndexOf("."));
		 classname =classname.substring(0,classname.lastIndexOf("."));
		 Map<String,BufferedReader> map = gettemplateFile(templatePath);
		 for(Map<String,BufferedWriter> bufferedWriterMap:list1) {
			 Set<Map.Entry<String, BufferedWriter>>  entrySet = bufferedWriterMap.entrySet();
			 Map.Entry<String, BufferedWriter> entry = entrySet.iterator().next();
		 	// 生成dao文件
			 BufferedReader bufferedReader =map.get(entry.getKey());
			 BufferedWriter bufferedWriter =entry.getValue();
			 String line="";
			 while ((line = bufferedReader.readLine()) != null) {
				 if (line.contains("~[dao]")) {
					 line = line.replace("~[dao]", classname + ".dao");
				 } else if (line.contains("~[service]")) {
					 line = line.replace("~[service]", classname + ".service");
				 } else if (line.contains("~[serviceImpl]")) {
					 line = line.replace("~[serviceImpl]", classname + ".service.impl");
				 } else if (line.contains("~[controller]")) {
					 line = line.replace("~[controller]", classname + ".controller");
				 }
				 if (line.contains("~[entityname]")) {
					 line = line.replace("~[entityname]", clazz.getName());
				 }
				 if (line.contains("~[parantService]")) {
					 line = line.replace("~[parantService]", classname + ".service." + clazz.getSimpleName() + "Service");
				 }
				 if (line.contains("~[entitySimpleNameFirstLower]")) {
					line = line.replace("~[entitySimpleNameFirstLower]", clazz.getSimpleName().substring(0,1).toLowerCase()+clazz.getSimpleName().substring(1));
				 }
				 if (line.contains("~[entitySimpleName]")) {
					 while (line.contains("~[entitySimpleName]")) {
						 line = line.replace("~[entitySimpleName]", clazz.getSimpleName());
					 }
				 }
				 if (line.contains("~[idtype]")) {
					 int m = 0;
					 for (int i = 0; i < list.size(); i++) {
						 Id id = list.get(i).getAnnotation(Id.class);
						 if (id != null) {
							 m = i;
							 break;
						 }
					 }
					 line = line.replace("~[idtype]", list.get(m).getType().getSimpleName());
				 }

				 bufferedWriter.write(line+"\n");
				 System.out.println(line);
			 }
			 bufferedWriter.flush();
			 bufferedWriter.close();
		 }

	}

	//跟库class分析这个实体需要生成查询条件和table要先显示的列
	public static void getTableMsgByListFields(List<Field> list){
		List<Map<String,Object>> seachList = new ArrayList<>();
		List<Map<String,Object>> columnList = new ArrayList<>();
		list.forEach(ele ->{
			Description description = ele.getAnnotation(Description.class);
			if (!ObjectUtils.isEmpty(description)&&description.search()){
				Map<String,Object> searchmap = new HashMap<String,Object>();
				searchmap.put("prop",ele.getName());
				searchmap.put("label",description.label());
				searchmap.put("searchType",description.searchType());
				seachList.add(searchmap);
			}
			if (!ObjectUtils.isEmpty(description)&&description.isColumn()){
				Map<String,Object> columnmap = new HashMap<String,Object>();
				columnmap.put("prop",ele.getName());
				columnmap.put("label",description.label());
				columnList.add(columnmap);
			}
		});


	}
	public  static  void  createTableByListMsg(List<Map<String,Object>> searchList,List<Map<String,Object>> columnList,Class<?> clazz){
			StringBuffer stringBuffer =new StringBuffer();
			stringBuffer.append("<template>\n");
		    stringBuffer.append("   <div>\n");
		    if (!searchList.isEmpty()){
		     	stringBuffer.append("     <el-form ref=\"serchObj\"\n" +
						"              class=\"demo-form-inline\"\n" +
						"              size=\"mini\"\n" +
						"              label-width=\"80px\">\n");
				int m=0;
				int n=0;
				for (int j = 0; j <searchList.size() ; j++) {
					if ((j+1)%6==0&&(j+1)!=searchList.size()) {
						stringBuffer.append("         <el-row>\n");
						m+=1;
					}
					Map<String,Object> ele =searchList.get(j);
					if ("text".equalsIgnoreCase(ele.get("searchType").toString())){
						stringBuffer.append("            <el-col :span=\"4\">");
						stringBuffer.append("                <el-form-item label=\""+ele.get("label")+"\">\n" +
											"                   <el-input v-model=\"serchObj['"+ele.get("prop")+"']\"/>\n" +
											"               </el-form-item>\n");
						stringBuffer.append("            </el-col>\n");
					} else if ("time".equalsIgnoreCase(ele.get("searchType").toString())){
						stringBuffer.append("            <el-col :span=\"4\">");
						stringBuffer.append("              <el-form-item label=\""+ele.get("label")+"\">\n" +
											"                 <el-date-picker v-model=\"serchObj['"+ele.get("prop")+"']\"\n" +
											"                                 type=\"datetime\"\n" +
											"                                 placeholder=\"选择日期时间\">\n" +
											"                 </el-date-picker>\n" +
											"              </el-form-item>\n");
						stringBuffer.append("            </el-col>\n");
					}else if ("select".equalsIgnoreCase(ele.get("searchType").toString())){
						stringBuffer.append("            <el-col :span=\"4\">");
						stringBuffer.append("                    <el-select v-model=\"serchObj['"+ele.get("prop")+"']\" placeholder=\""+ele.get("label")+"\">\n" +
											"                      <el-option v-for=\"(item,index) in selectData\"\n" +
											"                                               :key=\"index\"\n" +
											"                                               :label=\"item.lable\"\n" +
											"                                               :value=\"item.value\"/>\n" +
											"                    </el-select>\n" +
											"               </el-form-item>\n");
						stringBuffer.append("            </el-col>\n");
					}else if ("select".equalsIgnoreCase(ele.get("searchType").toString())){
						stringBuffer.append("            <el-col :span=\"4\">");
						stringBuffer.append("                    <el-select v-model=\"serchObj['"+ele.get("prop")+"']\" placeholder=\""+ele.get("label")+"\">\n" +
											"                      <el-option v-for=\"(item,index) in selectData\"\n" +
											"                                               :key=\"index\"\n" +
											"                                               :label=\"item.lable\"\n" +
											"                                               :value=\"item.value\"/>\n" +
											"                    </el-select>\n" +
											"               </el-form-item>\n");
						stringBuffer.append("            </el-col>\n");
					}
					stringBuffer.append("            <el-col :span=\"4\">");
					stringBuffer.append("              <el-button type=\"primary\"\n" +
										"                         size=\"mini\"\n" +
										"                         @click=\"filterByserchObj()\">\n" +
										"                            筛选\n" +
										"                          </el-button>\n" +
										"              <el-button type=\"primary\"\n" +
										"                         size=\"mini\"\n" +
										"                         @click=\"add()\">\n" +
										"                            新增\n" +
										"                          </el-button>\n");
					stringBuffer.append("            </el-col>\n");
					if (((j+1)%6==0&& m==n+1)||(j+1)!=searchList.size()) {
						stringBuffer.append("         </el-row>\n");
						n+=1;
					}
				}
				stringBuffer.append("      </el-form>\n");
			}

			if (!columnList.isEmpty()){
				stringBuffer.append("      <el-table :data=\"tableData\"\n"
								   +"                style=\"width: 100%\">\n");
				columnList.forEach(ele -> {
					stringBuffer.append("         <el-table-column\n" +
										"                 label=\""+ele.get("label")+"\"\n" +
										"                 prop=\""+ele.get("prop")+"\"/>\n");
				});
				stringBuffer.append("         <el-table-column label=\"操作\" :min-width=\"60\">\n" +
									"                 <template slot-scope=\"scope\">\n" +
									"                   <el-button type=\"text\" size=\"mini\" @click=\"edit(scope.row)\">编辑</el-button>\n" +
									"                   <el-button type=\"text\" size=\"mini\" @click=\"delete(scope.row)\">删除</el-button>\n" +
									"                 </template>\n" +
									"         </el-table-column>\n");
				stringBuffer.append("         <el-pagination\n" +
									"               @size-change=\"handleSizeChange\"\n" +
									"               @current-change=\"handleCurrentChange\"\n" +
									"               :current-page=\"currentPage\"\n" +
									"               :page-sizes=\"pageSizes\"\n" +
									"               :page-size=\"currentPageSizes\"\n" +
									"               layout=\"total, sizes, prev, pager, next, jumper\"\n" +
									"               :total=\"totalPage\"/>\n");
				stringBuffer.append("      </el-table>\n");
			}

			stringBuffer.append("   </div>\n");
			stringBuffer.append("</template>\n");
			stringBuffer.append("<script>\n");
			stringBuffer.append("import { Component, Mixins } from 'vue-property-decorator'\n" +
								"   import TableBase from '../../../plugins/TableBase'\n");

		stringBuffer.append("  @Component\n" +
							"  export default class "+clazz.getSimpleName()+" Mixins(TableBase) {\n");
		stringBuffer.append("\n" +
							"    templateSearch = ' url like t and (url like t or  companyId eq 1)'\n" +
							"\n" +
							"    serchObj = {}\n" +
							"\n" +
							"    params = {\n" +
							"        pageSize: 50,\n" +
							"        pageNum: 1\n" +
							"    }\n" +
							"    pageSizes = [50, 100, 200, 400]\n" +
							"\n" +
							"    tableData = []\n" +
							"\n" +
							"    controllerMapping = '"+clazz.getSimpleName().substring(0,1).toLowerCase()+clazz.getSimpleName().substring(1)+"'");
		stringBuffer.append("handleSizeChange (val) {\n" +
							"\t\t\tthis.params.pageSize = val\n" +
							"\t\t\tthis.filterByserchObj()\n" +
							"\t\t}\n" +
							"\t\thandleCurrentChange (val) {\n" +
							"\t\t\tthis.params.pageNum = val\n" +
							"\t\t\tthis.filterByserchObj()\n" +
							"\t\t}\n");

 		stringBuffer.append("\n" +
							"    filterByserchObj () {\n" +
							"      this.search(this.templateSearch, this.serchObj, this.params, this.controllerMapping)\n" +
							"          .then(ele => {\n" +
							"            this.tableData = ele\n" +
							"      })\n" +
							"    }\n");

		stringBuffer.append("    created () {\n" +
				"      this.filterByserchObj()\n" +
				"    }\n");
		stringBuffer.append("  }\n");
			stringBuffer.append("</script>\n");
	}


	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException {
		  String classpath = CreateUtils.class.getResource("/").getPath();
		  String packge = "com.yhl.create.componet.entity";
		  String packgePath = packge.replaceAll("\\.","/");
		  //根据包名加载Class
		  Map<String,Object> objectMap =getClassByFile(new File(classpath+packgePath));
		  String templatePath="D:\\code\\source\\ideaSource\\bigdata\\bigData\\core\\creatFile\\src\\main\\java\\com\\yhl\\create\\componet\\template";
		  createFileByMap(objectMap,"D:\\JavaFile1",templatePath);
	}
}
