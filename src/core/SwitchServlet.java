package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import util.UnZipUtil;

/**
 * 
 * @author XU_SUN
 *
 */
@WebServlet("/upload.do")
public class SwitchServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 拿到全局对象
		ServletContext sc = this.getServletContext();
		// 取得存放xmind文件的文件夹在服务器上的绝对路径/获取web根目录下放置缓存文件的文件夹“temp”的物理路径
		String xmindFolderPath = sc.getRealPath("Xmind");

		// 创建文件项工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 创建文件上传核心组件
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		// 解决中文乱码问题
		servletFileUpload.setHeaderEncoding("UTF-8");

		String fileName = null;
		try {
			// 解析Multipart
			List<FileItem> items = servletFileUpload.parseRequest(request);

			if (items.size() == 0) {

			}

			for (FileItem item : items) {

				// 获取文件名称和后缀
				fileName = item.getName();
				// 输入流
				InputStream inputStream = item.getInputStream();

				// 获取文件类型
				String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
				// 指定复制替换的文件类型
				if (prefix.equals("xmind")) {
					// 需要替换的文件类型
					String zipFileName = fileName.replace(".xmind", ".zip");
					// 将xmind文件转存为zip文件
					File srcZipFile = new File(xmindFolderPath, zipFileName);
					// 输出流
					FileOutputStream fileOutputStream = new FileOutputStream(srcZipFile);

					// 完成文件复制
					byte[] bytes = new byte[1024];
					int len = -1;
					while ((len = inputStream.read(bytes)) != -1) {
						fileOutputStream.write(bytes, 0, len);
					}
					// 关闭流
					fileOutputStream.close();
					inputStream.close();

					// 调用unZip()进行解压
					// 解压文件路径为tomact/webapps/xmindToExcelJava/webapps/Xmind/xmlFile/
					String xmlPath = xmindFolderPath + "\\xmlFile/";
					UnZipUtil.unZip(srcZipFile, xmlPath);
					
					// 读取Xml文件，获取所有用例集合
					List<List<String>> allCaseList = ReadXml.readXml(xmlPath);
					
					// 通过调用writeToExcel方法写入Excel
					WriteToExcel.writeToExcel(allCaseList);

					// 删除zip文件及解压文件
					UnZipUtil.delAllFiles(new File(xmlPath), null, xmlPath);
					srcZipFile.delete();

				}

			}

		} catch (FileUploadException e) {
			e.printStackTrace();
		} // try/catch

	}// doPost

}
