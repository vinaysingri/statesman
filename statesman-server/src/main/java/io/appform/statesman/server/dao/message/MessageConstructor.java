package io.appform.statesman.server.dao.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.statesman.model.MessageConfig;
import io.appform.statesman.model.exception.ResponseCode;
import io.appform.statesman.model.exception.StatesmanError;
import io.appform.statesman.server.utils.MapperUtils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
public class MessageConstructor implements IMessageConstructor {

    private final MessageConfigStoreCommand messageConfigStoreCommand;
    private final String DEFAULT_FILED_NAME = "default";

    public MessageConstructor(MessageConfigStoreCommand messageConfigStoreCommand) {
        this.messageConfigStoreCommand = messageConfigStoreCommand;
    }


    @Override
    public String constructMessage(String messageId, String language, String state) {

        Optional<MessageConfig> optionalMessageConfig = messageConfigStoreCommand.get(messageId);
        if(!optionalMessageConfig.isPresent()){
            throw new StatesmanError("No message config found", ResponseCode.NO_PROVIDER_FOUND);
        }

        MessageConfig storeOutput = optionalMessageConfig.get();
        JsonNode root = MapperUtils.readTree(storeOutput.getMessageBody());
        JsonNode languageNode;

        if(root == null){
            throw new StatesmanError("No message config found", ResponseCode.NO_PROVIDER_FOUND);
        }else if((root.get(language) == null)){
            if(root.get(DEFAULT_FILED_NAME) == null) {
                throw new StatesmanError("No ddefault language found", ResponseCode.NO_PROVIDER_FOUND);
            }else{
                languageNode = root.get(DEFAULT_FILED_NAME);
            }
        }else{
            languageNode = root.get(language);
        }

        if(languageNode.get(state) != null) {
            return languageNode.get(state).asText();
        }else if(languageNode.get(DEFAULT_FILED_NAME) != null) {
            return languageNode.get(DEFAULT_FILED_NAME).asText();
        }else{
            return root.get(DEFAULT_FILED_NAME).get(DEFAULT_FILED_NAME).asText();
        }
    }

    public static void main(String args[]){

        String input = "{\"welcome\": {\"default\": {\"KA\": \"msg1\", \"default\": \"msg2\"}, \"hindi\": {\"RAJ\": \"msg3\", \"UP\": \"msg4\", \"default\": \"msg5\"}, \"marathi\": {\"MAH\": \"msg6\", \"default\": \"msg7\"}}\n" +
                "\n}";

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(input);
            JsonNode languaugeNode = root.get("welcome");
            JsonNode stateeNodee = languaugeNode.get("hindi").get("RAJ");
            System.out.println(stateeNodee);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}


