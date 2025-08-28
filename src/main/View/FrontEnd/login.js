document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('btnCadastrar').addEventListener('click', function () {
        window.location.href = 'cadastro.html';
    });

    //Leitura do formulário etc
    document.getElementById('formLogin').addEventListener('submit', function (event) {
        event.preventDefault();

        const user = document.getElementById('user').value;
        const senha = document.getElementById('senha').value;

        //Direcionar rota
        fetch('http://localhost:8080/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ user, senha })
        })
            .then(response => response.json().then(data => ({ status: response.status, body: data })))
            .then(({ status, body }) => {
                if (status === 200 && body.success) {
                    alert(body.message);

                    localStorage.setItem('token', body.token);          //Token de segurança enviado para o localStorage
                    localStorage.setItem('userNome', body.userNome);
                    window.location.href = 'inicio.html';
                } else {
                    alert(body.message || 'Erro no login.');
                }
            })
            .catch(error => {
                console.error('Erro no login:', error);
                alert('Erro ao tentar realizar login. Tente novamente mais tarde.');
            });
    });
});