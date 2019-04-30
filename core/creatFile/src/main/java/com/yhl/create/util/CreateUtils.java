package com.yhl.create.util;


import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static void createFileByMap(Map<String,Object> map,String createPath) {
		if (!((Boolean) map.get("isDirectory"))) {
			Class<?> clazz =(Class)	map.get("class");

		}else {
			List<Map<String,Object>> list =(List<Map<String,Object>>) map.get("fileList");
			list.forEach(ele -> {
				createFileByMap(ele,createPath);
			});
		}
	}
	//根据模板和类生成文件
	public static void createFileByClass(Class<?> clazz,Map<String,File> map){
		 List<Field> list =	MyClassUtil.getAllFields(clazz);

	}


	public static void main(String[] args) throws ClassNotFoundException {
		  String classpath = CreateUtils.class.getResource("/").getPath();
		  String packge = "com.yhl.create.componet";
		  String packgePath = packge.replaceAll("\\.","/");
		  System.out.println(classpath+packgePath);
		System.out.println(getClassByFile(new File(classpath+packgePath)));
	}
}
