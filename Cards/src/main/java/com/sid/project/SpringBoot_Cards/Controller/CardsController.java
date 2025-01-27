package com.sid.project.SpringBoot_Cards.Controller;

import com.sid.project.SpringBoot_Cards.Constants.CardsConstants;
import com.sid.project.SpringBoot_Cards.DTO.CardDTO;
import com.sid.project.SpringBoot_Cards.DTO.CardsContactInfoDto;
import com.sid.project.SpringBoot_Cards.Exception.Response;
import com.sid.project.SpringBoot_Cards.Service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/cards")
public class CardsController
{

    private final CardService cardService;

    @Value("${build.version}")
    private String buildverion;

    @Autowired
    private Environment environment;

    @Autowired
    private CardsContactInfoDto cardsContactInfoDto;

    public CardsController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/generateCard")
    public ResponseEntity<Response> createCard(@Valid @RequestParam
                                                   @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits") String mobileNo)
    {
        cardService.createCard(mobileNo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CardDTO> fetchCardDetails(@RequestParam
                                                     @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                     String mobileNo) {
        CardDTO cardDto = cardService.fetchCard(mobileNo);
        return ResponseEntity.status(HttpStatus.OK).body(cardDto);
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateCardDetails(@Valid @RequestBody CardDTO cardDto)
    {
        boolean isUpdated = cardService.updateCard(cardDto);
        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Response(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new Response(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response> deleteCardDetails(@RequestParam
                                                         @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                         String mobileNumber)
    {
        boolean isDeleted = cardService.deleteCard(mobileNumber);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Response(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new Response(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_DELETE));
        }
    }

    @GetMapping("/build-info")
    public ResponseEntity<String> getVersionInfo()
    {
        return ResponseEntity.status(HttpStatus.FOUND).body(buildverion);
    }

    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion()
    {
        return ResponseEntity.status(HttpStatus.FOUND).body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<CardsContactInfoDto> getContactInfo()
    {
        return ResponseEntity.status(HttpStatus.FOUND).body(cardsContactInfoDto);
    }
}
