document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('btnCadastrar').addEventListener('click', function () {
        window.location.href = 'cadastro.html';
    });

    document.getElementById('formLogin').addEventListener('submit', function (event) {
        event.preventDefault();

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
                    Swal.fire({ title: "Sucesso!", text: "Login realizado com sucesso!", icon: "success", timer: 1500, showConfirmButton: false }).then(() => {
                        setTimeout(() => {
                            localStorage.setItem('token', body.token);
                            localStorage.setItem('userNome', body.userNome);
                            localStorage.setItem('userId', body.userId);
                            console.log('Resposta do login:', body);

                            window.location.href = 'inicio.html';
                        }, 10); 
                    });
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