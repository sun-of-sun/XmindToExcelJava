package entity;

/**
 * 节点对象
 * 
 * @author XU_SUN
 *
 */
public class NodeObj {

	// title的内容
	private String titleText;
	// topic的id
	private String id;
	// 父节点topic的id
	private String pId;

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

}
