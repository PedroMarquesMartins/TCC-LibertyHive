package unigran.br.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

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
}