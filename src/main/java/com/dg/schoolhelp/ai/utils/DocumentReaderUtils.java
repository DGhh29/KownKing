package com.dg.schoolhelp.ai.utils;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Component
public class DocumentReaderUtils {

    /**
     * 从输入流中读取文件
     * @param file
     * @return
     */
    public List<Document> readMultipartFile(MultipartFile file) {
        try {
            Resource resource = new InputStreamResource(file.getInputStream());

            // 将文本内容划分成更小的块
            return new TokenTextSplitter().apply(new TikaDocumentReader(resource).read());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从文件系统中读取文件
     * @param filePath
     * @return
     */
    public List<Document> readFile(String filePath) {
        try {
            Resource resource = new FileSystemResource(filePath);

            // 将文本内容划分成更小的块
            return new TokenTextSplitter().apply(new TikaDocumentReader(resource).read());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从URL中读取文件
     * @param fileUrl
     * @return
     */
    public List<Document> readUrl(String fileUrl) {
        try {
            Resource resource = new UrlResource(fileUrl);

            // 将文本内容划分成更小的块
            return new TokenTextSplitter().apply(new TikaDocumentReader(resource).read());
        } catch (MalformedURLException e) {
            return null;
        }

    }



}
