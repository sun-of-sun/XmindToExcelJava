package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Xmind {
	
	/**
	 * 获取文件夹下边的所有文件
	 * @param path
	 */
	public static void readFile(String path) {
		
		File file = new File(path);
		// 获取所有目录下的文件、文件夹
		File[] files = file.listFiles();
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
				if (prefix.equals("xminds")) {
					copy(oldPath, newPath);
				}
				System.out.println(files[i].getAbsolutePath());
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
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fileOfutputStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inStream.read(buffer)) != -1) {
                    fileOfutputStream.write(buffer, 0, length);
                }
                inStream.close();
                fileOfutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
