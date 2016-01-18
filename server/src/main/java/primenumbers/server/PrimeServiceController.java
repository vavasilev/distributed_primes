package primenumbers.server;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrimeServiceController {

	@RequestMapping("/")
    @ResponseBody
	public Boolean isPrime(Long number) {
		System.out.println("Calculating: "+number);
        long limit = (long)Math.sqrt(number);
        for(long i=2; i<=limit ; i++) {
            if(number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
