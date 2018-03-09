
package com.itt.kmt.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itt.kmt.models.Attachment;
import com.itt.kmt.response.models.AttachmentResponseMsg;
import com.itt.kmt.response.models.LoginResponseMsg;
import com.itt.kmt.response.models.ResponseMsg;
import com.itt.kmt.response.models.LoginResponseMsg.StatusMsg;
import com.itt.kmt.services.AttachmentService;
import com.itt.utility.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for exposing REST APis for Attachments.
 * 
 * @author Manoj Mewara
 */

@RestController
/** The Constant log. */
@PropertySource("classpath:application.properties")
@RequestMapping(value = "/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    /** The uploaded folder. */
    @Value("${attachments.path}")
    private String UPLOADED_FOLDER;

    /**
     * Upload file.
     *
     * @param uploadfile the uploadfile
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.POST)
    public AttachmentResponseMsg uploadFile(@RequestParam("file")
    final MultipartFile uploadfile, @RequestParam("fileName") String fileName,
        @RequestParam("fileType") String fileType) {

        AttachmentResponseMsg attachmentResponseMsg = new AttachmentResponseMsg();
        if (uploadfile.isEmpty()) {
            attachmentResponseMsg.setStatus(Boolean.FALSE);
            attachmentResponseMsg.setMessage("please select a file!");
        }

        try {

            ResponseMsg responseMsg = attachmentService.storeUploadedFiles(uploadfile);
            if (responseMsg.getStatus()) {
                Attachment attachment = new Attachment();
                attachment.setFileName(responseMsg.getMessage());
                attachment.setFileSize(uploadfile.getSize());
                attachment.setFileType(fileType);
                // Saving attachment in database
                attachment = attachmentService.saveAttachment(attachment);
                attachment.setUrl("api/attachments/" + attachment.getId());

                attachmentResponseMsg.setStatus(Boolean.TRUE);

                attachmentResponseMsg.setAttachement(attachment);

            } else {

                attachmentResponseMsg.setStatus(Boolean.FALSE);
                attachmentResponseMsg.setMessage(responseMsg.getMessage());
            }

        } catch (IOException e) {
            attachmentResponseMsg.setStatus(Boolean.FALSE);
            attachmentResponseMsg.setMessage(e.getMessage());
        }

        return attachmentResponseMsg;
    }

    /**
     * Download.
     *
     * @param fileName the file name
     * @return the response entity
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable("id")
    final String id)
        throws IOException {

        Attachment attachment = attachmentService.getAttachmentByID(id);
        File file = new File(UPLOADED_FOLDER + attachment.getFileName());
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                             .headers(headers)
                             .contentLength(file.length())
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .header("Content-Disposition", "attachment; filename=" + file.getName())
                             .body(resource);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ModelMap deleteAttachmentById(@PathVariable(value = "id")
    final String id, final HttpServletRequest httpServletRequest) {

        if (attachmentService.delete(id)) {
            return new ModelMap().addAttribute(
                "success", new ResponseMsg(Boolean.TRUE, Constants.ATTACHMENT_DELETED_MESSAGE));
        } else {
            return new ModelMap().addAttribute(
                "success", new ResponseMsg(Boolean.FALSE, "some problem occured while deleting attachment"));
        }
    }
}
