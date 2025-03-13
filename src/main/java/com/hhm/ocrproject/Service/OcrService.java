package com.hhm.ocrproject.Service;


import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class OcrService {

    @Value("${ocr.image.path}") // application.properties에서 경로 설정 가능
    private String imageSavePath;

    private final Environment env;


    public OcrService(Environment env) {
        this.env = env;
    }

    public String extractText(File imageFile) {
        ITesseract tesseract = new Tesseract();

        // ✅ Tesseract 실행 경로 지정
        tesseract.setDatapath("c:\\OCR\\temp\\"); // <-- 여기에 tessdata 폴더 경로 설정

        // ✅ 사용할 언어 설정
        tesseract.setLanguage("kor+eng");

        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "OCR 실패: " + e.getMessage();
        }
    }

    // JPG, PNG가 아닌 경우 PNG로 변환
    private File convertToSupportedFormat(File inputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("이미지 읽기 실패: " + inputFile.getName());
        }

        // 저장할 파일 이름 설정 (변환 후 저장)
        File outputFile = new File(imageSavePath, inputFile.getName() + ".png");
        ImageIO.write(image, "png", outputFile);
        return outputFile;
    }
}
