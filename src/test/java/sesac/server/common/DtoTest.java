package sesac.server.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import sesac.server.common.exception.BindingResultHandler;

public class DtoTest {

    protected Validator validator;

    protected BindingResultHandler bindingResultHandler = new BindingResultHandler();

    @BeforeEach
    public void setUp() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        this.validator = validatorFactoryBean;
    }

    protected BindingResult getBindingResult(Record request) {
        DataBinder dataBinder = new DataBinder(request);
        dataBinder.setValidator(validator);
        dataBinder.validate();

        return dataBinder.getBindingResult();
    }

    protected String testText(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        return sb.toString();
    }
}
