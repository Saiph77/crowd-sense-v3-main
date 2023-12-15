package com.fzu.crowdsense.model.request.feedback;

import lombok.Data;

@Data
public class AddFeedBackRequest {

    private static final long serialVersionUID = 3191241716373120793L;

    private String advice;

    private String contactDetails;


    //举报？
}
