package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author XU_SUN
 * @version 1.1.0
 */
public class ReadXml {

	public static void main(String[] args) {
		String path = "src/xmind/";
		readFile(path);
	}

	/**
	 * 获取文件夹下边的所有文件
	 * @param path
	 */
	public static void readFile(String path) {
		
		File file = new File(path);
		// 获取所有目录下的文件、文件夹
		File[] files = file.listFiles();
		// 如果文件为空则停止
		if(null == files || files.length == 0) {
			System.out.println("未找到文件");
			return;
		}
		
		// 遍历获取路径下所有文件、文件夹
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				readFile(files[i].getAbsolutePath());
			} else {
				String oldPath = files[i].getAbsolutePath();
				// 获取文件类型
				String prefix = oldPath.substring(oldPath.lastIndexOf(".") + 1);
				// 需要替换的文件类型
				String newPath = oldPath.replace(".xmind", ".zip");
				// 指定复制替换的文件类型
				if (prefix.equals("xmind")) {
					copy(oldPath, newPath);
				}
//				System.out.println(files[i].getAbsolutePath());
				
				// 解压
				unZipGetXml(path,newPath);
			}
		}
	}

	/**
     * 复制文件
     *
     * @param oldPath 需要复制的文件路径
     * @param newPath 复制后的文件路劲
     */
    public static void copy(String oldPath, String newPath) {
        try {
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
            	// 创建I/O流，读取源文件信息，写入新文件
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fileOfutputStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                // 读取源文件信息，写入新文件
                while ((length = inStream.read(buffer)) != -1) {
                    fileOfutputStream.write(buffer, 0, length);
                }
                // 关闭资源
                inStream.close();
                fileOfutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 解压zip
     * @param path main方法中的path，存放xmind文件的路径
     * @param newPath
     */
    public static void unZipGetXml(String path,String newPath) {
    	
    	// 获取zip文件的文件名
    	String zipFileName = newPath.substring(newPath.lastIndexOf("\\")+1);
    	File zipFile = new File(path,zipFileName);
    	
    	// 单个Xmind解压路径
//    	String ownPath = zipFileName.substring(0,zipFileName.indexOf("."));
//    	String destDirPath = "src/xmlFile/" + ownPath + "/";
    	
    	// 解压的目标文件夹
    	String destDirPath = "src/xmlFile/";
    	UnZipUtil.unZip(zipFile,destDirPath);
	    
        readXml();
        
        // 删除zip文件及解压文件
        UnZipUtil.delAllFiles(new File(destDirPath),null,destDirPath);
        zipFile.delete();
    }
    
	
	/**
	 * 解析xml文件
	 * 
	 * @author XU_SUN
	 */
	public static void readXml() {
		
		// 解析xml文件
		// 创建SAXReader的对象reader
		SAXReader reader = new SAXReader();
		try {
			// 通过reader对象的read方法加载xml文件，获取docuemnt对象
			Document document = reader.read(new File("src/xmlFile/content.xml"));

			// 通过document对象获取根节点，即xml中的<xmap-content>
			Element rootNode = document.getRootElement();
			// 获取rootNode下的子节点，即xml中的<sheet>
			Element sheetNode = rootNode.element("sheet");

			// 获取sheetNode下的子节点，即xml中的<topic>，获取中心主题
			Element centerTopicNode = sheetNode.element("topic");
			// 删除<topic>下的子节点<extensions>
			centerTopicNode.remove(centerTopicNode.element("extensions"));
			// ★★★【取得中心节点标题】★★★
			// String centerTopicTitleText = centerTopicNode.element("title").getStringValue();
			// System.out.println("获取到中心节点标题：" + centerTopicTitleText);

			// 创建一个集合，用于获取所有<title>节点
			List<Element> allNeedNodeList = new ArrayList<Element>();
			// 调用方法getAllNeedNodes获取所有title节点
			getAllNeedNodes(centerTopicNode, allNeedNodeList);
			
			// 打印到控制台验证一下取到的子节点是否正常
//			for (int i = 0; i < allNeedNodeList.size(); i++) {
//				System.out.println(allNeedNodeList.get(i).getText());
//			}
			
			// 调用getNodeObj方法遍历集合，得到所有节点对象集合
			List<NodeObj> allObjList = ObjToCase.getNodeObj(allNeedNodeList);
			// 调用getLeafObjList方法遍历集合，得到所有叶子对象
			List<NodeObj> leafObjList = ObjToCase.getLeafObjList(allObjList);
			
			// 所有用例集合
			List<List<String>> allCaseList = new ArrayList<>();
			// 调用toCase方法，获得所有用例
			ObjToCase.toCase(leafObjList, allObjList,allCaseList);
			
			//通过调用writeToExcel方法写入Excel
			WriteToExcel.writeToExcel(allCaseList);
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 递归获取所有title节点
	 * xml节点关系为：topic → title/children → topics → topic → title/children  → .....
	 * 
	 * @author XU_SUN
	 */
	public static void getAllNeedNodes(Element centerTopicNode, List<Element> allNeedNodeList) {

		// 通过中心节点centerTopicNode的elementIterator方法获取迭代器
		Iterator it = centerTopicNode.elementIterator();

		while (it.hasNext()) {
			// 获取下一级节点：分别为中心主题的title和children
			Element childElement = (Element) it.next();

			// 获取title/children的下一级节点放入集合，title下级为空，children下级为topics
			List<Element> childEltList = childElement.elements();

			// 如果集合不为空（即仍有下一级节点），则继续查找，递归
			if (childEltList.size() > 0) {
				// 继续查找children/topics的下级节点
				getAllNeedNodes(childElement, allNeedNodeList);
			} else {
				// 将title节点放入allNeedNodes集合
				allNeedNodeList.add(childElement);
			}
			// 方案二：另一种解决思路
			// 通过selectNodes方法获取子节点，并且将Node类型强转为Element类型
			// for (Node n : childElement.selectNodes("*")) {
			// Element e = (Element) n;
			// }
		}
	}
	

}
