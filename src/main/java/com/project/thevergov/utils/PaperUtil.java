package com.project.thevergov.utils;

import com.project.thevergov.dto.Paper;
import com.project.thevergov.dto.User;
import com.project.thevergov.entity.PaperEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Utility class for handling operations related to paper documents.
 * <p>
 * Provides methods for generating URIs for paper documents, determining the appropriate icon based on
 * the file extension, and mapping between PaperEntity and Paper DTO objects.
 */
public class PaperUtil {

    /**
     * Generates a URI string for accessing a paper document.
     *
     * @param filename The name of the file for which the URI is to be generated.
     * @return A URI string that points to the location of the document.
     */
    public static String getPaperUri(String filename) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/documents/%s", filename))
                .toUriString();
    }

    /**
     * Determines the appropriate icon URL based on the file extension.
     *
     * @param fileExtension The extension of the file (e.g., "DOC", "PDF").
     * @return A URL to an icon image representing the file type.
     */
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
            // Default icon if the file extension is not recognized
            return "https://htmlstream.com/preview/front-dashboard-v2.1.1/assets/svg/brands/word-icon.svg";
        }
    }

    /**
     * Converts a PaperEntity object to a Paper DTO, including additional information about the creators and updaters.
     *
     * @param paperEntity The PaperEntity object to convert.
     * @param createdBy The User who created the paper.
     * @param updatedBy The User who last updated the paper.
     * @return A Paper DTO populated with data from the PaperEntity and User objects.
     */
    public static Paper fromPaperEntity(PaperEntity paperEntity, User createdBy, User updatedBy) {
        var paper = new Paper();
        // Copy properties from PaperEntity to Paper DTO
        BeanUtils.copyProperties(paperEntity, paper);

        // Set additional information related to the paper's creator and updater
        paper.setOwnerName(createdBy.getFirstName() + " " + createdBy.getLastName());
        paper.setOwnerEmail(createdBy.getEmail());
        paper.setOwnerLastLogin(createdBy.getLastLogin());
        paper.setUpdaterName(updatedBy.getFirstName() + " " + updatedBy.getLastName());

        return paper;
    }
}
