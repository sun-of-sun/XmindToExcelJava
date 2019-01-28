package core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import entry.NodeObj;

public class ReadXml {

	/**
	 * 解析xml文件
	 * 
	 * @author XU_SUN
	 */
	public static List<List<String>> readXml(String xmlPath) {

		// 所有用例集合
		List<List<String>> allCaseList = null;
		
		// 解析xml文件
		// 创建SAXReader的对象reader
		SAXReader reader = new SAXReader();
		try {
			// 通过reader对象的read方法加载xml文件，获取docuemnt对象
			Document document = reader.read(new File(xmlPath + "content.xml"));

			// 通过document对象获取根节点，即xml中的<xmap-content>
			Element rootNode = document.getRootElement();
			// 获取rootNode下的子节点，即xml中的<sheet>
			Element sheetNode = rootNode.element("sheet");

			// 获取sheetNode下的子节点，即xml中的<topic>，获取中心主题
			Element centerTopicNode = sheetNode.element("topic");
			// 删除<topic>下的子节点<extensions>
			centerTopicNode.remove(centerTopicNode.element("extensions"));

			// 创建一个集合，获取所有<title>节点，并转换为NodeObj
			List<NodeObj> allObjList = new ArrayList<>();
			// 调用方法getAllNeedNodes获取所有title节点
			getAllObj(centerTopicNode, allObjList);
			
			// 所有用例集合
			allCaseList = new ArrayList<>();

			// 调用getLeafObjList方法遍历集合，得到所有叶子对象
			// 通过叶子对象获取父对象，递归找到该用例下所有对象，组成一条用例
			// 将所有用例放入总用例集合
			getLeafObjList(allObjList,allCaseList);
			

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return allCaseList;
	}

	/**
	 * 获取所有title节点，并转换为NodeObj
	 * 递归获取所有title节点 xml节点关系为：topic → title/children →
	 * topics → topic → title/children → .....
	 * 
	 * @author XU_SUN
	 */
	public static void getAllObj(Element centerTopicNode, List<NodeObj> allObjList) {

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
				getAllObj(childElement, allObjList);
			} else {
				// 将Element对象转换成NodeObj对象
				NodeObj nodeObj = new NodeObj();

				// 获取节点自己的id，即title上层节点topic的id属性
				nodeObj.setId(childElement.getParent().attributeValue("id"));
				// 获取节点title的内容，设置给节点对象的titleText属性
				nodeObj.setTitleText(childElement.getText());

				// title标签上层的上层，topic → topics/sheet，如果为sheet，说明该节点为中心节点
				if ("sheet".equals(childElement.getParent().getParent().getName())) {
					// 将中心节点的PID设置为centerTopicNoPID
					nodeObj.setpId("centerTopicNoPID");
				} else {
					// 其他节点title → topic → topics → children → topic → ...
					nodeObj.setpId(childElement.getParent().getParent().getParent().getParent().attributeValue("id"));
				}
				// 把节点对象放入nodeObjList集合
				allObjList.add(nodeObj);
			}
		}
	} // getAllObj()

	/**
	 * 获取所有叶子对象
	 * 
	 * @param allNeedNodeList
	 * @author XU_SUN
	 */
	public static void getLeafObjList(List<NodeObj> allObjList, List<List<String>> allCaseList) {

		// 复制该集合
		List<NodeObj> copyObjList = new ArrayList<>(allObjList);

		// 遍历两个集合，找出叶子对象
		for (NodeObj leafObj : copyObjList) {
			// 假设leafObj是叶子对象，flag为true
			boolean flag = true;
			for (NodeObj all : allObjList) {
				// id = pid，说明不是叶子对象，flag=false
				if (leafObj.getId().equals(all.getpId())) {
					// 方案二：也可以给外层循环命名，当id = pid时，continue外层循环，对下一个copy对象进行遍历
					flag = false;
				}
			}
			// 当leafObj是叶子对象的时候，寻找它的父对象组成一条用例
			if (flag) {
				// 单条用例
				List<String> caseList = new ArrayList<>();
				// 先将叶子集合放入单条用例集合中
				caseList.add(leafObj.getTitleText());

				// 调用getItParent()，寻找它的父对象组成一条用例
				getItParent(leafObj, allObjList, caseList);

				// 此时用例集合中元素的顺序为： 叶子对象 → 父对象 → ... → 根对象，因此需要通过Collections.reverse()倒序排列
				Collections.reverse(caseList);
				// 加入总用例集合
				allCaseList.add(caseList);
			}
		}
	} // getLeafObjList()

	/**
	 * 递归， 通过叶子对象获取父对象，递归找到该用例下所有对象，加入到用例集合中，组成一条用例
	 * 
	 * @param leafObj    叶子对象
	 * @param allObjList 所有对象集合
	 * @param caseList   测试用例集合（已在toCase方法中创建）
	 * @author XU_SUN
	 */
	public static void getItParent(NodeObj leafObj, List<NodeObj> allObjList, List<String> caseList) {
		
		// 遍历所有对象集合
		for (NodeObj allObj : allObjList) {
			// 如果是leafObj的父对象，则加入caseList单条用例集合
			if (leafObj.getpId().equals(allObj.getId())) {
				caseList.add(allObj.getTitleText());
				// 递归，继续寻找父对象的父对象
				getItParent(allObj, allObjList, caseList);
			}
		}
	}
	
}
