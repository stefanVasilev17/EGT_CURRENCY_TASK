package com.egt.digital.task.controller;

import com.egt.digital.task.exception.XmlExceptionHandler;
import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.model.XmlGetCommand;
import com.egt.digital.task.model.XmlHistoryCommand;
import com.egt.digital.task.model.XmlRequest;
import com.egt.digital.task.resource.XmlApiResource;
import com.egt.digital.task.service.IXmlApiService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(XmlApiResource.class)
@Import(XmlExceptionHandler.class)
public class XmlApiResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IXmlApiService xmlApiService;

    private final XmlMapper objectMapper = new XmlMapper();

    private String toXml(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private XmlRequest createValidGetRequest() {
        XmlGetCommand get = new XmlGetCommand("client1", "USD");
        return new XmlRequest("req-123", get, null);
    }

    private XmlRequest createValidHistoryRequest() {
        XmlHistoryCommand history = new XmlHistoryCommand("client1", "USD", 24);
        return new XmlRequest("req-456", null, history);
    }

    @Test
    void testGet_Success() throws Exception {
        ExchangeRate rate = new ExchangeRate("USD", BigDecimal.valueOf(1.5), LocalDateTime.now());

        Mockito.when(xmlApiService.isDuplicate("req-123")).thenReturn(false);
        Mockito.when(xmlApiService.getCurrentRate(any())).thenReturn(rate);

        // Prepare the XML request with only <get> and without <history>
        XmlRequest getRequest = new XmlRequest();
        getRequest.setId("req-123");
        getRequest.setGet(new XmlGetCommand("client1", "USD"));
        // Do not set history at all to avoid sending the <history/> tag
        // getRequest.setHistory(null);  // This is already omitted by not setting the history field

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(toXml(getRequest)))  // Ensure the XML is correctly serialized
                .andExpect(status().isOk())  // Expecting HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_XML))  // Expecting XML response
                .andExpect(xpath("/ExchangeRate/currency").string("USD"))  // Verify the currency
                .andExpect(xpath("/ExchangeRate/rate").string("1.5"));  // Verify the rate
    }
    @Test
    void testGet_DuplicateRequest() throws Exception {
        Mockito.when(xmlApiService.isDuplicate("req-123")).thenReturn(true);

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(toXml(createValidGetRequest())))
                .andExpect(status().isConflict())
                .andExpect(xpath("/XmlErrorResponse/message").string("Duplicate request"));
    }

    @Test
    void testHistory_Success() throws Exception {
        ExchangeRate rate = new ExchangeRate("USD", BigDecimal.valueOf(1.5), LocalDateTime.now());

        Mockito.when(xmlApiService.isDuplicate("req-456")).thenReturn(false);
        Mockito.when(xmlApiService.getHistoryRates(any())).thenReturn(List.of(rate));

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(toXml(createValidHistoryRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    @Test
    void testHistory_Empty() throws Exception {
        Mockito.when(xmlApiService.isDuplicate("req-456")).thenReturn(false);
        Mockito.when(xmlApiService.getHistoryRates(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(toXml(createValidHistoryRequest())))
                .andExpect(status().isNotFound())
                .andExpect(xpath("/XmlErrorResponse/message")
                        .string("No historical data found for currency: USD"));
    }

    @Test
    void testMissingIdValidation() throws Exception {
        XmlRequest request = createValidGetRequest();
        request.setId(null);

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(toXml(request)))
                .andExpect(status().isBadRequest());
    }
}