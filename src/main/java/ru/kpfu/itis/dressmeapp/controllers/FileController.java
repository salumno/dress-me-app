package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.kpfu.itis.dressmeapp.util.FileStorageUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
@Controller
public class FileController {
    private FileStorageUtil fileStorageUtil;

    public FileController(FileStorageUtil fileStorageUtil) {
        this.fileStorageUtil = fileStorageUtil;
    }

    @RequestMapping(value = "/file/{file-info-id}", method = RequestMethod.GET)
    public void getFile(@PathVariable("file-info-id")Long fileInfoId, HttpServletResponse response) {
        fileStorageUtil.writeFileToResponse(fileInfoId, response);
    }
}
