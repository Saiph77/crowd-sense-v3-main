package com.fzu.crowdsense.model.request.feedback;

import com.fzu.crowdsense.common.PageRequest;
import lombok.Data;

@Data
public class SelectFeedBackRequest extends PageRequest {
    private static final long serialVersionUID = 3191241716373120793L;

    private Long id;

    private String advice;

    private String contactDetails;

    private Long feedbackUserId;
}
