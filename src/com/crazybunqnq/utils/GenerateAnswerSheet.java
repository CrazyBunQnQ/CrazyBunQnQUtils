package com.crazybunqnq.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.filechooser.FileSystemView;

/**
 * 生成选择题成绩表，包括你的选择以及标准答案
 * @author Administrator
 *
 */
public class GenerateAnswerSheet {
	public static HashMap<Integer, String> answer;
	public static ArrayList<String> myAnswer;

	/**
	 * 在系统桌面生成选择题答题卡,每行 10 个空
	 * 
	 * @param fileName
	 *            文件名称，包括后缀
	 * @param num
	 *            题目总数
	 * @param isAppend
	 *            是否追加，false 则覆盖原文件
	 */
	public static void gradeTest(String fileName, int num, boolean isAppend) {
		File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
		String desktopPath = desktopDir.getAbsolutePath();
		gradeTest(desktopPath, fileName, 10, num, isAppend);
	}

	/**
	 * 在系统桌面生成选择题答题卡
	 * 
	 * @param fileName
	 *            文件名称，包括后缀
	 * @param colPerRow
	 *            每行的列数
	 * @param num
	 *            题目总数
	 * @param isAppend
	 *            是否追加，false 则覆盖原文件
	 */
	public static void gradeTest(String fileName, int colPerRow, int num, boolean isAppend) {
		File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
		String desktopPath = desktopDir.getAbsolutePath();
		gradeTest(desktopPath, fileName, colPerRow, num, isAppend);
	}

	/**
	 * 在指定目录中生成选择题答题卡,每行 10 个空
	 * 
	 * @param filePath
	 * @param fileName
	 * @param num
	 * @param isAppend
	 */
	public static void gradeTest(String filePath, String fileName, int num, boolean isAppend) {
		gradeTest(filePath, fileName, 10, num, isAppend);
	}

	/**
	 * 在指定目录中生成选择题答题卡
	 * 
	 * @param filePath
	 *            生成的文件所在目录
	 * @param fileName
	 *            文件名称，包括后缀
	 * @param colPerRow
	 *            每行的列数
	 * @param num
	 *            题目总数
	 * @param isAppend
	 *            是否追加，false 则覆盖原文件
	 */
	public static void gradeTest(String filePath, String fileName, int colPerRow, int num, boolean isAppend) {
		File file = new File(filePath + File.separator + fileName);
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		String data;
		// new BufferedOutputStream();
		try {
			if (!file.exists()) {
				System.out.println("文件不存在，创建文件... ");
				if (file.createNewFile()) {
					System.out.println("文件创建成功！");
				} else {
					System.out.println("文件创建失败！程序停止执行");
					return;
				}
			}
			System.out.println("开始生成测试结果...");
			data = markdownTable(num, colPerRow);
			fos = new FileOutputStream(file, isAppend);
			bos = new BufferedOutputStream(fos);
			byte[] b = data.getBytes("UTF-8");
			bos.write(b);
			bos.flush();
			System.out.println("写入完毕...");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (bos != null) {
					bos.close();
				}
				System.out.println("已生成选测试结果！");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("貌似创建失败了");
			}
		}
	}

	/**
	 * 创建 MarkDown 中的表格
	 * 
	 * @param num
	 *            格子数量
	 * @param colPerRow
	 *            每行的格子数量
	 * @return 字符串类型
	 */
	@SuppressWarnings("unused")
	private static String markdownTable(int num, int colPerRow) {
		int row = 1, col = 1, rightNum = num;
		String[] str = new String[colPerRow];
		StringBuilder all = new StringBuilder();
		StringBuilder oneLine = new StringBuilder();
		for (int i = 1; i < num; i++) {
			if (col == 1) {
				all.append("|");
				oneLine.append("|");
			}
			// 生成题号
			if (i <= colPerRow) {
				all.append(i + "|");// 首航不加粗
			} else {
				all.append("**" + i + "**|");// 加粗
			}
			String mySelect = myAnswer.get(i).toUpperCase();
			String rightSelect = answer.get(i);
			if (mySelect.equals(rightSelect)) {
				oneLine.append(mySelect + "|");
			} else {
				oneLine.append("~~" + mySelect + "~~ <font color=\"red\">**" + rightSelect + "**</font>|");
				rightNum--;
			}
			col++;// 每输入一列 col 加 1
			// 生成答题区
			if (i % colPerRow == 0) {
				if (i == colPerRow) {// 在第二行添加表格标识
					all.append("\n|");
					for (int j = 0; j < colPerRow; j++) {
						all.append(":--:|");
					}
				}
				all.append("\n|");
				all.append(oneLine.toString());
				all.append("\n");
				row++;// 当前行数 + 1
				col = 1;// 当前列初始化
				oneLine.delete(0, oneLine.length() - 1);
			}
		}
		// 添加最后不构成一整行的 空格子
		int n = num % colPerRow;
		if (n > 0) {
			all.append("\n|");
			all.append(oneLine.toString());
		}
		all = new StringBuilder("## " + printResult(rightNum, num) + "\n\n").append(all.toString());
		// all.append("\n\n>" + printResult(rightNum, num));
		return all.toString();
	}

	public static HashMap<Integer, String> getAnswer(String filePath, String fileName) {
		String fullPath = filePath + File.separator + fileName;
		File file = new File(fullPath);
		if (!file.exists()) {
			System.out.println("文件不存在！");
			return null;
		}
		FileReader fr = null;
		BufferedReader br = null;
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			boolean hasNextLine = true;
			do {
				String str;
				str = br.readLine();
				if (str == null) {
					hasNextLine = false;
				} else if (str.contains(".")) {
					String[] strArr = str.split("\\.");
					hm.put(Integer.parseInt(strArr[0]), strArr[1]);
				}
			} while (hasNextLine);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hm;
	}

	public static ArrayList<String> getAnswer(String fileName, int num) {
		// FileDialog fd = new FileDialog(, "请选择你的答案：");
		String fullPath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + File.separator
				+ fileName;
		File file = new File(fullPath);
		if (!file.exists()) {
			System.out.println("文件不存在！");
			return null;
		}
		FileReader fr = null;
		BufferedReader br = null;
		ArrayList<String> al = new ArrayList<String>();
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			boolean hasNextLine = true;
			for (int i = 0; i <= num; i++) {
				if (hasNextLine) {
					String str;
					str = br.readLine();
					if (str == null) {
						hasNextLine = false;
						al.add("<font color=\"red\">不会</font>");
					} else if (str.contains(".")) {
						String[] strArr = str.split("\\.");
						al.add(strArr.length < 2 ? "<font color=\"red\">不会</font>" : strArr[1]);
					} else if (str.length() == 0) {
						al.add("<font color=\"red\">不会</font>");
					} else {
						al.add(str);
					}
				} else {
					al.add("<font color=\"red\">不会</font>");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return al;
	}

	private static String printResult(int rightNum, int num) {
		float r = (float) rightNum / num;
		System.out.println(r);
		if (r < 0.25f) {
			return "这...你的正确率是 " + r * 100 + "% ，你真的打算从事这个行业吗？！";
		} else if (r < 0.60f) {
			return "太可惜了，你的正确率是 " + r * 100 + "% ，要努力呀！";
		} else if (r < 0.75f) {
			return "一般般吧，你的正确率是 " + r * 100 + "% ，再加把劲！";
		} else if (r < 0.90f) {
			return "不错嘛！你的正确率是 " + r * 100 + "% ，要做的更好哟";
		}
		return "太棒了！你的正确率是 " + r * 100 + "% ，不要骄傲哟！";
	}
}
