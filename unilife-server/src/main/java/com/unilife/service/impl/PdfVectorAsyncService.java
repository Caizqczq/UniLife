package com.unilife.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.unilife.model.entity.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PDF向量化异步处理服务
 */
@Slf4j
@Service
public class PdfVectorAsyncService {

    @Autowired
    private VectorStore vectorStore;

    /**
     * 异步处理PDF文件的向量存储
     * @param fileBytes PDF文件的字节数组
     * @param originalFilename 原始文件名
     * @param resource 资源记录
     */
    @Async("pdfVectorTaskExecutor")
    public void processPdfVectorAsync(byte[] fileBytes, String originalFilename, Resource resource) {
        try {
            log.info("开始异步处理PDF向量化，资源ID: {}，文件: {}", resource.getId(), originalFilename);
            
            // 创建临时文件
            String tempFileName = "pdf_" + resource.getId() + "_" + System.currentTimeMillis() + ".pdf";
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile(tempFileName, ".pdf");
            
            try {
                // 将字节数组写入临时文件
                java.nio.file.Files.write(tempFile, fileBytes);
                
                // 创建PDF文档读取器
                PagePdfDocumentReader reader = new PagePdfDocumentReader(
                        tempFile.toUri().toString(),
                        PdfDocumentReaderConfig.builder()
                                .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                                .withPagesPerDocument(1)
                                .build()
                );
                
                // 读取文档
                List<Document> documents = reader.read();
                
                // 为每个文档添加资源ID元数据，方便后续删除
                for (Document document : documents) {
                    Map<String, Object> metadata = new HashMap<>(document.getMetadata());
                    metadata.put("resourceId", resource.getId().toString());
                    metadata.put("title", resource.getTitle());
                    
                    // 创建新的文档对象，包含资源ID元数据
                    Document enrichedDocument = new Document(document.getText(), metadata);
                    documents.set(documents.indexOf(document), enrichedDocument);
                }
                
                // 存储到向量数据库
                vectorStore.add(documents);
                
                log.info("PDF向量化处理完成，资源ID: {}，处理文档数: {}", 
                        resource.getId(), documents.size());
                
            } finally {
                // 清理临时文件
                try {
                    java.nio.file.Files.deleteIfExists(tempFile);
                } catch (Exception e) {
                    log.warn("清理临时文件失败: {}", tempFile, e);
                }
            }
            
        } catch (Exception e) {
            log.error("PDF异步向量化处理失败，资源ID: {}", resource.getId(), e);
        }
    }

    /**
     * 异步删除向量库中的相关文档
     * @param resourceId 资源ID
     * @param resourceTitle 资源标题
     */
    @Async("pdfVectorTaskExecutor")
    public void deleteVectorDocumentsAsync(Long resourceId, String resourceTitle) {
        try {
            log.info("开始删除资源ID: {} 的向量文档，标题: {}", resourceId, resourceTitle);
            
            // 使用Spring AI标准的过滤删除方法
            String filterExpression = "resourceId == '" + resourceId.toString() + "'";
            vectorStore.delete(filterExpression);
            
            log.info("✅ 资源ID: {} 的向量文档删除成功", resourceId);
            
        } catch (Exception e) {
            log.error("❌ 删除资源ID: {} 的向量文档失败", resourceId, e);
            // 根据业务需求决定是否重新抛出异常
            // throw new VectorOperationException("向量文档删除失败", e);
        }
    }
} 