document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('btnCadastrar').addEventListener('click', function () {
        window.location.href = 'cadastro.html';
    });
    document.getElementById('formLogin').addEventListener('submit', function (event) {
        event.preventDefault();
//Realizar o login e tratar a resposta
        const user = document.getElementById('user').value;
        const senha = document.getElementById('senha').value;

        fetch('http://localhost:8080/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ user, senha })
        })
            .then(response => response.json().then(data => ({ status: response.status, body: data })))
            .then(({ status, body }) => {
                if (status === 200 && body.success) {
                    Swal.fire({
                        title: "Sucesso!",
                        text: "Login realizado com sucesso!",
                        icon: "success",
                        timer: 1500,
                        showConfirmButton: false
                    }).then(() => {
                        setTimeout(() => {
                            localStorage.setItem('token', body.token);
                            localStorage.setItem('userNome', body.userNome);
                            localStorage.setItem('userId', body.userId);
                            console.log('Resposta do login:', body);
                            window.location.href = 'inicio.html';
                        }, 10);
                    });
                } else {
                    Swal.fire('Erro', body.message || 'Erro no login.', 'error');
                }
            })
            .catch(error => {
                console.error('Erro no login:', error);
                Swal.fire('Erro', 'Erro ao tentar realizar login. Tente novamente mais tarde.', 'error');
            });
    });
    
    
    //Função de recuperacao da senha
    document.getElementById('linkAjuda').addEventListener('click', async (e) => {
        e.preventDefault();
        const { value: email } = await Swal.fire({
            title: 'Recuperação de Senha',
            input: 'email',
            inputLabel: 'Informe seu e-mail cadastrado:',
            inputPlaceholder: 'exemplo@email.com',
            confirmButtonText: 'Continuar',
            showCancelButton: true
        });
        if (!email) return;
        let camposDisponiveis;
        try {
            const resCampos = await fetch(`http://localhost:8080/api/login/dados-seguranca?email=${encodeURIComponent(email)}`);
            if (!resCampos.ok) throw new Error("E-mail não encontrado.");
            camposDisponiveis = await resCampos.json();
        } catch (err) {
            Swal.fire('Erro', err.message, 'error');
            return;
        }
        const respostas = {};
        if (camposDisponiveis.cpf) {
            const { value } = await Swal.fire({
                title: 'Confirmação de Segurança',
                input: 'text',
                inputLabel: 'Informe seu CPF:',
                showCancelButton: true
            });
            if (value) respostas.cpf = value;
        }

        if (camposDisponiveis.contato) {
            const { value } = await Swal.fire({
                title: 'Confirmação de Segurança',
                input: 'text',
                inputLabel: 'Informe seu telefone de contato:',
                showCancelButton: true
            });
            if (value) respostas.contato = value;
        }

        if (camposDisponiveis.datanasc) {
            const { value } = await Swal.fire({
                title: 'Confirmação de Segurança',
                input: 'text',
                inputLabel: 'Informe sua data de nascimento (YYYY-MM-DD):',
                showCancelButton: true
            });
            if (value) respostas.datanasc = value;
        }
        const { value: novaSenha } = await Swal.fire({
            title: 'Nova Senha',
            input: 'password',
            inputLabel: 'Digite sua nova senha:',
            inputPlaceholder: 'mínimo 6 caracteres',
            inputAttributes: { minlength: 6 },
            showCancelButton: true
        });
        if (!novaSenha) return;
        const payload = { email, novaSenha, ...respostas };

        try {
            const res = await fetch('http://localhost:8080/api/login/recuperar-senha', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await res.json();
            if (res.ok && data.success) {
                Swal.fire('Sucesso!', 'Sua senha foi redefinida com sucesso.', 'success');
            } else {
                Swal.fire('Erro', data.message || 'Falha ao redefinir senha.', 'error');
            }
        } catch (err) {
            Swal.fire('Erro', 'Não foi possível conectar ao servidor.', 'error');
        }
    });
});