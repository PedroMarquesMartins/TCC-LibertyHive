package unigran.br.Controllers;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AreaMatchControllerTest.class,
        CadastroControllerTest.class,
        ChatControllerTest.class,
        EscambistaControllerTest.class,
        PostagemControllerTest.class,
        PropostaControllerTest.class
})
public class ApiIntegrationTestSuite {

}