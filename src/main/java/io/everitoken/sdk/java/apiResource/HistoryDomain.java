package io.everitoken.sdk.java.apiResource;

import com.mashape.unirest.http.JsonNode;
import io.everitoken.sdk.java.dto.NameableResource;
import io.everitoken.sdk.java.exceptions.ApiResponseException;
import io.everitoken.sdk.java.param.RequestParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HistoryDomain extends ApiResource {
    private static final String uri = "/v1/history/get_domains";

    public HistoryDomain() {
        super(uri);
    }

    public HistoryDomain(@NotNull ApiRequestConfig apiRequestConfig) {
        super(uri, apiRequestConfig);
    }

    public List<NameableResource> request(RequestParams requestParams) throws ApiResponseException {
        JsonNode res = super.makeRequest(requestParams);

        return StreamSupport.stream(res.getArray().spliterator(), true)
                .map(name -> NameableResource.create((String) name))
                .collect(Collectors.toList());
    }
}
