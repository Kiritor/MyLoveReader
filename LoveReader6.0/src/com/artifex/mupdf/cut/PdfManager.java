package com.artifex.mupdf.cut;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfManager {
    public static File Pdf(ArrayList<String> imageUrllist,String mOutputPdfFileName) {
        String TAG = "PdfManager";
        Document doc = new Document(PageSize.A4, 20, 20, 20, 20);
        try {
            PdfWriter
                    .getInstance(doc, new FileOutputStream(mOutputPdfFileName));
            doc.open();
            for (int i = 0; i < imageUrllist.size(); i++) {
                doc.newPage();
                doc.add(new Paragraph("简单使用iText"));
                Image png1 = Image.getInstance(imageUrllist.get(i));
                float heigth = png1.getHeight();
                float width = png1.getWidth();
                int percent = getPercent2(heigth, width);
                png1.setAlignment(Image.MIDDLE);
                png1.scalePercent(percent+3);// 表示是原来图像的比例;
                doc.add(png1);
            }
            doc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File mOutputPdfFile = new File(mOutputPdfFileName);
        if (!mOutputPdfFile.exists()) {
            Log.i(TAG, "生成的Pdf失败");
            mOutputPdfFile.deleteOnExit();
            return null;
        }
        return mOutputPdfFile;
    }

    /**
     * 第一种解决方案 在不改变图片形状的同时，判断，如果h>w，则按h压缩，否则在w>h或w=h的情况下，按宽度压缩
     *
     * @param h
     * @param w
     * @return
     */

    public static int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        if (h > w) {
            p2 = 297 / h * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
    }

    /**
     * 第二种解决方案，统一按照宽度压缩 这样来的效果是，所有图片的宽度是相等的，自我认为给客户的效果是最好的
     *
     * @param args
     */
    public static int getPercent2(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 530 / w * 100;
        p = Math.round(p2);
        return p;
    }
}