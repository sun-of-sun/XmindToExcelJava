package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 将用例写入Excel
 * 
 * @author XU_SUN
 *
 */
public class WriteToExcel {

	/**
	 * 将用例写入Excel
	 * @return
	 */
	public static HSSFWorkbook writeToExcel(List<List<String>> allCaseList) {

		// 第一步：创建Excel工作簿对象
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 第二步：创建工作表
		HSSFSheet sheet = workbook.createSheet("测试用例");
		// 第三步：在sheet中添加表头第0行
		HSSFRow row = sheet.createRow(0);

		// 第四步:声明列对象
		HSSFCell cell1 = row.createCell(0);
		HSSFCell cell2 = row.createCell(1);
		HSSFCell cell3 = row.createCell(2);
		HSSFCell cell4 = row.createCell(3);
		HSSFCell cell5 = row.createCell(4);
		HSSFCell cell6 = row.createCell(5);
		HSSFCell cell7 = row.createCell(6);
		HSSFCell cell8 = row.createCell(7);

		cell1.setCellValue("NO.");
		cell2.setCellValue("项目");
		cell3.setCellValue("模块");
		cell4.setCellValue("功能");
		cell5.setCellValue("子功能");
		cell6.setCellValue("操作步骤");
		cell7.setCellValue("预期结果");
		cell8.setCellValue("实际结果");

		cell1.setCellStyle(getHeadStyle(workbook));
		cell2.setCellStyle(getHeadStyle(workbook));
		cell3.setCellStyle(getHeadStyle(workbook));
		cell4.setCellStyle(getHeadStyle(workbook));
		cell5.setCellStyle(getHeadStyle(workbook));
		cell6.setCellStyle(getHeadStyle(workbook));
		cell7.setCellStyle(getHeadStyle(workbook));
		cell8.setCellStyle(getHeadStyle(workbook));

		sheet.setColumnWidth(0, 5 * 256);
		sheet.setColumnWidth(1, 12 * 256);
		sheet.setColumnWidth(2, 12 * 256);
		sheet.setColumnWidth(3, 12 * 256);
		sheet.setColumnWidth(4, 16 * 256);
		sheet.setColumnWidth(5, 45 * 256);
		sheet.setColumnWidth(6, 45 * 256);
		sheet.setColumnWidth(7, 20 * 256);
		
		// 遍历所有case集合
		for (int i = 0; i < allCaseList.size(); i++) {
			// 创建用例内容的行，表头为第0行，因此内容从i+1开始
			row = sheet.createRow(i + 1);
			// 第一列为序号
			row.createCell(0).setCellValue(i);
			// 取出单条用例
			List<String> caseList = allCaseList.get(i);
			
			// 操作步骤
			String operationStr = "";
			int operaNum = 1;
			// 结果
			String resultStr = " ";
			int resNum = 1;
			
			// 取出每一个用例小步骤
			for (int j = 0; j < caseList.size(); j++) {
				

				// 前4个用例元素：0项目，1模块，2功能，3子功能
				if (j < 4) {
					// 序号为第0列，因此用例从j+1列开始，按照顺序把前四个用例写入
					row.createCell(j + 1).setCellValue(caseList.get(j));
				}
				// 0项目，1模块，2功能，3子功能，4操作步骤，5预期结果（定位元素），6实际结果
				// 获取定位元素 预期结果 的下标
				int expect = caseList.indexOf("预期结果");
				// 操作步骤
				if (j >= 4 && j < expect) {
					operationStr = operationStr + operaNum +"." + caseList.get(j) + " \n";
					operaNum++;
				}
				// 结果
				if(j > expect) {
					resultStr = resultStr + resNum +"." + caseList.get(j) + " \n";
					resNum++;
				}

				// 操作步骤写入Excel第6列
				row.createCell(5).setCellValue(operationStr.toString());
				// 结果写入Excel第7列
				row.createCell(6).setCellValue(resultStr.toString());
			}

		}
		
		FileOutputStream out;
		try {
			// 生成文件路径 例如"D:\\测试用例.xls"
			String filePath = "D:\\";
			// 文件名
			String fileName = allCaseList.get(0).get(0) + "测试用例.xls";
			
			// 生成excel文件
			out = new FileOutputStream(filePath + fileName);
			workbook.write(out);
			
			System.out.println("用例转换成功！路径：" + filePath + fileName);
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return workbook;
	}

	/**
	 * 设置表头格式
	 * 颜色可参照：https://blog.csdn.net/w405722907/article/details/76915903
	 * 
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getHeadStyle(HSSFWorkbook workbook) {
		// 设置样式
		HSSFCellStyle style = workbook.createCellStyle();

		// 水平居中
		style.setAlignment(HorizontalAlignment.CENTER);
		// 垂直居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		// 设置标题背景色
		style.setFillForegroundColor(IndexedColors.LIME.getIndex());
		// 设置自动换行; 
		style.setWrapText(true);

		// 自定义一个原谅色
//        HSSFPalette customPalette = workbook.getCustomPalette();
//        HSSFColor yuanLiangColor = customPalette.addColor((byte) 146, (byte) 208, (byte) 80);

		return style;
	}

	/**
	 * 设置单元格格式
	 * 颜色可参照：https://blog.csdn.net/w405722907/article/details/76915903
	 * 
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
		// 设置样式
		HSSFCellStyle style = workbook.createCellStyle();

		// 垂直居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		// 设置自动换行; 
		style.setWrapText(true);

		return style;
	}
	
}
