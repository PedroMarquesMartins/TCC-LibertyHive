package unigran.br.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//Service - API de email com mensagens prontas (precisa de internet)
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
//Bem vindo, na criação da conta
    public void enviarEmailBoasVindas(String para, String nomeUsuario) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(para);
        mensagem.setSubject("Bem-vindo à LibertyHive!");
        mensagem.setText("Olá " + nomeUsuario + ",\n\n" +
                "Seja bem-vindo à LibertyHive — sua plataforma amarelinha de trocas e conexões.💛\n\n" +
                "Sua conta foi criada com sucesso, e agora você já pode explorar, negociar e fazer parte de uma comunidade que valoriza a colaboração. 🔁\n\n" +
                "Qualquer dúvida ou sugestão, estamos por aqui para te ajudar.\n\n" +
                "Conte com a gente,\n" +
                "Equipe LibertyHive");

        mailSender.send(mensagem);
    }
//Email de nova proposta
    @Async
    public void enviarAlertaNovaProposta(String paraEmail, String nomeReceptor, String nomeProponente, String nomeItemDesejado) {
        try {
            String linkPropostas = "http://localhost:8080/minhasPropostas.html";

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(paraEmail);
            mensagem.setSubject("Nova Proposta de Troca em LibertyHive! 🎁");
            mensagem.setText("Olá " + nomeReceptor + ",\n\n" +
                    "Você tem uma NOVA PROPOSTA esperando por você na LibertyHive!\n\n" +
                    "O usuário " + nomeProponente + " enviou uma proposta de negócio para o seu item: " + nomeItemDesejado + ". \n\n" +
                    "Não perca tempo! Acesse a sua área de propostas para analisar, aceitar ou recusar a negociação:\n" +
                    linkPropostas + "\n\n" +
                    "Boas trocas!\n" +
                    "Equipe LibertyHive");

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.err.println("ERRO AO ENVIAR E-MAIL DE NOVA PROPOSTA PARA " + paraEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Proposta aceita
    @Async
    public void enviarNotificacaoPropostaAceita(String paraEmail, String nomeProponente, String nomeItemDesejado, String nomeItemOferecido) {
        try {
            String linkPropostas = "http://localhost:8080/minhasPropostas.html";
            String textoProposta;

            if (nomeItemOferecido != null && !nomeItemOferecido.isEmpty()) {
                textoProposta = "A sua proposta para o item \"" + nomeItemDesejado + "\" em troca do seu item \"" + nomeItemOferecido + "\" foi ACEITA!";
            } else {
                textoProposta = "A sua proposta para o item de doação \"" + nomeItemDesejado + "\" foi ACEITA!";
            }

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(paraEmail);
            mensagem.setSubject("Boas notícias! Sua proposta foi aceita na LibertyHive! ✅");
            mensagem.setText("Olá " + nomeProponente + ",\n\n" +
                    "Temos uma ótima notícia para você!\n\n" +
                    textoProposta + "\n\n" +
                    "Acesse a plataforma para ver os detalhes e combinar os próximos passos com o outro usuário:\n" +
                    linkPropostas + "\n\n" +
                    "Parabéns pela negociação!\n" +
                    "Equipe LibertyHive");

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.err.println("ERRO AO ENVIAR E-MAIL DE PROPOSTA ACEITA PARA " + paraEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}