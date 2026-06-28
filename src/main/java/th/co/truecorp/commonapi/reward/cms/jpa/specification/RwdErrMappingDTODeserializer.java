package th.co.truecorp.commonapi.reward.cms.jpa.specification;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import th.co.truecorp.commonapi.reward.cms.jpa.dto.RwdErrMappingDTO;

import java.io.IOException;

public class RwdErrMappingDTODeserializer extends JsonDeserializer<RwdErrMappingDTO> {

    @Override
    public RwdErrMappingDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        RwdErrMappingDTO dto = new RwdErrMappingDTO();
        dto.setAction(node.get("action").asText());
        dto.setErrCode(node.get("errCode").asText());
        dto.setCustomCode(node.get("customCode").asText());
        dto.setMessage(node.get("message").asText());
        dto.setHttpCode(node.get("httpCode").asText());
        dto.setBrandCode(node.get("brand").asText());
        dto.setLang(node.get("lang").asText());
        return dto;
    }
}
