package com.yhl.create.util;


import com.yhl.create.componet.annotation.Description;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/****
 * 字符串工具类
 *
 * @author Administrator
 */
public abstract class CreateUtils {

	/***
	 * 检验字符串是否为空
	 *
	 * @param str
	 *            字符串
	 * @return true 不为空 false 空
	 */
	public static boolean strNotNull(String str) {
		if (str != null && !("".equals(str.trim()))) {
			return true;
		}
		return false;
	}


	// ---------------------------------------文件处理----------------------------------
	/**
	 * 获取文件文本内容
	 *
	 * @param fileUrl
	 *            文件路径
	 * @return
	 */
	public static String getFileText(String fileUrl) {
		fileUrl = removeBackSlant(fileUrl.replace("/", "\\")); // 去除url结尾的所以反斜杠
		if (fileUrl == null || fileUrl.length() == 0) {
			String message = "文件路径为空!!";
			throw new RuntimeException(message);
		}

		File file = new File(fileUrl);
		String fileContentString = null;
		if (!file.isFile()) {
			String msg = "不是一个标准的文件";
			System.err.println(msg);
			return null;
		}

		FileInputStream is = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			is = new FileInputStream(file);

			byte[] buffers = new byte[512];
			int count = 0;
			while ((count = is.read(buffers)) > 0) {
				bos.write(buffers, 0, count);
			}
			fileContentString = new String(bos.toByteArray(), "UTF-8");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
					bos = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return fileContentString;
	}

