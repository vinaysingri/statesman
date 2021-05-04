package io.appform.statesman.server.dao.message;

import io.appform.statesman.engine.MessageConfigStore;
import io.appform.statesman.model.MessageConfig;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageConstructor implements IMessageConstructor {

    private final MessageConfigStore messageConfigStore;

    public MessageConstructor(MessageConfigStore messageConfigStore) {
        this.messageConfigStore = messageConfigStore;
    }


    @Override
    public String constructMessage(String messageId, String language, String state) {

        MessageConfig storeOutput = messageConfigStore.get(messageId).get();

        storeOutput.getMessageBody()

    }
}


