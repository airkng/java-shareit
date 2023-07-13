package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;


import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> addRequest(Long id, ItemRequestDto itemRequestDto) {

        return post("", id, itemRequestDto);
    }

    public ResponseEntity<Object> getAllOwnRequests(Long id, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);

        return get("" + "?from={from}&size={size}", id, parameters);
    }

    public ResponseEntity<Object> getAllRequests(Long id, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("/all" + "?from={from}&size={size}", id, parameters);
    }


    public ResponseEntity<Object> getRequestById(Long id, Long requestId) {
        return get("/" + requestId, id);
    }

}
