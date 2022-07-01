import com.pilipenko.deal.model.Application;
import com.pilipenko.deal.model.Client;
import com.pilipenko.deal.model.Credit;
import com.pilipenko.deal.model.LoanOfferDTO;
import com.pilipenko.deal.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;

@WebMvcTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Slf4j
class DealServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DealService dealService;
    @MockBean
    private ClientService clientService;
    @MockBean
    private ApplicationService applicationService;
    @MockBean
    private CreditService creditService;
    @MockBean
    private StatusHistoryService statusHistoryService;
    @MockBean
    private RestTemplate restTemplate;





    @Test
    void getLoanOffers() {
//        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO(BigDecimal.valueOf(100000),
//                10, "Igor", "Pilipenko", "Igorevich",
//                "iipilipenko@mail.ru", LocalDate.of(1993, 8, 21),
//                "4444", "666666");
        Mockito.when(clientService.createNew(ArgumentMatchers.any())).thenReturn(new Client());
        Mockito.when(applicationService.createNew(ArgumentMatchers.any())).thenReturn(new Application());
        Mockito.when(creditService.createNewCredit(ArgumentMatchers.any())).thenReturn(new Credit());
        doNothing().when(applicationService);
        doNothing().when(statusHistoryService);
        LoanOfferDTO loanOffer = new LoanOfferDTO(1000L, BigDecimal.valueOf(20000), BigDecimal.valueOf(30000),
                (Integer) 8, BigDecimal.valueOf(1000), BigDecimal.valueOf(20), true, true);
        List<LoanOfferDTO> offers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            loanOffer.setRate(loanOffer.getRate().add(BigDecimal.valueOf(i)));
            offers.add(i, loanOffer);
        }
        Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                new ParameterizedTypeReference<List<LoanOfferDTO>>() {}))
                .thenReturn(new ResponseEntity<List<LoanOfferDTO>>(offers, HttpStatus.OK));
        List<LoanOfferDTO> resultList = dealService.getLoanOffers(ArgumentMatchers.any());
        assertEquals(BigDecimal.valueOf(3), offers.get(0).getRate());


    }


}