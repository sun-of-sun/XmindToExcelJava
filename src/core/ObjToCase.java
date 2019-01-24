package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

/**
 * 实例化节点，获取所有叶子对象，并且转换成测试用例
 * 
 * @author XU_SUN
 */
public class ObjToCase {

	/**
	 * 遍历所需节点集合allNeedNodeList，实例化节点，包含title、id、pId三个属性
	 * 
	 * @param allNeedNodeList
	 * @author XU_SUN
	 */
	public static List<NodeObj> getNodeObj(List<Element> allNeedNodeList) {

		// 创建一个用于存放所有节点对象的集合
		List<NodeObj> allObjList = new ArrayList<>();
		// 遍历
		for (Element elm : allNeedNodeList) {

			NodeObj nodeObj = new NodeObj();

			// 获取节点自己的id，即title上层节点topic的id属性
			nodeObj.setId(elm.getParent().attributeValue("id"));
			// 获取节点title的内容，设置给节点对象的titleText属性
			nodeObj.setTitleText(elm.getText());

			// title标签上层的上层，topic → topics/sheet，如果为sheet，说明该节点为中心节点
			if ("sheet".equals(elm.getParent().getParent().getName())) {
				// 将中心节点的PID设置为centerTopicNoPID
				nodeObj.setpId("centerTopicNoPID");
			} else {
				// 其他节点title → topic → topics → children → topic → ...
				nodeObj.setpId(elm.getParent().getParent().getParent().getParent().attributeValue("id"));
			}
			// 把节点对象放入nodeObjList集合
			allObjList.add(nodeObj);
		}
		return allObjList;
	}

	/**
	 * 获取所有叶子对象
	 * 
	 * @param allNeedNodeList
	 * @author XU_SUN
	 */
	public static List<NodeObj> getLeafObjList(List<NodeObj> allObjList) {

		// 复制该集合
		List<NodeObj> copyObjList = new ArrayList<>(allObjList);
		// 叶子对象集合（即没有子对象）
		List<NodeObj> leafObjList = new ArrayList<>();

		// 遍历两个集合，找出叶子对象
		for (NodeObj copy : copyObjList) {
			// 假设copy是叶子对象，flag为true
			boolean flag = true;
			for (NodeObj all : allObjList) {
				// id = pid，说明copy不是叶子对象，flag=false
				if (copy.getId().equals(all.getpId())) {
					// 方案二：也可以给外层循环命名，当id = pid时，continue外层循环，对下一个copy对象进行遍历
					flag = false;
				}
			}
			// 当copy是叶子对象的时候，存入叶子对象集合
			if (flag) {
				// 没有子对象，存入叶子对象集合
				leafObjList.add(copy);
			}
		}
		// 打印到控制台验证leafObjList中是否均为叶子对象
//		for(int i = 0;i<leafObjList.size();i++) {
//			System.out.println(leafObjList.get(i).getTitleText());
//		}
		return leafObjList;
	}

	/**
	 * 递归方法，服务于toCase方法 通过叶子对象获取父对象，递归找到该用例下所有对象，加入到用例集合中，组成一条用例
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

	/**
	 * 遍历叶子对象集合与所有对象集合，组成测试用例
	 * 
	 * @param leafObjList
	 * @param allObjList
	 * @author XU_SUN
	 */
	public static void toCase(List<NodeObj> leafObjList, List<NodeObj> allObjList,List<List<String>> allCaseList) {
		
		// 遍历叶子对象集合
		for (NodeObj leafObj : leafObjList) {
			// 创建一个用于存放单条测试用例的集合（放在循环中，每个叶子对象创建一个caseList，从而实现单条用例）
			List<String> caseList = new ArrayList<>();

			// 将叶子节点的标题放入单条测试用例集合
			caseList.add(leafObj.getTitleText());
			// 调用getItParent获取其父对象，将所有父对象标题加入测试用例集合
			getItParent(leafObj, allObjList, caseList);
			// 此时用例集合中元素的顺序为： 叶子对象 → 父对象 → ... → 根对象，因此需要通过Collections.reverse()倒序排列
			Collections.reverse(caseList);
			// 将用例集合caseList输出到控制台
//			System.out.println(caseList);
			
			// 所有用例
			allCaseList.add(caseList);
		}

	}

}
