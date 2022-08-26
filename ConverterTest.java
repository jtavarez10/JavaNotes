package com.amazon.ata.advertising.service.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.amazon.ata.advertising.service.AdvertisementServiceException;
import com.amazon.ata.customerservice.CustomerProfile;
import com.amazon.ata.customerservice.Spend;
import com.amazon.ata.primeclubservice.Benefit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ConverterTest {
    private static final List<String> benefits = Arrays.asList(Benefit.MOM_DISCOUNT, Benefit.AMZN4KIDS,
        Benefit.FREE_TRIDENT_VOD);
    private final String jsonString = "[\"MOM_DISCOUNT\",\"AMZN4KIDS\",\"FREE_TRIDENT_VOD\"]";
    private CustomerProfile customerProfile = CustomerProfile.builder()
        .withAgeRange("AGE_22_TO_25")
        .withHomeState("RI")
        .withParent(false)
        .build();



    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    Converter converter;

    @BeforeEach
    void setup() {
        initMocks(this);
    }

    @Test
    void unConvert_WhenJsonStringIsEmpty_throwIllegalArgumentException() throws JsonProcessingException {
        converter = new Converter(new ObjectMapper());
        String jsonSpendMap = "";
        when(mapper.readValue(jsonSpendMap, List.class)).thenThrow(JsonProcessingException.class);
        assertThrows(IllegalArgumentException.class, () -> converter.unConvert(jsonSpendMap, new TypeReference<List<String>>() {}));
    }
    @Test
    void unConvert_WhenJsonStringIsNull_throwIllegalArgumentException() throws JsonProcessingException {
        converter = new Converter(new ObjectMapper());
        String jsonSpendMap = null;
        when(mapper.readValue(jsonSpendMap, List.class)).thenThrow(JsonProcessingException.class);
        assertThrows(IllegalArgumentException.class, () -> converter.unConvert(jsonSpendMap, new TypeReference<List<String>>() {}));
    }
    @Test
    void convert_WhenObjectIsNull_throwIllegalArgumentException() throws JsonProcessingException {
        converter = new Converter(new ObjectMapper());
        String jsonSpendMap = null;
        when(mapper.readValue(jsonSpendMap, List.class)).thenThrow(JsonProcessingException.class);
        assertThrows(AdvertisementServiceException.class, () -> converter.convert(jsonSpendMap));
    }
    @Test
    void convert_WhenObjectIsEmpty_throwIllegalArgumentException() {

        //WHEN
        converter = new Converter(new ObjectMapper());

        //THEN
        assertThrows(AdvertisementServiceException.class, () -> converter.convert(new Object()));
    }



    @Test
    public void convert_withEmptyList_throwAdvertisementServiceException()  throws JsonProcessingException {

        //WHEN
        converter = new Converter(mapper);
        List<String> prime = new ArrayList<>();
        when(mapper.writeValueAsString(prime)).thenThrow(AdvertisementServiceException.class);

        assertThrows(AdvertisementServiceException.class, () -> converter.convert(prime));
    }

    @Test
    void unConvert_WhenJsonStringIsNotValid_throwAdvertisementServiceException() throws JsonProcessingException {
        converter = new Converter(new ObjectMapper());
        String jsonSpendMap = "NotValid";
        when(mapper.readValue(jsonSpendMap, new TypeReference<List<String>>() {})).thenThrow(JsonProcessingException.class);
        assertThrows(AdvertisementServiceException.class, () -> converter.unConvert(jsonSpendMap, new TypeReference<List<String>>() {}));
    }



    //List Of Prime Benefits
    @Test
    public void unConvert_jsonString_returnListOfBenefits_() throws JsonProcessingException {
        //WHEN
        converter = new Converter(new ObjectMapper());
        List<String> result = (List<String>) converter.unConvert(jsonString, new TypeReference<List<String>>() {});

        //THEN
        assertEquals(benefits, result);
    }
    @Test
    public void convert_listOfBenefits_stringReturned() {
        //WHEN
        converter = new Converter(new ObjectMapper());
        String result = converter.convert(benefits);

        //THEN
        assertEquals(jsonString, result);
    }

    // Convert JsonString to CustomerProfile
    @Test
    void unConverter_whitValidJsonStringThenReturnACustomerProfile() throws JsonProcessingException {
        converter = new Converter(new ObjectMapper());

        String jsonCustomerProfile = "{\"homeState\":\"RI\",\"ageRange\":\"AGE_22_TO_25\",\"parent\":false}";
        CustomerProfile profile = (CustomerProfile)converter.unConvert(jsonCustomerProfile, new TypeReference<CustomerProfile>() {});

        assertEquals("AGE_22_TO_25",  profile.getAgeRange());
        assertEquals("RI",  profile.getHomeState());
        assertEquals(false,  profile.isParent());
    }

    @Test
    void convertWhenValidCustomerProfileThenReturnJsonString() {
        converter = new Converter(new ObjectMapper());

        String profile1 = converter.convert(customerProfile);
        assertEquals(
            "{\"homeState\":\"RI\",\"ageRange\":\"AGE_22_TO_25\",\"parent\":false}", profile1);
    }

    @Test
    void unConvertWithSpendMapIsNotNullThenReturnJsonString() throws JsonProcessingException {
        converter = new Converter(new ObjectMapper());
        String jsonString = "{\"1\":{\"numberOfPurchases\":30,\"usdSpent\":10}}";
        Map<String, Spend> spendMap = new HashMap<>();

        TypeReference<Map<String, Spend>> typeReference = new TypeReference<Map<String, Spend>>() {};

        spendMap = (Map<String, Spend>) converter.unConvert(jsonString, typeReference);

        assertEquals("1", spendMap.entrySet().stream().findFirst().get().getKey());
        assertEquals(10, spendMap.get("1").getUsdSpent());
        assertEquals(30, spendMap.get("1").getNumberOfPurchases());

    }

    @Test
    void convertWhenSpendMapIsNotNullThenReturnJsonString() {
        converter = new Converter(new ObjectMapper());
        Map<String, Spend> spendMap = new HashMap<>();
        Spend spend = Spend.builder()
            .withNumberOfPurchases(30).withUsdSpent(10).build();
        spendMap.put("1", spend);
        String jsonSpendMap = converter.convert(spendMap);
        assertEquals("{\"1\":{\"numberOfPurchases\":30,\"usdSpent\":10}}", jsonSpendMap);

    }

    @Test
    void jsonToTypeWhenJsonIsValidThenReturnCorrectType() {
        converter = new Converter(new ObjectMapper());
        String json = "{\"1\":{\"numberOfPurchases\":30,\"usdSpent\":10}}";
        TypeReference<Map<String, Spend>> typeReference = new TypeReference<Map<String, Spend>>() {};

        Map<String, Spend> result = (Map<String, Spend>) converter.unConvert(json, typeReference);
        assertEquals(1, result.size());
    }


}
