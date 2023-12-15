package com.fzu.crowdsense.model.request.feedback;

import lombok.Data;
@Data
public class UpdateFeedBackById {
    private static final long serialVersionUID = 3191241716373120793L;
    private Long id;

    private String advice;

    private String contactDetails;
}
