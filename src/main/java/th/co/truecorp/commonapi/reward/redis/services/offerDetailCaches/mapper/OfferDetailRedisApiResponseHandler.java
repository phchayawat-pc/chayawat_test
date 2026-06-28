package th.co.truecorp.commonapi.reward.redis.services.offerDetailCaches.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OfferDetailRedisApiResponseHandler {

    @Autowired
    private Gson gson;

    public String convertResponseBodyEntityToString(ResponseEntity<?> response) throws JsonProcessingException {
        Optional<String> text = Optional.of(gson.toJson(response.getBody()));
        return text.map(r -> r).orElseThrow(
                () -> new IllegalArgumentException("Error occurred while converting Object to JSON string"));
    }

    public String convertRequestObjectToString(Object request) throws JsonProcessingException {
        Optional<String> text = Optional.of(gson.toJson(request));
        return text.map(r -> r).orElseThrow(
                () -> new IllegalArgumentException("Error occurred while converting Object to JSON string"));
    }

    public <T> T convertResponseToObject(String value, Class<T> responseType) throws JsonProcessingException {
        Optional<T> text = Optional.of(gson.fromJson(value, responseType));
        return text.map(r -> r).orElseThrow(
                () -> new IllegalArgumentException("Error occurred while converting JSON string to object"));
    }
}
