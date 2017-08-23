package com.mkyong.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipal;

@Controller
public class UploadController {

	private static final Logger logger = Logger.getLogger(UploadController.class);
	
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "C://Users//temp//";

    /**
     * 
     * @return
     */
    @GetMapping("/")
    public String index() {
        return "upload";
    }

    /**
     * 
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            
            //Retrieving File attributes and write it file system
            BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            UserPrincipal principal = Files.getOwner(path, LinkOption.NOFOLLOW_LINKS);
            
            StringBuilder builder = new StringBuilder();
            builder.append("OwnerName: " + principal.getName());
            builder.append("\r\nCreationTime: " + fileAttributes.creationTime());
            builder.append("\r\nLastAccessTime: " + fileAttributes.lastAccessTime());
            builder.append("\r\nLastModifiedTime: " + fileAttributes.lastModifiedTime());
            builder.append("\r\nFile Size : " + fileAttributes.size());
            builder.append("\r\nFile ContentType : " + file.getContentType());
            
            Path metadataFilePath = Paths.get(UPLOADED_FOLDER + "Metadata_"+file.getName()+".txt");
            Files.write(metadataFilePath, builder.toString().getBytes());
            
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
        	logger.error(e);
        }

        return "redirect:/uploadStatus";
    }

    /**
     * 
     * @return
     */
    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}