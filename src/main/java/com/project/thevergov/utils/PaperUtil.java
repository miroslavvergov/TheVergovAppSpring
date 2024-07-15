package com.project.thevergov.utils;

import com.project.thevergov.dto.Paper;
import com.project.thevergov.dto.User;
import com.project.thevergov.entity.PaperEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class PaperUtil {


    public static String getPaperUri(String filename) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/documents/%s", filename))
                .toUriString();
    }

    public static String setIcon(String fileExtension) {
        String extension = StringUtils.trim(fileExtension);

        if (extension.equalsIgnoreCase("DOC") || extension.equalsIgnoreCase("DOCX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
        if (extension.equalsIgnoreCase("XLS") || extension.equalsIgnoreCase("XLSX")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/excel-icon.svg";
        }
        if (extension.equalsIgnoreCase("PDF")) {
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/pdf-icon.svg";
        } else {
            return "https://html.stream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
    }

    public static Paper fromPaperEntity(PaperEntity paperEntity, User createdBy, User updatedBy) {
        var paper = new Paper();
        BeanUtils.copyProperties(paperEntity, paper);
        paper.setOwnerName(createdBy.getFirstName() + " " + createdBy.getLastName());
        paper.setOwnerEmail(createdBy.getEmail());
        paper.setOwnerLastLogin(createdBy.getLastLogin());
        paper.setUpdaterName(updatedBy.getFirstName() + " " + updatedBy.getLastName());
        return paper;
    }
}
