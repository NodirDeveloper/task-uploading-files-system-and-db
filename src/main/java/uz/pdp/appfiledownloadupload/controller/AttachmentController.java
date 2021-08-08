package uz.pdp.appfiledownloadupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pdp.appfiledownloadupload.entitiy.Attachment;
import uz.pdp.appfiledownloadupload.entitiy.AttachmentContent;
import uz.pdp.appfiledownloadupload.repository.AttachmentContentRepository;
import uz.pdp.appfiledownloadupload.repository.AttachmentRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/attachment")
public class AttachmentController {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    //SYSTEMDA SAQLANADIGAN PAPKA OCHISH
    private static final String uploadDirectory="yuklanganlar";

    //DATABASEGA FAYL YUKLASH
    @PostMapping("/uploadDb")
    private String uploadFileToDb(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile multipartFile = request.getFile(fileNames.next());
        if (multipartFile != null) {
            //fayl haqida malumot olish
            String originalFilename = multipartFile.getOriginalFilename();
            long size = multipartFile.getSize();
            String contentType = multipartFile.getContentType();

            Attachment attachment = new Attachment();
            attachment.setFileOriginalName(originalFilename);
            attachment.setContentType(contentType);
            attachment.setSize(size);

            Attachment savedAttachment = attachmentRepository.save(attachment);

            //faylni content (byte[] arrayini saqllaymiz

            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setAsosiyContent(multipartFile.getBytes());
            attachmentContent.setAttachment(savedAttachment);
            attachmentContentRepository.save(attachmentContent);
            return "fayl saqlandi.Id si + " + savedAttachment.getId();


        }
        return "xatolik";

    }



    //SYSTEMGA FAYL YUKLASH
    @PostMapping("/uploadSystem")
    public String uploadFileToSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if (file!=null) {
            String originalFilename = file.getOriginalFilename();

            Attachment attachment = new Attachment();
            attachment.setFileOriginalName(originalFilename);
            attachment.setSize(file.getSize());
            attachment.setContentType(file.getContentType());
            //uyga.borish.jpg
            String[] split = originalFilename.split("\\.");

            String name = UUID.randomUUID().toString()+"."+split[split.length-1];

            attachment.setName(name);
            attachmentRepository.save(attachment);
            Path path=Paths.get(uploadDirectory+"/"+name);
            Files.copy(file.getInputStream(),path);

            return "saqlandi id= "+attachment.getId();
        }
        return "fayl saqlanmadi";
    }


    //DATABASEDAN FAYLNI ID ORQALI OLISH
    @GetMapping("/getFile/{id}")
    public void getFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()) {
            Attachment attachment = optionalAttachment.get();
            Optional<AttachmentContent> contentOptional = attachmentContentRepository.findByAttachmentId(id);
            if (contentOptional.isPresent()) {
                AttachmentContent attachmentContent = contentOptional.get();
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + attachment.getFileOriginalName() + "\"");
                response.setContentType(attachment.getContentType());
                FileCopyUtils.copy(attachmentContent.getAsosiyContent(), response.getOutputStream());
            }
        }

    }


    //systemdan oqish
    @GetMapping("/getsystem/{id}")
    public void getFileSystem(@PathVariable Integer id,HttpServletResponse response) throws IOException  {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + attachment.getFileOriginalName() + "\"");
            response.setContentType(attachment.getContentType());

            FileInputStream fileInputStream=new FileInputStream(uploadDirectory+"/"+attachment.getName());
            FileCopyUtils.copy(fileInputStream,response.getOutputStream());

        }


//
//        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
//        if (optionalAttachment.isPresent()) {
//            Attachment attachment=new Attachment();
//            response.setHeader("Content-Disposition",
//                    "attachment; filename=\""+attachment.getFileOriginalName()+"\"");
//
//            response.setContentType((attachment.getContentType()));
//
//            FileInputStream fileInputStream=new FileInputStream(uploadDirectory+"/"+attachment.getName());
//            FileCopyUtils.copy(fileInputStream,response.getOutputStream());
//
//        }
    }
}
