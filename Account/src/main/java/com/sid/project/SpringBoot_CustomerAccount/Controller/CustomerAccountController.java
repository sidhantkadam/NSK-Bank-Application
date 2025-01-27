package com.sid.project.SpringBoot_CustomerAccount.Controller;


import com.sid.project.SpringBoot_CustomerAccount.Constants.AccountsConstants;
import com.sid.project.SpringBoot_CustomerAccount.DTO.AccountContactInfoDto;
import com.sid.project.SpringBoot_CustomerAccount.DTO.AccountDto;
import com.sid.project.SpringBoot_CustomerAccount.DTO.CustomerDto;
import com.sid.project.SpringBoot_CustomerAccount.DTO.ResponseDto;
import com.sid.project.SpringBoot_CustomerAccount.Service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@Validated
public class CustomerAccountController
{
	private final AccountService accountService;

	@Value("${build.version}")
	private String buildVersion;

	@Autowired
	private Environment environment;

	@Autowired
	private AccountContactInfoDto accountContactInfoDto;

	public CustomerAccountController(AccountService accountService) {
		this.accountService = accountService;
	}

    @PostMapping("/createAccount")
	public ResponseEntity<ResponseDto> createAccount(@RequestBody CustomerDto customerDto)
	{
		accountService.createAccount(customerDto);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
	}

	@GetMapping("/getAccount")
	public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam
														   @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
														   String mobileNo)
	{
		CustomerDto customerDto = accountService.fetchAccount(mobileNo);
		return ResponseEntity.status(HttpStatus.FOUND).body(customerDto);
	}

	@PutMapping("/updateAccount/{customerId}")
	public ResponseEntity<CustomerDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto)
	{
		accountService.updateAccount(customerDto);
		return ResponseEntity.status(HttpStatus.OK).body(customerDto);
	}

	@DeleteMapping("/deleteAccount")
	public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam
															@Pattern(regexp="(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
															String mobileNo)
	{
		boolean isDeleted = accountService.deleteAccount(mobileNo);
		if(isDeleted)
		{
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
		}else
		{
			return ResponseEntity
					.status(HttpStatus.EXPECTATION_FAILED)
					.body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
		}
	}
	
	@GetMapping("/test")
	public String test()
	{
		return "Working Fine !";
	}

	@GetMapping("/build-info")
	public ResponseEntity<String> getVersionInfo()
	{
		return ResponseEntity.status(HttpStatus.FOUND).body(buildVersion);
	}

	@GetMapping("/java-version")
	public ResponseEntity<String> getJavaVersion()
	{
		return ResponseEntity.status(HttpStatus.FOUND).body(environment.getProperty("JAVA_HOME"));
	}

	@GetMapping("/contact-info")
	public ResponseEntity<AccountContactInfoDto> getContactInfo()
	{
		return ResponseEntity.status(HttpStatus.FOUND).body(accountContactInfoDto);
	}
}
