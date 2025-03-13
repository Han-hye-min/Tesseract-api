package com.hhm.ocrproject.Controller;

import com.hhm.ocrproject.Service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final OcrService ocrService;

    // application.yml에서 설정한 저장 경로 주입
    @Value("${ocr.image.path}")
    private String imageSavePath;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 업로드되지 않았습니다."));
        }

        File savedFile = null;
        try {
            // 원본 파일명 유지 (확장자 포함)
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase() : ".png";

            // 지원하는 파일 형식인지 확인
            if (!fileExtension.equals(".png") && !fileExtension.equals(".jpg") && !fileExtension.equals(".jpeg")) {
                return ResponseEntity.badRequest().body(Map.of("error", "지원하지 않는 파일 형식입니다. PNG 또는 JPG만 업로드 가능."));
            }

            // 저장될 경로 지정 (C:/OCR/uploads/ + 원본 파일명)
            Files.createDirectories(Paths.get(imageSavePath)); // 경로 없으면 생성
            savedFile = new File(imageSavePath, originalFilename);
            file.transferTo(savedFile);

            System.out.println("✅ 저장된 파일 경로: " + savedFile.getAbsolutePath());
            System.out.println("✅ 파일 크기: " + file.getSize() + " bytes");

            long startTime = System.currentTimeMillis();
            String extractedText = ocrService.extractText(savedFile);
            long endTime = System.currentTimeMillis();
            System.out.println("✅ OCR 처리 시간: " + (endTime - startTime) + "ms");

            return ResponseEntity.ok(Map.of("text", extractedText));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 저장 중 오류 발생: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "OCR 처리 중 오류 발생: " + e.getMessage()));
        }
    }
}


//        } finally {
//            if (tempFile != null && tempFile.exists()) {
//                boolean deleted = tempFile.delete();
//                if (deleted) {
//                    System.out.println("임시 파일 삭제 완료: " + tempFile.getAbsolutePath());
//                } else {
//                    System.err.println("임시 파일 삭제 실패: " + tempFile.getAbsolutePath());
//                }
//            }

