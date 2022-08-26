package com.amazon.ata.advertising.service.converter;

import com.amazon.ata.advertising.service.AdvertisementServiceException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;
/**
 * Class to convert a CustomerProfile  type to a string and vice-versa.
 */
public class Converter {
    private ObjectMapper objectMapper;

    /**
     * Class constructor.
     *
     * @param objectMapper ObjectMapper
     */
    public Converter(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * Convert Customer profile to a String in json format.
     *
     * @param object to be convert in to a json
     * @return jsonCustomerProfile
     */
    public String convert(Object object) {
        if (Objects.isNull(object)) {
            throw new AdvertisementServiceException("Object can not be null");
        }
        String jsonCustomerProfile;
        try {
            jsonCustomerProfile = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new AdvertisementServiceException("Unable to convert this json" + e);
        }

        return jsonCustomerProfile;
    }

    /**
     * Convert Customer profile to a String in json format.
     * @param <T> Generic Param
     * @param json receive a json String
     * @param typeReference TypeReference
     * @return objet converted
     * @throws AdvertisementServiceException exception
     */

    public <T> T unConvert(String json, TypeReference<T> typeReference) {
        if (Objects.isNull(json) || json.isEmpty()) {
            throw new IllegalArgumentException("JSON is empty or null");
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new AdvertisementServiceException("cannot convert to " + typeReference.getType(), e);
        }
    }



}
