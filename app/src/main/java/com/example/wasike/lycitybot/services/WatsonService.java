package com.wasike.lycitybot.services;

import com.wasike.lycitybot.Constants;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;

public class WatsonService {
    public ConversationService watsonConversationService = new ConversationService("2017-05-06", Constants.BLUEMIX_USER_NAME, Constants.BLUEMIX_PASSWORD);
}