	/**
	 * 创建文本文件
	 *
	 * @param text
	 * @param catalogUrl
	 */
	public static void text2File(String text, String catalogUrl) {
		catalogUrl = removeBackSlant(catalogUrl.replace("/", "\\")); // 去除url结尾的所以反斜杠
		if (catalogUrl == null || catalogUrl.length() == 0) {
			String message = "文件路径为空!!";
			throw new RuntimeException(message);
		} else {
			String new_catalogUrl = catalogUrl.substring(0, catalogUrl
					.lastIndexOf("\\") + 1);
			File file_new = new File(new_catalogUrl);
			if (!file_new.exists()) {
				file_new.mkdirs();
			}
		}
		File file = new File(catalogUrl);
		if (!file.exists()) {
			try {
				file.createNewFile();
				if (!file.exists()) {
					String msg = "不是一个标准的文件";
					System.err.println(msg);
					throw new RuntimeException(msg);
				}
			} catch (IOException e) {
				String msg = "创建文件失败!";
				System.err.println(msg);
				e.printStackTrace();
			}
		}

		FileWriter fw = null;
		BufferedWriter out = null;
		try {
			fw = new FileWriter(file);
			out = new BufferedWriter(fw);
			out.write(text, 0, text.length());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
					fw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 对文件文本内容选择替换
	 *
	 * @param fileUrl
	 *            文件路径
	 */
	public static void replaceFileText(String fileUrl, String[] oldChars,
			String[] newChars) {
		fileUrl = removeBackSlant(fileUrl.replace("/", "\\")); // 去除url结尾的所以反斜杠
		if (fileUrl == null || fileUrl.length() == 0) {
			String message = "文件路径为空!!";
			throw new RuntimeException(message);
		}
		if (oldChars == null) {
			String message = "要替换的内容数组为null";
			throw new RuntimeException(message);
		}
		if (newChars == null) {
			String message = "替换的新内容数组为null";
			throw new RuntimeException(message);
		}
		if (oldChars.length != newChars.length) {
			String message = "旧内容数组与新内容数组长度不一致";
			throw new RuntimeException(message);
		}
		String fileContentString = getFileText(fileUrl); // 得到文件文本内容
		for (int i = 0; i < oldChars.length; i++) {
			String oldChar = oldChars[i];
			String newChar = newChars[i];
			fileContentString = fileContentString.replace(oldChar, newChar);
		}
		text2File(fileContentString, fileUrl);

	}

	/**
	 * 提取文本文件内容并可替换内容后到新文件
	 *
	 * @param fileUrl
	 * @param catalogUrl
	 * @param oldChars
	 * @param newChars
	 */
	public static void fileText2NewFile(String fileUrl, String catalogUrl,
			String[] oldChars, String[] newChars) {
		fileUrl = removeBackSlant(fileUrl.replace("/", "\\")); // 去除url结尾的所以反斜杠
		catalogUrl = removeBackSlant(catalogUrl.replace("/", "\\"));
		if (fileUrl == null || fileUrl.length() == 0) {
			String message = "文件路径为空!!";
			throw new RuntimeException(message);
		}
		if (catalogUrl == null || catalogUrl.length() == 0) {
			String message = "文件路径为空!!";
			throw new RuntimeException(message);
		}
		if (oldChars == null) {
			String message = "要替换的内容数组为null";
			throw new RuntimeException(message);
		}
		if (newChars == null) {
			String message = "替换的新内容数组为null";
			throw new RuntimeException(message);
		}
		if (oldChars.length != newChars.length) {
			String message = "旧内容数组与新内容数组长度不一致";
			throw new RuntimeException(message);
		}
		String fileContentString = getFileText(fileUrl); // 得到文件文本内容
		for (int i = 0; i < oldChars.length; i++) {
			String oldChar = oldChars[i];
			String newChar = newChars[i];
			fileContentString = fileContentString.replace(oldChar, newChar);
		}
		text2File(fileContentString, catalogUrl);
	}

	/**
	 * 去除字符串后的反斜杠"\" 会将正斜杠换为两个反斜杠,并且去除结尾的反斜杠"\"
	 *
	 * @param url
	 * @return
	 */
	public static String removeBackSlant(String url) {
		String _url = url.replaceAll("/", "\\");
		boolean endsWith = _url.endsWith("\\");
		if (endsWith) {
			int lastIndexOf = _url.lastIndexOf("\\");
			String _new = _url.substring(0, lastIndexOf);
			return removeBackSlant(_new);
		} else {
			return _url;
		}
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	// -----------------------------------------------------
	public static String file_save_path = "D:\\JavaFile\\";// 文件保存路径
	public static String model_name = "";
	static String x_model_name = "";// 首字母转小写
	static String bao = "com.c1tech.backend.model." + model_name + "";

	/**
	 * 首字母转小写
	 *
	 * @param s
	 * @return
	 */
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(
					Character.toLowerCase(s.charAt(0))).append(s.substring(1))
					.toString();
	}

	/**
	 * 根据 model 创建 后台 和 页面
	 *
	 *
	 *            com.c1tech.backend.model.Account
	 * @param ModelName
	 * @param file_save_path_new
	 *            D:\\JavaFile\\
	 */
	public static void createFileALL(String baonew, String ModelName,
			String file_save_path_new,
			List<LinkedHashMap<String, Object>> zhujie, String packagebase) {

		if (org.springframework.util.StringUtils.isEmpty(baonew)
				||org.springframework.util.StringUtils.isEmpty(ModelName)
				|| !(file_save_path_new.indexOf("JavaFile") != -1)) {
			System.out.println("基础信息不正确");
			return;
		} else {
			file_save_path = file_save_path_new;
			bao = baonew;
			model_name = ModelName;
			x_model_name = toLowerCaseFirstOne(model_name);
		}
		/*
		 * if(file_save_path.indexOf("JavaFile")!=-1){//删除原始文件
		 * StringUtils.delFolder(file_save_path); }else{
		 * System.out.println("文件路径不正确 必须包含 JavaFile "); return; }
		 */

		creaeDao(model_name, packagebase);
		createService(model_name, packagebase);
		createServiceImpl(model_name, packagebase);
		createController(model_name, packagebase);

		createWebAddJsp(model_name, zhujie);
		createWebUpdate(model_name, zhujie);
		createWebList(model_name, zhujie);
	}

	/**
	 * 创建到
	 *
	 * @param model_name
	 *            model 名称
	 */
	public static void creaeDao(String model_name, String packagebase) {
		String daoName = model_name + "Dao.java";
		String path = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/MUBANDao.java";// 原始文件路径
		System.out.println(path);
		CreateUtils.fileText2NewFile(path, file_save_path + "/src/Dao/"
				+ daoName, new String[] { "MMMM", "mmmm", "BBBB" },
				new String[] { model_name, x_model_name, packagebase });
		System.out.println(daoName + "----" + "创建成功");
	}

	/**
	 * 创建Service
	 *
	 * @param model_name
	 *            model 需要被创建Model名称
	 */
	public static void createService(String model_name, String packagebase) {
		String serviceName = model_name + "Service.java";
		String path = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/MUBANService.java";// 原始文件路径
		CreateUtils.fileText2NewFile(path, file_save_path + "/src/Service/"
				+ serviceName, new String[] { "MMMM", "mmmm", "BBBB" },
				new String[] { model_name, x_model_name, packagebase });
		System.out.println(serviceName + "----" + "创建成功");
	}

	/**
	 * 创建ServiceImpl
	 *
	 * @param model_name
	 *            model 需要被创建Model名称
	 */
	public static void createServiceImpl(String model_name, String packagebase) {
		String serviceImplName = model_name + "ServiceImpl.java";
		String path = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/MUBANServiceImpl.java";// 原始文件路径/
		CreateUtils.fileText2NewFile(path, file_save_path + "/src/ServiceImpl/"
				+ serviceImplName, new String[] { "MMMM", "mmmm", "BBBB" },
				new String[] { model_name, x_model_name, packagebase });
		System.out.println(serviceImplName + "----" + "创建成功");
	}

	/**
	 * 创建Controller
	 *
	 * @param model_name
	 *            model 需要被创建Model名称
	 */
	public static void createController(String model_name, String packagebase) {
		String ControllerName = model_name + "Controller.java";
		String path = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/MUBANController.java";// 原始文件路径
		CreateUtils.fileText2NewFile(path, file_save_path + "/src/Controller/"
				+ ControllerName, new String[] { "MMMM", "mmmm", "BBBB" },
				new String[] { model_name, x_model_name, packagebase });
		System.out.println(ControllerName + "----" + "创建成功");
	}

	// ----------------------生成页面 ---------------

	/**
	 * 创建 添加页面
	 *
	 * @param model_name
	 *            model 需要被创建Model名称
	 */
	public static void createWebAddJsp(String model_name,
			List<LinkedHashMap<String, Object>> zhujie) {
		String addName = "add.jsp";
		String path = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/addMain.jsp";// 原始文件路径
		String pathPara = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/add.txt";// 原始文件路径 --

		String para = CreateUtils.getFileText(pathPara);
		String allpara = "";
		/*
		 * List<String> nameList=getName(bao); for (String p : nameList) {
		 * allpara+="\r\n "; allpara+=para.replace("XXYY", p); }
		 */

		LinkedHashMap<String, Object> lmap = zhujie.get(0);
		for (Entry<String, Object> entry : lmap.entrySet()) {
			// System.out.println(entry.getKey() + ": " + entry.getValue());
			if (entry.getKey() == "id" || entry.getKey() == "createtime")
				continue;

			allpara += "\r\n ";
			allpara += para.replace("MMMM", entry.getValue() + "").replace(
					"XXYY", entry.getKey());
		}
		String addContent = CreateUtils.getFileText(path).replace("XXYY",
				allpara).replace("MODEL_NAME", model_name).replace("muban",
				model_name);
		CreateUtils.createDir(file_save_path + "/web/" + model_name + "/");
		CreateUtils.text2File(addContent, file_save_path + "/web/" + model_name
				+ "/add.jsp");
		System.out.println(addName + "----" + "创建成功");
	}

	/**
	 * 创建 更新 页面
	 *
	 * @param model_name
	 *            model 需要被创建Model名称
	 */
	public static void createWebUpdate(String model_name,
			List<LinkedHashMap<String, Object>> zhujie) {
		String addName = "update.jsp";
		String path = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/updateMain.jsp";// 原始文件路径
		String pathPara = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/update.txt";// 原始文件路径 --

		String para = CreateUtils.getFileText(pathPara);
		String allpara = "";
		/*
		 * List<String> nameList=getName(bao); for (String p : nameList) {
		 * allpara+="\r\n "; allpara+=para.replace("XXYY", p).replace("VVVV",
		 * "${info."+p+"}"); }
		 */

		LinkedHashMap<String, Object> lmap = zhujie.get(0);
		for (Entry<String, Object> entry : lmap.entrySet()) {
			// System.out.println(entry.getKey() + ": " + entry.getValue());
			if (entry.getKey() == "id" || entry.getKey() == "createtime")
				continue;
			allpara += "\r\n ";
			allpara += para.replace("MMMM", entry.getValue() + "").replace(
					"XXYY", entry.getKey()).replace("VVVV",
					"${info." + entry.getKey() + "}");
		}

		String addContent = CreateUtils.getFileText(path).replace("XXYY",
				allpara).replace("MODEL_NAME", model_name).replace("muban",
				model_name);

		CreateUtils.createDir(file_save_path + "/web/" + model_name + "/");
		CreateUtils.text2File(addContent, file_save_path + "/web/" + model_name
				+ "/update.jsp");
		System.out.println(addName + "----" + "创建成功");
	}

	/**
	 * 创建 更新 页面
	 *
	 * @param model_name
	 *            model 需要被创建Model名称
	 */
	public static void createWebList(String model_name,
			List<LinkedHashMap<String, Object>> zhujie) {
		String addName = "list.jsp";
		String path = System.getProperty("user.dir")
				+ "/WebRoot/MUBAN/ListMain.jsp";// 原始文件路径

		List<String> nameList = getName(bao);
		String THTHTHTH = "\r\n";
		String TDTDTDTD = "\r\n";

		LinkedHashMap<String, Object> lmap = zhujie.get(0);
		for (Entry<String, Object> entry : lmap.entrySet()) {
			// System.out.println(entry.getKey() + ": " + entry.getValue());
			THTHTHTH += "                                                  <th>"
					+ entry.getValue() + "</th>\r\n";
			TDTDTDTD += "                                                	<td>${info."
					+ entry.getKey() + "}</td>\r\n";
		}

		String addContent = CreateUtils.getFileText(path).replace("kanjiamain",
				x_model_name).replace("TDTDTDTD", TDTDTDTD).replace("THTHTHTH",
				THTHTHTH).replace("diaoYanMain", x_model_name);

		CreateUtils.createDir(file_save_path + "/web/" + model_name + "/");
		CreateUtils.text2File(addContent, file_save_path + "/web/" + model_name
				+ "/list.jsp");
		System.out.println(addName + "----" + "创建成功");
	}

	/**
	 * 获取model 所有属性名称
	 *
	 * @return
	 */
	public static List<String> getName(String MODEL_NAME) {
		try {
			List<String> nameList = new ArrayList<String>();
			Object O = Class.forName(MODEL_NAME).newInstance();
			BeanInfo bi = Introspector.getBeanInfo(O.getClass());
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
			for (int i = 0; i < pds.length; i++) {
				String propName = pds[i].getName();
				nameList.add(propName);
				// System.out.println(propName +i);
			}
			return nameList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

 		String packagebase = "com.zx.base";
		String packageName = packagebase + ".model";

		List<Class<?>> classes;
		try {
			classes = ClassUtilF.getClasses(packageName);
			for (Class clas : classes) {
				// System.out.println(clas.getName());

				List<LinkedHashMap<String, Object>> zhujie =  initAnnoFieldDic(clas);

				String c = clas.getName();
				String d = c.split("[.]")[4];
				if (d.indexOf("SecondProxyMember") != -1) {
					// System.out.println(d);
					 CreateUtils.createFileALL(c, d, "D:\\JavaFile\\",
							zhujie, packagebase);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 创建目录
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {// 判断目录是否存在
			System.out.println("创建目录失败，目标目录已存在！");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
			destDirName = destDirName + File.separator;
		}
		if (dir.mkdirs()) {// 创建目标目录
			System.out.println("创建目录成功！" + destDirName);
			return true;
		} else {
			System.out.println("创建目录失败！");
			return false;
		}
	}
	/**
	 * 根据实体类名获取字段名称和中文名称
	 *            实体类名
	 * @return List<Map<String,Object>>
	 */
	public static List<LinkedHashMap<String, Object>> initAnnoFieldDic(
			@SuppressWarnings("rawtypes") Class clzz) {
		// 用于存储字段和中文值的集合
		List<LinkedHashMap<String, Object>> fieldList = new ArrayList<LinkedHashMap<String, Object>>();
		// 用于存储实体类字段(key)和中文名(value)
		LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();
		// 获取对象中所有的Field
		Field[] fields = clzz.getDeclaredFields();
		// 循环实体类字段集合,获取标注@ColumnConfig的字段
		for (Field field : fields) {
			if (field.isAnnotationPresent(Description.class)) {
				// 获取字段名
				String fieldNames = field.getName();
				// 获取字段注解
				Description columnConfig = field
						.getAnnotation(Description.class);
				// 判断是否已经获取过该code的字典数据 避免重复获取
				if (valueMap.get(columnConfig.headerName()) == null) {
					valueMap.put(fieldNames, columnConfig.headerName());
				}
			}
		}
		fieldList.add(valueMap);// 将LinkedHashMap放入List集合中
		return fieldList;
	}

}
