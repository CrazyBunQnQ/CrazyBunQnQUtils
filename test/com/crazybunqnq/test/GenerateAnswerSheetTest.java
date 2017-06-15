package com.crazybunqnq.test;

import com.crazybunqnq.utils.GenerateAnswerSheet;

import java.io.File;

public class GenerateAnswerSheetTest {
    public void aa(int num) {
        GenerateAnswerSheet.answer = GenerateAnswerSheet.getAnswer("E:" + File.separator + "笔记", "基础测试-选择题答案.txt");
        GenerateAnswerSheet.myAnswer = GenerateAnswerSheet.getAnswer("答案.txt", num);
        GenerateAnswerSheet.gradeTest("判卷结果.md", num, false);
    }
}
